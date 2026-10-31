package io.github.viktoralex2000.ledger.model;

import io.github.viktoralex2000.ledger.model.enums.WalletStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Entity
@Table(
    name = "wallets",
    uniqueConstraints = @UniqueConstraint(
            name = "uk_wallet_user_currency",
            columnNames = {"user_id", "currency"}
    )
)
//UK (user_id, currency)
public class WalletEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //* id : BIGINT «PK»

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    //* user_id : BIGINT «FK»

    @Column(name = "currency", nullable = false, updatable = false, length = 3)
    private String currency;
    //* currency : VARCHAR(3)

    @Column(name = "balance", nullable = false, precision = 28, scale = 12)
    private BigDecimal balance = BigDecimal.ZERO;
    //* balance : NUMERIC(28,12)

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private WalletStatus status = WalletStatus.ACTIVE;
    //* status : VARCHAR(16)

    @Version
    @Column(name = "version", nullable = false)
    private long version;
    //* version : BIGINT

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    //* created_at : TIMESTAMPTZ


    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setStatus(WalletStatus status) {
        this.status = status;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @PrePersist
    private void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
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