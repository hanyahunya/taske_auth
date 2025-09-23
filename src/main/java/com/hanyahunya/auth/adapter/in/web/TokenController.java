package com.hanyahunya.auth.adapter.in.web;

import com.hanyahunya.auth.adapter.in.web.util.CookieUtil;
import com.hanyahunya.auth.application.dto.Tokens;
import com.hanyahunya.auth.application.port.in.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(HttpServletRequest request, @RequestHeader("Authorization") String expiredAccessToken) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = (cookies != null) ? Arrays.stream(cookies)
                .filter(cookie -> "refresh_token".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null) : null;

        String accessToken = null;
        if (expiredAccessToken != null && expiredAccessToken.startsWith("Bearer ")) {
            accessToken = expiredAccessToken.substring(7);
        }

        if (refreshToken == null || accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Tokens tokens = tokenService.reissueTokens(accessToken, refreshToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokens.getAccessToken());
        if (tokens.getRefreshToken() != null) {
            ResponseCookie refreshTokenCookie = CookieUtil.createRefreshTokenCookie(tokens.getRefreshToken());
            headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        }
        return ResponseEntity.ok().headers(headers).build();
    }
}
