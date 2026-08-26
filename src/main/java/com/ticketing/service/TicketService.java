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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RedissonClient redissonClient; // Redisson istemcisi (Dağıtık kilitler için)
    private final TransactionTemplate transactionTemplate; // Programatik transaction yönetimi için

    public TicketResponse purchaseTicket(TicketRequest request) {
        String lockKey = "lock:event:" + request.getEventId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 1. Dağıtık Kilidi almayı deniyoruz.
            // waitTime: Kilidi almak için en fazla 5 saniye bekler.
            // leaseTime: Kilit alındıktan sonra işlem bitmese bile kilit 10 saniye sonra serbest kalır (aşırı yükte kilit kilitlenmesini önler).
            boolean isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new IllegalStateException("System is busy, could not acquire lock for event: " + request.getEventId());
            }

            // 2. Kilit alındıktan SONRA veritabanı transaction'ını başlatıyoruz.
            // DİKKAT: "Lock -> Transaction -> Commit -> Unlock" sırası hayati önem taşır.
            // Eğer transaction'ı kilidin dışına çıkarırsak, kilit açıldığında henüz veritabanına commit gitmemiş olabilir
            // ve sıradaki thread eski veriyi okuyarak çakışmaya (overselling) sebep olur.
            return transactionTemplate.execute(status -> {
                // A. Kullanıcıyı ve Etkinliği çekiyoruz (Kilit altında olduğumuz için locksız select yeterlidir)
                User user = userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));

                Event event = eventRepository.findById(request.getEventId())
                        .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + request.getEventId()));

                // B. Kapasite doğrulaması
                if (event.getAvailableCapacity() <= 0) {
                    throw new IllegalStateException("No available tickets left for event: " + event.getTitle());
                }

                // C. Kapasiteyi düşürme (JPA üzerinden normal güncelleme)
                event.setAvailableCapacity(event.getAvailableCapacity() - 1);
                eventRepository.save(event);

                // D. Bilet kaydı oluşturma
                Ticket ticket = Ticket.builder()
                        .event(event)
                        .user(user)
                        .purchaseDate(LocalDateTime.now())
                        .build();
                Ticket savedTicket = ticketRepository.save(ticket);

                // E. Response DTO
                return TicketResponse.builder()
                        .ticketId(savedTicket.getId())
                        .eventId(event.getId())
                        .eventTitle(event.getTitle())
                        .userId(user.getId())
                        .username(user.getUsername())
                        .purchaseDate(savedTicket.getPurchaseDate())
                        .build();
            }); // TransactionTemplate bloğundan çıkarken otomatik COMMIT olur.

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Transaction interrupted: " + e.getMessage());
        } finally {
            // 3. Transaction COMMIT olduktan veya hata oluşup sonlandıktan SONRA kilidi açıyoruz.
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
