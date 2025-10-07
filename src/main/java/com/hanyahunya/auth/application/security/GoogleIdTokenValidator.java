package com.hanyahunya.auth.application.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanyahunya.auth.domain.exception.InvalidTokenException;
import com.hanyahunya.auth.domain.model.Provider;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// todo 추후 올바른 예외 클래스로 변경.
@Slf4j
@Component
public class GoogleIdTokenValidator implements IdTokenValidator {

    @Value("${oauth2.google.client-id}")
    private String googleClientId;

    @Value("${oauth2.google.issuer}")
    private String googleIssuer;

    private static final String GOOGLE_JWKS_URL = "https://www.googleapis.com/oauth2/v3/certs";
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, Key> publicKeyCache = new ConcurrentHashMap<>();

    private final Locator<Key> keyLocator = new Locator<Key>() {
        @Override
        public Key locate(Header header) {
            String keyId = (String) header.get("kid");
            if (keyId == null) {
                throw new InvalidTokenException("ID 토큰 헤더에 'kid'가 없습니다.");
            }
            if (publicKeyCache.containsKey(keyId)) {
                return publicKeyCache.get(keyId);
            }
            return refreshPublicKeysAndGet(keyId);
        }
    };

    @Override
    public String validateAndGetSub(String idToken, String nonce) {
        try {
            Jws<Claims> jwsClaims = Jwts.parser()
                    .keyLocator(keyLocator)
                    .build()
                    .parseSignedClaims(idToken);

            Claims claims = jwsClaims.getPayload();
            validateClaims(claims, nonce);
            return claims.getSubject();

        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("ID 토큰이 만료되었습니다.");
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            throw new InvalidTokenException("유효하지 않은 형식의 ID 토큰입니다.");
        } catch (InvalidTokenException e) {
            // validateClaims 또는 keyLocator에서 발생한 InvalidTokenException을 그대로 다시 던짐
            throw e;
        } catch (Exception e) {
            log.error("ID Token validation failed: {}", e.getMessage());
            throw new InvalidTokenException("ID 토큰 검증에 실패했습니다.");
        }
    }

    private Key refreshPublicKeysAndGet(String keyId) {
        try {
            URL jwksUrl = URI.create(GOOGLE_JWKS_URL).toURL();
            Map<String, List<Map<String, String>>> keys = objectMapper.readValue(jwksUrl, new TypeReference<>() {});
            List<Map<String, String>> keyList = keys.get("keys");

            publicKeyCache.clear();

            for (Map<String, String> keyMap : keyList) {
                BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(keyMap.get("n")));
                BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(keyMap.get("e")));
                RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
                KeyFactory factory = KeyFactory.getInstance("RSA");
                Key publicKey = factory.generatePublic(spec);
                publicKeyCache.put(keyMap.get("kid"), publicKey);
            }

            if (publicKeyCache.containsKey(keyId)) {
                return publicKeyCache.get(keyId);
            } else {
                throw new InvalidTokenException("ID 토큰에 해당하는 공개키를 찾을 수 없습니다. (kid=" + keyId + ")");
            }

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Failed to refresh Google public keys", e);
            throw new RuntimeException("Google 공개키를 가져오거나 생성하는 데 실패했습니다.", e);
        }
    }

    private void validateClaims(Claims claims, String expectedNonce) {
        if (!googleIssuer.equals(claims.getIssuer())) {
            throw new InvalidTokenException("ID 토큰의 발급자(issuer)가 일치하지 않습니다.");
        }
        if (!googleClientId.equals(claims.getAudience().iterator().next())) {
            throw new InvalidTokenException("ID 토큰의 대상(audience)이 일치하지 않습니다.");
        }
        if (claims.getExpiration().before(new Date())) {
            throw new InvalidTokenException("ID 토큰이 만료되었습니다.");
        }
        String tokenNonce = claims.get("nonce", String.class);
        if (tokenNonce == null || !tokenNonce.equals(expectedNonce)) {
            throw new InvalidTokenException("ID 토큰의 Nonce 값이 일치하지 않습니다.");
        }
    }

    @Override
    public Provider getProvider() {
        return Provider.GOOGLE;
    }
}