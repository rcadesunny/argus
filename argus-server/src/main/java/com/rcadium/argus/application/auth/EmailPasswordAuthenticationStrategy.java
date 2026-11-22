package com.rcadium.argus.application.auth;

import com.rcadium.argus.application.exception.InvalidCredentialsException;
import com.rcadium.argus.domain.AuthMethod;
import com.rcadium.argus.domain.User;
import com.rcadium.argus.domain.port.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EmailPasswordAuthenticationStrategy implements AuthenticationStrategy {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final String dummyHash;

    public EmailPasswordAuthenticationStrategy(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.dummyHash = passwordEncoder.encode(UUID.randomUUID().toString());
    }

    @Override
    public AuthMethod method() {
        return AuthMethod.EMAIL_PASSWORD;
    }

    @Override
    public User authenticate(AuthCredentials credentials) {
        if (!(credentials instanceof EmailPasswordCredentials emailPassword)) {
            throw new IllegalArgumentException(
                    "Unsupported credentials: " + credentials.getClass().getName());
        }

        User user = users.findByEmail(emailPassword.email()).orElse(null);
        String hash = user != null ? user.passwordHash() : dummyHash;

        boolean passwordMatches = passwordEncoder.matches(emailPassword.password(), hash);

        if (user == null || !passwordMatches || !user.canAuthenticate()) {
            throw new InvalidCredentialsException();
        }

        return user;
    }
}
