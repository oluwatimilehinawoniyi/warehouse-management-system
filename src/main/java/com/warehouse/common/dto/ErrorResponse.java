package com.warehouse.common.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        Object errors,
        LocalDateTime timestamp,
        String path,
        int status
) {
    public ErrorResponse(String message,
                         Object errors,
                         String path,
                         int status) {
        this(message, errors, LocalDateTime.now(), path, status);
    }
}
