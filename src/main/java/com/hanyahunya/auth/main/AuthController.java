package com.hanyahunya.auth.main;

import com.hanyahunya.auth.main.dto.SignupDto;
import com.hanyahunya.auth.main.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

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
}
