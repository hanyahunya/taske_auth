package com.hanyahunya.auth.domain.exception;

public class UserPendingVerificationException extends RuntimeException {
    public UserPendingVerificationException(String message) {
        super(message);
    }
}
