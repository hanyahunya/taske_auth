package com.hanyahunya.auth.domain.exception;

import lombok.Getter;

import java.util.UUID;

public class TokenCompromisedException extends RuntimeException {
    @Getter
    private final UUID userId;

    public TokenCompromisedException(String message, UUID userId) {
        super(message);
        this.userId = userId;
    }
}
