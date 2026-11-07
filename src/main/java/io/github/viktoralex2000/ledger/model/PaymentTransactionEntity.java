package io.github.viktoralex2000.ledger.model;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;

@Getter
@Entity
@Table(name = "payment_transactions")
public class PaymentTransactionEntity {

	 public enum PaymentType {TOP_UP, WITHDRAW}

	 public enum PaymentStatus {PENDING, COMPLETED, FAILED, ROLLED_BACK}
	
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

    @PrePersist
    private void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public WalletEntity getWallet() {
		return wallet;
	}

	public void setWallet(WalletEntity wallet) {
		this.wallet = wallet;
	}

	public PaymentType getType() {
		return type;
	}

	public void setType(PaymentType type) {
		this.type = type;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentStatus status) {
		this.status = status;
	}

	public String getExternalPaymentId() {
		return externalPaymentId;
	}

	public void setExternalPaymentId(String externalPaymentId) {
		this.externalPaymentId = externalPaymentId;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
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