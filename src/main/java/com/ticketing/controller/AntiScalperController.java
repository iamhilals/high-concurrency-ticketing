package com.ticketing.controller;

import com.ticketing.dto.BotMetricsResponse;
import com.ticketing.dto.BotThreatLog;
import com.ticketing.service.AntiScalperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/anti-scalper")
public class AntiScalperController {

    private final AntiScalperService antiScalperService;

    public AntiScalperController(AntiScalperService antiScalperService) {
        this.antiScalperService = antiScalperService;
    }

    @GetMapping("/stats")
    public ResponseEntity<BotMetricsResponse> getMetrics() {
        return ResponseEntity.ok(antiScalperService.getMetrics());
    }

    @PostMapping("/simulate-attack")
    public ResponseEntity<List<BotThreatLog>> simulateAttack() {
        List<BotThreatLog> threats = antiScalperService.simulateBotAttack();
        return ResponseEntity.ok(threats);
    }

    @PostMapping("/toggle-defense")
    public ResponseEntity<Map<String, Boolean>> toggleDefense() {
        boolean active = antiScalperService.toggleDefenseMode();
        return ResponseEntity.ok(Map.of("defenseModeActive", active));
    }
}
