package com.hanyahunya.auth.adapter.in.web;

import com.hanyahunya.auth.adapter.in.web.dto.LoginDto;
import com.hanyahunya.auth.application.port.in.AuthService;
import com.hanyahunya.auth.adapter.in.web.dto.SignupDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService; // In-Port 주입

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupDto signupDto) {
        authService.signUp(signupDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify/{code}")
    public ResponseEntity<Void> verify(@PathVariable("code") String verificationCode) {
        authService.completeSignup(verificationCode);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginDto loginDto) {

    }
}