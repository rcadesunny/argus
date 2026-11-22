package com.rcadium.argus.service;

import com.rcadium.argus.config.TokenProperties;
import com.rcadium.argus.entity.RoleEntity;
import com.rcadium.argus.entity.UserEntity;
import com.rcadium.argus.enums.UserStatus;
import com.rcadium.argus.exception.AccountNotActiveException;
import com.rcadium.argus.exception.EmailAlreadyRegisteredException;
import com.rcadium.argus.repository.RoleJpaRepository;
import com.rcadium.argus.repository.UserJpaRepository;
import com.rcadium.argus.service.auth.AuthCredentials;
import com.rcadium.argus.service.auth.AuthenticationStrategyRegistry;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final String DEFAULT_ROLE = "USER";

    private final AuthenticationStrategyRegistry strategies;
    private final RefreshTokenService refreshTokenService;
    private final TokenIssuerService tokenIssuerService;
    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProperties properties;

    public AuthService(AuthenticationStrategyRegistry strategies,
                       RefreshTokenService refreshTokenService,
                       TokenIssuerService tokenIssuerService,
                       UserJpaRepository userRepository,
                       RoleJpaRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       TokenProperties properties) {
        this.strategies = strategies;
        this.refreshTokenService = refreshTokenService;
        this.tokenIssuerService = tokenIssuerService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }


    @Transactional
    public TokenPair register(String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException();
        }
        RoleEntity defaultRole = roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new IllegalStateException(
                        "Role " + DEFAULT_ROLE + " missing — V2 seed migration did not run"));

        UserEntity user = new UserEntity(email, passwordEncoder.encode(rawPassword), UserStatus.ACTIVE);
        user.getRoles().add(defaultRole);
        try {
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyRegisteredException();
        }
        return issueFor(user);
    }

    @Transactional
    public TokenPair login(AuthCredentials credentials) {
        UserEntity user = strategies.resolve(credentials.method()).authenticate(credentials);
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AccountNotActiveException();
        }
        return issueFor(user);
    }

    @Transactional
    public TokenPair refresh(String refreshToken) {
        IssuedRefreshToken rotated = refreshTokenService.rotate(refreshToken);
        if (rotated.user().getStatus() != UserStatus.ACTIVE) {
            throw new AccountNotActiveException();
        }
        return new TokenPair(
                tokenIssuerService.issue(rotated.user()),
                rotated.token(),
                properties.accessTokenTtl().toSeconds());
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revokeFamilyOf(refreshToken);
    }

    private TokenPair issueFor(UserEntity user) {
        return new TokenPair(
                tokenIssuerService.issue(user),
                refreshTokenService.issue(user).token(),
                properties.accessTokenTtl().toSeconds());
    }
}
