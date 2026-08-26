package com.ticketing.config;

import com.ticketing.entity.Event;
import com.ticketing.entity.User;
import com.ticketing.repository.EventRepository;
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
    private final StringRedisTemplate redisTemplate; // Spring Boot'un otomatik yapılandırdığı Redis template

    @Override
    public void run(String... args) throws Exception {
        // 1. Initialize a test user if none exists
        if (userRepository.count() == 0) {
            User testUser = User.builder()
                    .username("johndoe")
                    .email("john.doe@example.com")
                    .build();
            userRepository.save(testUser);
            System.out.println("Initialized test user: " + testUser.getUsername() + " (ID: " + testUser.getId() + ")");
        }

        // 2. Initialize a test event if none exists
        if (eventRepository.count() == 0) {
            Event testEvent = Event.builder()
                    .title("Rock Concert 2026")
                    .description("Live performance of legendary rock bands.")
                    .dateTime(LocalDateTime.of(2026, 10, 15, 19, 30))
                    .price(new BigDecimal("120.00"))
                    .availableCapacity(100)
                    .build();
            eventRepository.save(testEvent);
            System.out.println("Initialized test event: " + testEvent.getTitle() + " (Capacity: 100)");
        }

        // 3. Veritabanındaki tüm etkinliklerin kapasitelerini Redis'e senkronize et
        // Uygulama her başladığında Redis'teki sayaçları güncel durumla eşitler
        for (Event event : eventRepository.findAll()) {
            String redisKey = "event:" + event.getId() + ":capacity";
            redisTemplate.opsForValue().set(redisKey, String.valueOf(event.getAvailableCapacity()));
            System.out.println("Synced event capacity to Redis: " + redisKey + " = " + event.getAvailableCapacity());
        }
    }
}
