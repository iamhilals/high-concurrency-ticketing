package com.ticketing.service;

import com.ticketing.dto.BotDetectionResult;
import com.ticketing.dto.BotMetricsResponse;
import com.ticketing.dto.BotThreatLog;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AntiScalperService {

    private final StringRedisTemplate redisTemplate;

    // Track request timestamps per key (IP or UserId)
    private final Map<String, List<Long>> requestHistory = new ConcurrentHashMap<>();
    private final Map<String, Set<Long>> userEventHistory = new ConcurrentHashMap<>();

    // Metrics counters
    private final AtomicLong totalAnalyzedRequests = new AtomicLong(0);
    private final AtomicLong blockedBotsCount = new AtomicLong(0);
    private final AtomicLong suspiciousFlagCount = new AtomicLong(0);

    private final List<BotThreatLog> recentThreats = new CopyOnWriteArrayList<>();
    private volatile boolean defenseModeActive = true; // Strict defense enabled by default

    public AntiScalperService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        // Pre-populate with sample realistic threat history for immediate rich UI visualization
        initSampleThreatLogs();
    }

    private void initSampleThreatLogs() {
        recentThreats.add(BotThreatLog.builder()
                .threatId("TRT-" + UUID.randomUUID().toString().substring(0, 6))
                .userId(9991L)
                .ipAddress("185.220.101.5")
                .eventId(101L)
                .seatId("B-14")
                .riskScore(92)
                .riskLevel("KARABORSACI_BOT")
                .riskFactors(Arrays.asList("İnsansı olmayan ultra-hızlı istek (42 ms aralık)", "Son 10 saniyede 12 bilet denemesi", "Otomatik headless tarayıcı imzası"))
                .actionTaken("BLOCKED")
                .timestamp(LocalDateTime.now().minusMinutes(3))
                .build());

        recentThreats.add(BotThreatLog.builder()
                .threatId("TRT-" + UUID.randomUUID().toString().substring(0, 6))
                .userId(9992L)
                .ipAddress("45.142.120.18")
                .eventId(102L)
                .seatId("A-01")
                .riskScore(84)
                .riskLevel("KARABORSACI_BOT")
                .riskFactors(Arrays.asList("Çapraz etkinlik toplu koltuk kapatma (3 farklı etkinlik)", "Hızlı IP döngüsü (Proxy)"))
                .actionTaken("BLOCKED")
                .timestamp(LocalDateTime.now().minusMinutes(12))
                .build());

        recentThreats.add(BotThreatLog.builder()
                .threatId("TRT-" + UUID.randomUUID().toString().substring(0, 6))
                .userId(1042L)
                .ipAddress("88.230.45.12")
                .eventId(101L)
                .seatId("VIP-04")
                .riskScore(58)
                .riskLevel("ŞÜPHELİ")
                .riskFactors(Arrays.asList("Yüksek tıklama frekansı (Son 10sn'de 5 deneme)"))
                .actionTaken("WARNED")
                .timestamp(LocalDateTime.now().minusMinutes(25))
                .build());

        totalAnalyzedRequests.set(1420);
        blockedBotsCount.set(38);
        suspiciousFlagCount.set(112);
    }

    /**
     * AI Risk Engine: Analyzes seat reservation or ticket purchase attempt.
     */
    public BotDetectionResult analyzeReservationAttempt(Long userId, String ipAddress, Long eventId, String seatId) {
        totalAnalyzedRequests.incrementAndGet();
        long nowMs = System.currentTimeMillis();

        String trackerKey = (userId != null && userId > 0) ? "USER:" + userId : "IP:" + ipAddress;
        List<String> riskFactors = new ArrayList<>();
        int riskScore = 0;

        // 1. Time interval between last request (Millisecond velocity check)
        List<Long> history = requestHistory.computeIfAbsent(trackerKey, k -> new ArrayList<>());
        synchronized (history) {
            if (!history.isEmpty()) {
                long lastReqTime = history.get(history.size() - 1);
                long delta = nowMs - lastReqTime;

                if (delta < 150) { // Sub-150ms interval is physically impossible for a human clicking manually
                    riskScore += 45;
                    riskFactors.add("İnsansı olmayan ultra-hızlı istek tespiti (" + delta + " ms aralık)");
                } else if (delta < 400) {
                    riskScore += 25;
                    riskFactors.add("Yüksek hızlı seri tıklama hızı (" + delta + " ms)");
                }
            }

            // Clean old entries (> 60 seconds)
            history.removeIf(t -> (nowMs - t) > 60000);
            history.add(nowMs);

            // 2. Frequency check (rolling 10-second window)
            long recentCount10s = history.stream().filter(t -> (nowMs - t) <= 10000).count();
            if (recentCount10s >= 8) {
                riskScore += 40;
                riskFactors.add("Son 10 saniyede aşırı bilet denemesi (" + recentCount10s + " istek/10sn)");
            } else if (recentCount10s >= 4) {
                riskScore += 20;
                riskFactors.add("Hızlı rezervasyon frekansı (" + recentCount10s + " istek/10sn)");
            }
        }

        // 3. Cross-Event Multi-Target Booking Pattern (Carpet Buying Bot)
        if (eventId != null) {
            Set<Long> events = userEventHistory.computeIfAbsent(trackerKey, k -> Collections.synchronizedSet(new HashSet<>()));
            events.add(eventId);
            if (events.size() >= 3) {
                riskScore += 30;
                riskFactors.add("Çapraz etkinlik toplu koltuk kapatma deseni (" + events.size() + " farklı etkinlik)");
            }
        }

        // Strict Mode bonus risk calculation
        if (defenseModeActive && riskScore > 20) {
            riskScore = Math.min(100, riskScore + 10);
        }

        // Determine Risk Level & Action
        String riskLevel;
        boolean blocked;
        String actionTaken;
        String recommendation;

        if (riskScore >= 75) {
            riskLevel = "KARABORSACI_BOT";
            blocked = true;
            actionTaken = "BLOCKED";
            recommendation = "🚨 Karaborsacı / Otomatik Bot Girişimi Tespiti! Koltuk Rezervasyonu Engellendi.";
            blockedBotsCount.incrementAndGet();
        } else if (riskScore >= 45) {
            riskLevel = "ŞÜPHELİ";
            blocked = false; // Flagged for monitoring / captcha check
            actionTaken = "WARNED";
            recommendation = "⚠️ Şüpheli Kullanıcı Davranışı Tespiti. CAPTCHA doğrulaması istenebilir.";
            suspiciousFlagCount.incrementAndGet();
        } else {
            riskLevel = "TEMİZ";
            blocked = false;
            actionTaken = "CLEARED";
            recommendation = "✅ Güvenli Kullanıcı Davranışı.";
        }

        // Log suspicious or blocked threats
        if (riskScore >= 45) {
            BotThreatLog threatLog = BotThreatLog.builder()
                    .threatId("TRT-" + UUID.randomUUID().toString().substring(0, 6))
                    .userId(userId)
                    .ipAddress(ipAddress)
                    .eventId(eventId)
                    .seatId(seatId)
                    .riskScore(riskScore)
                    .riskLevel(riskLevel)
                    .riskFactors(riskFactors)
                    .actionTaken(actionTaken)
                    .timestamp(LocalDateTime.now())
                    .build();

            recentThreats.add(0, threatLog);
            if (recentThreats.size() > 50) {
                recentThreats.remove(recentThreats.size() - 1);
            }
        }

        return BotDetectionResult.builder()
                .riskScore(riskScore)
                .riskLevel(riskLevel)
                .blocked(blocked)
                .recommendation(recommendation)
                .riskFactors(riskFactors)
                .build();
    }

    /**
     * Helper to simulate a high-speed bot attack for demo / testing.
     */
    public List<BotThreatLog> simulateBotAttack() {
        List<BotThreatLog> simulated = new ArrayList<>();
        String botIp = "194.168.4." + (new Random().nextInt(200) + 10);
        Long botUserId = 8800L + new Random().nextInt(100);

        // Send 5 lightning fast request simulations
        for (int i = 0; i < 5; i++) {
            BotDetectionResult res = analyzeReservationAttempt(botUserId, botIp, 101L, "BOT-SEAT-" + (i + 1));
            if (!recentThreats.isEmpty()) {
                simulated.add(recentThreats.get(0));
            }
        }
        return simulated;
    }

    public BotMetricsResponse getMetrics() {
        long total = totalAnalyzedRequests.get();
        double avgRisk = recentThreats.isEmpty() ? 12.5 :
                recentThreats.stream().mapToInt(BotThreatLog::getRiskScore).average().orElse(15.0);

        return BotMetricsResponse.builder()
                .totalAnalyzedRequests(total)
                .blockedBotsCount(blockedBotsCount.get())
                .suspiciousFlagCount(suspiciousFlagCount.get())
                .averageRiskScore(Math.round(avgRisk * 10.0) / 10.0)
                .defenseModeActive(defenseModeActive)
                .recentThreats(new ArrayList<>(recentThreats))
                .build();
    }

    public boolean toggleDefenseMode() {
        this.defenseModeActive = !this.defenseModeActive;
        return this.defenseModeActive;
    }
}
