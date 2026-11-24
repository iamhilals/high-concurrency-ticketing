package com.ticketing.consumer;

import com.ticketing.dto.TicketBookingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class TicketDlqConsumer {

    /**
     * Listens to Dead Letter Queue topics for unprocessable booking requests.
     */
    @KafkaListener(topics = {"ticket-bookings.DLQ", "ticket-bookings-dlq"}, groupId = "ticketing-dlq-group")
    public void consumeDeadLetterQueue(TicketBookingEvent failedMsg) {
        System.err.println("⚠️ [KAFKA DLQ WARNING] Bilet talebi işlenemedi ve Ölü Mektup Kuyruğuna (DLQ) taşındı!");
        if (failedMsg != null) {
            System.err.println("   Etkinlik ID: " + failedMsg.getEventId() + ", Kullanıcı ID: " + failedMsg.getUserId());
        }
    }
}
