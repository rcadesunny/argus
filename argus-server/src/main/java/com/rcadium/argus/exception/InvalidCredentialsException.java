package com.rcadium.argus.exception;

import com.rcadium.argus.enums.ErrorCode;

public class InvalidCredentialsException extends ArgusException {
    public InvalidCredentialsException(){
        super(ErrorCode.INVALID_CREDENTIALS, "Invalid email or password");
    }
}
