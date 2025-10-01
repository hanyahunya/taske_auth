package com.hanyahunya.auth.adapter.in.handler;

import com.hanyahunya.auth.adapter.in.web.util.CookieUtil;
import com.hanyahunya.auth.application.port.in.TokenCompromiseUseCase;
import com.hanyahunya.auth.domain.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    // signup
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Void> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        log.warn("EmailAlreadyExistsException: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @ExceptionHandler(VerificationCooldownException.class)
    public ResponseEntity<Void> handleVerificationCooldownException(VerificationCooldownException e) {
        log.warn("VerificationCooldownException: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<Void> handleInvalidVerificationCodeException(InvalidVerificationCodeException e) {
        log.warn("InvalidVerificationCodeException: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Void> handleInvalidTokenException(InvalidTokenException e) {
        log.warn("InvalidTokenException: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Void> handleUserNotFoundException(ResourceNotFoundException e) {
        log.warn("UserNotFoundException: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private final TokenCompromiseUseCase tokenCompromiseUseCase;

    @ExceptionHandler(TokenCompromisedException.class)
    public ResponseEntity<Void> handleTokenCompromisedException(TokenCompromisedException e) {
        log.warn("ユーザーID '{}' でトークンの侵害を検知しました。", e.getUserId());

        long unixTimestamp = Instant.now().getEpochSecond();
        tokenCompromiseUseCase.handleCompromise(e.getUserId(), unixTimestamp);

        ResponseCookie deletedCookie = CookieUtil.deleteRefreshTokenCookie();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.SET_COOKIE, deletedCookie.toString());

        return new ResponseEntity<>(headers, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserCompromisedException.class)
    public ResponseEntity<Void> handleUserCompromisedException(UserCompromisedException e) {
        log.info("UserCompromisedException: {}", e.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + e.getToken());

        // 202
        return ResponseEntity.status(HttpStatus.ACCEPTED).headers(headers).build();
    }

    @ExceptionHandler(UserPendingVerificationException.class)
    public ResponseEntity<Void> handleUserPendingVerificationException(UserPendingVerificationException e) {
        log.info("UserPendingVerificationException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleGenericException(Exception e) {
        log.error("An unexpected error occurred", e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}