package com.ticketing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Getter
@Setter
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime purchaseDate;

    @Column(name = "pnr_code")
    private String pnrCode;

    @Column(name = "seat_info")
    private String seatInfo;

    @Column(name = "zone_name")
    private String zoneName;

    @Column(name = "block_code")
    private String blockCode;

    @Column(name = "paid_price")
    private Double paidPrice;

    public Ticket() {}

    public Ticket(Long id, Event event, User user, LocalDateTime purchaseDate,
                  String pnrCode, String seatInfo, String zoneName, String blockCode, Double paidPrice) {
        this.id = id;
        this.event = event;
        this.user = user;
        this.purchaseDate = purchaseDate;
        this.pnrCode = pnrCode;
        this.seatInfo = seatInfo;
        this.zoneName = zoneName;
        this.blockCode = blockCode;
        this.paidPrice = paidPrice;
    }

    public static TicketBuilder builder() {
        return new TicketBuilder();
    }

    public static class TicketBuilder {
        private Long id;
        private Event event;
        private User user;
        private LocalDateTime purchaseDate;
        private String pnrCode;
        private String seatInfo;
        private String zoneName;
        private String blockCode;
        private Double paidPrice;

        public TicketBuilder id(Long id) { this.id = id; return this; }
        public TicketBuilder event(Event event) { this.event = event; return this; }
        public TicketBuilder user(User user) { this.user = user; return this; }
        public TicketBuilder purchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; return this; }
        public TicketBuilder pnrCode(String pnrCode) { this.pnrCode = pnrCode; return this; }
        public TicketBuilder seatInfo(String seatInfo) { this.seatInfo = seatInfo; return this; }
        public TicketBuilder zoneName(String zoneName) { this.zoneName = zoneName; return this; }
        public TicketBuilder blockCode(String blockCode) { this.blockCode = blockCode; return this; }
        public TicketBuilder paidPrice(Double paidPrice) { this.paidPrice = paidPrice; return this; }

        public Ticket build() {
            return new Ticket(id, event, user, purchaseDate, pnrCode, seatInfo, zoneName, blockCode, paidPrice);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }
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
}
