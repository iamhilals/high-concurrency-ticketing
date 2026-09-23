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

    public Ticket() {}

    public Ticket(Long id, Event event, User user, LocalDateTime purchaseDate) {
        this.id = id;
        this.event = event;
        this.user = user;
        this.purchaseDate = purchaseDate;
    }

    public static TicketBuilder builder() {
        return new TicketBuilder();
    }

    public static class TicketBuilder {
        private Long id;
        private Event event;
        private User user;
        private LocalDateTime purchaseDate;

        public TicketBuilder id(Long id) { this.id = id; return this; }
        public TicketBuilder event(Event event) { this.event = event; return this; }
        public TicketBuilder user(User user) { this.user = user; return this; }
        public TicketBuilder purchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; return this; }

        public Ticket build() {
            return new Ticket(id, event, user, purchaseDate);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
