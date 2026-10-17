package io.github.viktoralex2000.ledger.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;
    //* currency : VARCHAR(3)

    @Column(name = "balance", nullable = false, precision = 28, scale = 12)
    private BigDecimal balance;
    //* balance : NUMERIC(28,12)

    @Column(name = "status", nullable = false, length = 16)
    private String status;
    //* status : VARCHAR(16)

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
    //* version : BIGINT

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    //* created_at : TIMESTAMPTZ

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