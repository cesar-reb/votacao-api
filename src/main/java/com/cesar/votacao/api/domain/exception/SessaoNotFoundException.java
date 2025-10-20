package com.cesar.votacao.api.domain.exception;

public class SessaoNotFoundException extends RuntimeException {
    public SessaoNotFoundException(String message) {
        super(message);
    }
}
