package io.github.viktoralex2000.ledger.service;

import io.github.viktoralex2000.ledger.model.LedgerEntryEntity;
import io.github.viktoralex2000.ledger.model.WalletEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class LedgerService {

    public BigDecimal currentBalance(WalletEntity wallet) {
        return null;
    }

    public BigDecimal post(
            WalletEntity wallet,
            LedgerEntryEntity.EntryType entryType,
            BigDecimal amount,
            String description,
            String operationKey) {
        return null;
    }

}
