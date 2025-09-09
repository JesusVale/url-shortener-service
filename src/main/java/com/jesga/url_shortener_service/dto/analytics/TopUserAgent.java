package com.jesga.url_shortener_service.dto.analytics;

public record TopUserAgent(
        String userAgent,
        Long totalClicks
) {

}
