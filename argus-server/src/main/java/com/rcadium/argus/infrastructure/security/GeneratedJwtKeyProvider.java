package com.rcadium.argus.infrastructure.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;

import java.util.UUID;

public class GeneratedJwtKeyProvider implements JwtKeyProvider {

    private final RSAKey key;

    public GeneratedJwtKeyProvider() {
        try {
            this.key = new RSAKeyGenerator(2048)
                    .keyUse(KeyUse.SIGNATURE)
                    .algorithm(JWSAlgorithm.RS256)
                    .keyID(UUID.randomUUID().toString())
                    .generate();
        } catch (JOSEException e) {
            throw new IllegalStateException("Unable to generate RSA signing key", e);
        }
    }

    @Override
    public RSAKey signingKey() {
        return key;
    }

    @Override
    public JWKSet publicKeys() {
        return new JWKSet(key.toPublicJWK());
    }

}
