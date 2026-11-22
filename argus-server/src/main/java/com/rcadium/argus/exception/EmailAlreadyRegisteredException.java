package com.rcadium.argus.exception;

import com.rcadium.argus.enums.ErrorCode;

public class EmailAlreadyRegisteredException extends ArgusException {
    public EmailAlreadyRegisteredException(){
        super(ErrorCode.EMAIL_ALREADY_REGISTERED, "Email already registered");
    }
}
