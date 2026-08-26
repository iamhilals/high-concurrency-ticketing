package com.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketBookingEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Long eventId;
    private Long userId;
    private LocalDateTime purchaseDate;
}
