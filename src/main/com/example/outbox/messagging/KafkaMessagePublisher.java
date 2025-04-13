package com.example.outbox.messagging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

// Implementazione specifica per Kafka
@Service
public class KafkaMessagePublisher implements MessagePublisher {
    private static final Logger log = LoggerFactory.getLogger(KafkaMessagePublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaMessagePublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(String eventType, String aggregateType, String aggregateId, String payload) {
        String topic = determineTopic(aggregateType);

        // Crea un oggetto MessageEnvelope per aggiungere metadati
        MessageEnvelope envelope = new MessageEnvelope(
                UUID.randomUUID().toString(),
                eventType,
                aggregateType,
                aggregateId,
                LocalDateTime.now().toString(),
                payload
        );

        try {
            String messageJson = new ObjectMapper().writeValueAsString(envelope);

            // Pubblica il messaggio su Kafka
            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(topic, aggregateId, messageJson);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send message to topic {}", topic, ex);
                } else {
                    log.debug("Message sent successfully to topic {} with key {}", topic, aggregateId);
                }
            });

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing message envelope", e);
        }
    }

    private String determineTopic(String aggregateType) {
        // Logica per determinare il topic in base al tipo di aggregato
        // Ad esempio, "orders" per "com.example.outbox.domain.model.Order", "payments" per "Payment", ecc.
        return aggregateType.toLowerCase() + "s";
    }

    // Classe interna per rappresentare il messaggio inviato
    private static class MessageEnvelope {
        private final String messageId;
        private final String eventType;
        private final String aggregateType;
        private final String aggregateId;
        private final String timestamp;
        private final String payload;

        public MessageEnvelope(String messageId, String eventType, String aggregateType,
                               String aggregateId, String timestamp, String payload) {
            this.messageId = messageId;
            this.eventType = eventType;
            this.aggregateType = aggregateType;
            this.aggregateId = aggregateId;
            this.timestamp = timestamp;
            this.payload = payload;
        }

        // Getters e setters necessari per la serializzazione
        // ...
    }
}
