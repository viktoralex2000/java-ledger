package io.github.viktoralex2000.ledger.service;

import io.github.viktoralex2000.ledger.exception.ConflictException;
import io.github.viktoralex2000.ledger.model.LedgerEntryEntity;
import io.github.viktoralex2000.ledger.model.WalletEntity;
import io.github.viktoralex2000.ledger.repository.LedgerEntryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class LedgerService {

    private final LedgerEntryRepository ledgerEntryRepository;

    public LedgerService(LedgerEntryRepository ledgerEntryRepository) {
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Transactional(readOnly = true)
    public BigDecimal currentBalance(WalletEntity wallet) {
        return ledgerEntryRepository.findFirstByWalletIdOrderByIdDesc(wallet.getId())
                .map(LedgerEntryEntity::getBalanceAfter)
                .orElse(BigDecimal.ZERO);
    }

   
    @Transactional
    public BigDecimal post(WalletEntity wallet, LedgerEntryEntity.EntryType type, BigDecimal amount, String operationKey,
                           String description) {
        if (ledgerEntryRepository.existsByWalletIdAndOperationKey(wallet.getId(), operationKey)) {
            throw new ConflictException("Ledger entry already exists for wallet " + wallet.getId()
                    + " and operation " + operationKey);
        }
        BigDecimal current = wallet.getBalance();
        BigDecimal balanceAfter = (type == LedgerEntryEntity.EntryType.CREDIT)
                ? current.add(amount)
                : current.subtract(amount);

        LedgerEntryEntity entry = new LedgerEntryEntity();
        entry.setWallet(wallet);
        entry.setEntryType(type);
        entry.setAmount(amount);
        entry.setBalanceAfter(balanceAfter);
        entry.setCurrency(wallet.getCurrency());
        entry.setDescription(description);
        entry.setOperationKey(operationKey);
        ledgerEntryRepository.save(entry);

        wallet.setBalance(balanceAfter);
        return balanceAfter;
    }
}