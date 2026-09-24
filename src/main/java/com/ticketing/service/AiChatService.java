package com.ticketing.service;

import com.ticketing.dto.AiChatRequest;
import com.ticketing.dto.AiChatResponse;
import com.ticketing.entity.Event;
import com.ticketing.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AiChatService {

    private final EventRepository eventRepository;

    public AiChatService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public AiChatResponse processChat(AiChatRequest request) {
        String message = request.getMessage() != null ? request.getMessage().trim().toLowerCase() : "";
        List<Event> allEvents = eventRepository.findAll();

        if (message.isEmpty()) {
            return AiChatResponse.builder()
                    .reply("Merhaba! Ben PassoBot🤖. Size konser, tiyatro, festival ve bilet arama konusunda yardımcı olabilirim. Nasıl bir etkinlik arıyorsunuz?")
                    .suggestedEvents(new ArrayList<>())
                    .build();
        }

        List<Event> matchedEvents = new ArrayList<>();
        StringBuilder replyBuilder = new StringBuilder();

        // 1. Bütçe analizi (Örn: "500 tl altı", "400 lira", "en ucuz")
        BigDecimal maxPrice = extractMaxPrice(message);

        // 2. Kategori Tespiti
        String matchedCategory = detectCategory(message);

        // 3. Şehir / Mekan Tespiti
        String matchedVenue = detectVenue(message);

        // 4. Arama ve Eşleştirme Mantığı
        for (Event event : allEvents) {
            boolean matchesCategory = matchedCategory == null || (event.getCategory() != null && event.getCategory().toLowerCase().contains(matchedCategory));
            boolean matchesPrice = maxPrice == null || (event.getPrice() != null && event.getPrice().compareTo(maxPrice) <= 0);
            boolean matchesVenue = matchedVenue == null || (event.getVenue() != null && event.getVenue().toLowerCase().contains(matchedVenue));
            
            // Metin bazlı arama (Sanatçı veya Başlık eşleşmesi)
            boolean matchesText = message.contains("hepsi") || message.contains("öner") || message.contains("popüler") ||
                    (event.getTitle() != null && event.getTitle().toLowerCase().contains(message)) ||
                    (event.getDescription() != null && event.getDescription().toLowerCase().contains(message));

            if (matchesCategory && matchesPrice && matchesVenue && (matchesText || matchedCategory != null || maxPrice != null || matchedVenue != null)) {
                matchedEvents.add(event);
            }
        }

        // Eğer doğrudan eşleşen kalmadıysa, en azından kategori veya bütçeye uyan ilk 3 etkinliği getir
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

        // Yanıt Metnini Oluşturma
        if (!matchedEvents.isEmpty()) {
            replyBuilder.append("Harika bir seçim! Aradığınız kriterlere uygun **").append(matchedEvents.size()).append(" adet** harika etkinlik buldum:\n\n");
            if (maxPrice != null) {
                replyBuilder.append("💰 **Bütçe Sınırı:** ").append(maxPrice).append(" TL ve altı\n");
            }
            if (matchedCategory != null) {
                replyBuilder.append("🏷️ **Kategori:** ").append(capitalize(matchedCategory)).append("\n");
            }
            replyBuilder.append("\nAşağıdaki kartlardan etkinlik detaylarını inceleyebilir ve **anında 10 dakikalık koltuk rezervasyonu** yapabilirsiniz 🎟️:");
        } else {
            replyBuilder.append("Aradığınız kriterlere tam uyan bir etkinlik bulamadım 😔 Ama şu anda en çok ilgi gören popüler etkinliklerimizi aşağıda sizin için listeledim:");
            matchedEvents = allEvents.stream().limit(3).collect(Collectors.toList());
        }

        return AiChatResponse.builder()
                .reply(replyBuilder.toString())
                .suggestedEvents(matchedEvents)
                .build();
    }

    private BigDecimal extractMaxPrice(String text) {
        Pattern pattern = Pattern.compile("(\\d+)\\s*(tl|lira|altı|alti|bütçe|butce)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            try {
                return new BigDecimal(matcher.group(1));
            } catch (Exception ignored) {}
        }
        if (text.contains("ucuz") || text.contains("ekonomik")) {
            return new BigDecimal("500.00");
        }
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
