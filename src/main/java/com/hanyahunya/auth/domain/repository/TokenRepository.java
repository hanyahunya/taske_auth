package com.hanyahunya.auth.domain.repository;

import com.hanyahunya.auth.domain.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {
    void deleteAllByUser_UserId(UUID userUserId);
}