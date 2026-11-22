package com.rcadium.argus.application.exception;

import com.rcadium.argus.client.ErrorCode;

public class InvalidRefreshTokenException extends ArgusException {

    public InvalidRefreshTokenException() {
        super(ErrorCode.INVALID_REFRESH_TOKEN, "Refresh token is invalid or expired");
    }
}
