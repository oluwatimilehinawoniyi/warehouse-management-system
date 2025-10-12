package com.warehouse.common.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        LocalDateTime timestamp,
        String path,
        int status
) {
    public ErrorResponse(String message,
                         String path,
                         int status) {
        this(message, LocalDateTime.now(), path, status);
    }
}
