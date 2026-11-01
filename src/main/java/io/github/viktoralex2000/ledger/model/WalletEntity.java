package io.github.viktoralex2000.ledger.model;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;

@Entity
@Table(name = "wallets")
public class WalletEntity {

    public enum Status { ACTIVE, FROZEN, CLOSED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, length = 3, updatable = false)
    private String currency;

    @Column(nullable = false, precision = 28, scale = 12)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status = Status.ACTIVE;

    @Version
    @Column(nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}

/*
    CREATE TABLE IF NOT EXISTS wallets (
        id BIGSERIAL PRIMARY KEY,
        user_id BIGINT NOT NULL REFERENCES users(id),
        currency VARCHAR(3) NOT NULL,
        balance NUMERIC(28,12) NOT NULL DEFAULT 0,
        status VARCHAR(16) NOT NULL,
        version BIGINT NOT NULL DEFAULT 0,
        created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
        CONSTRAINT uk_wallet_user_currency UNIQUE (user_id, currency)
    );
*/