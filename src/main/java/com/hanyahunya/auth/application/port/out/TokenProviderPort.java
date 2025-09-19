package com.hanyahunya.auth.application.port.out;

import com.hanyahunya.auth.application.dto.Tokens;
import io.jsonwebtoken.Claims;

import java.util.UUID;

public interface TokenProviderPort {
    // access, refresh token 만드는 메서드
    Tokens generateTokens(UUID userId, String role, UUID tokenId);

    // 유효성 검증 + 검증안된건 예외로 처리
    void validateToken(String token);

    // 클레임 추출
    Claims getClaims(String token);

    // 만료된 토큰에서 클레임 추출
    Claims getClaimsFromExpiredToken(String expiredToken);
}