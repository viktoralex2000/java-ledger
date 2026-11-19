package io.github.viktoralex2000.ledger.exception;

import org.springframework.http.HttpStatus;


public class InsufficientFundsException extends ApiException {

    public InsufficientFundsException(String message) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, message);
    }
}
/*
InsufficientFundsException
+InsufficientFundsException(String) «422»
 */