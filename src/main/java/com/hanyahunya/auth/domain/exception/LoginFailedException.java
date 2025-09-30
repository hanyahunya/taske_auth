package com.hanyahunya.auth.domain.exception;

public class LoginFailedException extends RuntimeException {
    public LoginFailedException() {
        super("Failed to login");
    }
}
