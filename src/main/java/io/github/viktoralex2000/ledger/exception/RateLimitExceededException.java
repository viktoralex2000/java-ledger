package io.github.viktoralex2000.ledger.exception;

import org.springframework.http.HttpStatus;


public class RateLimitExceededException extends ApiException {

    public RateLimitExceededException(String message) {
        super(HttpStatus.TOO_MANY_REQUESTS, message);
    }
}
/*
RateLimitExceededException
+RateLimitExceededException(String) «429
 */