package com.ticketing;

import com.ticketing.dto.BotDetectionResult;
import com.ticketing.dto.SeatReserveRequest;
import com.ticketing.dto.SeatReserveResponse;
import com.ticketing.dto.TicketRequest;
import com.ticketing.dto.TicketResponse;
import com.ticketing.entity.Event;
import com.ticketing.entity.User;
import com.ticketing.repository.EventRepository;
import com.ticketing.repository.TicketRepository;
import com.ticketing.repository.UserRepository;
import com.ticketing.service.AntiScalperService;
import com.ticketing.service.AsyncTicketProcessor;
import com.ticketing.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 500 Eşzamanlı Kullanıcı (High-Concurrency) Stres & Yarış Durumu Testleri.
 * Redis'in atomik SETNX ve DECR davranışını birebir simüle ederek çifte koltuk kilidi
 * ve fazla bilet satışı (overselling) olmadığını kanıtlar.
 */
class TicketConcurrencyTest {

    private TicketRepository ticketRepository;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private KafkaTemplate<String, Object> kafkaTemplate;
    private AsyncTicketProcessor asyncTicketProcessor;
    private AntiScalperService antiScalperService;
    private TicketService ticketService;

    private ConcurrentHashMap<String, String> simulatedRedisLocks;
    private ConcurrentHashMap<String, AtomicInteger> simulatedRedisCounters;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        ticketRepository = mock(TicketRepository.class);
        eventRepository = mock(EventRepository.class);
        userRepository = mock(UserRepository.class);
        redisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        kafkaTemplate = mock(KafkaTemplate.class);
        asyncTicketProcessor = mock(AsyncTicketProcessor.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        simulatedRedisLocks = new ConcurrentHashMap<>();
        simulatedRedisCounters = new ConcurrentHashMap<>();

        // Redis SETNX (setIfAbsent) atomik simülasyonu
        when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenAnswer(inv -> {
                    String key = inv.getArgument(0);
                    String val = inv.getArgument(1);
                    return simulatedRedisLocks.putIfAbsent(key, val) == null;
                });

        // Redis GET simülasyonu
        when(valueOperations.get(anyString()))
                .thenAnswer(inv -> {
                    String key = inv.getArgument(0);
                    if (simulatedRedisCounters.containsKey(key)) {
                        return String.valueOf(simulatedRedisCounters.get(key).get());
                    }
                    return simulatedRedisLocks.get(key);
                });

        // Redis DECR atomik simülasyonu
        when(valueOperations.decrement(anyString()))
                .thenAnswer(inv -> {
                    String key = inv.getArgument(0);
                    AtomicInteger counter = simulatedRedisCounters.get(key);
                    return counter == null ? null : (long) counter.decrementAndGet();
                });

        // Redis INCR atomik simülasyonu
        when(valueOperations.increment(anyString()))
                .thenAnswer(inv -> {
                    String key = inv.getArgument(0);
                    AtomicInteger counter = simulatedRedisCounters.get(key);
                    return counter == null ? null : (long) counter.incrementAndGet();
                });

        antiScalperService = new AntiScalperService(redisTemplate);
        ticketService = new TicketService(
                ticketRepository,
                eventRepository,
                userRepository,
                redisTemplate,
                kafkaTemplate,
                antiScalperService,
                asyncTicketProcessor,
                true
        );
    }

