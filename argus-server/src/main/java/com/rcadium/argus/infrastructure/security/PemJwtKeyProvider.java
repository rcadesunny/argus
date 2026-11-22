package com.rcadium.argus.infrastructure.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

public class PemJwtKeyProvider implements JwtKeyProvider {

    private static final String BEGIN = "-----BEGIN PRIVATE KEY-----";
    private static final String END = "-----END PRIVATE KEY-----";

    private final RSAKey key;

    public PemJwtKeyProvider(String privateKeyPem) {
        this.key = load(privateKeyPem);
    }

    @Override
    public RSAKey signingKey() {
        return key;
    }

    @Override
    public JWKSet publicKeys() {
        return new JWKSet(key.toPublicJWK());
    }

    private RSAKey load(String pem) {
        try {
            byte[] der = Base64.getDecoder().decode(normalize(pem));

            KeyFactory factory = KeyFactory.getInstance("RSA");
            RSAPrivateCrtKey privateKey =
                    (RSAPrivateCrtKey) factory.generatePrivate(new PKCS8EncodedKeySpec(der));
            RSAPublicKey publicKey = (RSAPublicKey) factory.generatePublic(
                    new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent()));

            RSAKey unidentified = new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .build();

            return new RSAKey.Builder(unidentified)
                    .keyID(unidentified.computeThumbprint().toString())
                    .keyUse(KeyUse.SIGNATURE)
                    .algorithm(JWSAlgorithm.RS256)
                    .build();

        } catch (GeneralSecurityException | com.nimbusds.jose.JOSEException e) {
            throw new IllegalStateException("Unable to load RSA signing key from PEM", e);
        }
    }

    private String normalize(String pem) {
        return pem.replace(BEGIN, "")
                .replace(END, "")
                .replaceAll("\\s", "");
    }

}
