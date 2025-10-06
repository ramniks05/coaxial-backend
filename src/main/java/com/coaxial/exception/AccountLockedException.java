package com.coaxial.exception;

public class AccountLockedException extends RuntimeException {
    
    public AccountLockedException(String message) {
        super(message);
    }

    public AccountLockedException(String message, Throwable cause) {
        super(message, cause);
    }
}
