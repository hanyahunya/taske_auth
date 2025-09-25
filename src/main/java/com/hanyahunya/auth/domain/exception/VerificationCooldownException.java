package com.hanyahunya.auth.domain.exception;

public class VerificationCooldownException extends RuntimeException {
    public VerificationCooldownException(String message) {
        super(message);
    }
}
