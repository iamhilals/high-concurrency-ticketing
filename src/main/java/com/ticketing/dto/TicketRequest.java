package com.ticketing.dto;

import lombok.Data;

@Data
public class TicketRequest {
    private Long eventId;
    private Long userId;
    private String seatInfo;
    private String zoneName;
    private String blockCode;
    private Double paidPrice;

    public TicketRequest() {}

    public TicketRequest(Long eventId, Long userId, String seatInfo, String zoneName, String blockCode, Double paidPrice) {
        this.eventId = eventId;
        this.userId = userId;
        this.seatInfo = seatInfo;
        this.zoneName = zoneName;
        this.blockCode = blockCode;
        this.paidPrice = paidPrice;
    }

    public static TicketRequestBuilder builder() {
        return new TicketRequestBuilder();
    }

    public static class TicketRequestBuilder {
        private Long eventId;
        private Long userId;
        private String seatInfo;
        private String zoneName;
        private String blockCode;
        private Double paidPrice;

        public TicketRequestBuilder eventId(Long eventId) { this.eventId = eventId; return this; }
        public TicketRequestBuilder userId(Long userId) { this.userId = userId; return this; }
        public TicketRequestBuilder seatInfo(String seatInfo) { this.seatInfo = seatInfo; return this; }
        public TicketRequestBuilder zoneName(String zoneName) { this.zoneName = zoneName; return this; }
        public TicketRequestBuilder blockCode(String blockCode) { this.blockCode = blockCode; return this; }
        public TicketRequestBuilder paidPrice(Double paidPrice) { this.paidPrice = paidPrice; return this; }

        public TicketRequest build() {
            return new TicketRequest(eventId, userId, seatInfo, zoneName, blockCode, paidPrice);
        }
    }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getSeatInfo() { return seatInfo; }
    public void setSeatInfo(String seatInfo) { this.seatInfo = seatInfo; }
    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }
    public String getBlockCode() { return blockCode; }
    public void setBlockCode(String blockCode) { this.blockCode = blockCode; }
    public Double getPaidPrice() { return paidPrice; }
    public void setPaidPrice(Double paidPrice) { this.paidPrice = paidPrice; }
}
