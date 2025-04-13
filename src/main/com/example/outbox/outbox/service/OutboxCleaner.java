package com.example.outbox.outbox.service;

@Component
public class OutboxCleaner {
    private static final Logger log = LoggerFactory.getLogger(OutboxCleaner.class);

    private final EntityManager entityManager;

    @Autowired
    public OutboxCleaner(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Ogni giorno a mezzanotte
    @Transactional
    public void cleanOutbox() {
        log.info("Starting outbox cleanup job");

        // Calcola la data limite (es. eventi più vecchi di 7 giorni)
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);

        // Elimina gli eventi già processati più vecchi della data limite
        int deleted = entityManager.createQuery(
                        "DELETE FROM com.example.outbox.outbox.model.OutboxEvent o WHERE o.processed = true AND o.processedAt < :cutoffDate")
                .setParameter("cutoffDate", cutoffDate)
                .executeUpdate();

        log.info("Deleted {} processed events from outbox", deleted);
    }
}
