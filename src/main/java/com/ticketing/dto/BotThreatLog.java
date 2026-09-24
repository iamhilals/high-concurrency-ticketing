package com.ticketing.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BotThreatLog {
    private String threatId;
    private Long userId;
    private String ipAddress;
    private Long eventId;
    private String seatId;
    private int riskScore;
    private String riskLevel;
    private List<String> riskFactors;
    private String actionTaken;
    private LocalDateTime timestamp;

    public BotThreatLog() {}

    public BotThreatLog(String threatId, Long userId, String ipAddress, Long eventId, String seatId, int riskScore, String riskLevel, List<String> riskFactors, String actionTaken, LocalDateTime timestamp) {
        this.threatId = threatId;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.eventId = eventId;
        this.seatId = seatId;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.riskFactors = riskFactors;
        this.actionTaken = actionTaken;
        this.timestamp = timestamp;
    }

    public static BotThreatLogBuilder builder() {
        return new BotThreatLogBuilder();
    }

    public static class BotThreatLogBuilder {
        private String threatId;
        private Long userId;
        private String ipAddress;
        private Long eventId;
        private String seatId;
        private int riskScore;
        private String riskLevel;
        private List<String> riskFactors;
        private String actionTaken;
        private LocalDateTime timestamp;

        public BotThreatLogBuilder threatId(String threatId) { this.threatId = threatId; return this; }
        public BotThreatLogBuilder userId(Long userId) { this.userId = userId; return this; }
        public BotThreatLogBuilder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public BotThreatLogBuilder eventId(Long eventId) { this.eventId = eventId; return this; }
        public BotThreatLogBuilder seatId(String seatId) { this.seatId = seatId; return this; }
        public BotThreatLogBuilder riskScore(int riskScore) { this.riskScore = riskScore; return this; }
        public BotThreatLogBuilder riskLevel(String riskLevel) { this.riskLevel = riskLevel; return this; }
        public BotThreatLogBuilder riskFactors(List<String> riskFactors) { this.riskFactors = riskFactors; return this; }
        public BotThreatLogBuilder actionTaken(String actionTaken) { this.actionTaken = actionTaken; return this; }
        public BotThreatLogBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

        public BotThreatLog build() {
            return new BotThreatLog(threatId, userId, ipAddress, eventId, seatId, riskScore, riskLevel, riskFactors, actionTaken, timestamp);
        }
    }

    public String getThreatId() { return threatId; }
    public void setThreatId(String threatId) { this.threatId = threatId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public String getSeatId() { return seatId; }
    public void setSeatId(String seatId) { this.seatId = seatId; }

    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public List<String> getRiskFactors() { return riskFactors; }
    public void setRiskFactors(List<String> riskFactors) { this.riskFactors = riskFactors; }

    public String getActionTaken() { return actionTaken; }
    public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
