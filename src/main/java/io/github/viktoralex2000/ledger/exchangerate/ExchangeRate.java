package io.github.viktoralex2000.ledger.exchangerate;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "exchange_rates",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_base_currency_quote_currency",
                columnNames = {"base_currency", "quote_currency"}
        )
)
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //* id : BIGINT «PK»

    @Column(name = "base_currency", nullable = false, length = 3)
    private String baseCurrency;
    //* base_currency : VARCHAR(3)

    @Column(name = "quote_currency", nullable = false, length = 3)
    private String quoteCurrency;
    //* quote_currency : VARCHAR(3)

    @Column(name = "rate", precision = 28, scale = 12)
    private BigDecimal rate;
    //* rate : NUMERIC(28,12)

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
    //* updated_at : TIMESTAMPTZ
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
