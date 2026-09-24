package com.ticketing.service;

import com.ticketing.dto.*;
import com.ticketing.entity.Event;
import com.ticketing.entity.User;
import com.ticketing.repository.EventRepository;
import com.ticketing.repository.TicketRepository;
import com.ticketing.repository.UserRepository;
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

    public TicketService(TicketRepository ticketRepository, EventRepository eventRepository, UserRepository userRepository, StringRedisTemplate redisTemplate, KafkaTemplate<String, Object> kafkaTemplate, AntiScalperService antiScalperService) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.antiScalperService = antiScalperService;
    }

    private static final long SEAT_LOCK_MINUTES = 10;

    @Transactional(readOnly = true)
    public List<TicketResponse> getUserTickets(Long userId) {
        return ticketRepository.findByUserId(userId).stream()
                .map(ticket -> TicketResponse.builder()
                        .ticketId(ticket.getId())
                        .eventId(ticket.getEvent().getId())
                        .eventTitle(ticket.getEvent().getTitle())
                        .userId(ticket.getUser().getId())
                        .username(ticket.getUser().getUsername())
                        .purchaseDate(ticket.getPurchaseDate())
                        .status("CONFIRMED")
                        .build())
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
                    .message("🚨 Karaborsacı / Bot Algılama Koruması: Şüpheli ve otomatik bilet alma davranışı tespit edildi. İşleminiz engellendi! (Risk Puanı: " + botResult.getRiskScore() + "/100)")
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
                .message("Koltuk 10 dakikalığına başarıyla rezerve edildi. Lütfen ödemeyi tamamlayın. (AI Risk Puanı: " + botResult.getRiskScore() + "/100 - " + botResult.getRiskLevel() + ")")
                .build();
    }

    /**
     * Rezerve Edilen Koltuğu Ödeme Sonrası Kesinleştirme (Confirm Reservation)
     */
    public TicketResponse confirmSeatReservation(SeatConfirmRequest request) {
        String seatLockKey = "seat_lock:event:" + request.getEventId() + ":seat:" + request.getSeatId();
        String lockVal = redisTemplate.opsForValue().get(seatLockKey);

        if (lockVal == null || !lockVal.contains(request.getReservationId())) {
            throw new IllegalStateException("Koltuk rezervasyon süresi doldu (10 dk) veya geçersiz rezervasyon ID!");
        }

        // Genel satın alma akışına yönlendir
        TicketRequest ticketRequest = TicketRequest.builder()
                .eventId(request.getEventId())
                .userId(request.getUserId())
                .build();

        TicketResponse response = purchaseTicket(ticketRequest);

        // Başarılı Kafka kuyruğuna yazım sonrası Redis kilidini serbest bırak
        redisTemplate.delete(seatLockKey);

        return response;
    }

    /**
     * Bilet satın alma talebini karşılayan metot (Redis DECR + Kafka).
     */
    public TicketResponse purchaseTicket(TicketRequest request) {
        String redisKey = "event:" + request.getEventId() + ":capacity";

        // 1. Redis'ten kapasiteyi atomik olarak 1 azaltıyoruz (DECR)
        Long remaining = redisTemplate.opsForValue().decrement(redisKey);
        
        if (remaining == null) {
            throw new IllegalStateException("Redis capacity counter is missing for key: " + redisKey);
        }

        if (remaining < 0) {
            redisTemplate.opsForValue().increment(redisKey);
            throw new IllegalStateException("No available tickets left in cache for event: " + request.getEventId());
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + request.getEventId()));

        TicketBookingEvent bookingEvent = TicketBookingEvent.builder()
                .eventId(event.getId())
                .userId(user.getId())
                .build();

        kafkaTemplate.send("ticket-bookings", String.valueOf(event.getId()), bookingEvent);

        return TicketResponse.builder()
                .ticketId(null)
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .userId(user.getId())
                .username(user.getUsername())
                .purchaseDate(LocalDateTime.now())
                .status("PENDING")
                .build();
    }
}
