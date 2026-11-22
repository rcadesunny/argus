package com.rcadium.argus.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class User {
    private final UUID id;
    private final String email;
    private String passwordHash;
    private UserStatus status;
    private final Set<Role> roles;
    private final Instant createdAt;
    private Instant updatedAt;

    public User (
            UUID id,
            String email,
            String passwordHash,
            UserStatus status,
            Set<Role> roles,
            Instant createdAt,
            Instant updatedAt){
        this.id = Objects.requireNonNull(id, "id is required");
        this.email = Objects.requireNonNull(email, "email is required");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash is required");
        this.status = Objects.requireNonNull(status, "status is required");
        this.roles = Set.copyOf(roles == null ? Set.of() : roles);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");
    }

    public boolean canAuthenticate(){
        return status.canAuthenticate();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    public Set<String> authorities() {
        return roles.stream()
                .flatMap(role -> java.util.stream.Stream.concat(
                        java.util.stream.Stream.of(role.authority()),
                        role.permissions().stream().map(Permission::name)))
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    public void lock(){
        this.status = UserStatus.LOCKED;
        touch();
    }

    public void changePassword(String newPasswordHash){
        this.passwordHash = Objects.requireNonNull(newPasswordHash, "passwordHash is required");
        touch();
    }

    public UUID id() {
        return id;
    }

    public String email() {
        return email;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public UserStatus status() {
        return status;
    }

    public Set<Role> roles() {
        return roles;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
