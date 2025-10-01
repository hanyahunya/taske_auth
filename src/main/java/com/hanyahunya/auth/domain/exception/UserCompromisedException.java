package com.hanyahunya.auth.domain.exception;

import lombok.Getter;

public class UserCompromisedException extends RuntimeException {
    @Getter
    private final String token;

    public UserCompromisedException(String message, String token) {
        super(message);
        this.token = token;
    }
}
