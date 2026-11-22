package com.rcadium.argus.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rcadium.argus.config.TokenProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtKeyConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JwtKeyConfiguration.class);

    @Bean
    public JwtKeyProvider jwtKeyProvider(TokenProperties properties) {
        String pem = properties.privateKeyPem();

        if (pem == null || pem.isBlank()) {
            log.warn("No argus.token.private-key-pem configured - generating an ephemeral RSA key. "
                    + "Tokens will stop validating on restart. Never use this outside local development.");
            return new GeneratedJwtKeyProvider();
        }

        return new PemJwtKeyProvider(pem);
    }

}
