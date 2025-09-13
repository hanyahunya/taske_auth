package com.hanyahunya.auth;

import com.hanyahunya.auth.dto.SignupDto;
import com.hanyahunya.auth.encoding.EncodeService;
import com.hanyahunya.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final EncodeService encodeService;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupDto signupDto) {
        authService.signUp(signupDto);
        return ResponseEntity.ok().build();
    }
}
