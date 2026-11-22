package com.rcadium.argus.service.auth;

import com.rcadium.argus.enums.AuthMethod;

public record EmailPasswordCredentials (
        String email,
        String password
) implements AuthCredentials {

    @Override
    public AuthMethod method(){
        return AuthMethod.EMAIL_PASSWORD;
    }
}
