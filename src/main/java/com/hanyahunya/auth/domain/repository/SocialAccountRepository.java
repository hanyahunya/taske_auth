package com.hanyahunya.auth.domain.repository;

import com.hanyahunya.auth.domain.model.Provider;
import com.hanyahunya.auth.domain.model.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    Optional<SocialAccount> findByProviderAndProviderId(Provider provider, String providerId);
}
