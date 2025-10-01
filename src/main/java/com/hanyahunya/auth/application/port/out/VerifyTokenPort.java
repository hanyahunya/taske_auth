package com.hanyahunya.auth.application.port.out;

public interface VerifyTokenPort {
    String issueToken(String email);

    String getEmailFromToken(String token);
}
