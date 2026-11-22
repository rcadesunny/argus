package com.rcadium.argus.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class RefreshToken {

    private final UUID id;
    private final UUID userId;
    private final String tokenHash;
    private final UUID familyId;
    private final Instant expiresAt;
    private Instant usedAt;
    private Instant revokedAt;

    public RefreshToken(
            UUID id,
            UUID userId,
            String tokenHash,
            UUID familyId,
            Instant expiresAt,
            Instant usedAt,
            Instant revokedAt
    ) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.userId = Objects.requireNonNull(userId, "userId is required");
        this.tokenHash = Objects.requireNonNull(tokenHash, "tokenHash is required");
        this.familyId = Objects.requireNonNull(familyId, "familyId is required");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt is required");
        this.usedAt = usedAt;
        this.revokedAt = revokedAt;
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isExpired(Instant now) {
        return !now.isBefore(expiresAt);
    }

    public boolean isUsable(Instant now) {
        return !isUsed() && !isRevoked() && !isExpired(now);
    }

    public void markUsed(Instant now) {
        this.usedAt = Objects.requireNonNull(now, "now is required");
    }

    public void revoke(Instant now) {
        if (revokedAt == null) {
            this.revokedAt = Objects.requireNonNull(now, "now is required");
        }
    }

    public UUID id() {
        return id;
    }

    public UUID userId() {
        return userId;
    }

    public String tokenHash() {
        return tokenHash;
    }

    public UUID familyId() {
        return familyId;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    public Instant usedAt() {
        return usedAt;
    }

    public Instant revokedAt() {
        return revokedAt;
    }
}
