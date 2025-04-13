package com.example.outbox.messagging;

// Interfaccia per il publisher dei messaggi
public interface MessagePublisher {
    void publish(String eventType, String aggregateType, String aggregateId, String payload);
}