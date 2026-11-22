package com.rcadium.argus.domain;

import java.util.Objects;
import java.util.Set;

public record Role(
        String name,
        Set<Permission> permissions
) {
    public Role {
        Objects.requireNonNull(name, "role name is required");
        permissions = permissions == null ? Set.of() : Set.copyOf(permissions);
    }

    public String authority() {
        return "ROLE_" + name;
    }

}
