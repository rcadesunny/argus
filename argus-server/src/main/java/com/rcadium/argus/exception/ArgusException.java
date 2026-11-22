package com.rcadium.argus.exception;

import com.rcadium.argus.ArgusServerApplication;
import com.rcadium.argus.enums.ErrorCode;

public class ArgusException extends RuntimeException {
    private final ErrorCode code;

    protected ArgusException(ErrorCode code, String message){
        super(message);
        this.code = code;
    }

    public ErrorCode code() {
        return code;
    }
}
