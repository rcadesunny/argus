package com.rcadium.argus.application.exception;

import com.rcadium.argus.client.ErrorCode;

public class AccountNotActiveException extends ArgusException {

    public AccountNotActiveException() {
        super(ErrorCode.ACCOUNT_NOT_ACTIVE, "Account is not active");
    }
}
