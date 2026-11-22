package com.rcadium.argus.application;

import com.rcadium.argus.domain.port.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenFamilyRevoker {

    private final RefreshTokenRepository tokens;

    public RefreshTokenFamilyRevoker(RefreshTokenRepository tokens) {
        this.tokens = tokens;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int revoke(UUID familyId, Instant now) {
        return tokens.revokeFamily(familyId, now);
    }
}
