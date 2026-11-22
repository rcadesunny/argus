package com.rcadium.argus.service.auth;

import com.rcadium.argus.enums.AuthMethod;

public sealed interface AuthCredentials permits EmailPasswordCredentials {
    AuthMethod method();
}
