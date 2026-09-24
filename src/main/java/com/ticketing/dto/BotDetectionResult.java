package com.ticketing.dto;

import java.util.List;

public class BotDetectionResult {
    private int riskScore;
    private String riskLevel;
    private boolean blocked;
    private String recommendation;
    private List<String> riskFactors;

    public BotDetectionResult() {}

    public BotDetectionResult(int riskScore, String riskLevel, boolean blocked, String recommendation, List<String> riskFactors) {
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.blocked = blocked;
        this.recommendation = recommendation;
        this.riskFactors = riskFactors;
    }

    public static BotDetectionResultBuilder builder() {
        return new BotDetectionResultBuilder();
    }

    public static class BotDetectionResultBuilder {
        private int riskScore;
        private String riskLevel;
        private boolean blocked;
        private String recommendation;
        private List<String> riskFactors;

        public BotDetectionResultBuilder riskScore(int riskScore) { this.riskScore = riskScore; return this; }
        public BotDetectionResultBuilder riskLevel(String riskLevel) { this.riskLevel = riskLevel; return this; }
        public BotDetectionResultBuilder blocked(boolean blocked) { this.blocked = blocked; return this; }
        public BotDetectionResultBuilder recommendation(String recommendation) { this.recommendation = recommendation; return this; }
        public BotDetectionResultBuilder riskFactors(List<String> riskFactors) { this.riskFactors = riskFactors; return this; }

        public BotDetectionResult build() {
            return new BotDetectionResult(riskScore, riskLevel, blocked, recommendation, riskFactors);
        }
    }

    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public List<String> getRiskFactors() { return riskFactors; }
    public void setRiskFactors(List<String> riskFactors) { this.riskFactors = riskFactors; }
}
