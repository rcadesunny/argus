package com.rcadium.argus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties(prefix = "argus.token")
public record TokenProperties(
        @DefaultValue("argus")
        String issuer,

        @DefaultValue("argus")
        String audience,

        @DefaultValue("PT15M")
        Duration accessTokenTtl,

        @DefaultValue("P30D")
        Duration refreshTokenTtl,

        String privateKeyPem
) {
}
