package com.ticketing.config;

import com.ticketing.entity.Event;
import com.ticketing.entity.User;
import com.ticketing.repository.EventRepository;
import com.ticketing.repository.TicketRepository;
import com.ticketing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import org.springframework.data.redis.core.StringRedisTemplate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final StringRedisTemplate redisTemplate; // Spring Boot'un otomatik yapılandırdığı Redis template

    @Override
    public void run(String... args) throws Exception {
        // 1. Test kullanıcısını kontrol et / oluştur
        if (userRepository.count() == 0) {
            User testUser = User.builder()
                    .username("johndoe")
                    .email("john.doe@example.com")
                    .build();
            userRepository.save(testUser);
            System.out.println("Initialized test user: " + testUser.getUsername() + " (ID: " + testUser.getId() + ")");
        }

        // 2. Eğlence temalı etkinlik dizisini oluştur
        if (eventRepository.count() < 4) {
            // Yabancı anahtar (Foreign Key) hatasını önlemek için önce biletleri temizliyoruz
            ticketRepository.deleteAll();
            eventRepository.deleteAll();

            Event e1 = Event.builder()
                    .title("Rock Festival World Tour 2026")
                    .description("Dünyanın en ünlü rock grupları ve büyüleyici sahne şovları tek sahnede!")
                    .dateTime(LocalDateTime.of(2026, 10, 15, 20, 0))
                    .price(new BigDecimal("450.00"))
                    .availableCapacity(100)
                    .category("Müzik & Konser")
                    .venue("KüçükÇiftlik Park, İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=800&q=80")
                    .build();

            Event e2 = Event.builder()
                    .title("Cyberpunk Neon Electronic Night")
                    .description("Lazer gösterileri, ışık animasyonları ve dünya çapında DJ performansları.")
                    .dateTime(LocalDateTime.of(2026, 11, 20, 22, 30))
                    .price(new BigDecimal("600.00"))
                    .availableCapacity(50)
                    .category("Festival & DJ")
                    .venue("Volkswagen Arena, İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=800&q=80")
                    .build();

            Event e3 = Event.builder()
                    .title("UEFA Champions League Finali 2026")
                    .description("Avrupa futbolunun en büyüğünün belirleneceği dev final mücadelesi!")
                    .dateTime(LocalDateTime.of(2026, 9, 28, 21, 45))
                    .price(new BigDecimal("1500.00"))
                    .availableCapacity(25)
                    .category("Spor")
                    .venue("Atatürk Olimpiyat Stadyumu")
                    .imageUrl("https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=800&q=80")
                    .build();

            Event e4 = Event.builder()
                    .title("Stand-Up Comedy Night: Kahkaha Tufanı")
                    .description("Türkiye'nin en sevilen komedyenlerinin özel canlı performansı.")
                    .dateTime(LocalDateTime.of(2026, 10, 5, 20, 30))
                    .price(new BigDecimal("250.00"))
                    .availableCapacity(80)
                    .category("Komedi & Tiyatro")
                    .venue("Zorlu PSM Drama Sahnesi")
                    .imageUrl("https://images.unsplash.com/photo-1585699324551-f6c309eedeca?auto=format&fit=crop&w=800&q=80")
                    .build();

            eventRepository.save(e1);
            eventRepository.save(e2);
            eventRepository.save(e3);
            eventRepository.save(e4);
            System.out.println("Populated rich entertainment events!");
        }

        // 3. Veritabanındaki tüm etkinliklerin kapasitelerini Redis'e senkronize et
        for (Event event : eventRepository.findAll()) {
            String redisKey = "event:" + event.getId() + ":capacity";
            redisTemplate.opsForValue().set(redisKey, String.valueOf(event.getAvailableCapacity()));
            System.out.println("Synced event capacity to Redis: " + redisKey + " = " + event.getAvailableCapacity());
        }
    }
}
