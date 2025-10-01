package com.hanyahunya.auth.application.port.in;

import com.hanyahunya.auth.application.command.LoginCommand;
import com.hanyahunya.auth.application.command.SignupCommand;
import com.hanyahunya.auth.application.command.ValidateTfaCommand;
import com.hanyahunya.auth.application.dto.Tokens;

public interface AuthService {
    void signUp(SignupCommand signupCommand);
    void completeSignup(String verificationCode);

    Tokens login(LoginCommand command);

    Tokens validateTfa(ValidateTfaCommand command);
    
    // todo: 소셜로그인 추가. provider, 고유 id 값이 없을경우 회원가입처리. 있을경우 연결된 User 객체 불러와서 처리
    // todo: 소셜로그인용 회원가입 추가 (이메일, 비밀번호 없음 )
}