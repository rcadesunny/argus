package com.rcadium.argus.service;

import com.rcadium.argus.config.TokenProperties;
import com.rcadium.argus.entity.RefreshTokenEntity;
import com.rcadium.argus.entity.UserEntity;
import com.rcadium.argus.exception.InvalidRefreshTokenException;
import com.rcadium.argus.repository.RefreshTokenJpaRepository;
import com.rcadium.argus.repository.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
public class RefreshTokenService {

    private static final int TOKEN_BYTES = 32;

    private final RefreshTokenJpaRepository refreshTokenRepository;
    private final UserJpaRepository userRepository;
    private final TokenProperties properties;
    private final Clock clock;
    private final SecureRandom random = new SecureRandom();

    public RefreshTokenService(RefreshTokenJpaRepository refreshTokenRepository,
                               UserJpaRepository userRepository,
                               TokenProperties properties,
                               Clock clock) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.properties = properties;
        this.clock = clock;
    }

    /** Starts a new family — one per login. */
    @Transactional
    public IssuedRefreshToken issue(UserEntity user) {
        return issueInFamily(user, UUID.randomUUID());
    }

    @Transactional
    public IssuedRefreshToken rotate(String presentedToken) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        RefreshTokenEntity stored = refreshTokenRepository.findByTokenHash(hash(presentedToken))
                .orElseThrow(InvalidRefreshTokenException::new);

        if (stored.getUsedAt() != null) {
            int revoked = refreshTokenRepository.revokeFamily(stored.getFamilyId(), now);
            log.warn("Refresh token reuse detected, family :: {} revoked {} tokens, user :: {}",
                    stored.getFamilyId(), revoked, stored.getUser().getId());
            throw new InvalidRefreshTokenException();
        }
        if (!stored.isUsable(now)) {
            throw new InvalidRefreshTokenException();
        }

        stored.markUsed(now);
        UserEntity user = userRepository.findByIdWithRoles(stored.getUser().getId())
                .orElseThrow(InvalidRefreshTokenException::new);
        return issueInFamily(user, stored.getFamilyId());
    }

    @Transactional
    public void revokeFamilyOf(String presentedToken) {
        refreshTokenRepository.findByTokenHash(hash(presentedToken)).ifPresent(stored ->
                refreshTokenRepository.revokeFamily(stored.getFamilyId(), OffsetDateTime.now(clock)));
    }

    private IssuedRefreshToken issueInFamily(UserEntity user, UUID familyId) {
        byte[] bytes = new byte[TOKEN_BYTES];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        refreshTokenRepository.save(new RefreshTokenEntity(
                user,
                hash(token),
                familyId,
                OffsetDateTime.now(clock).plus(properties.refreshTokenTtl())));

        return new IssuedRefreshToken(token, user);
    }

    private static String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
