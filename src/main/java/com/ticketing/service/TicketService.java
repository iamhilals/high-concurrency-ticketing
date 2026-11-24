package com.ticketing.service;

import com.ticketing.dto.*;
import com.ticketing.entity.Event;
import com.ticketing.entity.User;
import com.ticketing.repository.EventRepository;
import com.ticketing.repository.TicketRepository;
import com.ticketing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AntiScalperService antiScalperService;
    private final AsyncTicketProcessor asyncTicketProcessor;
    private final boolean kafkaEnabled;

    public TicketService(
            TicketRepository ticketRepository,
            EventRepository eventRepository,
            UserRepository userRepository,
            StringRedisTemplate redisTemplate,
            @Autowired(required = false) KafkaTemplate<String, Object> kafkaTemplate,
            AntiScalperService antiScalperService,
            AsyncTicketProcessor asyncTicketProcessor,
            @Value("${app.kafka.enabled:true}") boolean kafkaEnabled) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.antiScalperService = antiScalperService;
        this.asyncTicketProcessor = asyncTicketProcessor;
        this.kafkaEnabled = kafkaEnabled;
    }

    private static final long SEAT_LOCK_MINUTES = 10;

    @Transactional(readOnly = true)
    public List<TicketResponse> getUserTickets(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return ticketRepository.findByUserId(userId).stream()
                .map(ticket -> {
                    Event ev = ticket.getEvent();
                    boolean isExpired = ev != null && ev.getDateTime() != null && ev.getDateTime().isBefore(now);
                    String fallbackPnr = "PSLV-" + String.format("%06d", ticket.getId() != null ? ticket.getId() : 0L);
                    Double priceVal = ticket.getPaidPrice() != null
                            ? ticket.getPaidPrice()
                            : (ev != null && ev.getPrice() != null ? ev.getPrice().doubleValue() : null);

                    return TicketResponse.builder()
                            .ticketId(ticket.getId())
                            .eventId(ev != null ? ev.getId() : null)
                            .eventTitle(ev != null ? ev.getTitle() : "Etkinlik")
                            .eventVenue(ev != null ? ev.getVenue() : null)
                            .eventDateTime(ev != null ? ev.getDateTime() : null)
                            .userId(ticket.getUser().getId())
                            .username(ticket.getUser().getUsername())
                            .purchaseDate(ticket.getPurchaseDate())
                            .status(isExpired ? "EXPIRED" : "CONFIRMED")
                            .pnrCode(ticket.getPnrCode() != null && !ticket.getPnrCode().isEmpty() ? ticket.getPnrCode() : fallbackPnr)
                            .seatInfo(ticket.getSeatInfo())
                            .zoneName(ticket.getZoneName())
                            .blockCode(ticket.getBlockCode())
                            .paidPrice(priceVal)
                            .expired(isExpired)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 10 Dakikalık Geçici Koltuk Rezervasyonu (Anti-Scalper AI Bot Kontrolü + Redis Lock with 10m TTL)
     */
    public SeatReserveResponse reserveSeat(SeatReserveRequest request) {
        // 1. Karaborsacı / Bot Algılama AI Analizi
        BotDetectionResult botResult = antiScalperService.analyzeReservationAttempt(
                request.getUserId(),
                "127.0.0.1", // Client IP
                request.getEventId(),
                request.getSeatId()
        );

        if (botResult.isBlocked()) {
            return SeatReserveResponse.builder()
                    .success(false)
                    .riskScore(botResult.getRiskScore())
                    .riskLevel(botResult.getRiskLevel())
                    .message("🚨 Güvenlik Koruması: Olağandışı hızlı işlem tespit edildiği için talebiniz geçici olarak engellendi.")
                    .build();
        }

        // 2. Etkinlik kontenjanı tükenmişse rezervasyonu ve satışı kesin olarak kapat
        String capKey = "event:" + request.getEventId() + ":capacity";
        String capStr = redisTemplate.opsForValue().get(capKey);
        int currentCap = -1;
        if (capStr != null) {
            try { currentCap = Integer.parseInt(capStr); } catch (NumberFormatException ignored) {}
        } else {
            currentCap = eventRepository.findById(request.getEventId())
                    .map(e -> e.getAvailableCapacity() != null ? e.getAvailableCapacity() : 0)
                    .orElse(0);
        }
        if (currentCap <= 0) {
            return SeatReserveResponse.builder()
                    .success(false)
                    .riskScore(botResult.getRiskScore())
                    .riskLevel(botResult.getRiskLevel())
                    .message("🚫 Bu etkinlik için tüm biletler tükendi! Etkinlik satışa kapatılmıştır.")
                    .build();
        }

        String seatLockKey = "seat_lock:event:" + request.getEventId() + ":seat:" + request.getSeatId();
        String reservationId = "RES-" + UUID.randomUUID().toString().substring(0, 8);

        // Atomik setIfAbsent (NX) ile koltuğu 10 dakikalığına kilitliyoruz
        Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(seatLockKey, reservationId + ":" + request.getUserId(), SEAT_LOCK_MINUTES, TimeUnit.MINUTES);

        if (Boolean.FALSE.equals(isLocked)) {
            return SeatReserveResponse.builder()
                    .success(false)
                    .riskScore(botResult.getRiskScore())
                    .riskLevel(botResult.getRiskLevel())
                    .message("Bu koltuk şu anda başka bir kullanıcı tarafından 10 dakikalığına rezerve edilmiştir!")
                    .build();
        }

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(SEAT_LOCK_MINUTES);

        return SeatReserveResponse.builder()
                .success(true)
                .reservationId(reservationId)
                .eventId(request.getEventId())
                .userId(request.getUserId())
                .seatId(request.getSeatId())
                .zoneName(request.getZoneName())
                .price(request.getPrice())
                .expiresAt(expiresAt)
                .riskScore(botResult.getRiskScore())
                .riskLevel(botResult.getRiskLevel())
                .message("Koltuk 10 dakikalığına başarıyla rezerve edildi. Lütfen ödemeyi tamamlayın.")
                .build();
    }

    /**
     * Rezerve Edilen Koltuğun Kilit Süresini 3 Dakika Uzatma (+3 Dk Extension)
     */
    public SeatReserveResponse extendSeatLock(SeatReserveRequest request) {
        String seatLockKey = "seat_lock:event:" + request.getEventId() + ":seat:" + request.getSeatId();
        String lockVal = redisTemplate.opsForValue().get(seatLockKey);

        if (lockVal == null) {
            return SeatReserveResponse.builder()
                    .success(false)
                    .message("Rezervasyon süresi dolduğu için süre uzatılamadı!")
                    .build();
        }

        Long currentTtlSeconds = redisTemplate.getExpire(seatLockKey, TimeUnit.SECONDS);
        long extensionSeconds = 180; // +3 dakika
        long newTtlSeconds = (currentTtlSeconds != null && currentTtlSeconds > 0) ? currentTtlSeconds + extensionSeconds : extensionSeconds;

        // Maksimum 15 dakikayı aşmamasını sağla (900 saniye)
        if (newTtlSeconds > 900) {
            newTtlSeconds = 900;
        }

        redisTemplate.expire(seatLockKey, newTtlSeconds, TimeUnit.SECONDS);
        LocalDateTime newExpiresAt = LocalDateTime.now().plusSeconds(newTtlSeconds);

        return SeatReserveResponse.builder()
                .success(true)
                .eventId(request.getEventId())
                .seatId(request.getSeatId())
                .userId(request.getUserId())
                .expiresAt(newExpiresAt)
                .message("Koltuk rezervasyon süreniz başarıyla +3 dakika uzatıldı!")
                .build();
    }

    /**
     * Rezerve Edilen Koltuğu Ödeme Sonrası Kesinleştirme (Confirm Reservation)
     */
    public TicketResponse confirmSeatReservation(SeatConfirmRequest request) {
        String seatLockKey = "seat_lock:event:" + request.getEventId() + ":seat:" + request.getSeatId();
        String lockVal = redisTemplate.opsForValue().get(seatLockKey);

        if (lockVal == null || !lockVal.contains(request.getReservationId())) {
            throw new IllegalStateException("Koltuk rezervasyon süreniz doldu veya geçersiz rezervasyon!");
        }

        // Genel satın alma akışına gerçek koltuk, blok, kategori ve fiyat bilgileriyle yönlendir
        String resolvedSeat = (request.getSeatInfo() != null && !request.getSeatInfo().isEmpty())
                ? request.getSeatInfo()
                : request.getSeatId();

        TicketRequest ticketRequest = TicketRequest.builder()
                .eventId(request.getEventId())
                .userId(request.getUserId())
                .seatInfo(resolvedSeat)
                .zoneName(request.getZoneName())
                .blockCode(request.getBlockCode())
                .paidPrice(request.getPaidPrice())
                .build();

        TicketResponse response = purchaseTicket(ticketRequest);

        // Başarılı Kafka kuyruğuna yazım sonrası Redis kilidini serbest bırak
        redisTemplate.delete(seatLockKey);

        return response;
    }

    /**
     * Redis'te kapasite anahtarı yoksa (ör. Redis yeniden başladıysa) PostgreSQL'den
     * atomik (setIfAbsent) olarak senkronize eder. Böylece DECR komutu olmayan anahtarı
     * -1 yapıp sahte "tükendi" durumuna düşürmez.
     */
    private void ensureRedisCapacityInitialized(Long eventId) {
        String redisKey = "event:" + eventId + ":capacity";
        String existing = redisTemplate.opsForValue().get(redisKey);
        if (existing == null) {
            int dbCap = eventRepository.findById(eventId)
                    .map(e -> e.getAvailableCapacity() != null ? e.getAvailableCapacity() : 0)
                    .orElse(0);
            redisTemplate.opsForValue().setIfAbsent(redisKey, String.valueOf(dbCap));
        }
    }

    /**
     * Bilet satın alma talebini karşılayan metot (Redis DECR + Kafka / Async Fallback).
     */
    public TicketResponse purchaseTicket(TicketRequest request) {
        String redisKey = "event:" + request.getEventId() + ":capacity";

        // 0. Redis anahtarı eksikse DB'den otomatik tamamla
        ensureRedisCapacityInitialized(request.getEventId());

        // 1. Redis'ten kapasiteyi atomik olarak 1 azaltıyoruz (DECR)
        Long remaining = redisTemplate.opsForValue().decrement(redisKey);
        
        if (remaining == null) {
            throw new IllegalStateException("Etkinlik kontenjan bilgisine şu an ulaşılamıyor.");
        }

        if (remaining < 0) {
            redisTemplate.opsForValue().increment(redisKey);
            throw new IllegalStateException("Bu etkinlik için tüm biletler tükendi!");
        }

        User user;
        Event event;
        try {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Oturum kullanıcısı veritabanında bulunamadı. Lütfen çıkış yapıp tekrar giriş yapın."));

            event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new IllegalArgumentException("Etkinlik bilgisi bulunamadı."));
        } catch (RuntimeException ex) {
            // Kullanıcı veya etkinlik bulunamazsa Redis stoğunu geri iade et
            redisTemplate.opsForValue().increment(redisKey);
            throw ex;
        }

        String generatedPnr = "PSLV-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        Double effectivePrice = request.getPaidPrice() != null
                ? request.getPaidPrice()
                : (event.getPrice() != null ? event.getPrice().doubleValue() : null);

        TicketBookingEvent bookingEvent = TicketBookingEvent.builder()
                .eventId(event.getId())
                .userId(user.getId())
                .pnrCode(generatedPnr)
                .seatInfo(request.getSeatInfo())
                .zoneName(request.getZoneName())
                .blockCode(request.getBlockCode())
                .paidPrice(effectivePrice)
                .build();

        if (kafkaEnabled && kafkaTemplate != null) {
            try {
                kafkaTemplate.send("ticket-bookings", String.valueOf(event.getId()), bookingEvent);
            } catch (Exception kafkaErr) {
                System.err.println("⚠️ Kafka gönderimi başarısız, Async kuyruğa düşülüyor: " + kafkaErr.getMessage());
                asyncTicketProcessor.processBookingAsync(bookingEvent);
            }
        } else {
            asyncTicketProcessor.processBookingAsync(bookingEvent);
        }

        boolean isExpired = event.getDateTime() != null && event.getDateTime().isBefore(LocalDateTime.now());

        return TicketResponse.builder()
                .ticketId(null)
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .eventVenue(event.getVenue())
                .eventDateTime(event.getDateTime())
                .userId(user.getId())
                .username(user.getUsername())
                .purchaseDate(LocalDateTime.now())
                .status("PENDING")
                .pnrCode(generatedPnr)
                .seatInfo(request.getSeatInfo())
                .zoneName(request.getZoneName())
                .blockCode(request.getBlockCode())
                .paidPrice(effectivePrice)
                .expired(isExpired)
                .build();
    }
}
