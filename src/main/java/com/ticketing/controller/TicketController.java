package com.ticketing.controller;

import com.ticketing.dto.*;
import com.ticketing.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> purchaseTicket(@RequestBody TicketRequest request) {
        TicketResponse response = ticketService.purchaseTicket(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reserve")
    public ResponseEntity<SeatReserveResponse> reserveSeat(@RequestBody SeatReserveRequest request) {
        SeatReserveResponse response = ticketService.reserveSeat(request);
        if (!response.isSuccess()) {
            return ResponseEntity.status(409).body(response); // HTTP 409 Conflict if locked
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/extend-lock")
    public ResponseEntity<SeatReserveResponse> extendLock(@RequestBody SeatReserveRequest request) {
        SeatReserveResponse response = ticketService.extendSeatLock(request);
        if (!response.isSuccess()) {
            return ResponseEntity.status(400).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<TicketResponse> confirmReservation(@RequestBody SeatConfirmRequest request) {
        TicketResponse response = ticketService.confirmSeatReservation(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TicketResponse>> getUserTickets(@PathVariable Long userId) {
        return ResponseEntity.ok(ticketService.getUserTickets(userId));
    }
}
