package com.ticketing.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Render.com health check ve UptimeRobot 7/24 uyanık tutma (keep-alive) ucu.
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Value("${app.kafka.enabled:true}")
    private boolean kafkaEnabled;

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "UP");
        body.put("service", "PassoLive High-Concurrency Ticketing");
        body.put("queueMode", kafkaEnabled ? "APACHE_KAFKA" : "SPRING_ASYNC_QUEUE");
        body.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(body);
    }
}
