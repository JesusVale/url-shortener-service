package com.jesga.url_shortener_service.controller;

import com.jesga.url_shortener_service.ShortUrlService;
import com.jesga.url_shortener_service.entities.ShortUrl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@AllArgsConstructor
@RestController
public class ShortUrlController {

    private ShortUrlService service;

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestBody String originalUrl) {

        ShortUrl shortUrl = service.shorten(originalUrl);

        String shortenUrl = "http://localhost:8081" + "/" + shortUrl.getShortCode();

        return ResponseEntity.ok().body(shortenUrl);

    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {

        ShortUrl shortUrlSaved = service.getOriginalUrl(shortCode);

        if(shortUrlSaved == null) {
            return ResponseEntity.status(HttpStatus.GONE).build();
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(shortUrlSaved.getOriginalUrl()))
                .build();

    }

}
