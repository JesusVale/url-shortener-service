package com.jesga.url_shortener_service.service;

import com.jesga.url_shortener_service.dto.analytics.DailyClick;
import com.jesga.url_shortener_service.dto.analytics.ShortenUrlAnalytics;
import com.jesga.url_shortener_service.dto.analytics.TopUserAgent;
import com.jesga.url_shortener_service.entities.UrlClicks;
import com.jesga.url_shortener_service.exception.custom.URLNotFoundException;
import com.jesga.url_shortener_service.repository.ShortUrlRepository;
import com.jesga.url_shortener_service.repository.UrlClicksRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class UrlClicksService {

    private ShortUrlRepository shortUrlRepository;
    private UrlClicksRepository repository;

    public void logClick(String shortCode, String ip, String userAgent) {

        UrlClicks urlClicks = new UrlClicks();
        urlClicks.setClickedAt(LocalDateTime.now());
        urlClicks.setIpAddress(ip);
        urlClicks.setShortCode(shortCode);
        urlClicks.setUserAgent(userAgent);

        repository.save(urlClicks);

    }

    public ShortenUrlAnalytics getStats(String shortCode) {

        if(shortUrlRepository.findByShortCode(shortCode).isEmpty()) {
            throw new URLNotFoundException(shortCode);
        }

        List<DailyClick> dailyClickList = repository.getDailyClicks(shortCode);
        Integer totalClicks = repository.getTotalClicks(shortCode);
        List<TopUserAgent> topUserAgentList = repository.getTopUserAgents(shortCode);

        return new ShortenUrlAnalytics(
                totalClicks,
                dailyClickList,
                topUserAgentList
        );

    }

}
