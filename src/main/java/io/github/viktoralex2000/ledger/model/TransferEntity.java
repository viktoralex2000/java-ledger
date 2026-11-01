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
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;

@Getter
@Entity
@Table(name = "transfers")
public class TransferEntity {

	public enum TransferStatus {PENDING, COMPLETED, ROLLED_BACK, FAILED}
	
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

    

    @PrePersist
    private void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
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



	public WalletEntity getFromWallet() {
		return fromWallet;
	}



	public void setFromWallet(WalletEntity fromWallet) {
		this.fromWallet = fromWallet;
	}



	public WalletEntity getToWallet() {
		return toWallet;
	}



	public void setToWallet(WalletEntity toWallet) {
		this.toWallet = toWallet;
	}



	public String getCurrency() {
		return currency;
	}



	public void setCurrency(String currency) {
		this.currency = currency;
	}



	public BigDecimal getAmount() {
		return amount;
	}



	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}



	public BigDecimal getConvertedAmount() {
		return convertedAmount;
	}



	public void setConvertedAmount(BigDecimal convertedAmount) {
		this.convertedAmount = convertedAmount;
	}



	public BigDecimal getFxRate() {
		return fxRate;
	}



	public void setFxRate(BigDecimal fxRate) {
		this.fxRate = fxRate;
	}



	public BigDecimal getFee() {
		return fee;
	}



	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}



	public TransferStatus getStatus() {
		return status;
	}



	public void setStatus(TransferStatus status) {
		this.status = status;
	}



	public String getErrorMessage() {
		return errorMessage;
	}



	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
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



	public Instant getCompletedAt() {
		return completedAt;
	}



	public void setCompletedAt(Instant completedAt) {
		this.completedAt = completedAt;
	}



	public Long getId() {
		return id;
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