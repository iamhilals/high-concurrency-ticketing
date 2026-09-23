package com.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SeatConfirmRequest {
    private String reservationId;
    private Long eventId;
    private Long userId;
    private String seatId;

    public SeatConfirmRequest() {}

    public SeatConfirmRequest(String reservationId, Long eventId, Long userId, String seatId) {
        this.reservationId = reservationId;
        this.eventId = eventId;
        this.userId = userId;
        this.seatId = seatId;
    }

    public static SeatConfirmRequestBuilder builder() {
        return new SeatConfirmRequestBuilder();
    }

    public static class SeatConfirmRequestBuilder {
        private String reservationId;
        private Long eventId;
        private Long userId;
        private String seatId;

        public SeatConfirmRequestBuilder reservationId(String reservationId) { this.reservationId = reservationId; return this; }
        public SeatConfirmRequestBuilder eventId(Long eventId) { this.eventId = eventId; return this; }
        public SeatConfirmRequestBuilder userId(Long userId) { this.userId = userId; return this; }
        public SeatConfirmRequestBuilder seatId(String seatId) { this.seatId = seatId; return this; }

        public SeatConfirmRequest build() {
            return new SeatConfirmRequest(reservationId, eventId, userId, seatId);
        }
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
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

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }
}
