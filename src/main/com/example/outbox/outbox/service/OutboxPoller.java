package com.example.outbox.outbox.service;

import com.example.outbox.outbox.model.OutboxEvent;
import com.example.outbox.outbox.repository.OutboxRepository;

@Component
public class OutboxPoller {
    private static final Logger log = LoggerFactory.getLogger(OutboxPoller.class);
    private static final int BATCH_SIZE = 100;

    private final OutboxRepository outboxRepository;
    private final MessagePublisher messagePublisher;
    private final EventSerializer eventSerializer;

    @Autowired
    public OutboxPoller(OutboxRepository outboxRepository, MessagePublisher messagePublisher, EventSerializer eventSerializer) {
        this.outboxRepository = outboxRepository;
        this.messagePublisher = messagePublisher;
        this.eventSerializer = eventSerializer;
    }

    @Scheduled(fixedRate = 5000) // Esegue ogni 5 secondi
    @Transactional
    public void pollAndPublish() {
        log.debug("Polling outbox for events...");

        // 1. Recupera eventi non processati
        List<OutboxEvent> events = outboxRepository.findUnprocessedEvents(BATCH_SIZE);

        if (events.isEmpty()) {
            return;
        }

        log.info("Found {} unprocessed events", events.size());
        List<UUID> processedEventIds = new ArrayList<>();

        for (OutboxEvent event : events) {
            try {
                // 2. Pubblica l'evento
                messagePublisher.publish(
                        event.getEventType(),
                        event.getAggregateType(),
                        event.getAggregateId(),
                        event.getPayload()
                );

                // 3. Aggiunge l'ID alla lista degli eventi processati
                processedEventIds.add(event.getId());

            } catch (Exception e) {
                // In caso di errore, l'evento rimarrà non processato
                // e verrà riprovato nel prossimo ciclo
                log.error("Failed to process event {}", event.getId(), e);

                // Interrompe il ciclo per evitare di processare eventi in ordine non corretto
                break;
            }
        }

        // 4. Marca gli eventi come processati in batch
        if (!processedEventIds.isEmpty()) {
            outboxRepository.markAsProcessed(processedEventIds, LocalDateTime.now());
            log.info("Marked {} events as processed", processedEventIds.size());
        }
    }
}
