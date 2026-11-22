package com.rcadium.argus.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.rcadium.argus.config.TokenProperties;
import com.rcadium.argus.entity.PermissionEntity;
import com.rcadium.argus.entity.RoleEntity;
import com.rcadium.argus.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class TokenIssuerService {

    private final TokenProperties properties;
    private final Clock clock;
    private final JWSHeader header;
    private final JWSSigner signer;

    public TokenIssuerService(
            TokenProperties properties,
            JwtKeyService keyService,
            Clock clock
    ) {
        this.properties = properties;
        this.clock = clock;
        this.header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(keyService.signingKey().getKeyID())
                .type(com.nimbusds.jose.JOSEObjectType.JWT)
                .build();

        try {
            this.signer = new RSASSASigner(keyService.signingKey());
        } catch (JOSEException e){
            throw new IllegalStateException("Signing key is not usable for RS256", e);
        }
    }

    public String issue(UserEntity user){
        Instant now = clock.instant();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(properties.issuer())
                .subject(user.getId().toString())
                .audience(properties.audience())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(properties.accessTokenTtl())))
                .jwtID(UUID.randomUUID().toString())
                .claim("roles", roleNames(user))
                .claim("permissions", permissionNames(user))
                .build();

        SignedJWT jwt = new SignedJWT(header, claims);
        try {
            jwt.sign(signer);
        } catch (JOSEException e){
            throw new IllegalStateException("Failed to sign access token", e);
        }

        return jwt.serialize();
    }

    private static List<String> roleNames(UserEntity user){
        return user.getRoles().stream()
                .map(RoleEntity::getName)
                .sorted()
                .toList();
    }

    private static List<String> permissionNames(UserEntity user){
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(PermissionEntity::getName)
                .distinct()
                .sorted(Comparator.naturalOrder())
                .toList();
    }
}
