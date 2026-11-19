package io.github.viktoralex2000.ledger.messaging;


import io.github.viktoralex2000.ledger.config.LedgerProperties;
import io.github.viktoralex2000.ledger.model.OutboxEventEntity;
import io.github.viktoralex2000.ledger.repository.OutboxEventRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;


@Component
public class OutboxPoller {

    private static final Logger log = LoggerFactory.getLogger(OutboxPoller.class);

    private final OutboxEventRepository outboxRepository;
    private final KafkaEventPublisher publisher;
    private final LedgerProperties properties;
    private final MeterRegistry meterRegistry;

    public OutboxPoller(OutboxEventRepository outboxRepository,
                        KafkaEventPublisher publisher,
                        LedgerProperties properties,
                        MeterRegistry meterRegistry) {
        this.outboxRepository = outboxRepository;
        this.publisher = publisher;
        this.properties = properties;
        this.meterRegistry = meterRegistry;
    }

    @Scheduled(fixedDelayString = "${ledger.outbox.poll-interval-ms}")
    @Transactional
    public void publishPending() {
        LedgerProperties.Outbox cfg = properties.outbox();
        List<OutboxEventEntity> batch = outboxRepository.lockNextBatch(Instant.now(), cfg.batchSize());
        for (OutboxEventEntity event : batch) {
            try {
                publisher.publish(event);
                event.setStatus(OutboxEventEntity.OutboxStatus.PUBLISHED);
                event.setPublishedAt(Instant.now());
                outboxRepository.save(event);
            } catch (Exception e) {
                event.setAttempts(event.getAttempts() + 1);
                if (event.getAttempts() >= cfg.maxAttempts()) {
                    event.setStatus(OutboxEventEntity.OutboxStatus.FAILED);
                    log.error("Outbox event {} permanently failed after {} attempts: {}",
                            event.getId(), event.getAttempts(), e.getMessage());
                } else {
                    event.setAvailableAt(Instant.now().plus(backoff(event.getAttempts())));
                    log.warn("Outbox event {} attempt {} failed, retrying in {}ms: {}",
                            event.getId(), event.getAttempts(), backoff(event.getAttempts()).toMillis(), e.getMessage());
                }
                outboxRepository.save(event);
            }
        }
        if (!batch.isEmpty()) {
            meterRegistry.counter("ledger.outbox.processed", "batch", String.valueOf(batch.size())).increment();
        }
    }

    private Duration backoff(int attempts) {
        long base = properties.outbox().pollIntervalMs();
        long delay = Math.min(base * (1L << Math.min(attempts, 6)), 60_000L);
        return Duration.ofMillis(delay);
    }
}
