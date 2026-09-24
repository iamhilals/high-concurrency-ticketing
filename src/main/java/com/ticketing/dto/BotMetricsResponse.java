package com.ticketing.dto;

import java.util.List;

public class BotMetricsResponse {
    private long totalAnalyzedRequests;
    private long blockedBotsCount;
    private long suspiciousFlagCount;
    private double averageRiskScore;
    private boolean defenseModeActive;
    private List<BotThreatLog> recentThreats;

    public BotMetricsResponse() {}

    public BotMetricsResponse(long totalAnalyzedRequests, long blockedBotsCount, long suspiciousFlagCount, double averageRiskScore, boolean defenseModeActive, List<BotThreatLog> recentThreats) {
        this.totalAnalyzedRequests = totalAnalyzedRequests;
        this.blockedBotsCount = blockedBotsCount;
        this.suspiciousFlagCount = suspiciousFlagCount;
        this.averageRiskScore = averageRiskScore;
        this.defenseModeActive = defenseModeActive;
        this.recentThreats = recentThreats;
    }

    public static BotMetricsResponseBuilder builder() {
        return new BotMetricsResponseBuilder();
    }

    public static class BotMetricsResponseBuilder {
        private long totalAnalyzedRequests;
        private long blockedBotsCount;
        private long suspiciousFlagCount;
        private double averageRiskScore;
        private boolean defenseModeActive;
        private List<BotThreatLog> recentThreats;

        public BotMetricsResponseBuilder totalAnalyzedRequests(long totalAnalyzedRequests) { this.totalAnalyzedRequests = totalAnalyzedRequests; return this; }
        public BotMetricsResponseBuilder blockedBotsCount(long blockedBotsCount) { this.blockedBotsCount = blockedBotsCount; return this; }
        public BotMetricsResponseBuilder suspiciousFlagCount(long suspiciousFlagCount) { this.suspiciousFlagCount = suspiciousFlagCount; return this; }
        public BotMetricsResponseBuilder averageRiskScore(double averageRiskScore) { this.averageRiskScore = averageRiskScore; return this; }
        public BotMetricsResponseBuilder defenseModeActive(boolean defenseModeActive) { this.defenseModeActive = defenseModeActive; return this; }
        public BotMetricsResponseBuilder recentThreats(List<BotThreatLog> recentThreats) { this.recentThreats = recentThreats; return this; }

        public BotMetricsResponse build() {
            return new BotMetricsResponse(totalAnalyzedRequests, blockedBotsCount, suspiciousFlagCount, averageRiskScore, defenseModeActive, recentThreats);
        }
    }

    public long getTotalAnalyzedRequests() { return totalAnalyzedRequests; }
    public void setTotalAnalyzedRequests(long totalAnalyzedRequests) { this.totalAnalyzedRequests = totalAnalyzedRequests; }

    public long getBlockedBotsCount() { return blockedBotsCount; }
    public void setBlockedBotsCount(long blockedBotsCount) { this.blockedBotsCount = blockedBotsCount; }

    public long getSuspiciousFlagCount() { return suspiciousFlagCount; }
    public void setSuspiciousFlagCount(long suspiciousFlagCount) { this.suspiciousFlagCount = suspiciousFlagCount; }

    public double getAverageRiskScore() { return averageRiskScore; }
    public void setAverageRiskScore(double averageRiskScore) { this.averageRiskScore = averageRiskScore; }

    public boolean isDefenseModeActive() { return defenseModeActive; }
    public void setDefenseModeActive(boolean defenseModeActive) { this.defenseModeActive = defenseModeActive; }

    public List<BotThreatLog> getRecentThreats() { return recentThreats; }
    public void setRecentThreats(List<BotThreatLog> recentThreats) { this.recentThreats = recentThreats; }
}
