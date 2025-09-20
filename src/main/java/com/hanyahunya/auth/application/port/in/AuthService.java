package com.hanyahunya.auth.application.port.in;

import com.hanyahunya.auth.adapter.in.web.dto.LoginDto;
import com.hanyahunya.auth.adapter.in.web.dto.SignupDto;
import com.hanyahunya.auth.application.dto.Tokens;

public interface AuthService {
    void signUp(SignupDto signupDto);
    void completeSignup(String verificationCode);
    void cleanupUnverifiedUsers();

    Tokens login(LoginDto loginDto);
    
    // todo: 소셜로그인 추가. provider, 고유 id 값이 없을경우 회원가입처리. 있을경우 연결된 User 객체 불러와서 처리
    // todo: 소셜로그인용 회원가입 추가 (이메일, 비밀번호 없음 )
}