package com.hanyahunya.auth.application.port.out;

import com.hanyahunya.auth.application.dto.Tokens;
import com.hanyahunya.auth.domain.model.Role;
import io.jsonwebtoken.Claims;

import java.util.UUID;

public interface TokenProviderPort {
    // access, refresh token
    Tokens generateTokens(UUID userId, Role role, UUID tokenId);
    // access
    String generateAccessToken(UUID userId, Role role, UUID tokenId);

    // 만료된 토큰에서 클레임 추출
    void validateExpiredAccessToken(String expiredAccessToken);

    Claims getRefreshClaims(String refreshToken);
}