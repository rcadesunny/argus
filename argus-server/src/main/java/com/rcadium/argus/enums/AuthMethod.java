package com.rcadium.argus.enums;

public enum AuthMethod {
    EMAIL_PASSWORD("email_password");

    private final String provider;

    AuthMethod(String provider){
        this.provider = provider;
    }

    public String provider(){
        return provider;
    }
}
