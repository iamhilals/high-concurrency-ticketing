package com.ticketing.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketResponse {
    private Long ticketId;
    private Long eventId;
    private String eventTitle;
    private Long userId;
    private String username;
    private LocalDateTime purchaseDate;
    private String status;

    public TicketResponse() {}

    public TicketResponse(Long ticketId, Long eventId, String eventTitle, Long userId, String username, LocalDateTime purchaseDate, String status) {
        this.ticketId = ticketId;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.userId = userId;
        this.username = username;
        this.purchaseDate = purchaseDate;
        this.status = status;
    }

    public static TicketResponseBuilder builder() {
        return new TicketResponseBuilder();
    }

    public static class TicketResponseBuilder {
        private Long ticketId;
        private Long eventId;
        private String eventTitle;
        private Long userId;
        private String username;
        private LocalDateTime purchaseDate;
        private String status;

        public TicketResponseBuilder ticketId(Long ticketId) { this.ticketId = ticketId; return this; }
        public TicketResponseBuilder eventId(Long eventId) { this.eventId = eventId; return this; }
        public TicketResponseBuilder eventTitle(String eventTitle) { this.eventTitle = eventTitle; return this; }
        public TicketResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public TicketResponseBuilder username(String username) { this.username = username; return this; }
        public TicketResponseBuilder purchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; return this; }
        public TicketResponseBuilder status(String status) { this.status = status; return this; }

        public TicketResponse build() {
            return new TicketResponse(ticketId, eventId, eventTitle, userId, username, purchaseDate, status);
        }
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
