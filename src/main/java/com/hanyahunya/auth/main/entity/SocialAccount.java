package com.hanyahunya.auth.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(
        name = "social_accounts",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"provider", "provider_id"})}
)
@NoArgsConstructor
@AllArgsConstructor
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "provider", nullable = false, length = 32)
    private String provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;
}
