package com.ticketing.service;

import com.ticketing.dto.TicketRequest;
import com.ticketing.dto.TicketResponse;
import com.ticketing.entity.Event;
import com.ticketing.entity.Ticket;
import com.ticketing.entity.User;
import com.ticketing.repository.EventRepository;
import com.ticketing.repository.TicketRepository;
import com.ticketing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final org.springframework.data.redis.core.StringRedisTemplate redisTemplate; // Redis işlemlerini yöneteceğimiz template

    @Transactional
    public TicketResponse purchaseTicket(TicketRequest request) {
        String redisKey = "event:" + request.getEventId() + ":capacity";

        // 1. Redis'ten kapasiteyi atomik olarak 1 azaltıyoruz (DECR).
        // Redis tek iş parçacıklı (single-threaded) çalıştığı için bu işlem tamamen güvenlidir.
        Long remaining = redisTemplate.opsForValue().decrement(redisKey);
        
        if (remaining == null) {
            throw new IllegalStateException("Redis capacity counter is missing for key: " + redisKey);
        }

        // Eğer kapasite sıfırın altına düştüyse bilet tükenmiş demektir.
        if (remaining < 0) {
            // Eksiye düşen sayacı eski haline getirmek için 1 arttırıyoruz (Rollback Redis counter).
            redisTemplate.opsForValue().increment(redisKey);
            throw new IllegalStateException("No available tickets left in cache for event: " + request.getEventId());
        }

        // 2. Redis bariyerini geçen istekler için DB işlemleri yapılıyor (Veritabanında kilit kullanılmıyor).
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + request.getEventId()));

        // 3. Veritabanındaki kapasiteyi atomik UPDATE ile azaltıyoruz.
        // SQL: UPDATE events SET available_capacity = available_capacity - 1 WHERE id = ?
        eventRepository.decrementCapacity(event.getId());

        // 4. Bilet kaydı oluşturuluyor.
        Ticket ticket = Ticket.builder()
                .event(event)
                .user(user)
                .purchaseDate(LocalDateTime.now())
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);

        // 5. İstemciye (frontend) cevap DTO'su dönülüyor.
        return TicketResponse.builder()
                .ticketId(savedTicket.getId())
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .userId(user.getId())
                .username(user.getUsername())
                .purchaseDate(savedTicket.getPurchaseDate())
                .build();
    }
}
