package com.ticketing.dto;

import com.ticketing.entity.Event;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AiChatResponse {
    private String reply;
    private List<Event> suggestedEvents = new ArrayList<>();

    public AiChatResponse() {}

    public AiChatResponse(String reply, List<Event> suggestedEvents) {
        this.reply = reply;
        this.suggestedEvents = suggestedEvents != null ? suggestedEvents : new ArrayList<>();
    }

    public static AiChatResponseBuilder builder() {
        return new AiChatResponseBuilder();
    }

    public static class AiChatResponseBuilder {
        private String reply;
        private List<Event> suggestedEvents = new ArrayList<>();

        public AiChatResponseBuilder reply(String reply) { this.reply = reply; return this; }
        public AiChatResponseBuilder suggestedEvents(List<Event> suggestedEvents) { this.suggestedEvents = suggestedEvents; return this; }

        public AiChatResponse build() {
            return new AiChatResponse(reply, suggestedEvents);
        }
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public List<Event> getSuggestedEvents() {
        return suggestedEvents;
    }

    public void setSuggestedEvents(List<Event> suggestedEvents) {
        this.suggestedEvents = suggestedEvents;
    }
}
