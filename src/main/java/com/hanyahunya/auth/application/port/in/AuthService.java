package com.hanyahunya.auth.application.port.in;

import com.hanyahunya.auth.application.web.dto.SignupDto;

public interface AuthService {
    void signUp(SignupDto signupDto);
    void completeSignup(String verificationCode);
}