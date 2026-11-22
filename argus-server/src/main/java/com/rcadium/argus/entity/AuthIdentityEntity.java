package com.rcadium.argus.entity;

import com.rcadium.argus.enums.AuthMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "auth_identities")
@Getter
@NoArgsConstructor
public class AuthIdentityEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String provider;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public AuthIdentityEntity(UserEntity user, AuthMethod method, String providerUserId) {
        this.user = user;
        this.provider = method.provider();
        this.providerUserId = providerUserId;
    }
}
