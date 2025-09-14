package com.hanyahunya.auth.main.service;

import com.hanyahunya.auth.main.dto.SignupDto;

public interface AuthService {
    void signUp(SignupDto signupDto);

    void completeSignup(String verificationCode);
}
