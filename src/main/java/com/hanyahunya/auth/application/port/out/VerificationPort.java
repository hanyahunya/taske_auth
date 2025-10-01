package com.hanyahunya.auth.application.port.out;

import com.hanyahunya.auth.application.command.SignupCommand;

import java.util.Optional;

public interface VerificationPort {
    String createTemporaryUser(String email, String password, String locale);

    Optional<SignupCommand> findTemporaryUserByCode(String verificationCode);

    boolean isCooldown(String email);

    void deleteVerificationCode(String verificationCode);

    void saveSecondFactorCode(String email, String code);
    boolean verifySecondFactorCode(String email, String submittedCode);
    void deleteSecondFactorCode(String email);
}
