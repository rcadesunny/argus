package com.rcadium.argus.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor
public class RefreshTokenEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "family_id", nullable = false)
    private UUID familyId;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name = "used_at")
    private OffsetDateTime usedAt;

    @Column(name = "revoked_at")
    private OffsetDateTime revokedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public RefreshTokenEntity(
            UserEntity user,
            String tokenHash,
            UUID familyId,
            OffsetDateTime expiresAt
    ) {
        this.user = user;
        this.tokenHash = tokenHash;
        this.familyId = familyId;
        this.expiresAt = expiresAt;
    }

    public boolean isUsable(OffsetDateTime now){
        return usedAt == null && revokedAt == null && expiresAt.isAfter(now);
    }

    public void markUsed(OffsetDateTime now) {
        this.usedAt = now;
    }

    public void revoke(OffsetDateTime now) {
        if (revokedAt == null) {
            this.revokedAt = now;
        }
    }
}
