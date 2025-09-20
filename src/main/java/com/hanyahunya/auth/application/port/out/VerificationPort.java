package com.hanyahunya.auth.application.port.out;

import java.util.Optional;
import java.util.UUID;

public interface VerificationPort {
    String createVerificationCode(UUID userId);
    Optional<String> getUserIdByVerificationCode(String verificationCode);
    void deleteVerificationCode(String verificationCode);
}
