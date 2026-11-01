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

@Entity
@Table(name = "wallets")
public class WalletEntity {

	public enum WalletStatus {ACTIVE, FROZEN, CLOSED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;


   
    @Column(name = "currency", nullable = false, updatable = false, length = 3)

    private String currency;


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

    


   

    @PrePersist
    private void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
    }






	public UserEntity getUser() {
		return user;
	}






	public void setUser(UserEntity user) {
		this.user = user;
	}






	public String getCurrency() {
		return currency;
	}






	public void setCurrency(String currency) {
		this.currency = currency;
	}






	public BigDecimal getBalance() {
		return balance;
	}






	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}






	public WalletStatus getStatus() {
		return status;
	}






	public void setStatus(WalletStatus status) {
		this.status = status;
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