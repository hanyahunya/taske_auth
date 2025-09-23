package com.hanyahunya.auth.adapter.in.handler;

import com.hanyahunya.auth.adapter.in.web.util.CookieUtil;
import com.hanyahunya.auth.application.port.in.TokenCompromiseUseCase;
import com.hanyahunya.auth.domain.exception.EmailAlreadyExistsException;
import com.hanyahunya.auth.domain.exception.InvalidTokenException;
import com.hanyahunya.auth.domain.exception.ResourceNotFoundException;
import com.hanyahunya.auth.domain.exception.TokenCompromisedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Void> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        log.warn("EmailAlreadyExistsException: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.CONFLICT);
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

        tokenCompromiseUseCase.handleCompromise(e.getUserId());

        ResponseCookie deletedCookie = CookieUtil.deleteRefreshTokenCookie();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.SET_COOKIE, deletedCookie.toString());

        return new ResponseEntity<>(headers, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleGenericException(Exception e) {
        log.error("An unexpected error occurred", e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}