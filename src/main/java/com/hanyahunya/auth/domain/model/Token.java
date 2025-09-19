package com.hanyahunya.auth.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Getter
@Table(name = "tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "token_id", columnDefinition = "BINARY(16)")
    private UUID tokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "access_token_hash", nullable = false, length = 128)
    private String accessTokenHash;

    @Column(name = "refresh_token_hash", nullable = false, length = 128)
    private String refreshTokenHash;
}