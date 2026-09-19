package com.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatConfirmRequest {
    private String reservationId;
    private Long eventId;
    private Long userId;
    private String seatId;
}
