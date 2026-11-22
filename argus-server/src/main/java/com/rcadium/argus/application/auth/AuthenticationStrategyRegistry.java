package com.rcadium.argus.application.auth;

import com.rcadium.argus.domain.AuthMethod;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AuthenticationStrategyRegistry {

    private final Map<AuthMethod, AuthenticationStrategy> strategies;

    public AuthenticationStrategyRegistry(List<AuthenticationStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toUnmodifiableMap(
                        AuthenticationStrategy::method,
                        Function.identity()));
    }

    public AuthenticationStrategy resolve(AuthMethod method) {
        AuthenticationStrategy strategy = strategies.get(method);

        if (strategy == null) {
            throw new IllegalStateException("No authentication strategy registered for " + method);
        }

        return strategy;
    }

}
