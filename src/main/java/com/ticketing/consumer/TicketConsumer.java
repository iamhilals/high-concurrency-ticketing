package com.ticketing.consumer;

import com.ticketing.dto.TicketBookingEvent;
import com.ticketing.entity.Event;
import com.ticketing.entity.Ticket;
import com.ticketing.entity.User;
import com.ticketing.repository.EventRepository;
import com.ticketing.repository.TicketRepository;
import com.ticketing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TicketConsumer {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    /**
     * Kafka kuyruğundan bilet taleplerini asenkron olarak dinleyen tüketici metot.
     * topics: Dinlenecek kanal adı ("ticket-bookings")
     * groupId: Tüketici grubu ("ticketing-group")
     */
    @KafkaListener(topics = "ticket-bookings", groupId = "ticketing-group")
    @Transactional // Her bir mesaj işleme adımı kendi veritabanı transaction'ında koşar.
    public void consumeTicketBooking(TicketBookingEvent eventMsg) {
        System.out.println("Kafka'dan bilet talebi alındı. Kullanıcı ID: " + eventMsg.getUserId() + ", Etkinlik ID: " + eventMsg.getEventId());

        try {
            // 1. Veritabanından Kullanıcı ve Etkinlik bilgilerini sorguluyoruz.
            User user = userRepository.findById(eventMsg.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + eventMsg.getUserId()));

            Event event = eventRepository.findById(eventMsg.getEventId())
                    .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventMsg.getEventId()));

            // 2. Veritabanı seviyesinde son bir kapasite kontrolü (Çift güvenlik bariyeri)
            if (event.getAvailableCapacity() <= 0) {
                System.err.println("Kritik Hata: Veritabanında kapasite tükenmiş! Bilet üretilemedi. Etkinlik: " + event.getTitle());
                return;
            }

            // 3. Veritabanındaki kapasiteyi 1 azaltıyoruz (Atomik güncelleme metodumuzu çağırıyoruz).
            eventRepository.decrementCapacity(event.getId());

            // 4. Bilet kaydını veritabanına kalıcı olarak yazıyoruz.
            Ticket ticket = Ticket.builder()
                    .event(event)
                    .user(user)
                    .purchaseDate(java.time.LocalDateTime.now())
                    .build();

            Ticket savedTicket = ticketRepository.save(ticket);
            System.out.println("Bilet başarıyla veritabanına kaydedildi! Bilet ID: " + savedTicket.getId());

        } catch (Exception e) {
            System.err.println("Bilet işlenirken hata oluştu! Hata: " + e.getMessage());
            // Not: Spring Kafka varsayılan olarak hata durumunda mesajı tekrar denemeye alabilir.
            // Gerçek dünyada burada hata alan mesajlar bir DLQ (Dead Letter Queue - Ölü Mektup Kuyruğu) kanalına gönderilir.
        }
    }
}
