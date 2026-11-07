package io.github.viktoralex2000.ledger.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class IdempotencyService {

    //todo Разобрать отдельно
    public ResponseEntity<?> execute(Long userId, String idempotencyKey, Supplier<ResponseEntity<?>> action) {
        return null;
    }
}
