package io.github.viktoralex2000.ledger.service;

import io.github.viktoralex2000.ledger.dto.CreateWalletRequest;
import io.github.viktoralex2000.ledger.dto.LedgerEntryDto;
import io.github.viktoralex2000.ledger.dto.PageResponse;
import io.github.viktoralex2000.ledger.dto.WalletDto;
import io.github.viktoralex2000.ledger.model.WalletEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletService {
    public WalletDto create(Long userId, CreateWalletRequest createWalletRequest) {
        return null;
    }

    public List<WalletDto> list(Long userId) {
        return null;
    }

    public WalletDto get(Long walletId, Long userId) {
        return null;
    }

    public PageResponse<LedgerEntryDto> ledger(
            Long userId, Long walletId, int page, int size) {
        return null;
    }

    public WalletEntity requireOwned(Long walletId, Long userId) {
        return null;
    }

    public WalletEntity findOwned(Long walletId, Long userId) {
        return null;
    }


}
