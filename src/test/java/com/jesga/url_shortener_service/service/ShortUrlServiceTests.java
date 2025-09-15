package com.jesga.url_shortener_service.service;

import com.jesga.url_shortener_service.entities.ShortUrl;
import com.jesga.url_shortener_service.exception.custom.ExpiredUrlException;
import com.jesga.url_shortener_service.exception.custom.URLNotFoundException;
import com.jesga.url_shortener_service.repository.ShortUrlRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShortUrlServiceTests {

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @InjectMocks
    private ShortUrlService shortUrlService;

    @Test
    public void shorten_ReturnsShortUrl() {

        ShortUrl shortUrl1 = ShortUrl.builder()
                .id(1L)
                .originalUrl("https://www.youtube.com/watch?v=A29fvYHNfqw&list=RDA29fvYHNfqw&start_radio=1")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(2))
                .clickCount(0).build();

        ShortUrl shortUrl2 = ShortUrl.builder()
                .id(1L)
                .originalUrl("https://www.youtube.com/watch?v=A29fvYHNfqw&list=RDA29fvYHNfqw&start_radio=1")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(2))
                .shortCode("1")
                .clickCount(0).build();

        when(shortUrlRepository.save(Mockito.any(ShortUrl.class)))
                .thenReturn(shortUrl1)
                .thenReturn(shortUrl2);

        ShortUrl shortUrlSaved = shortUrlService.shorten("https://www.youtube.com/watch?v=A29fvYHNfqw&list=RDA29fvYHNfqw&start_radio=1");

        Assertions.assertNotNull(shortUrlSaved);
        verify(shortUrlRepository, times(2)).save(Mockito.any(ShortUrl.class));

    }

    @Test
    public void getOriginalUrl_ReturnsShortUrl() {

        //Arrange
        ShortUrl shortUrl1 = ShortUrl.builder()
                .id(1L)
                .originalUrl("https://www.youtube.com/watch?v=A29fvYHNfqw&list=RDA29fvYHNfqw&start_radio=1")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(2))
                .shortCode("abc123")
                .clickCount(0).build();

        ShortUrl shortUrlClickedUpdated = ShortUrl.builder()
                .id(1L)
                .originalUrl("https://www.youtube.com/watch?v=A29fvYHNfqw&list=RDA29fvYHNfqw&start_radio=1")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(2))
                .shortCode("abc123")
                .clickCount(1).build();

        //Act
        Mockito.when(shortUrlRepository.findByShortCode("abc123")).thenReturn(Optional.ofNullable(shortUrl1));
        Mockito.when(shortUrlRepository.save(any(ShortUrl.class))).thenReturn(shortUrlClickedUpdated);

        ShortUrl shortUrlFound = shortUrlService.getOriginalUrl("abc123");

        //Assert
        Assertions.assertNotNull(shortUrlFound);
        Assertions.assertEquals("abc123", shortUrlFound.getShortCode());
        Assertions.assertEquals("https://www.youtube.com/watch?v=A29fvYHNfqw&list=RDA29fvYHNfqw&start_radio=1", shortUrlFound.getOriginalUrl());

        verify(shortUrlRepository).findByShortCode("abc123");
        verify(shortUrlRepository).save(any(ShortUrl.class));
    }

    @Test
    public void getOriginalUrl_ThrowsURLNotFoundException() {

        String shortCode = "trhd";
        URLNotFoundException exUrl = new URLNotFoundException(shortCode);

        //Act & Assert
        Mockito.when(shortUrlRepository.findByShortCode(shortCode)).thenThrow(exUrl);

        URLNotFoundException thrown = Assertions.assertThrows(URLNotFoundException.class,
                () -> shortUrlService.getOriginalUrl(shortCode)
        );

        Assertions.assertTrue(thrown.getMessage().contains(shortCode));
        verify(shortUrlRepository).findByShortCode(shortCode);

    }


    @Test
    public void getOriginalUrl_ThrowsExpiredUrlException() {

        ShortUrl shortUrl1 = ShortUrl.builder()
                .id(1L)
                .originalUrl("https://www.youtube.com/watch?v=A29fvYHNfqw&list=RDA29fvYHNfqw&start_radio=1")
                .createdAt(LocalDateTime.now().minusDays(5))

                .expiresAt(LocalDateTime.now().minusDays(2))
                .shortCode("abc123")
                .clickCount(0).build();

        //Act & Assert
        Mockito.when(shortUrlRepository.findByShortCode("abc123")).thenReturn(Optional.ofNullable(shortUrl1));

        ExpiredUrlException thrown = Assertions.assertThrows(ExpiredUrlException.class,
                () -> shortUrlService.getOriginalUrl("abc123")
        );

        Assertions.assertTrue(thrown.getMessage().contains("abc123"));
        verify(shortUrlRepository).findByShortCode("abc123");
    }


}
