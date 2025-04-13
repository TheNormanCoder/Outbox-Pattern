package com.example.outbox.outbox.repository;

// Repository per gli eventi dell'Outbox
public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {

    @Query("SELECT o FROM com.example.outbox.outbox.model.OutboxEvent o WHERE o.processed = false ORDER BY o.createdAt ASC LIMIT :limit")
    List<OutboxEvent> findUnprocessedEvents(@Param("limit") int limit);

    @Modifying
    @Query("UPDATE com.example.outbox.outbox.model.OutboxEvent o SET o.processed = true, o.processedAt = :now WHERE o.id IN :ids")
    void markAsProcessed(@Param("ids") List<UUID> ids, @Param("now") LocalDateTime now);
}