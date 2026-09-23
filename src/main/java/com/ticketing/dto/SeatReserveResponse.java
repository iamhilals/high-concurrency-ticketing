package com.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class SeatReserveResponse {
    private boolean success;
    private String reservationId;
    private Long eventId;
    private Long userId;
    private String seatId;
    private String zoneName;
    private Double price;
    private LocalDateTime expiresAt;
    private String message;

    public SeatReserveResponse() {}

    public SeatReserveResponse(boolean success, String reservationId, Long eventId, Long userId, String seatId, String zoneName, Double price, LocalDateTime expiresAt, String message) {
        this.success = success;
        this.reservationId = reservationId;
        this.eventId = eventId;
        this.userId = userId;
        this.seatId = seatId;
        this.zoneName = zoneName;
        this.price = price;
        this.expiresAt = expiresAt;
        this.message = message;
    }

    public static SeatReserveResponseBuilder builder() {
        return new SeatReserveResponseBuilder();
    }

    public static class SeatReserveResponseBuilder {
        private boolean success;
        private String reservationId;
        private Long eventId;
        private Long userId;
        private String seatId;
        private String zoneName;
        private Double price;
        private LocalDateTime expiresAt;
        private String message;

        public SeatReserveResponseBuilder success(boolean success) { this.success = success; return this; }
        public SeatReserveResponseBuilder reservationId(String reservationId) { this.reservationId = reservationId; return this; }
        public SeatReserveResponseBuilder eventId(Long eventId) { this.eventId = eventId; return this; }
        public SeatReserveResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public SeatReserveResponseBuilder seatId(String seatId) { this.seatId = seatId; return this; }
        public SeatReserveResponseBuilder zoneName(String zoneName) { this.zoneName = zoneName; return this; }
        public SeatReserveResponseBuilder price(Double price) { this.price = price; return this; }
        public SeatReserveResponseBuilder expiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; return this; }
        public SeatReserveResponseBuilder message(String message) { this.message = message; return this; }

        public SeatReserveResponse build() {
            return new SeatReserveResponse(success, reservationId, eventId, userId, seatId, zoneName, price, expiresAt, message);
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
