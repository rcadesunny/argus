package com.rcadium.argus.domain;

public enum UserStatus {
    ACTIVE,
    LOCKED,
    DISABLED;

    public boolean canAuthenticate(){
        return this == ACTIVE;
    }
}
