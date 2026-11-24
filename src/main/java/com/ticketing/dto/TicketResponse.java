package com.ticketing.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketResponse {
    private Long ticketId;
    private Long eventId;
    private String eventTitle;
    private String eventVenue;
    private LocalDateTime eventDateTime;
    private Long userId;
    private String username;
    private LocalDateTime purchaseDate;
    private String status;
    private String pnrCode;
    private String seatInfo;
    private String zoneName;
    private String blockCode;
    private Double paidPrice;
    private boolean expired;

    public TicketResponse() {}

    public TicketResponse(Long ticketId, Long eventId, String eventTitle, String eventVenue, LocalDateTime eventDateTime,
                          Long userId, String username, LocalDateTime purchaseDate, String status,
                          String pnrCode, String seatInfo, String zoneName, String blockCode, Double paidPrice, boolean expired) {
        this.ticketId = ticketId;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventVenue = eventVenue;
        this.eventDateTime = eventDateTime;
        this.userId = userId;
        this.username = username;
        this.purchaseDate = purchaseDate;
        this.status = status;
        this.pnrCode = pnrCode;
        this.seatInfo = seatInfo;
        this.zoneName = zoneName;
        this.blockCode = blockCode;
        this.paidPrice = paidPrice;
        this.expired = expired;
    }

    public static TicketResponseBuilder builder() {
        return new TicketResponseBuilder();
    }

    public static class TicketResponseBuilder {
        private Long ticketId;
        private Long eventId;
        private String eventTitle;
        private String eventVenue;
        private LocalDateTime eventDateTime;
        private Long userId;
        private String username;
        private LocalDateTime purchaseDate;
        private String status;
        private String pnrCode;
        private String seatInfo;
        private String zoneName;
        private String blockCode;
        private Double paidPrice;
        private boolean expired;

        public TicketResponseBuilder ticketId(Long ticketId) { this.ticketId = ticketId; return this; }
        public TicketResponseBuilder eventId(Long eventId) { this.eventId = eventId; return this; }
        public TicketResponseBuilder eventTitle(String eventTitle) { this.eventTitle = eventTitle; return this; }
        public TicketResponseBuilder eventVenue(String eventVenue) { this.eventVenue = eventVenue; return this; }
        public TicketResponseBuilder eventDateTime(LocalDateTime eventDateTime) { this.eventDateTime = eventDateTime; return this; }
        public TicketResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public TicketResponseBuilder username(String username) { this.username = username; return this; }
        public TicketResponseBuilder purchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; return this; }
        public TicketResponseBuilder status(String status) { this.status = status; return this; }
        public TicketResponseBuilder pnrCode(String pnrCode) { this.pnrCode = pnrCode; return this; }
        public TicketResponseBuilder seatInfo(String seatInfo) { this.seatInfo = seatInfo; return this; }
        public TicketResponseBuilder zoneName(String zoneName) { this.zoneName = zoneName; return this; }
        public TicketResponseBuilder blockCode(String blockCode) { this.blockCode = blockCode; return this; }
        public TicketResponseBuilder paidPrice(Double paidPrice) { this.paidPrice = paidPrice; return this; }
        public TicketResponseBuilder expired(boolean expired) { this.expired = expired; return this; }

        public TicketResponse build() {
            return new TicketResponse(ticketId, eventId, eventTitle, eventVenue, eventDateTime, userId, username, purchaseDate, status, pnrCode, seatInfo, zoneName, blockCode, paidPrice, expired);
        }
    }

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
    public String getEventVenue() { return eventVenue; }
    public void setEventVenue(String eventVenue) { this.eventVenue = eventVenue; }
    public LocalDateTime getEventDateTime() { return eventDateTime; }
    public void setEventDateTime(LocalDateTime eventDateTime) { this.eventDateTime = eventDateTime; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPnrCode() { return pnrCode; }
    public void setPnrCode(String pnrCode) { this.pnrCode = pnrCode; }
    public String getSeatInfo() { return seatInfo; }
    public void setSeatInfo(String seatInfo) { this.seatInfo = seatInfo; }
    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }
    public String getBlockCode() { return blockCode; }
    public void setBlockCode(String blockCode) { this.blockCode = blockCode; }
    public Double getPaidPrice() { return paidPrice; }
    public void setPaidPrice(Double paidPrice) { this.paidPrice = paidPrice; }
    public boolean isExpired() { return expired; }
    public void setExpired(boolean expired) { this.expired = expired; }
}
