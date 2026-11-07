package io.github.viktoralex2000.ledger.controller;

import io.github.viktoralex2000.ledger.dto.CreateWalletRequest;
import io.github.viktoralex2000.ledger.dto.LedgerEntryDto;
import io.github.viktoralex2000.ledger.dto.PageResponse;
import io.github.viktoralex2000.ledger.dto.WalletDto;
import io.github.viktoralex2000.ledger.repository.LedgerEntryRepository;
import io.github.viktoralex2000.ledger.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;
    private final LedgerEntryRepository ledgerEntryRepository;

    @PostMapping("/create")
    public ResponseEntity<WalletDto> create(
            /*UserEntity user,*/
            @Valid @RequestBody CreateWalletRequest createWalletRequest) {
        return null;
    }

    @GetMapping("/list")
    public ResponseEntity<List<WalletDto>> list(/*UserEntity user*/) {
        return null;
    }

    @GetMapping("/get/{walletId}")
    public ResponseEntity<WalletDto> get(
            /*UserEntity user*/
            @PathVariable Long walletId) {
        return null;
    }

    @GetMapping("/ledger/{walletId}")
    public ResponseEntity<PageResponse<LedgerEntryDto>> ledger(
            /*UserEntity user*/
            @PathVariable Long walletId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return null;
    }
}
