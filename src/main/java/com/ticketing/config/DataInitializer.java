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
                    .password("password123")
                    .build();
            userRepository.save(testUser);
            System.out.println("Initialized test user: " + testUser.getUsername() + " (ID: " + testUser.getId() + ")");
        }

        // 2. Bubilet tarzı gerçekçi etkinlik dizisini oluştur
        if (eventRepository.count() < 5) {
            // Yabancı anahtar hatasını önlemek için önce biletleri temizliyoruz
            ticketRepository.deleteAll();
            eventRepository.deleteAll();

            Event e1 = Event.builder()
                    .title("Duman & Manga - Rock Festivali 2026")
                    .description("Türk rock müziğinin dev isimleri Duman ve Manga unutulmaz bir açık hava konseri için sahnede!")
                    .dateTime(LocalDateTime.of(2026, 10, 15, 21, 0))
                    .price(new BigDecimal("450.00"))
                    .availableCapacity(100)
                    .category("Müzik & Konser")
                    .venue("KüçükÇiftlik Park, Harbiye / İstanbul")
                    .imageUrl("https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=800&q=80")
                    .build();

            Event e2 = Event.builder()
                    .title("Fazıl Say - Klasik Müzik & Piyano Gecesi")
                    .description("Dünyaca ünlü piyanist ve besteci Fazıl Say'ın canlı piyano performansı ve senfoni ziyafeti.")
                    .dateTime(LocalDateTime.of(2026, 11, 12, 20, 30))
                    .price(new BigDecimal("750.00"))
                    .availableCapacity(60)
                    .category("Müzik & Konser")
                    .venue("Zorlu PSM Turkcell Sahnesi, Beşiktaş")
                    .imageUrl("https://images.unsplash.com/photo-1520523839897-bd0b52f945a0?auto=format&fit=crop&w=800&q=80")
                    .build();

            Event e3 = Event.builder()
                    .title("Cem Yılmaz - CMXXIV Stand-Up")
                    .description("Cem Yılmaz'ın kapalı gişe oynayan yepyeni stand-up gösterisi CMXXIV canlı sahnede!")
                    .dateTime(LocalDateTime.of(2026, 10, 24, 21, 0))
                    .price(new BigDecimal("850.00"))
                    .availableCapacity(40)
                    .category("Komedi & Tiyatro")
                    .venue("Bostancı Gösteri Merkezi, Kadıköy")
                    .imageUrl("https://images.unsplash.com/photo-1585699324551-f6c309eedeca?auto=format&fit=crop&w=800&q=80")
                    .build();

            Event e4 = Event.builder()
                    .title("Armin van Buuren - Electronic Neon Night")
                    .description("Dünyanın 1 numaralı DJ'lerinden Armin van Buuren ile lazer ve görsel şovlarla dolu muazzam bir gece.")
                    .dateTime(LocalDateTime.of(2026, 11, 20, 22, 30))
                    .price(new BigDecimal("650.00"))
                    .availableCapacity(50)
                    .category("Festival & DJ")
                    .venue("Volkswagen Arena, Maslak")
                    .imageUrl("https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?auto=format&fit=crop&w=800&q=80")
                    .build();

            Event e5 = Event.builder()
                    .title("Galatasaray - Fenerbahçe Süper Derbi")
                    .description("Trendyol Süper Lig şampiyonluk yolundaki dev kitle derbisi!")
                    .dateTime(LocalDateTime.of(2026, 9, 28, 19, 0))
                    .price(new BigDecimal("1250.00"))
                    .availableCapacity(30)
                    .category("Spor")
                    .venue("RAMS Park Stadyumu, Seyrantepe")
                    .imageUrl("https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=800&q=80")
                    .build();

            eventRepository.save(e1);
            eventRepository.save(e2);
            eventRepository.save(e3);
            eventRepository.save(e4);
            eventRepository.save(e5);
            System.out.println("Populated authentic Bubilet-style events!");
        }

        // 3. Veritabanındaki tüm etkinliklerin kapasitelerini Redis'e senkronize et
        for (Event event : eventRepository.findAll()) {
            String redisKey = "event:" + event.getId() + ":capacity";
            redisTemplate.opsForValue().set(redisKey, String.valueOf(event.getAvailableCapacity()));
            System.out.println("Synced event capacity to Redis: " + redisKey + " = " + event.getAvailableCapacity());
        }
    }
}
