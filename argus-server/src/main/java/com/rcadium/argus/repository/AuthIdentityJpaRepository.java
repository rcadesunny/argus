package com.rcadium.argus.repository;

import com.rcadium.argus.entity.AuthIdentityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthIdentityJpaRepository extends JpaRepository<AuthIdentityEntity, UUID> {
    Optional<AuthIdentityEntity> findByProviderAndProviderUserId(String provider, String providerUserId);
}
