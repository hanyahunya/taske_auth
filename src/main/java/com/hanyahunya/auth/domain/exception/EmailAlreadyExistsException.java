package com.hanyahunya.auth.domain.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("常に登録されたメールです。");
    }
}
