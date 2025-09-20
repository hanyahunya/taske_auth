package com.hanyahunya.auth.domain.repository;

import com.hanyahunya.auth.domain.model.Status;
import com.hanyahunya.auth.domain.model.User; // 변경
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @Modifying
    @Query(value = "UPDATE users u SET u.status = :status WHERE u.user_id = :userId AND u.status = 'PENDING_VERIFICATION'", nativeQuery = true)
    int updateUserStatus(@Param("userId") UUID userId, @Param("status") String status);

    void deleteByStatusAndUpdatedAtBefore(Status status, LocalDateTime updatedAtBefore);

    Optional<User> findByEmail(String email);
}