package com.example.outbox.domain.event;

// Classe base per gli eventi di dominio
public abstract class DomainEvent {
    private final UUID eventId;
    private final LocalDateTime timestamp;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
    }

    public UUID getEventId() {
        return eventId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}