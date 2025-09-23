package com.hanyahunya.auth.adapter.in.web.util;

import org.springframework.http.ResponseCookie;

public final class CookieUtil {
    private CookieUtil() {}

    public static ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true) // https
                .path("/taske") // only taske
                .maxAge(15 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();
    }

    public static ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from("refresh_token", "")
                .maxAge(0)
                .path("/taske")
                .build();
    }
}
