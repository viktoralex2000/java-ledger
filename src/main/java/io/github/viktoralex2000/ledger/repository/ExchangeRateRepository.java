package io.github.viktoralex2000.ledger.repository;

import io.github.viktoralex2000.ledger.model.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, Long> {

    Optional<ExchangeRateEntity> findByBaseCurrencyAndQuoteCurrency(
            String baseCurrency, String quoteCurrency);

    List<ExchangeRateEntity> findByBaseCurrencyOrderByQuoteCurrency(String baseCurrency);
}
