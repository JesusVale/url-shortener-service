package com.jesga.url_shortener_service.dto.analytics;

import java.time.LocalDate;

public interface DailyClick {
    LocalDate getDate();
    Long getTotalClicks();
}
