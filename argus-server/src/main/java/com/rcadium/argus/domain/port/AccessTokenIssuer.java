package com.rcadium.argus.domain.port;

import com.rcadium.argus.domain.User;

import java.time.Instant;

public interface AccessTokenIssuer {
    IssuedAccessToken issue(User user, Instant now);
}
