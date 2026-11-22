package com.rcadium.argus.service.auth.strategy;

import com.rcadium.argus.entity.UserEntity;
import com.rcadium.argus.enums.AuthMethod;
import com.rcadium.argus.exception.InvalidCredentialsException;
import com.rcadium.argus.repository.UserJpaRepository;
import com.rcadium.argus.service.auth.AuthCredentials;
import com.rcadium.argus.service.auth.AuthenticationStrategy;
import com.rcadium.argus.service.auth.EmailPasswordCredentials;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EmailPasswordAuthenticationStrategy implements AuthenticationStrategy {

    private static final String DUMMY_HASH =
            "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public EmailPasswordAuthenticationStrategy(
            UserJpaRepository userJpaRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userJpaRepository = userJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthMethod method() {
        return AuthMethod.EMAIL_PASSWORD;
    }

    @Override
    public UserEntity authenticate(AuthCredentials credentials) {
        EmailPasswordCredentials input = (EmailPasswordCredentials) credentials;
        Optional<UserEntity> user = userJpaRepository.findByEmailWithRoles(input.email());

        String hash = user.map(UserEntity::getPasswordHash).orElse(DUMMY_HASH);
        boolean matches = passwordEncoder.matches(input.password(), hash);

        if (user.isEmpty() || !matches) {
            throw new InvalidCredentialsException();
        }
        return user.get();
    }
}
