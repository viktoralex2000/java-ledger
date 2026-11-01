package io.github.viktoralex2000.ledger.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Getter
@Entity
@Table(name = "outbox_events")
public class OutboxEventEntity {

	public enum OutboxStatus {PENDING, PUBLISHED, FAILED}
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //* id : BIGINT «PK»

    @Column(name = "aggregate_type", nullable = false, length = 32)
    private String aggregateType;
    //* aggregate_type : VARCHAR(32)

    @Column(name = "aggregate_id", nullable = false, length = 64)
    private String aggregateId;
    //* aggregate_id : VARCHAR(64)

    @Column(name = "event_type", nullable = false, length = 64)
    private String eventType;
    //* event_type : VARCHAR(64)

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;
    //*payload : JSONB

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private OutboxStatus status = OutboxStatus.PENDING;
    //* status : VARCHAR(16)

    @Column(name = "attempts", nullable = false)
    private int attempts = 0;
    //* attempts : INTEGER

    @Column(name = "available_at", nullable = false)
    private Instant availableAt;
    //*available_at : TIMESTAMPTZ

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    //* created_at : TIMESTAMPTZ

    @Column(name = "published_at")
    private Instant publishedAt;
    //published_at : TIMESTAMPTZ

    @Version
    @Column(name = "version", nullable = false)
    private long version;
    //* version : BIGINT

    public void setId(Long id) {
        this.id = id;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void setStatus(OutboxStatus status) {
        this.status = status;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public void setAvailableAt(Instant availableAt) {
        this.availableAt = availableAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    @PrePersist
    private void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
    }
}

/*

    CREATE TABLE IF NOT EXISTS outbox_events (
        id BIGSERIAL PRIMARY KEY,
        aggregate_type VARCHAR(32) NOT NULL,
        aggregate_id VARCHAR(64) NOT NULL,
        event_type VARCHAR(64) NOT NULL,
        payload JSONB NOT NULL,
        status VARCHAR(16) NOT NULL,
        attempts INTEGER NOT NULL DEFAULT 0,
        available_at TIMESTAMPTZ NOT NULL,
        created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
        published_at TIMESTAMPTZ,
        version BIGINT NOT NULL DEFAULT 0
    );

 */