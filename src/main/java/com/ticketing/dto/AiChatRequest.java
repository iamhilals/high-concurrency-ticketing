package com.ticketing.dto;

import lombok.Data;

@Data
public class AiChatRequest {
    private String message;
    private Long userId;

    public AiChatRequest() {}

    public AiChatRequest(String message, Long userId) {
        this.message = message;
        this.userId = userId;
    }

    public static AiChatRequestBuilder builder() {
        return new AiChatRequestBuilder();
    }

    public static class AiChatRequestBuilder {
        private String message;
        private Long userId;

        public AiChatRequestBuilder message(String message) { this.message = message; return this; }
        public AiChatRequestBuilder userId(Long userId) { this.userId = userId; return this; }

        public AiChatRequest build() {
            return new AiChatRequest(message, userId);
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
