package com.ticketing.controller;

import com.ticketing.dto.TicketRequest;
import com.ticketing.dto.TicketResponse;
import com.ticketing.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> purchaseTicket(@RequestBody TicketRequest request) {
        TicketResponse response = ticketService.purchaseTicket(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TicketResponse>> getUserTickets(@PathVariable Long userId) {
        return ResponseEntity.ok(ticketService.getUserTickets(userId));
    }
}
