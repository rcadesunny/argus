package com.rcadium.argus.domain;

import java.util.Objects;

public record Permission(String name) {

    public Permission {
        Objects.requireNonNull(name, "permission name is required");
    }
}
