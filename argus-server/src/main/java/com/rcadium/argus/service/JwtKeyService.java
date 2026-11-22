package com.rcadium.argus.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.rcadium.argus.config.TokenProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

@Service
@Slf4j
public class JwtKeyService {

    private static final int KEY_SIZE = 2048;
    private final RSAKey signingKey;

    public JwtKeyService(TokenProperties properties){
        String pem = properties.privateKeyPem();
        KeyPair keyPair = (pem == null) || pem.isBlank() ? generate() : fromPem(pem);
        this.signingKey = toJwk(keyPair);
        log.info("JWT signing key ready, kid: {}", signingKey.getKeyID());
    }

    public RSAKey signingKey(){
        return signingKey;
    }

    public Map<String, Object> publicJwtSet(){
        return new JWKSet(signingKey.toPublicJWK()).toJSONObject();
    }

    private static RSAKey toJwk(KeyPair keyPair){
        try {
            return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                    .privateKey((RSAPrivateKey) keyPair.getPrivate())
                    .keyUse(KeyUse.SIGNATURE)
                    .algorithm(JWSAlgorithm.RS256)
                    .keyIDFromThumbprint()
                    .build();
        } catch (JOSEException e){
            throw new IllegalStateException("Could not build RSA JWK from key pair", e);
        }
    }

    private static KeyPair generate(){
        log.warn("argus.token.private-key-pem is blank — generating an ephemeral RSA key pair. "
                + "Tokens signed now stop verifying on restart. Local development only.");

        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(KEY_SIZE);
            return generator.generateKeyPair();
        } catch (Exception e){
            throw new IllegalStateException("RSA key pair generation failed", e);
        }
    }

    private static KeyPair fromPem(String pem) {
        try {
            String base64 = pem.replaceAll("-----(BEGIN|END) PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] der = Base64.getDecoder().decode(base64);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            RSAPrivateCrtKey privateKey =
                    (RSAPrivateCrtKey) factory.generatePrivate(new PKCS8EncodedKeySpec(der));
            RSAPublicKey publicKey = (RSAPublicKey) factory.generatePublic(
                    new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent()));
            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new IllegalStateException("argus.token.private-key-pem is not a valid PKCS#8 RSA private key", e);
        }
    }
}
