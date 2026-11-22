package com.rcadium.argus.domain.port;

import com.rcadium.argus.domain.RefreshToken;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    RefreshToken save(RefreshToken token);

    int revokeFamily(UUID familyId, Instant revokedAt);
}
