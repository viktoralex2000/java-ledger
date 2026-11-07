package io.github.viktoralex2000.ledger.service;

import io.github.viktoralex2000.ledger.dto.TransferDto;
import io.github.viktoralex2000.ledger.dto.TransferRequest;
import io.github.viktoralex2000.ledger.model.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransferService {

    public TransferDto transfer(UserEntity user, TransferRequest transferRequest) {
        return null;
    }

    public TransferDto get(Long transferId, Long userId) {
        return null;
    }

    public List<TransferDto> list(Long userId) {
        return null;
    }

    public List<TransferDto> listAll() {
        return null;
    }

}
