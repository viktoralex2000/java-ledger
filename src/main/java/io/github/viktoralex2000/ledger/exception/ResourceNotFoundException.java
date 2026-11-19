package io.github.viktoralex2000.ledger.exception;

import org.springframework.http.HttpStatus;


public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
/*
ResourceNotFoundException
+ResourceNotFoundException(String) «404»
 */