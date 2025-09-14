package com.hanyahunya.auth.mail;

public interface MailService {
    void sendVerificationEmail(String email, String verificationCode);
}
