package io.github.viktoralex2000.ledger.model;

import io.github.viktoralex2000.ledger.model.enums.TransferStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Entity
@Table(name = "transfers")
public class TransferEntity {

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
    @JoinColumn(name = "from_wallet_id")
    private WalletEntity fromWallet;
    //from_wallet_id : BIGINT «FK»

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_wallet_id")
    private WalletEntity toWallet;
    //to_wallet_id : BIGINT «FK»

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;
    //* currency : VARCHAR(3)

    @Column(name = "amount", nullable = false, precision = 28, scale = 12)
    private BigDecimal amount;
    //* amount : NUMERIC(28,12)

    @Column(name = "converted_amount", precision = 28, scale = 12)
    private BigDecimal convertedAmount;
    //converted_amount : NUMERIC(28,12)

    @Column(name = "fx_rate", precision = 28, scale = 12)
    private BigDecimal fxRate;
    //fx_rate : NUMERIC(28,12)

    @Column(name = "fee", nullable = false, precision = 28, scale = 12)
    private BigDecimal fee = BigDecimal.ZERO;
    //* fee : NUMERIC(28,12)

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private TransferStatus status = TransferStatus.PENDING;
    //* status : VARCHAR(16)

    @Column(name = "error_message", length = 1000)
    private String errorMessage;
    //error_message : VARCHAR(1000)

    @Version
    @Column(name = "version", nullable = false)
    private long version;
    //* version : BIGINT

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    //* created_at : TIMESTAMPTZ

    @Column(name = "completed_at")
    private Instant completedAt;
    //completed_at : TIMESTAMPTZ

    public void setId(Long id) {
        this.id = id;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setFromWallet(WalletEntity fromWallet) {
        this.fromWallet = fromWallet;
    }

    public void setToWallet(WalletEntity toWallet) {
        this.toWallet = toWallet;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setConvertedAmount(BigDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public void setFxRate(BigDecimal fxRate) {
        this.fxRate = fxRate;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    @PrePersist
    private void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
    }
}

/*
    CREATE TABLE IF NOT EXISTS transfers (
        id BIGSERIAL PRIMARY KEY,
        idempotency_key VARCHAR(64) NOT NULL UNIQUE,
        user_id BIGINT NOT NULL REFERENCES users(id),
        from_wallet_id BIGINT REFERENCES wallets(id),
        to_wallet_id BIGINT REFERENCES wallets(id),
        currency VARCHAR(3) NOT NULL,
        amount NUMERIC(28,12) NOT NULL,
        converted_amount NUMERIC(28,12),
        fx_rate NUMERIC(28,12),
        fee NUMERIC(28,12) NOT NULL DEFAULT 0,
        status VARCHAR(16) NOT NULL,
        error_message VARCHAR(1000),
        version BIGINT NOT NULL DEFAULT 0,
        created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
        completed_at TIMESTAMPTZ
    );
*/