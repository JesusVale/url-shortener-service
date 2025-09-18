package com.jesga.url_shortener_service.controller;

import com.jesga.url_shortener_service.dto.ShortenUrlRequest;
import com.jesga.url_shortener_service.dto.analytics.ShortenUrlAnalytics;
import com.jesga.url_shortener_service.service.ShortUrlService;
import com.jesga.url_shortener_service.entities.ShortUrl;
import com.jesga.url_shortener_service.service.UrlClicksService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@AllArgsConstructor
@RestController
public class ShortUrlController implements IShortUrlController {

    private ShortUrlService service;
    private UrlClicksService clicksLog;

    @Override
    public ResponseEntity<String> shortenUrl(ShortenUrlRequest urlRequest) {

        ShortUrl shortUrl = service.shorten(urlRequest.originalUrl());

        String shortenUrl = "http://localhost:8081" + "/" + shortUrl.getShortCode();

        return ResponseEntity.ok().body(shortenUrl);

    }

    @Override
    public ResponseEntity<Void> redirect(String shortCode, HttpServletRequest request) {

        ShortUrl shortUrlSaved = service.getOriginalUrl(shortCode);

        clicksLog.logClick(shortCode, request.getRemoteAddr(), request.getHeader("User-Agent"));

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(shortUrlSaved.getOriginalUrl()))
                .build();

    }

    @Override
    public ResponseEntity<ShortenUrlAnalytics> getStatsUrl(@PathVariable String shortCode) {
        ShortenUrlAnalytics stats = clicksLog.getStats(shortCode);
        return ResponseEntity.ok(stats);
    }
}
