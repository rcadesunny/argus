package com.rcadium.argus.infrastructure.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

public interface JwtKeyProvider {
    RSAKey signingKey();
    JWKSet publicKeys();
}
