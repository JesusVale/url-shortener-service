package com.jesga.url_shortener_service.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record ShortenUrlRequest(
        @NotBlank
        @URL
        String originalUrl
) {
}
