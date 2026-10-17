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
@Table(name = "ledger_entries",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_wallet_operation_key",
                columnNames = {"wallet_id", "operation_key"}
        )
)
public class LedgerEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //* id : BIGINT «PK»

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private WalletEntity wallet;
    //* wallet_id : BIGINT «FK»

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_id")
    private TransferEntity transfer;
    //transfer_id : BIGINT «FK»

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private PaymentTransactionEntity payment;
    //payment_id : BIGINT «FK»

    @Column(name = "entry_type", nullable = false, length = 8)
    private String entryType;
    //* entry_type : VARCHAR(8)

    @Column(name = "amount", nullable = false, precision = 28, scale = 12)
    private BigDecimal amount;
    //* amount : NUMERIC(28,12)

    @Column(name = "balance_after", nullable = false, precision = 28, scale = 12)
    private BigDecimal balanceAfter;
    //*  balance_after : NUMERIC(28,12)

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;
    //*  currency : VARCHAR(3)

    @Column(name = "description", length = 500)
    private String description;
    //description : VARCHAR(500)

    @Column(name = "operation_key", nullable = false, length = 120)
    private String operationKey;
    //* operation_key : VARCHAR(120) «UK per wallet»

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    //* created_at : TIMESTAMPTZ

}

/*

    CREATE TABLE IF NOT EXISTS ledger_entries (
        id BIGSERIAL PRIMARY KEY,
        wallet_id BIGINT NOT NULL REFERENCES wallets(id),
        transfer_id BIGINT REFERENCES transfers(id),
        payment_id BIGINT REFERENCES payment_transactions(id),
        entry_type VARCHAR(8) NOT NULL,
        amount NUMERIC(28,12) NOT NULL,
        balance_after NUMERIC(28,12) NOT NULL,
        currency VARCHAR(3) NOT NULL,
        description VARCHAR(500),
        operation_key VARCHAR(120) NOT NULL,
        created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
        CONSTRAINT uk_wallet_operation_key UNIQUE (wallet_id, operation_key)
    );

 */