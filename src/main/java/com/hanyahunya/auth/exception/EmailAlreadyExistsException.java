package com.hanyahunya.auth.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("常に登録されたメールです。");
    }
}
