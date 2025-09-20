package com.hanyahunya.auth.adapter.out.security;

import com.hanyahunya.auth.application.dto.Tokens;
import com.hanyahunya.auth.application.port.out.TokenProviderPort;
import com.hanyahunya.auth.domain.exception.TokenCompromisedException;
import com.hanyahunya.auth.domain.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;


@Component
public class JwtTokenProvider implements TokenProviderPort {

    @Value("${jwt.accesstoken.secret}")
    private String accessSecret;
    @Value("${jwt.refreshtoken.secret}")
    private String refreshSecret;

    private SecretKey accessKey;
    private SecretKey refreshKey;

    private final long accessTokenExpirationTime = 15 * 60 * 1000L;
    private final long refreshTokenExpirationTime = 7 * 24 * 60 * 60 * 1000L;

    @PostConstruct
    public void init() {
        accessKey = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Tokens generateTokens(UUID userId, Role role, UUID tokenId) {
        return Tokens.builder()
                .accessToken(issueAccess(userId, role, tokenId))
                .refreshToken(issueRefresh(tokenId))
                .build();
    }

    @Override
    public String generateAccessToken(UUID userId, Role role, UUID tokenId) {
        return issueAccess(userId, role, tokenId);
    }

    @Override
    public void validateExpiredAccessToken(String expiredAccessToken) {
        try {
            String userId = Jwts.parser()
                    .verifyWith(accessKey)
                    .build()
                    .parseSignedClaims(expiredAccessToken)
                    .getPayload()
                    .getSubject();
            throw new TokenCompromisedException("d", UUID.fromString(userId));
        } catch (ExpiredJwtException ignored) {}
    }

    @Override
    public void validateRefreshToken(String refreshToken) {
        parseRefreshClaims(refreshToken);
    }

    @Override
    public Claims getRefreshClaims(String refreshToken) {
        return parseRefreshClaims(refreshToken);
    }

    private Claims parseRefreshClaims(String refreshToken) {
        return Jwts.parser()
                .verifyWith(refreshKey)
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();
    }

    private String issueAccess(UUID userId, Role role, UUID tokenId) {
        return Jwts.builder()
                .subject(userId.toString())
                .id(tokenId.toString())
                .claim("role", role.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                .signWith(accessKey)
                .compact();
    }

    private String issueRefresh(UUID tokenId) {
        return Jwts.builder()
                .id(tokenId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                .signWith(refreshKey)
                .compact();
    }
}