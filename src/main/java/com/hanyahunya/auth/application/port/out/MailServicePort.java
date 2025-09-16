package com.hanyahunya.auth.application.port.out;

public interface MailServicePort {
    void sendVerificationEmail(String email, String verificationCode, String locale);
}