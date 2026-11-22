package com.rcadium.argus.service.auth;

import com.rcadium.argus.entity.UserEntity;
import com.rcadium.argus.enums.AuthMethod;

public interface AuthenticationStrategy {

    AuthMethod method();

    UserEntity authenticate(AuthCredentials credentials);
}
