package io.github.viktoralex2000.ledger.controller;

import io.github.viktoralex2000.ledger.dto.ExchangeRateDto;
import io.github.viktoralex2000.ledger.dto.ExchangeRateUpdateRequest;
import io.github.viktoralex2000.ledger.dto.PaymentTransactionDto;
import io.github.viktoralex2000.ledger.dto.TransferDto;
import io.github.viktoralex2000.ledger.service.ExchangeRateService;
import io.github.viktoralex2000.ledger.service.PaymentSagaService;
import io.github.viktoralex2000.ledger.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//todo доделать безопасность и ROLE_ADMIN
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ExchangeRateService exchangeRateService;
    private final TransferService transferService;
    private final PaymentSagaService paymentSagaService;

    public AdminController(ExchangeRateService exchangeRateService,
            TransferService transferService,
            PaymentSagaService paymentSagaService) {
this.exchangeRateService = exchangeRateService;
this.transferService = transferService;
this.paymentSagaService = paymentSagaService;
}
    
    @PutMapping("/exchange-rates")
    public ResponseEntity<ExchangeRateDto> upsertRate(
            @Valid @RequestBody ExchangeRateUpdateRequest exchangeRateUpdateRequest) {
        return ResponseEntity.ok(exchangeRateService.upsert(exchangeRateUpdateRequest));
    }

    @DeleteMapping("/exchange-rates/{base}/{quote}")
   public ResponseEntity<Void> deleteRate(@PathVariable String base, @PathVariable String quote) {
        exchangeRateService.delete(base, quote);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/allTransfers")
    public ResponseEntity<List<TransferDto>> allTransfers() {
        return ResponseEntity.ok(transferService.listAll());
    }

    @GetMapping("/allPayments")
    public ResponseEntity<List<PaymentTransactionDto>> allPayments() {
        return ResponseEntity.ok(paymentSagaService.listAll());
    }
}
