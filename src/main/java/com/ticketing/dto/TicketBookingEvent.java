package com.ticketing.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TicketBookingEvent implements Serializable {

    private static final long serialVersionUID = 2L;

    private Long eventId;
    private Long userId;
    private String pnrCode;
    private String seatInfo;
    private String zoneName;
    private String blockCode;
    private Double paidPrice;

    public TicketBookingEvent() {}

    public TicketBookingEvent(Long eventId, Long userId, String pnrCode, String seatInfo, String zoneName, String blockCode, Double paidPrice) {
        this.eventId = eventId;
        this.userId = userId;
        this.pnrCode = pnrCode;
        this.seatInfo = seatInfo;
        this.zoneName = zoneName;
        this.blockCode = blockCode;
        this.paidPrice = paidPrice;
    }

    public static TicketBookingEventBuilder builder() {
        return new TicketBookingEventBuilder();
    }

    public static class TicketBookingEventBuilder {
        private Long eventId;
        private Long userId;
        private String pnrCode;
        private String seatInfo;
        private String zoneName;
        private String blockCode;
        private Double paidPrice;

        public TicketBookingEventBuilder eventId(Long eventId) { this.eventId = eventId; return this; }
        public TicketBookingEventBuilder userId(Long userId) { this.userId = userId; return this; }
        public TicketBookingEventBuilder pnrCode(String pnrCode) { this.pnrCode = pnrCode; return this; }
        public TicketBookingEventBuilder seatInfo(String seatInfo) { this.seatInfo = seatInfo; return this; }
        public TicketBookingEventBuilder zoneName(String zoneName) { this.zoneName = zoneName; return this; }
        public TicketBookingEventBuilder blockCode(String blockCode) { this.blockCode = blockCode; return this; }
        public TicketBookingEventBuilder paidPrice(Double paidPrice) { this.paidPrice = paidPrice; return this; }

        public TicketBookingEvent build() {
            return new TicketBookingEvent(eventId, userId, pnrCode, seatInfo, zoneName, blockCode, paidPrice);
        }
    }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
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
    public Long eventId() { return eventId; }
    public Long userId() { return userId; }
}
