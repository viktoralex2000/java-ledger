package io.github.viktoralex2000.ledger.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Entity
@Table(name = "exchange_rates",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_base_currency_quote_currency",
                columnNames = {"base_currency", "quote_currency"}
        )
)
public class ExchangeRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //* id : BIGINT «PK»

    @Column(name = "base_currency", nullable = false, updatable = false, length = 3)
    private String baseCurrency;
    //* base_currency : VARCHAR(3)

    @Column(name = "quote_currency", nullable = false, updatable = false, length = 3)
    private String quoteCurrency;
    //* quote_currency : VARCHAR(3)

    @Column(name = "rate", nullable = false, precision = 28, scale = 12)
    private BigDecimal rate;
    //* rate : NUMERIC(28,12)

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    //* updated_at : TIMESTAMPTZ

    public void setId(Long id) {
        this.id = id;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @PrePersist
    @PreUpdate
    private void onPersist() {
        this.updatedAt = Instant.now();
    }
}

/*
    CREATE TABLE IF NOT EXISTS exchange_rates (
        id BIGSERIAL PRIMARY KEY,
        base_currency VARCHAR(3) NOT NULL,
        quote_currency VARCHAR(3) NOT NULL,
        rate NUMERIC(28,12) NOT NULL,
        updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
        CONSTRAINT uk_base_currency_quote_currency UNIQUE (base_currency, quote_currency)
    );
 */
