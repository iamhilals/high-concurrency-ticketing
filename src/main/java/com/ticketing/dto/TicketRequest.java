package com.ticketing.dto;

import lombok.Data;

@Data
public class TicketRequest {
    private Long eventId;
    private Long userId;

    public TicketRequest() {}

    public TicketRequest(Long eventId, Long userId) {
        this.eventId = eventId;
        this.userId = userId;
    }

    public static TicketRequestBuilder builder() {
        return new TicketRequestBuilder();
    }

    public static class TicketRequestBuilder {
        private Long eventId;
        private Long userId;

        public TicketRequestBuilder eventId(Long eventId) { this.eventId = eventId; return this; }
        public TicketRequestBuilder userId(Long userId) { this.userId = userId; return this; }

        public TicketRequest build() {
            return new TicketRequest(eventId, userId);
        }
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
