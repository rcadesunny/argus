package com.rcadium.argus.application.auth;

import com.rcadium.argus.domain.AuthMethod;
import com.rcadium.argus.domain.User;

public interface AuthenticationStrategy {
    AuthMethod method();

    User authenticate(AuthCredentials credentials);
}
