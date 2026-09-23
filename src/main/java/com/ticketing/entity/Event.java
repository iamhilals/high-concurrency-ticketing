package com.ticketing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer availableCapacity;

    private String category;

    private String imageUrl;

    public Event() {}

    public Event(Long id, String title, String description, LocalDateTime dateTime, BigDecimal price, Integer availableCapacity, String category, String venue, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.price = price;
        this.availableCapacity = availableCapacity;
        this.category = category;
        this.venue = venue;
        this.imageUrl = imageUrl;
    }

    public static EventBuilder builder() {
        return new EventBuilder();
    }

    public static class EventBuilder {
        private Long id;
        private String title;
        private String description;
        private LocalDateTime dateTime;
        private BigDecimal price;
        private Integer availableCapacity;
        private String category;
        private String venue;
        private String imageUrl;

        public EventBuilder id(Long id) { this.id = id; return this; }
        public EventBuilder title(String title) { this.title = title; return this; }
        public EventBuilder description(String description) { this.description = description; return this; }
        public EventBuilder dateTime(LocalDateTime dateTime) { this.dateTime = dateTime; return this; }
        public EventBuilder price(BigDecimal price) { this.price = price; return this; }
        public EventBuilder availableCapacity(Integer availableCapacity) { this.availableCapacity = availableCapacity; return this; }
        public EventBuilder category(String category) { this.category = category; return this; }
        public EventBuilder venue(String venue) { this.venue = venue; return this; }
        public EventBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }

        public Event build() {
            return new Event(id, title, description, dateTime, price, availableCapacity, category, venue, imageUrl);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(Integer availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
