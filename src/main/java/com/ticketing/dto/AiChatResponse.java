package com.ticketing.dto;

import com.ticketing.entity.Event;

import java.util.ArrayList;
import java.util.List;

public class AiChatResponse {
    private String reply;
    private List<Event> suggestedEvents = new ArrayList<>();
    private String usedEngine;

    public AiChatResponse() {}

    public AiChatResponse(String reply, List<Event> suggestedEvents, String usedEngine) {
        this.reply = reply;
        this.suggestedEvents = suggestedEvents != null ? suggestedEvents : new ArrayList<>();
        this.usedEngine = usedEngine;
    }

    public static AiChatResponseBuilder builder() {
        return new AiChatResponseBuilder();
    }

    public static class AiChatResponseBuilder {
        private String reply;
        private List<Event> suggestedEvents = new ArrayList<>();
        private String usedEngine;

        public AiChatResponseBuilder reply(String reply) { this.reply = reply; return this; }
        public AiChatResponseBuilder suggestedEvents(List<Event> suggestedEvents) { this.suggestedEvents = suggestedEvents; return this; }
        public AiChatResponseBuilder usedEngine(String usedEngine) { this.usedEngine = usedEngine; return this; }

        public AiChatResponse build() {
            return new AiChatResponse(reply, suggestedEvents, usedEngine);
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

    public String getUsedEngine() {
        return usedEngine;
    }

    public void setUsedEngine(String usedEngine) {
        this.usedEngine = usedEngine;
    }
}
