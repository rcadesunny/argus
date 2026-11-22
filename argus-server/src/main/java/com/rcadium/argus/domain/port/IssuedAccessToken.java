package com.rcadium.argus.domain.port;

import java.time.Instant;

public record IssuedAccessToken(
        String value, Instant expiresAt
) {
}
