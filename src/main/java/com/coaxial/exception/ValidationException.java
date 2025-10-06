package com.coaxial.exception;

import java.util.List;

public class ValidationException extends RuntimeException {
    private List<String> validationErrors;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, List<String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}
