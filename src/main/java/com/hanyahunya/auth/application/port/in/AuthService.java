package com.hanyahunya.auth.application.port.in;

import com.hanyahunya.auth.adapter.in.web.dto.LoginDto;
import com.hanyahunya.auth.adapter.in.web.dto.SignupDto;
import com.hanyahunya.auth.application.dto.Tokens;

public interface AuthService {
    void signUp(SignupDto signupDto);
    void completeSignup(String verificationCode);

    Tokens login(LoginDto loginDto);
}