    @Test
    @DisplayName("Senaryo 1: 500 farklı kullanıcı aynı koltuğa aynı milisaniyede tıklarsa SADECE 1 kişi kilitleyebilir")
    void test500ConcurrentUsersRacingForSingleSeat_OnlyOneWins() throws InterruptedException {
        int totalUsers = 500;
        long eventId = 1L;
        String targetSeat = "001-A1";
        simulatedRedisCounters.put("event:" + eventId + ":capacity", new AtomicInteger(100));

        ExecutorService pool = Executors.newFixedThreadPool(64);
        CountDownLatch readyLatch = new CountDownLatch(totalUsers);
        CountDownLatch startGun = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(totalUsers);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        for (int i = 1; i <= totalUsers; i++) {
            final long uid = 1000L + i;
            pool.submit(() -> {
                readyLatch.countDown();
                try {
                    startGun.await();
                    SeatReserveRequest req = SeatReserveRequest.builder()
                            .eventId(eventId)
                            .userId(uid)
                            .seatId(targetSeat)
                            .zoneName("Sahne Önü VIP")
                            .price(810.0)
                            .build();
                    SeatReserveResponse resp = ticketService.reserveSeat(req);
                    if (resp.isSuccess()) {
                        successCount.incrementAndGet();
                    } else {
                        conflictCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await(5, TimeUnit.SECONDS);
        long t0 = System.currentTimeMillis();
        startGun.countDown(); // 500 thread aynı anda başlar!
        assertTrue(doneLatch.await(15, TimeUnit.SECONDS), "500 thread zamanında tamamlanmalı");
        long elapsedMs = System.currentTimeMillis() - t0;
        pool.shutdown();

        System.out.printf("🏆 [TEST 1 - 500 Kişi / 1 Koltuk] Başarılı: %d | Reddedilen: %d | Süre: %d ms%n",
                successCount.get(), conflictCount.get(), elapsedMs);

        assertEquals(1, successCount.get(), "Aynı koltuğu tam olarak SADECE 1 kişi kilitleyebilmelidir!");
        assertEquals(totalUsers - 1, conflictCount.get(), "Kalan 499 kişi reddedilmelidir!");
    }

    @Test
    @DisplayName("Senaryo 2: 500 kullanıcı 50 biletlik Flash-Sale etkinliğine saldırırsa TAM 50 bilet satılır (0 Overselling)")
    void test500ConcurrentUsersFlashSaleFor50Tickets_ZeroOverselling() throws InterruptedException {
        int totalUsers = 500;
        int initialCapacity = 50;
        long eventId = 24L;

        simulatedRedisCounters.put("event:" + eventId + ":capacity", new AtomicInteger(initialCapacity));

        Event event = Event.builder()
                .id(eventId)
                .title("Turkish Grand Prix Formula 1")
                .availableCapacity(initialCapacity)
                .price(new BigDecimal("2200.00"))
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.findById(anyLong())).thenAnswer(inv -> {
            Long uid = inv.getArgument(0);
            return Optional.of(User.builder().id(uid).username("user_" + uid).email("u" + uid + "@test.com").build());
        });

        ExecutorService pool = Executors.newFixedThreadPool(64);
        CountDownLatch readyLatch = new CountDownLatch(totalUsers);
        CountDownLatch startGun = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(totalUsers);

        AtomicInteger soldCount = new AtomicInteger(0);
        AtomicInteger soldOutRejectCount = new AtomicInteger(0);

        for (int i = 1; i <= totalUsers; i++) {
            final long uid = 2000L + i;
            pool.submit(() -> {
                readyLatch.countDown();
                try {
                    startGun.await();
                    TicketRequest req = TicketRequest.builder()
                            .eventId(eventId)
                            .userId(uid)
                            .build();
                    TicketResponse resp = ticketService.purchaseTicket(req);
                    if ("PENDING".equals(resp.getStatus())) {
                        soldCount.incrementAndGet();
                    }
                } catch (IllegalStateException ex) {
                    soldOutRejectCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await(5, TimeUnit.SECONDS);
        long t0 = System.currentTimeMillis();
        startGun.countDown();
        assertTrue(doneLatch.await(15, TimeUnit.SECONDS), "500 flash-sale thread zamanında tamamlanmalı");
        long elapsedMs = System.currentTimeMillis() - t0;
        pool.shutdown();

        int remainingStock = simulatedRedisCounters.get("event:" + eventId + ":capacity").get();

        System.out.printf("🔥 [TEST 2 - 500 Kişi / 50 Bilet Flash-Sale] Satılan: %d | Tükendi Reddi: %d | Kalan Stok: %d | Süre: %d ms%n",
                soldCount.get(), soldOutRejectCount.get(), remainingStock, elapsedMs);

        assertEquals(initialCapacity, soldCount.get(), "Tam olarak 50 bilet satılmalı, 1 fazla bile satılmamalı!");
        assertEquals(totalUsers - initialCapacity, soldOutRejectCount.get(), "450 kullanıcı tükendi hatası almalı!");
        assertEquals(0, remainingStock, "Redis stok sayacı sıfırda sabitlenmeli, eksiye düşmemeli!");
        verify(kafkaTemplate, times(initialCapacity)).send(eq("ticket-bookings"), eq(String.valueOf(eventId)), any());
    }

    @Test
    @DisplayName("Senaryo 3: Milisaniyelik seri bot saldırısı AntiScalperService tarafından anında engellenir")
    void testScalperBotFloodIsBlocked() {
        long botUserId = 88888L;
        int blockedCount = 0;

        for (int i = 1; i <= 12; i++) {
            BotDetectionResult result = antiScalperService.analyzeReservationAttempt(
                    botUserId,
                    "185.220.101.99",
                    (long) ((i % 4) + 1),
                    "001-A" + i
            );
            if (result.isBlocked()) {
                blockedCount++;
            }
        }

        System.out.printf("🛡️ [TEST 3 - Bot Saldırısı] 12 seri istekten engellenen: %d%n", blockedCount);
        assertTrue(blockedCount > 0, "Seri istek atan karaborsacı bot Anti-Scalper tarafından engellenmelidir!");
    }
}
