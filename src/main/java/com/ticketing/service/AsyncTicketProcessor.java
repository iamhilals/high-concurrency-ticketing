package com.ticketing.service;

import com.ticketing.dto.TicketBookingEvent;
import com.ticketing.entity.Event;
import com.ticketing.entity.Ticket;
import com.ticketing.entity.User;
import com.ticketing.repository.EventRepository;
import com.ticketing.repository.TicketRepository;
import com.ticketing.repository.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Ücretsiz bulut ortamında (KAFKA_ENABLED=false) Apache Kafka yerine
 * Spring @Async arka plan kuyruğu üzerinden biletleri veritabanına işler.
 */
@Service
public class AsyncTicketProcessor {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public AsyncTicketProcessor(TicketRepository ticketRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Async
    @Transactional
    public void processBookingAsync(TicketBookingEvent eventMsg) {
        try {
            User user = userRepository.findById(eventMsg.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + eventMsg.getUserId()));

            Event event = eventRepository.findById(eventMsg.getEventId())
                    .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventMsg.getEventId()));

            if (event.getAvailableCapacity() <= 0) {
                System.err.println("[AsyncQueue] Kapasite tükenmiş! Etkinlik: " + event.getTitle());
                return;
            }

            eventRepository.decrementCapacity(event.getId());

            Ticket ticket = Ticket.builder()
                    .event(event)
                    .user(user)
                    .purchaseDate(LocalDateTime.now())
                    .pnrCode(eventMsg.getPnrCode())
                    .seatInfo(eventMsg.getSeatInfo())
                    .zoneName(eventMsg.getZoneName())
                    .blockCode(eventMsg.getBlockCode())
                    .paidPrice(eventMsg.getPaidPrice() != null ? eventMsg.getPaidPrice() : (event.getPrice() != null ? event.getPrice().doubleValue() : null))
                    .build();

            Ticket saved = ticketRepository.save(ticket);
            System.out.println("[AsyncQueue] Bilet veritabanına kaydedildi! Bilet ID: " + saved.getId());
        } catch (Exception e) {
            System.err.println("[AsyncQueue] Bilet işlenirken hata oluştu: " + e.getMessage());
        }
    }
}
