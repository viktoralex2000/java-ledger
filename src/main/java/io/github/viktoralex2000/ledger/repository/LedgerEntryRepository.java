package io.github.viktoralex2000.ledger.repository;

import io.github.viktoralex2000.ledger.model.LedgerEntryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntryEntity, Long> {

    Page<LedgerEntryEntity> findByWalletIdOrderByCreatedAtDescIdDesc(Long walletId, Pageable page);

    Optional<LedgerEntryEntity> findFirstByWalletIdOrderByIdDesc(Long walletId);

    boolean existsByWalletIdAndOperationKey(Long walletId, String operationKey);

}
