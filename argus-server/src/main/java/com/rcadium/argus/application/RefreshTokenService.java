package com.rcadium.argus.application;

import com.rcadium.argus.application.exception.InvalidRefreshTokenException;
import com.rcadium.argus.config.TokenProperties;
import com.rcadium.argus.domain.RefreshToken;
import com.rcadium.argus.domain.port.IssuedRefreshToken;
import com.rcadium.argus.domain.port.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);
    private static final int TOKEN_BYTES = 32;

    private final RefreshTokenRepository tokens;
    private final TokenProperties properties;
    private final SecureRandom random = new SecureRandom();

    private final RefreshTokenFamilyRevoker familyRevoker;

    public RefreshTokenService(RefreshTokenRepository tokens, TokenProperties properties, RefreshTokenFamilyRevoker familyRevoker) {
        this.tokens = tokens;
        this.properties = properties;
        this.familyRevoker = familyRevoker;
    }

    @Transactional
    public IssuedRefreshToken issue(UUID userId, Instant now) {
        return issue(userId, UUID.randomUUID(), now);
    }

    @Transactional
    public IssuedRefreshToken issue(UUID userId, UUID familyId, Instant now) {
        String value = randomToken();
        Instant expiresAt = now.plus(properties.refreshTokenTtl());

        tokens.save(new RefreshToken(
                UUID.randomUUID(),
                userId,
                hash(value),
                familyId,
                expiresAt,
                null,
                null));

        return new IssuedRefreshToken(value, familyId, expiresAt);
    }

    @Transactional
    public RefreshToken consume(String presented, Instant now) {
        RefreshToken token = tokens.findByTokenHash(hash(presented))
                .orElseThrow(InvalidRefreshTokenException::new);

        if (token.isUsed() || token.isRevoked()) {
            int revoked = familyRevoker.revoke(token.familyId(), now);
            log.warn("Refresh token reuse detected for family {} - revoked {} tokens",
                    token.familyId(), revoked);
            throw new InvalidRefreshTokenException();
        }

        if (!token.isUsable(now)) {
            throw new InvalidRefreshTokenException();
        }

        token.markUsed(now);
        tokens.save(token);

        return token;
    }

    public void revokeFamily(UUID familyId, Instant now) {
        familyRevoker.revoke(familyId, now);
    }


    private String randomToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }

    @Transactional
    public void revokeByToken(String presented, Instant now) {
        tokens.findByTokenHash(hash(presented))
                .ifPresent(token -> familyRevoker.revoke(token.familyId(), now));
    }

}
