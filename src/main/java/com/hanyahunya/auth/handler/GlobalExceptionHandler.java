package com.hanyahunya.auth.handler;

import com.hanyahunya.auth.exception.EmailAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Void> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        log.warn("EmailAlreadyExistsException: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleGenericException(Exception e) {
        log.error("An unexpected error occurred", e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
