package com.rcadium.argus.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "argus.token")
public record TokenProperties (
        @NotBlank String issuer,
        @NotBlank String audience,
        @NotBlank Duration accessTokenTtl,
        @NotBlank Duration refreshTokenTtl,
        String privateKeyPem
        ) {
}
