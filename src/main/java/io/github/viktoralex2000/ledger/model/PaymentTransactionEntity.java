package io.github.viktoralex2000.ledger.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payment_transactions")
public class PaymentTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //* id : BIGINT «PK»

    @Column(name = "idempotency_key", nullable = false, unique = true,length = 64)
    private String idempotencyKey;
    //* idempotency_key : VARCHAR(64) «UK»

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    //* user_id : BIGINT «FK»

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private WalletEntity wallet;
    //* wallet_id : BIGINT «FK»

    @Column(name = "type", nullable = false, length = 16)
    private String type;
    //* type : VARCHAR(16)

    @Column(name = "amount", nullable = false, precision = 28, scale = 12)
    private BigDecimal amount;
    //* amount : NUMERIC(28,12)

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;
    //* currency : VARCHAR(3)

    @Column(name = "status", nullable = false, length = 16)
    private String status;
    //* status : VARCHAR(16)

    @Column(name = "external_payment_id", length = 64)
    private String externalPaymentId;
    //external_payment_id : VARCHAR(64)

    @Column(name = "failure_reason", length = 1000)
    private String failureReason;
    //failure_reason : VARCHAR(1000)

    @Column(name = "attempts", nullable = false)
    private int attempts;
    //* attempts : INTEGER

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
    //* version : BIGINT

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    //* created_at : TIMESTAMPTZ

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
    //* updated_at : TIMESTAMPTZ
}

/*
    CREATE TABLE IF NOT EXISTS payment_transactions (
        id BIGSERIAL PRIMARY KEY,
        idempotency_key VARCHAR(64) NOT NULL UNIQUE,
        user_id BIGINT NOT NULL REFERENCES users(id),
        wallet_id BIGINT NOT NULL REFERENCES wallets(id),
        type VARCHAR(16) NOT NULL,
        amount NUMERIC(28,12) NOT NULL,
        currency VARCHAR(3) NOT NULL,
        status VARCHAR(16) NOT NULL,
        external_payment_id VARCHAR(64),
        failure_reason VARCHAR(1000),
        attempts INTEGER NOT NULL DEFAULT 0,
        version BIGINT NOT NULL DEFAULT 0,
        created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
        updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );
 */