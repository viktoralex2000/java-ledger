package io.github.viktoralex2000.ledger.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    protected final HttpStatus status;

    protected ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
/*
ApiException
#status: HttpStatus
+ApiException(HttpStatus, String) «protected»
+getStatus(): HttpStatus
 */