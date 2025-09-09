package com.jesga.url_shortener_service.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        Integer status,
        LocalDateTime timestamp
) {
}
