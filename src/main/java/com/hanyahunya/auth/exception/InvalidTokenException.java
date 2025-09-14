package com.hanyahunya.auth.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super("認証トークンが無効か、または有効期限が切れています。");
    }
}
