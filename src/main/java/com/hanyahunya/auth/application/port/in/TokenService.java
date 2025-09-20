package com.hanyahunya.auth.application.port.in;

import com.hanyahunya.auth.application.dto.Tokens;
import com.hanyahunya.auth.domain.model.User;

import java.util.UUID;

public interface TokenService {
    // 최초로 토큰발급 및 db저장
    Tokens loginAndIssueTokens(User user);

    // 토큰 리프레시
    Tokens reissueTokens(String expiredAccessToken, String refreshToken);

    void revokeAllTokensForUser(UUID userId);
}
