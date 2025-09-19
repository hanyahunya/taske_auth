package com.hanyahunya.auth.application.service;

import com.hanyahunya.auth.application.dto.Tokens;
import com.hanyahunya.auth.application.port.in.TokenService;

import java.util.UUID;

public class JwtTokenService implements TokenService {
    @Override
    public Tokens loginAndIssueTokens(UUID userId, String role) {
        return null;
    }

    @Override
    public Tokens reissueTokens(String expiredAccessToken, String refreshToken) {
        return null;
    }
}
