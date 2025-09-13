package com.hanyahunya.auth.service;

import com.hanyahunya.auth.dto.SignupDto;

public interface AuthService {
    boolean signUp(SignupDto signupDto);
}
