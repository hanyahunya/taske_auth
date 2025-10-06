package com.hanyahunya.auth.adapter.in.web.util;

import org.springframework.http.ResponseCookie;

public final class CookieUtil {
    private CookieUtil() {}

//    public static ResponseCookie createRefreshTokenCookie(String refreshToken) {
//        return ResponseCookie.from("refresh_token", refreshToken)
//                .httpOnly(true)
//                .secure(true) // https
//                .path("/taske") // only taske
//                .maxAge(15 * 24 * 60 * 60)
//                .sameSite("Lax")
//                .build();
//    }
//
//    public static ResponseCookie deleteRefreshTokenCookie() {
//        return ResponseCookie.from("refresh_token", "")
//                .maxAge(0)
//                .path("/taske")
//                .build();
//    }

    public static ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                //.secure(true) // 프로덕션(HTTPS) 환경용 설정
                .secure(false) // 개발용으로 HTTP 환경에서도 쿠키를 허용하도록 변경
//                .path("/taske") // only taske
                .maxAge(15 * 24 * 60 * 60)
                //.sameSite("Lax") // 개발 시 프론트/백엔드 포트가 다를 경우를 대비해 SameSite 제한 제거
                .build();
    }

    public static ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from("refresh_token", "")
                .maxAge(0)
//                .path("/taske")
                .build();
    }
}
