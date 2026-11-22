package com.rcadium.argus.service.auth;

import com.rcadium.argus.enums.AuthMethod;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class AuthenticationStrategyRegistry {

    private final Map<AuthMethod, AuthenticationStrategy> strategies = new EnumMap<>(AuthMethod.class);

    public AuthenticationStrategyRegistry(List<AuthenticationStrategy> discovered){
        for(AuthenticationStrategy strategy: discovered){
            AuthenticationStrategy existing = strategies.put(strategy.method(), strategy);

            if(existing != null){
                throw new IllegalStateException(
                        "Two strategies claim " + strategy.method() + " :: "
                                + existing.getClass().getName() + " and " + strategy.getClass().getName()
                );
            }
        }
    }

    public AuthenticationStrategy resolve(AuthMethod method) {
        AuthenticationStrategy strategy = strategies.get(method);
        if (strategy == null) {
            throw new IllegalStateException("No authentication strategy registered for " + method);
        }
        return strategy;
    }
}
