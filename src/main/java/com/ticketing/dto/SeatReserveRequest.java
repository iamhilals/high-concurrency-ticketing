package com.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SeatReserveRequest {
    private Long eventId;
    private Long userId;
    private String seatId;
    private String zoneName;
    private Double price;

    public SeatReserveRequest() {}

    public SeatReserveRequest(Long eventId, Long userId, String seatId, String zoneName, Double price) {
        this.eventId = eventId;
        this.userId = userId;
        this.seatId = seatId;
        this.zoneName = zoneName;
        this.price = price;
    }

    public static SeatReserveRequestBuilder builder() {
        return new SeatReserveRequestBuilder();
    }

    public static class SeatReserveRequestBuilder {
        private Long eventId;
        private Long userId;
        private String seatId;
        private String zoneName;
        private Double price;

        public SeatReserveRequestBuilder eventId(Long eventId) { this.eventId = eventId; return this; }
        public SeatReserveRequestBuilder userId(Long userId) { this.userId = userId; return this; }
        public SeatReserveRequestBuilder seatId(String seatId) { this.seatId = seatId; return this; }
        public SeatReserveRequestBuilder zoneName(String zoneName) { this.zoneName = zoneName; return this; }
        public SeatReserveRequestBuilder price(Double price) { this.price = price; return this; }

        public SeatReserveRequest build() {
            return new SeatReserveRequest(eventId, userId, seatId, zoneName, price);
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
}
