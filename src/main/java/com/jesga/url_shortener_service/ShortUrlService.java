package com.jesga.url_shortener_service;

import com.jesga.url_shortener_service.entities.ShortUrl;
import com.jesga.url_shortener_service.repository.ShortUrlRepository;
import com.jesga.url_shortener_service.util.Base62Encoder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ShortUrlService {

    private ShortUrlRepository shortUrlRepository;

    public ShortUrl shorten(String originalUrl) {

        //TODO VALIDATE VALID URL

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

    public ShortUrl getOriginalUrl(String shortCode) {

        //TODO VALIDATE EXPIRATION DATE

        Optional<ShortUrl> shortUrlFound = shortUrlRepository.findByShortCode(shortCode);

        shortUrlFound.ifPresent(shortUrl -> {
            shortUrl.setClickCount(shortUrl.getClickCount() + 1);
            shortUrlRepository.save(shortUrl);
        });

        return shortUrlFound.orElse(null);

    }


}
