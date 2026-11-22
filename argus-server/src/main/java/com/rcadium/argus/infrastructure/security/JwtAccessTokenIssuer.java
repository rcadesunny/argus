package com.rcadium.argus.infrastructure.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.rcadium.argus.config.TokenProperties;
import com.rcadium.argus.domain.Permission;
import com.rcadium.argus.domain.Role;
import com.rcadium.argus.domain.User;
import com.rcadium.argus.domain.port.AccessTokenIssuer;
import com.rcadium.argus.domain.port.IssuedAccessToken;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAccessTokenIssuer implements AccessTokenIssuer {

    private final JwtKeyProvider keyProvider;
    private final TokenProperties properties;

    public JwtAccessTokenIssuer(JwtKeyProvider keyProvider, TokenProperties properties) {
        this.keyProvider = keyProvider;
        this.properties = properties;
    }

    @Override
    public IssuedAccessToken issue(User user, Instant now) {
        Instant expiresAt = now.plus(properties.accessTokenTtl());
        RSAKey key = keyProvider.signingKey();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(properties.issuer())
                .audience(properties.audience())
                .subject(user.id().toString())
                .jwtID(UUID.randomUUID().toString())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(expiresAt))
                .claim("roles", roleNames(user))
                .claim("permissions", permissionNames(user))
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .keyID(key.getKeyID())
                .build();

        SignedJWT jwt = new SignedJWT(header, claims);
        try {
            jwt.sign(new RSASSASigner(key.toPrivateKey()));
        } catch (JOSEException e) {
            throw new IllegalStateException("Unable to sign access token", e);
        }

        return new IssuedAccessToken(jwt.serialize(), expiresAt);
    }

    private List<String> roleNames(User user) {
        return user.roles().stream()
                .map(Role::name)
                .sorted()
                .toList();
    }

    private List<String> permissionNames(User user) {
        return user.roles().stream()
                .flatMap(role -> role.permissions().stream())
                .map(Permission::name)
                .distinct()
                .sorted()
                .toList();
    }
}
