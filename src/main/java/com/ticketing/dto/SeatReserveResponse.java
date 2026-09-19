package com.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
