package com.example.backend.common.response;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        List<FieldError> errors,
        String traceId
) {

    public record FieldError(
            String field,
            String message
    ) {
    }
}