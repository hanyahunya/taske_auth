package com.hanyahunya.auth.domain.exception;

public class InvalidTokenException extends RuntimeException {
  public InvalidTokenException() {
    super("認証トークンが無効か、または有効期限が切れています。");
  }
  public InvalidTokenException(String message) {
    super(message);
  }
}
