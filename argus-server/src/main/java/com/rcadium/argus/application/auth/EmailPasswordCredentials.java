package com.rcadium.argus.application.auth;

import java.util.Objects;

public record EmailPasswordCredentials (String email, String password) implements AuthCredentials {
    public EmailPasswordCredentials {
        Objects.requireNonNull(email, "email is required");
        Objects.requireNonNull(password, "password is required");
    }
}
