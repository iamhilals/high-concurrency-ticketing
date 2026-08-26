package com.ticketing.service;

import com.ticketing.dto.TicketBookingEvent;
import com.ticketing.dto.TicketRequest;
import com.ticketing.dto.TicketResponse;
import com.ticketing.entity.Event;
import com.ticketing.entity.User;
import com.ticketing.repository.EventRepository;
import com.ticketing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate; // Redis işlemleri için
    private final KafkaTemplate<String, Object> kafkaTemplate; // Kafka mesaj şablonu (Producer)

    /**
     * Bilet satın alma talebini karşılayan metot.
     * Bu metot veritabanına doğrudan yazmaz veya kilit tutmaz.
     * Redis ile hızlı kapasite doğrulaması yapar ve talebi asenkron işlenmek üzere Kafka'ya gönderir.
     */
    public TicketResponse purchaseTicket(TicketRequest request) {
        String redisKey = "event:" + request.getEventId() + ":capacity";

        // 1. Redis'ten kapasiteyi atomik olarak 1 azaltıyoruz (DECR) - Locksız Hızlı Kontrol
        Long remaining = redisTemplate.opsForValue().decrement(redisKey);
        
        if (remaining == null) {
            throw new IllegalStateException("Redis capacity counter is missing for key: " + redisKey);
        }

        // Eğer kapasite sıfırın altına düştüyse bilet tükenmiştir
        if (remaining < 0) {
            // Eksiye düşen sayacı eski haline getirmek için 1 arttırıyoruz (Rollback)
            redisTemplate.opsForValue().increment(redisKey);
            throw new IllegalStateException("No available tickets left in cache for event: " + request.getEventId());
        }

        // 2. Redis bariyerini geçen istek için kullanıcı ve etkinlik bilgilerini çekiyoruz (Sadece isim doğrulamak vb. için)
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + request.getEventId()));

        // 3. Asenkron bilet işleme kuyruk mesajını oluşturuyoruz
        TicketBookingEvent bookingEvent = TicketBookingEvent.builder()
                .eventId(event.getId())
                .userId(user.getId())
                .build();

        // 4. Mesajı Kafka kuyruğuna gönderiyoruz
        // eventId mesaj anahtarı (key) olarak kullanılarak, aynı etkinliğe ait tüm biletlerin
        // Kafka'da aynı partition'a gidip sıralı işlenmesi garanti edilir.
        kafkaTemplate.send("ticket-bookings", String.valueOf(event.getId()), bookingEvent);

        // 5. Kullanıcıya talebin alındığını (PENDING) bildiren cevabı anında dönüyoruz
        return TicketResponse.builder()
                .ticketId(null) // Bilet ID'si henüz veritabanına yazılmadığı için null
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .userId(user.getId())
                .username(user.getUsername())
                .purchaseDate(LocalDateTime.now())
                .status("PENDING") // Durum beklemede olarak işaretlendi
                .build();
    }
}
