package com.ticketing.controller;

import com.ticketing.dto.AiChatRequest;
import com.ticketing.dto.AiChatResponse;
import com.ticketing.service.AiChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(@RequestBody AiChatRequest request) {
        AiChatResponse response = aiChatService.processChat(request);
        return ResponseEntity.ok(response);
    }
}
