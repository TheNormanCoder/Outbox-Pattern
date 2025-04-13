package com.example.outbox.messagging;

import com.example.outbox.domain.event.OrderCreatedEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class OrderEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final EventSerializer eventSerializer;

    @Autowired
    public OrderEventConsumer(EventSerializer eventSerializer) {
        this.eventSerializer = eventSerializer;
    }

    @KafkaListener(topics = "orders", groupId = "inventory-service")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            // 1. Estrai il payload dall'envelope
            JsonNode rootNode = new ObjectMapper().readTree(record.value());
            String eventType = rootNode.get("eventType").asText();
            String payload = rootNode.get("payload").toString();

            // 2. Elabora l'evento in base al tipo
            switch (eventType) {
                case "OrderCreated":
                    processOrderCreatedEvent(eventSerializer.deserialize(payload, OrderCreatedEvent.class));
                    break;
                // Altri tipi di eventi...
                default:
                    log.warn("Unknown event type: {}", eventType);
            }

        } catch (Exception e) {
            log.error("Error processing event", e);
            // Gestione degli errori: potrebbe essere necessario un meccanismo di dead-letter
        }
    }

    private void processOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Processing com.example.outbox.domain.event.OrderCreatedEvent for orderId: {}", event.getOrderId());

        // Logica di business specifica per l'evento OrderCreated
        // Ad esempio, aggiornamento dell'inventario

        log.info("Successfully processed com.example.outbox.domain.event.OrderCreatedEvent for orderId: {}", event.getOrderId());
    }
}