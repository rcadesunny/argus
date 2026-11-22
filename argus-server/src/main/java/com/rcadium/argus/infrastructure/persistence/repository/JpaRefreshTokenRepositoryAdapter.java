package com.rcadium.argus.infrastructure.persistence.repository;

import com.rcadium.argus.domain.RefreshToken;
import com.rcadium.argus.domain.port.RefreshTokenRepository;
import com.rcadium.argus.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRefreshTokenRepositoryAdapter implements RefreshTokenRepository {
    private final RefreshTokenJpaRepository tokens;

    public JpaRefreshTokenRepositoryAdapter(RefreshTokenJpaRepository tokens) {
        this.tokens = tokens;
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return tokens.findByTokenHash(tokenHash).map(this::toDomain);
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        RefreshTokenEntity entity = tokens.findById(token.id())
                .map(existing -> sync(existing, token))
                .orElseGet(() -> toEntity(token));

        return toDomain(tokens.save(entity));
    }

    @Override
    public int revokeFamily(UUID familyId, Instant revokedAt) {
        return tokens.revokeFamily(familyId, revokedAt);
    }

    private RefreshTokenEntity sync(RefreshTokenEntity entity, RefreshToken token) {
        entity.markUsed(token.usedAt());
        entity.revoke(token.revokedAt());
        return entity;
    }

    private RefreshTokenEntity toEntity(RefreshToken token) {
        return new RefreshTokenEntity(
                token.id(),
                token.userId(),
                token.tokenHash(),
                token.familyId(),
                token.expiresAt(),
                token.usedAt(),
                token.revokedAt(),
                Instant.now()
        );
    }

    private RefreshToken toDomain(RefreshTokenEntity entity) {
        return new RefreshToken(
                entity.getId(),
                entity.getUserId(),
                entity.getTokenHash(),
                entity.getFamilyId(),
                entity.getExpiresAt(),
                entity.getUsedAt(),
                entity.getRevokedAt()
        );
    }
}
