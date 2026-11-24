package com.ticketing.dto;

import lombok.Data;

@Data
public class SeatConfirmRequest {
    private String reservationId;
    private Long eventId;
    private Long userId;
    private String seatId;
    private String seatInfo;
    private String zoneName;
    private String blockCode;
    private Double paidPrice;

    public SeatConfirmRequest() {}

    public SeatConfirmRequest(String reservationId, Long eventId, Long userId, String seatId,
                              String seatInfo, String zoneName, String blockCode, Double paidPrice) {
        this.reservationId = reservationId;
        this.eventId = eventId;
        this.userId = userId;
        this.seatId = seatId;
        this.seatInfo = seatInfo;
        this.zoneName = zoneName;
        this.blockCode = blockCode;
        this.paidPrice = paidPrice;
    }

    public static SeatConfirmRequestBuilder builder() {
        return new SeatConfirmRequestBuilder();
    }

    public static class SeatConfirmRequestBuilder {
        private String reservationId;
        private Long eventId;
        private Long userId;
        private String seatId;
        private String seatInfo;
        private String zoneName;
        private String blockCode;
        private Double paidPrice;

        public SeatConfirmRequestBuilder reservationId(String reservationId) { this.reservationId = reservationId; return this; }
        public SeatConfirmRequestBuilder eventId(Long eventId) { this.eventId = eventId; return this; }
        public SeatConfirmRequestBuilder userId(Long userId) { this.userId = userId; return this; }
        public SeatConfirmRequestBuilder seatId(String seatId) { this.seatId = seatId; return this; }
        public SeatConfirmRequestBuilder seatInfo(String seatInfo) { this.seatInfo = seatInfo; return this; }
        public SeatConfirmRequestBuilder zoneName(String zoneName) { this.zoneName = zoneName; return this; }
        public SeatConfirmRequestBuilder blockCode(String blockCode) { this.blockCode = blockCode; return this; }
        public SeatConfirmRequestBuilder paidPrice(Double paidPrice) { this.paidPrice = paidPrice; return this; }

        public SeatConfirmRequest build() {
            return new SeatConfirmRequest(reservationId, eventId, userId, seatId, seatInfo, zoneName, blockCode, paidPrice);
        }
    }

    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getSeatId() { return seatId; }
    public void setSeatId(String seatId) { this.seatId = seatId; }
    public String getSeatInfo() { return seatInfo; }
    public void setSeatInfo(String seatInfo) { this.seatInfo = seatInfo; }
    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }
    public String getBlockCode() { return blockCode; }
    public void setBlockCode(String blockCode) { this.blockCode = blockCode; }
    public Double getPaidPrice() { return paidPrice; }
    public void setPaidPrice(Double paidPrice) { this.paidPrice = paidPrice; }
}
