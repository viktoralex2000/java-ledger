package io.github.viktoralex2000.ledger.model;

import io.github.viktoralex2000.ledger.model.enums.WalletStatus;
import jakarta.persistence.*;
import lombok.*;
<<<<<<< HEAD

import org.hibernate.annotations.CreationTimestamp;
=======
>>>>>>> branch 'create-the-project-structure' of https://github.com/viktoralex2000/java-ledger.git

import java.math.BigDecimal;
import java.time.Instant;
<<<<<<< HEAD
import java.time.OffsetDateTime;
=======
>>>>>>> branch 'create-the-project-structure' of https://github.com/viktoralex2000/java-ledger.git

<<<<<<< HEAD
=======
@Getter
>>>>>>> branch 'create-the-project-structure' of https://github.com/viktoralex2000/java-ledger.git
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

<<<<<<< HEAD
    @Column(nullable = false, length = 3, updatable = false)
=======
    @Column(name = "currency", nullable = false, updatable = false, length = 3)
>>>>>>> branch 'create-the-project-structure' of https://github.com/viktoralex2000/java-ledger.git
    private String currency;

<<<<<<< HEAD
    @Column(nullable = false, precision = 28, scale = 12)
    private BigDecimal balance = BigDecimal.ZERO;
=======
    @Column(name = "balance", nullable = false, precision = 28, scale = 12)
    private BigDecimal balance = BigDecimal.ZERO;
    //* balance : NUMERIC(28,12)
>>>>>>> branch 'create-the-project-structure' of https://github.com/viktoralex2000/java-ledger.git

    @Enumerated(EnumType.STRING)
<<<<<<< HEAD
    @Column(nullable = false, length = 16)
    private Status status = Status.ACTIVE;
=======
    @Column(name = "status", nullable = false, length = 16)
    private WalletStatus status = WalletStatus.ACTIVE;
    //* status : VARCHAR(16)
>>>>>>> branch 'create-the-project-structure' of https://github.com/viktoralex2000/java-ledger.git

    @Version
<<<<<<< HEAD
    @Column(nullable = false)
    private long version;
=======
    @Column(name = "version", nullable = false)
    private long version;
    //* version : BIGINT
>>>>>>> branch 'create-the-project-structure' of https://github.com/viktoralex2000/java-ledger.git

    @Column(name = "created_at", nullable = false, updatable = false)
<<<<<<< HEAD
    private Instant createdAt = Instant.now();
=======
    private Instant createdAt;
    //* created_at : TIMESTAMPTZ
>>>>>>> branch 'create-the-project-structure' of https://github.com/viktoralex2000/java-ledger.git

<<<<<<< HEAD
    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
=======

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

>>>>>>> branch 'create-the-project-structure' of https://github.com/viktoralex2000/java-ledger.git
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