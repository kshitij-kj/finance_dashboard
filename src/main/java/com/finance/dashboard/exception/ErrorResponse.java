package com.finance.dashboard.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        String code,
        String message,
        Map<String, String> fieldErrors,   // populated only for validation failures
        LocalDateTime timestamp
) {

    // Convenience factory for simple errors (no field errors)
    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, null, LocalDateTime.now());
    }

    public static ErrorResponse ofValidation(Map<String, String> fieldErrors) {
        return new ErrorResponse(
                "VALIDATION_FAILED",
                "One or more fields are invalid",
                fieldErrors,
                LocalDateTime.now()
        );
    }
}
