package com.hanyahunya.auth.adapter.out.security;

import com.hanyahunya.auth.application.dto.Tokens;
import com.hanyahunya.auth.application.port.out.TokenProviderPort;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class JwtTokenProvider implements TokenProviderPort {

    @Override
    public Tokens generateTokens(UUID userId, String role, UUID tokenId) {
        return null;
    }

    @Override
    public void validateToken(String token) {

    }

    @Override
    public Claims getClaims(String token) {
        return null;
    }

    @Override
    public Claims getClaimsFromExpiredToken(String expiredToken) {
        return null;
    }
}