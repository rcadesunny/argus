package com.rcadium.argus.repository;

import com.rcadium.argus.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update RefreshTokenEntity t
               set t.revokedAt = :now
             where t.familyId = :familyId
               and t.revokedAt is null
            """)
    int revokeFamily(@Param("familyId") UUID familyId, @Param("now") OffsetDateTime now);
}
