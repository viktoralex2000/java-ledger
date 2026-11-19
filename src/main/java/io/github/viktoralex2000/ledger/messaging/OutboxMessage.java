package io.github.viktoralex2000.ledger.messaging;

import java.time.Instant;


public record OutboxMessage(
        String eventId,
        String aggregateType,
        String aggregateId,
        String eventType,
        Object payload,
        Instant occurredAt) {
}
