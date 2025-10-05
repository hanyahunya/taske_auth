package com.hanyahunya.auth.adapter.in.web;

import com.hanyahunya.auth.adapter.in.web.dto.LoginDto;
import com.hanyahunya.auth.adapter.in.web.dto.SignupDto;
import com.hanyahunya.auth.adapter.in.web.dto.SocialLoginDto;
import com.hanyahunya.auth.adapter.in.web.dto.VerifyTfaDto;
import com.hanyahunya.auth.adapter.in.web.util.CookieUtil;
import com.hanyahunya.auth.application.command.LoginCommand;
import com.hanyahunya.auth.application.command.SignupCommand;
import com.hanyahunya.auth.application.command.SocialLoginCommand;
import com.hanyahunya.auth.application.command.ValidateTfaCommand;
import com.hanyahunya.auth.application.dto.Tokens;
import com.hanyahunya.auth.application.port.in.AuthService;
import com.hanyahunya.auth.domain.model.Provider;
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

    // todo 비밀번호 초기화.

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupDto requestDto) {

        SignupCommand signupCommand = new SignupCommand(requestDto.getEmail(), requestDto.getPassword(), requestDto.getLocale());

        authService.signUp(signupCommand);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify/{code}")
    public ResponseEntity<Void> verify(@PathVariable("code") String verificationCode) {
        authService.completeSignup(verificationCode);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginDto loginDto) {

        LoginCommand command = new LoginCommand(loginDto.getEmail(), loginDto.getPassword());

        Tokens tokens = authService.login(command);

        HttpHeaders headers = createAuthHeaders(tokens);

        return ResponseEntity.ok().headers(headers).build();
    }

    @PostMapping("/2fa/verify")
    public ResponseEntity<Void> verify2fa(@RequestAttribute("validatedEmail") String email, @RequestBody @Valid VerifyTfaDto requestDto) {
        Tokens tokens = authService.validateTfa(new ValidateTfaCommand(email, requestDto.getValidateCode()));

        HttpHeaders headers = createAuthHeaders(tokens);

        return ResponseEntity.ok().headers(headers).build();
    }

    @PostMapping("/login/{provider}")
    public ResponseEntity<Void> socialLogin(@PathVariable("provider") String provider, @RequestBody @Valid SocialLoginDto requestDto) {

        SocialLoginCommand command = new SocialLoginCommand(Provider.valueOf(provider.toUpperCase()), requestDto.getValidateCode(), requestDto.getLocale());

        Tokens tokens = authService.socialLogin(command);

        HttpHeaders headers = createAuthHeaders(tokens);

        return ResponseEntity.ok().headers(headers).build();
    }

    private HttpHeaders createAuthHeaders(Tokens tokens) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokens.getAccessToken());
        ResponseCookie refreshTokenCookie = CookieUtil.createRefreshTokenCookie(tokens.getRefreshToken());
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        return headers;
    }
}