package com.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResponse {
    private Long ticketId;
    private Long eventId;
    private String eventTitle;
    private Long userId;
    private String username;
    private LocalDateTime purchaseDate;
}
