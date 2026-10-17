package io.github.viktoralex2000.ledger.outboxevent;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

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

    @Column(name = "status", nullable = false, length = 16)
    private String status;
    //* status : VARCHAR(16)

    @Column(name = "attempts", nullable = false)
    private int attempts;
    //* attempts : INTEGER

    @Column(name = "available_at", nullable = false)
    private OffsetDateTime availableAt;
    //*available_at : TIMESTAMPTZ

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    //* created_at : TIMESTAMPTZ

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;
    //published_at : TIMESTAMPTZ

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
    //* version : BIGINT

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