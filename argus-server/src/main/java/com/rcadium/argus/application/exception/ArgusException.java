package com.rcadium.argus.application.exception;

import com.rcadium.argus.client.ErrorCode;

public abstract class ArgusException extends RuntimeException {

    private final ErrorCode code;

    protected ArgusException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode code() {
        return code;
    }

}
