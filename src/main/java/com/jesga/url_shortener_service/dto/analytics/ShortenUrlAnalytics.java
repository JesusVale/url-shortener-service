package com.jesga.url_shortener_service.dto.analytics;

import java.util.List;

public record ShortenUrlAnalytics(
        Integer totalClicks,
        List<DailyClick> dailyClicks,
        List<TopUserAgent> topUsers
) {
}
