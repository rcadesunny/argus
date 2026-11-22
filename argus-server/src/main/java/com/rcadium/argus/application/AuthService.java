package com.rcadium.argus.application;

import com.rcadium.argus.application.auth.AuthCredentials;
import com.rcadium.argus.application.auth.AuthenticationStrategyRegistry;
import com.rcadium.argus.application.exception.AccountNotActiveException;
import com.rcadium.argus.application.exception.EmailAlreadyRegisteredException;
import com.rcadium.argus.application.exception.InvalidRefreshTokenException;
import com.rcadium.argus.client.dto.RegisterRequest;
import com.rcadium.argus.client.dto.TokenResponse;
import com.rcadium.argus.domain.*;
import com.rcadium.argus.domain.port.AccessTokenIssuer;
import com.rcadium.argus.domain.port.IssuedAccessToken;
import com.rcadium.argus.domain.port.IssuedRefreshToken;
import com.rcadium.argus.domain.port.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService {

    private static final String DEFAULT_ROLE = "USER";

    private final UserRepository users;
    private final AuthenticationStrategyRegistry strategies;
    private final AccessTokenIssuer accessTokens;
    private final RefreshTokenService refreshTokens;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public AuthService(UserRepository users,
                       AuthenticationStrategyRegistry strategies,
                       AccessTokenIssuer accessTokens,
                       RefreshTokenService refreshTokens,
                       PasswordEncoder passwordEncoder,
                       Clock clock) {
        this.users = users;
        this.strategies = strategies;
        this.accessTokens = accessTokens;
        this.refreshTokens = refreshTokens;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        String email = normalize(request.email());

        if (users.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException();
        }

        Instant now = clock.instant();
        User user = new User(
                UUID.randomUUID(),
                email,
                passwordEncoder.encode(request.password()),
                UserStatus.ACTIVE,
                Set.of(new Role(DEFAULT_ROLE, Set.of())),
                now,
                now);

        try {
            return issueTokens(users.save(user), now);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyRegisteredException();
        }
    }

    @Transactional
    public TokenResponse authenticate(AuthMethod method, AuthCredentials credentials) {
        User user = strategies.resolve(method).authenticate(credentials);
        return issueTokens(user, clock.instant());
    }

    @Transactional
    public TokenResponse refresh(String refreshToken) {
        Instant now = clock.instant();
        RefreshToken consumed = refreshTokens.consume(refreshToken, now);

        User user = users.findById(consumed.userId())
                .orElseThrow(InvalidRefreshTokenException::new);

        if (!user.canAuthenticate()) {
            refreshTokens.revokeFamily(consumed.familyId(), now);
            throw new AccountNotActiveException();
        }

        IssuedAccessToken access = accessTokens.issue(user, now);
        IssuedRefreshToken refresh = refreshTokens.issue(user.id(), consumed.familyId(), now);

        return toResponse(access, refresh, now);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokens.revokeByToken(refreshToken, clock.instant());
    }

    private TokenResponse issueTokens(User user, Instant now) {
        IssuedAccessToken access = accessTokens.issue(user, now);
        IssuedRefreshToken refresh = refreshTokens.issue(user.id(), now);

        return toResponse(access, refresh, now);
    }

    private TokenResponse toResponse(IssuedAccessToken access, IssuedRefreshToken refresh, Instant now) {
        long expiresIn = Duration.between(now, access.expiresAt()).toSeconds();

        return TokenResponse.bearer(access.value(), refresh.value(), expiresIn);
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

}
