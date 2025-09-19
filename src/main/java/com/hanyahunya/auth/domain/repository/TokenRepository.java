package com.hanyahunya.auth.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TokenRepository extends JpaRepository<UUID, String> {
}