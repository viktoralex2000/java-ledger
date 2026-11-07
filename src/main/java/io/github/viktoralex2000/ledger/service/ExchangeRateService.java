package io.github.viktoralex2000.ledger.service;

import io.github.viktoralex2000.ledger.dto.ExchangeRateDto;
import io.github.viktoralex2000.ledger.dto.ExchangeRateUpdateRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExchangeRateService {

    public List<ExchangeRateDto> list(String baseCurrency) {
        return null;
    }

    public ExchangeRateDto upsert(ExchangeRateUpdateRequest exchangeRateUpdateRequest) {
        return null;
    }

    public void delete(String baseCurrency, String quoteCurrency) {
    }

}
