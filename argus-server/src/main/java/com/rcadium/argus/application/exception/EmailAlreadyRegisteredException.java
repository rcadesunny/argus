package com.rcadium.argus.application.exception;

import com.rcadium.argus.client.ErrorCode;

public class EmailAlreadyRegisteredException extends ArgusException {

    public EmailAlreadyRegisteredException() {
        super(ErrorCode.EMAIL_ALREADY_REGISTERED, "Email is already registered");
    }
}
