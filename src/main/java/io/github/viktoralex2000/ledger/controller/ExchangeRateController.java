package io.github.viktoralex2000.ledger.controller;

import io.github.viktoralex2000.ledger.dto.ExchangeRateDto;
import io.github.viktoralex2000.ledger.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class ExchangeRateController {
    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }
    
    @GetMapping("/list")
    public ResponseEntity<List<ExchangeRateDto>> list(
            @RequestParam String baseCurrency) {
        return null;
    }
}
