package com.hanyahunya.auth.domain.exception;

public class InvalidVerificationCodeException extends RuntimeException {
    public InvalidVerificationCodeException() {
      super("無効なメール認証コードです。");
    }
    public InvalidVerificationCodeException(String message) {
        super(message);
    }
}
