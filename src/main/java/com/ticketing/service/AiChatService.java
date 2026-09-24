package com.ticketing.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ticketing.dto.AiChatRequest;
import com.ticketing.dto.AiChatResponse;
import com.ticketing.entity.Event;
import com.ticketing.repository.EventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AiChatService {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${gemini.api.key:}")
    private String configuredApiKey;

    public AiChatService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public AiChatResponse processChat(AiChatRequest request) {
        String message = request.getMessage() != null ? request.getMessage().trim() : "";
        if (message.isEmpty()) {
            return AiChatResponse.builder()
                    .reply("Merhaba! Ben PassoBot🤖. Size konser, tiyatro, festival ve bilet arama konusunda yardımcı olabilirim. Nasıl bir etkinlik arıyorsunuz?")
                    .suggestedEvents(new ArrayList<>())
                    .usedEngine("Lokal NLP Asistanı")
                    .build();
        }

        List<Event> allEvents = eventRepository.findAll();
        String effectiveKey = (request.getApiKey() != null && !request.getApiKey().trim().isEmpty())
                ? request.getApiKey().trim()
                : (configuredApiKey != null ? configuredApiKey.trim() : "");

        // If Gemini API Key is available, attempt real Generative AI call
        if (!effectiveKey.isEmpty()) {
            try {
                AiChatResponse geminiResponse = callGeminiApi(effectiveKey, message, allEvents);
                if (geminiResponse != null && geminiResponse.getReply() != null && !geminiResponse.getReply().isEmpty()) {
                    return geminiResponse;
                }
            } catch (Exception e) {
                System.err.println("Gemini API call failed, falling back to Local NLP Engine: " + e.getMessage());
            }
        }

        // Fallback: Local Rule-Based NLP Recommendation Engine
        return processLocalChat(message, allEvents);
    }

    /**
     * Calls Google Gemini 1.5 Flash Generative LLM API via HTTP
     */
    private AiChatResponse callGeminiApi(String apiKey, String userMessage, List<Event> allEvents) throws Exception {
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        // Build catalogue context for Gemini
        StringBuilder eventsCatalogue = new StringBuilder();
        for (Event e : allEvents) {
            eventsCatalogue.append(String.format("- ID: %d, Etkinlik: '%s', Kategori: '%s', Mekan: '%s', Fiyat: %.0f TL, Stok: %d\n",
                    e.getId(), e.getTitle(), e.getCategory(), e.getVenue(),
                    e.getPrice() != null ? e.getPrice().doubleValue() : 0,
                    e.getAvailableCapacity() != null ? e.getAvailableCapacity() : 0));
        }

        String systemPrompt = "Sen PassoLive Bilet Satış Hizmetleri'nin resmi ve süper zeki AI Bilet Asistanısın (PassoBot 🤖).\n" +
                "Görevin: Kullanıcıya çok samimi, enerjik, kibar ve bilet almaya heveslendiren yanıtlar vermektir.\n\n" +
                "Sistemimizde şu anda yayında olan CANLI ETKİNLİK KATALOĞUMUZ:\n" +
                eventsCatalogue.toString() + "\n" +
                "KULLANICI SORUSU: \"" + userMessage + "\"\n\n" +
                "KURAL 1: Yanıtını Türkçe ver. Kullanıcının sorusuna doğrudan, detaylı ve coşkulu cevap ver.\n" +
                "KURAL 2: Önerdiğin etkinliklerin tam başlıklarını ve ID'lerini cevabının içinde doğal bir şekilde geçir.\n" +
                "KURAL 3: Biletlerin 10 dakikalığına geçici olarak rezerve edilebildiğini ve anında tükenmeden almasını tavsiye et.";

        // Construct Gemini JSON payload
        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode contentsNode = rootNode.putArray("contents");
        ObjectNode contentObj = contentsNode.addObject();
        ArrayNode partsNode = contentObj.putArray("parts");
        ObjectNode partObj = partsNode.addObject();
        partObj.put("text", systemPrompt);

        String jsonPayload = objectMapper.writeValueAsString(rootNode);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .timeout(Duration.ofSeconds(12))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() != 200) {
            throw new RuntimeException("Gemini API Error Code: " + httpResponse.statusCode() + " - " + httpResponse.body());
        }

        JsonNode responseJson = objectMapper.readTree(httpResponse.body());
        JsonNode candidatesNode = responseJson.path("candidates");
        if (candidatesNode.isArray() && candidatesNode.size() > 0) {
            JsonNode textNode = candidatesNode.get(0).path("content").path("parts").get(0).path("text");
            String aiReply = textNode.asText();

            // Match recommended events in Gemini's response
            List<Event> matchedEvents = new ArrayList<>();
            String lowerReply = aiReply.toLowerCase();
            for (Event e : allEvents) {
                if ((e.getTitle() != null && lowerReply.contains(e.getTitle().toLowerCase())) ||
                    lowerReply.contains("id: " + e.getId()) ||
                    lowerReply.contains("#" + e.getId())) {
                    matchedEvents.add(e);
                }
            }

            // If no explicit title matched, match by category/budget keywords
            if (matchedEvents.isEmpty()) {
                String cat = detectCategory(userMessage.toLowerCase());
                if (cat != null) {
                    matchedEvents = allEvents.stream()
                            .filter(e -> e.getCategory() != null && e.getCategory().toLowerCase().contains(cat))
                            .limit(3)
                            .collect(Collectors.toList());
                }
            }

            return AiChatResponse.builder()
                    .reply(aiReply)
                    .suggestedEvents(matchedEvents)
                    .usedEngine("Google Gemini 1.5 Flash (Generative AI)")
                    .build();
        }

        throw new RuntimeException("Empty response candidates from Gemini API");
    }

    /**
     * Local Fast Regex & NLP Fallback Matcher
     */
    private AiChatResponse processLocalChat(String rawMessage, List<Event> allEvents) {
        String message = rawMessage.toLowerCase();
        List<Event> matchedEvents = new ArrayList<>();
        StringBuilder replyBuilder = new StringBuilder();

        BigDecimal maxPrice = extractMaxPrice(message);
        String matchedCategory = detectCategory(message);
        String matchedVenue = detectVenue(message);

        for (Event event : allEvents) {
            boolean matchesCategory = matchedCategory == null || (event.getCategory() != null && event.getCategory().toLowerCase().contains(matchedCategory));
            boolean matchesPrice = maxPrice == null || (event.getPrice() != null && event.getPrice().compareTo(maxPrice) <= 0);
            boolean matchesVenue = matchedVenue == null || (event.getVenue() != null && event.getVenue().toLowerCase().contains(matchedVenue));
            
            boolean matchesText = message.contains("hepsi") || message.contains("öner") || message.contains("popüler") ||
                    (event.getTitle() != null && event.getTitle().toLowerCase().contains(message)) ||
                    (event.getDescription() != null && event.getDescription().toLowerCase().contains(message));

            if (matchesCategory && matchesPrice && matchesVenue && (matchesText || matchedCategory != null || maxPrice != null || matchedVenue != null)) {
                matchedEvents.add(event);
            }
        }

        if (matchedEvents.isEmpty() && maxPrice != null) {
            matchedEvents = allEvents.stream()
                    .filter(e -> e.getPrice() != null && e.getPrice().compareTo(maxPrice) <= 0)
                    .limit(3)
                    .collect(Collectors.toList());
        }

        if (matchedEvents.isEmpty() && matchedCategory != null) {
            final String cat = matchedCategory;
            matchedEvents = allEvents.stream()
                    .filter(e -> e.getCategory() != null && e.getCategory().toLowerCase().contains(cat))
                    .limit(3)
                    .collect(Collectors.toList());
        }

        if (!matchedEvents.isEmpty()) {
            replyBuilder.append("Harika bir seçim! Aradığınız kriterlere uygun **").append(matchedEvents.size()).append(" adet** harika etkinlik buldum:\n\n");
            if (maxPrice != null) replyBuilder.append("💰 **Bütçe Sınırı:** ").append(maxPrice).append(" TL ve altı\n");
            if (matchedCategory != null) replyBuilder.append("🏷️ **Kategori:** ").append(capitalize(matchedCategory)).append("\n");
            replyBuilder.append("\nAşağıdaki kartlardan etkinlik detaylarını inceleyebilir ve **anında 10 dakikalık koltuk rezervasyonu** yapabilirsiniz 🎟️:");
        } else {
            replyBuilder.append("Aradığınız kriterlere tam uyan bir etkinlik bulamadım 😔 Ama şu anda en çok ilgi gören popüler etkinliklerimizi aşağıda sizin için listeledim:");
            matchedEvents = allEvents.stream().limit(3).collect(Collectors.toList());
        }

        return AiChatResponse.builder()
                .reply(replyBuilder.toString())
                .suggestedEvents(matchedEvents)
                .usedEngine("Lokal NLP Asistanı (Fallback)")
                .build();
    }

    private BigDecimal extractMaxPrice(String text) {
        Pattern pattern = Pattern.compile("(\\d+)\\s*(tl|lira|altı|alti|bütçe|butce)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            try { return new BigDecimal(matcher.group(1)); } catch (Exception ignored) {}
        }
        if (text.contains("ucuz") || text.contains("ekonomik")) return new BigDecimal("500.00");
        return null;
    }

    private String detectCategory(String text) {
        if (text.contains("konser") || text.contains("müzik") || text.contains("muzik") || text.contains("rock") || text.contains("pop")) return "müzik";
        if (text.contains("tiyatro") || text.contains("komedi") || text.contains("stand-up") || text.contains("gösteri")) return "komedi";
        if (text.contains("festival") || text.contains("dj") || text.contains("parti") || text.contains("techno")) return "festival";
        if (text.contains("spor") || text.contains("derbi") || text.contains("maç") || text.contains("mac") || text.contains("futbol") || text.contains("voleybol")) return "spor";
        return null;
    }

    private String detectVenue(String text) {
        if (text.contains("istanbul") || text.contains("harbiye") || text.contains("kadıköy") || text.contains("beşiktaş") || text.contains("maslak")) return "istanbul";
        if (text.contains("ankara")) return "ankara";
        if (text.contains("izmir")) return "izmir";
        if (text.contains("bursa")) return "bursa";
        if (text.contains("antalya")) return "antalya";
        return null;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
