package com.hanyahunya.auth.application.service;

import com.hanyahunya.auth.application.dto.Tokens;
import com.hanyahunya.auth.application.port.in.TokenService;
import com.hanyahunya.auth.application.port.out.EncodeServicePort;
import com.hanyahunya.auth.application.port.out.TokenProviderPort;
import com.hanyahunya.auth.domain.exception.InvalidTokenException;
import com.hanyahunya.auth.domain.exception.TokenCompromisedException;
import com.hanyahunya.auth.domain.model.Token;
import com.hanyahunya.auth.domain.model.User;
import com.hanyahunya.auth.domain.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenProviderPort tokenProviderPort;
    private final TokenRepository tokenRepository;
    private final EncodeServicePort encodeServicePort;

    @Override
    public Tokens loginAndIssueTokens(User user) {
        UUID tokenId = UUID.randomUUID();
        Tokens tokens = tokenProviderPort.generateTokens(user.getUserId(), user.getRole(), tokenId);

        Token token = Token.builder()
                .tokenId(tokenId)
                .user(user)
                .accessTokenHash(encodeServicePort.encode(tokens.getAccessToken()))
                .refreshTokenHash(encodeServicePort.encode(tokens.getRefreshToken()))
                .build();
        tokenRepository.save(token);
        return tokens;
    }

    @Override
    @Transactional
    public Tokens reissueTokens(String expiredAccessToken, String refreshToken) {
        tokenProviderPort.validateExpiredAccessToken(expiredAccessToken);

        Claims refreshClaims = tokenProviderPort.getRefreshClaims(refreshToken);
        UUID tokenIdFromRefreshToken = UUID.fromString(refreshClaims.getId());

        Optional<Token> optionalToken = tokenRepository.findById(tokenIdFromRefreshToken);
        if (optionalToken.isEmpty()) {
            throw new InvalidTokenException();
        }
        Token dbToken = optionalToken.get();
        User user = dbToken.getUser();
        if (!encodeServicePort.matches(expiredAccessToken, dbToken.getAccessTokenHash()) || !encodeServicePort.matches(refreshToken, dbToken.getRefreshTokenHash())) {
            throw new TokenCompromisedException("d", user.getUserId());
        }
        Date expiryDateFromRefreshToken = refreshClaims.getExpiration();

        Instant threeDaysFromNow = Instant.now().plus(3, ChronoUnit.DAYS);
        if (expiryDateFromRefreshToken.toInstant().isBefore(threeDaysFromNow)) {
            Tokens newTokens = tokenProviderPort.generateTokens(user.getUserId(), user.getRole(), tokenIdFromRefreshToken);
            dbToken.updateAccessTokenHash(encodeServicePort.encode(newTokens.getAccessToken()));
            dbToken.updateRefreshTokenHash(encodeServicePort.encode(newTokens.getRefreshToken()));
            return newTokens;
        } else {
            String newAccessToken = tokenProviderPort.generateAccessToken(user.getUserId(), user.getRole(), tokenIdFromRefreshToken);
            dbToken.updateAccessTokenHash(encodeServicePort.encode(newAccessToken));
            return Tokens.builder()
                    .accessToken(newAccessToken)
                    .build();
        }
        // tokenRepository.save(Entity) -> Dirty Checking
    }

    @Override
    public void revokeAllTokensForUser(UUID userId) {
        tokenRepository.deleteAllByUser_UserId(userId);
    }
}
