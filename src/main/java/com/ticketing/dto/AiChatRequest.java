package com.ticketing.dto;

public class AiChatRequest {
    private String message;
    private Long userId;
    private String apiKey;

    public AiChatRequest() {}

    public AiChatRequest(String message, Long userId, String apiKey) {
        this.message = message;
        this.userId = userId;
        this.apiKey = apiKey;
    }

    public static AiChatRequestBuilder builder() {
        return new AiChatRequestBuilder();
    }

    public static class AiChatRequestBuilder {
        private String message;
        private Long userId;
        private String apiKey;

        public AiChatRequestBuilder message(String message) { this.message = message; return this; }
        public AiChatRequestBuilder userId(Long userId) { this.userId = userId; return this; }
        public AiChatRequestBuilder apiKey(String apiKey) { this.apiKey = apiKey; return this; }

        public AiChatRequest build() {
            return new AiChatRequest(message, userId, apiKey);
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

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
