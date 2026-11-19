package io.github.viktoralex2000.ledger.service;

import io.github.viktoralex2000.ledger.dto.ExchangeRateDto;
import io.github.viktoralex2000.ledger.dto.ExchangeRateUpdateRequest;
import io.github.viktoralex2000.ledger.exception.ConflictException;
import io.github.viktoralex2000.ledger.exception.ResourceNotFoundException;
import io.github.viktoralex2000.ledger.model.ExchangeRateEntity;
import io.github.viktoralex2000.ledger.repository.ExchangeRateRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final FxService fxService;

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository, FxService fxService) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.fxService = fxService;
    }

    @Transactional(readOnly = true)
    public List<ExchangeRateDto> list(String baseCurrency) {
        return exchangeRateRepository.findByBaseCurrencyOrderByQuoteCurrency(baseCurrency).stream()
                .map(ExchangeRateDto::from)
                .toList();
    }

    @Transactional
    public ExchangeRateDto upsert(ExchangeRateUpdateRequest request) {
        String base = request.baseCurrency().toUpperCase();
        String quote = request.quoteCurrency().toUpperCase();
        if (base.equals(quote)) {
            throw new ConflictException("Base and quote currencies must differ");
        }
        ExchangeRateEntity rate = exchangeRateRepository.findByBaseCurrencyAndQuoteCurrency(base, quote)
                .orElseGet(() -> {
                    ExchangeRateEntity r = new ExchangeRateEntity();
                    r.setBaseCurrency(base);
                    r.setQuoteCurrency(quote);
                    return r;
                });
        rate.setRate(request.rate());
        exchangeRateRepository.save(rate);
        fxService.refresh();
        return ExchangeRateDto.from(rate);
    }

    @Transactional
    public void delete(String baseCurrency, String quoteCurrency) {
        ExchangeRateEntity rate = exchangeRateRepository
                .findByBaseCurrencyAndQuoteCurrency(baseCurrency.toUpperCase(), quoteCurrency.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Exchange rate not found"));
        exchangeRateRepository.delete(rate);
        fxService.refresh();
    }
}
