package com.hanyahunya.auth.adapter.out.security;

import com.hanyahunya.auth.application.port.out.VerifyTokenPort;
import com.hanyahunya.auth.domain.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtVerifyTokenProvider implements VerifyTokenPort {

    @Value("${jwt.verificationtoken.secret}")
    private String verificationSecret;

    private SecretKey verificationKey;

    // ttl = time to live
    private final long verificationTokenTtl = 3 * 60 * 1000L;

    @PostConstruct
    public void init() {
        verificationKey = Keys.hmacShaKeyFor(verificationSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String issueToken(String email) {
        return Jwts.builder()
                .subject(email)
                .claim("purpose", Purpose.EMAIL_VERIFICATION.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + verificationTokenTtl))
                .signWith(verificationKey)
                .compact();
    }

    @Override
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(verificationKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        if (!claims.get("purpose", String.class).equals(Purpose.EMAIL_VERIFICATION.name())) {
            throw new InvalidTokenException();
        }
        return claims.getSubject();
    }
}
