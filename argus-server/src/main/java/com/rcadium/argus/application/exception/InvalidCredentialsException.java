package com.rcadium.argus.application.exception;

import com.rcadium.argus.client.ErrorCode;

public class InvalidCredentialsException extends ArgusException {

    public InvalidCredentialsException() {
        super(ErrorCode.INVALID_CREDENTIALS, "Invalid email or password");
    }
}
