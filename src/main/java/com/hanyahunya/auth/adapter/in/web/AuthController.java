package com.hanyahunya.auth.adapter.in.web;

import com.hanyahunya.auth.adapter.in.web.dto.LoginDto;
import com.hanyahunya.auth.adapter.in.web.dto.SignupDto;
import com.hanyahunya.auth.adapter.in.web.util.CookieUtil;
import com.hanyahunya.auth.application.command.LoginCommand;
import com.hanyahunya.auth.application.command.SignupCommand;
import com.hanyahunya.auth.application.dto.Tokens;
import com.hanyahunya.auth.application.port.in.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService; // In-Port 주입

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupDto signupDto) {

        SignupCommand signupCommand = new SignupCommand(signupDto.getEmail(), signupDto.getPassword(), signupDto.getLocale());

        authService.signUp(signupCommand);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify/{code}")
    public ResponseEntity<Void> verify(@PathVariable("code") String verificationCode) {
        authService.completeSignup(verificationCode);
        return ResponseEntity.ok().build();
    }

    // todo 로그인시 ACTIVE 가 아니면, 202 (요청은 수신했으나 처리가 완료되지 않음) 를 반환후 프론트에선 인증번호 입력 화면 보여주기. *redis에 5분간 유효한 데이터 저장. userId 같은거 + 지금 로그인한 기기에서만 작동하게
    // todo -> 이미 로그인 한 상태 id, pw 입력후 화면. 202 리턴시 적절한 값 (랜덤 String 같은거) 전달후 /login/verify 에서 해당 값도 받게하면 좋을듯
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginDto loginDto) {

        LoginCommand command = new LoginCommand(loginDto.getEmail(), loginDto.getPassword());

        Tokens tokens = authService.login(command);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokens.getAccessToken());
        ResponseCookie refreshTokenCookie = CookieUtil.createRefreshTokenCookie(tokens.getRefreshToken());
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok().headers(headers).build();

    }
}