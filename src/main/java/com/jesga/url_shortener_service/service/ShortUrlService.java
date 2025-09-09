package com.jesga.url_shortener_service.service;

import com.jesga.url_shortener_service.entities.ShortUrl;
import com.jesga.url_shortener_service.exception.custom.ExpiredUrlException;
import com.jesga.url_shortener_service.exception.custom.URLNotFoundException;
import com.jesga.url_shortener_service.repository.ShortUrlRepository;
import com.jesga.url_shortener_service.util.Base62Encoder;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class ShortUrlService {

    private ShortUrlRepository shortUrlRepository;

    @CachePut(value = "SHORT_CACHE", key = "#result.getShortCode()")
    public ShortUrl shorten(String originalUrl) {

        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl(originalUrl);
        shortUrl.setCreatedAt(LocalDateTime.now());
        shortUrl.setExpiresAt(LocalDateTime.now().plusDays(2));
        shortUrl.setClickCount(0);

        ShortUrl urlSaved = shortUrlRepository.save(shortUrl);

        String shortCode = Base62Encoder.encode(urlSaved.getId());

        urlSaved.setShortCode(shortCode);

        return shortUrlRepository.save(urlSaved);

    }

    @Cacheable(value = "SHORT_CACHE", key = "#shortCode")
    public ShortUrl getOriginalUrl(String shortCode) {

        ShortUrl shortUrlFound = shortUrlRepository.findByShortCode(shortCode)
                    .orElseThrow(() -> new URLNotFoundException(shortCode));

        if(shortUrlFound.getExpiresAt() != null && shortUrlFound.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ExpiredUrlException(shortCode);
        }

        shortUrlFound.setClickCount(shortUrlFound.getClickCount() + 1);
        shortUrlRepository.save(shortUrlFound);


        return shortUrlFound;

    }


}
