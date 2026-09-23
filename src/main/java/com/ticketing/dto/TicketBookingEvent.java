package com.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class TicketBookingEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Long eventId;
    private Long userId;

    public TicketBookingEvent() {}

    public TicketBookingEvent(Long eventId, Long userId) {
        this.eventId = eventId;
        this.userId = userId;
    }

    public static TicketBookingEventBuilder builder() {
        return new TicketBookingEventBuilder();
    }

    public static class TicketBookingEventBuilder {
        private Long eventId;
        private Long userId;

        public TicketBookingEventBuilder eventId(Long eventId) { this.eventId = eventId; return this; }
        public TicketBookingEventBuilder userId(Long userId) { this.userId = userId; return this; }

        public TicketBookingEvent build() {
            return new TicketBookingEvent(eventId, userId);
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

    public Long eventId() {
        return eventId;
    }

    public Long userId() {
        return userId;
    }
}
