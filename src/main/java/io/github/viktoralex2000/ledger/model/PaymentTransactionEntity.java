package io.github.viktoralex2000.ledger.model;

import io.github.viktoralex2000.ledger.model.enums.PaymentStatus;
import io.github.viktoralex2000.ledger.model.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
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

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 16)
    private PaymentType type;
    //* type : VARCHAR(16)

    @Column(name = "amount", nullable = false, precision = 28, scale = 12)
    private BigDecimal amount;
    //* amount : NUMERIC(28,12)

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;
    //* currency : VARCHAR(3)

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private PaymentStatus status = PaymentStatus.PENDING;
    //* status : VARCHAR(16)

    @Column(name = "external_payment_id", length = 64)
    private String externalPaymentId;
    //external_payment_id : VARCHAR(64)

    @Column(name = "failure_reason", length = 1000)
    private String failureReason;
    //failure_reason : VARCHAR(1000)

    @Column(name = "attempts", nullable = false)
    private int attempts = 0;
    //* attempts : INTEGER

    @Version
    @Column(name = "version", nullable = false)
    private long version;
    //* version : BIGINT

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    //* created_at : TIMESTAMPTZ

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    //* updated_at : TIMESTAMPTZ


    public void setId(Long id) {
        this.id = id;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setWallet(WalletEntity wallet) {
        this.wallet = wallet;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setExternalPaymentId(String externalPaymentId) {
        this.externalPaymentId = externalPaymentId;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    @PrePersist
    private void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = Instant.now();
    }
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