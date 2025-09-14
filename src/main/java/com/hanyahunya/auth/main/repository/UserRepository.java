package com.hanyahunya.auth.main.repository;

import com.hanyahunya.auth.main.entity.Status;
import com.hanyahunya.auth.main.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    @Modifying
    @Query(value = "UPDATE users u SET u.status = :status WHERE u.user_id = :userId AND u.status = 'PENDING_VERIFICATION'", nativeQuery = true)
    int updateUserStatus(@Param("userId") UUID userId, @Param("status") String status);
}
