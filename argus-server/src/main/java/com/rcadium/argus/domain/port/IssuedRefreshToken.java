package com.rcadium.argus.domain.port;

import java.time.Instant;
import java.util.UUID;

public record IssuedRefreshToken(String value, UUID familyId, Instant expiresAt) {
}
