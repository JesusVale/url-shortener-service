package com.jesga.url_shortener_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record ShortenUrlRequest(
        @NotBlank(message = "URL is blank")
        @URL(message = "URL Invalid")
        @Schema(
                description = "The original URL that will be shortened",
                example = "https://example.com"
        )
        String originalUrl
) {
}