CREATE TABLE outbox (
                        id UUID PRIMARY KEY,
                        aggregate_type VARCHAR(255) NOT NULL,
                        aggregate_id VARCHAR(255) NOT NULL,
                        event_type VARCHAR(255) NOT NULL,
                        payload JSONB NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        processed BOOLEAN NOT NULL DEFAULT FALSE,
                        processed_at TIMESTAMP,

    -- Indici per migliorare le performance
                        INDEX idx_outbox_processed_created (processed, created_at),
                        INDEX idx_outbox_aggregate (aggregate_type, aggregate_id)
);