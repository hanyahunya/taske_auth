package com.hanyahunya.auth.application.port.out;

import com.hanyahunya.auth.adapter.in.web.dto.SignupDto;

import java.util.Optional;

public interface VerificationPort {
    String createTemporaryUser(SignupDto signupDto);

    Optional<SignupDto> findTemporaryUserByCode(String verificationCode);

    boolean isCooldown(String email);

    void deleteVerificationCode(String verificationCode);
}
