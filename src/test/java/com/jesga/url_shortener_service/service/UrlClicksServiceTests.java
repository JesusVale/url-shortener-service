package com.jesga.url_shortener_service.service;

import com.jesga.url_shortener_service.dto.analytics.DailyClick;
import com.jesga.url_shortener_service.dto.analytics.ShortenUrlAnalytics;
import com.jesga.url_shortener_service.dto.analytics.TopUserAgent;
import com.jesga.url_shortener_service.entities.ShortUrl;
import com.jesga.url_shortener_service.entities.UrlClicks;
import com.jesga.url_shortener_service.exception.custom.URLNotFoundException;
import com.jesga.url_shortener_service.repository.ShortUrlRepository;
import com.jesga.url_shortener_service.repository.UrlClicksRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UrlClicksServiceTests {

    @Mock
    UrlClicksRepository urlClicksRepository;

    @Mock
    ShortUrlRepository shortUrlRepository;

    @InjectMocks
    UrlClicksService urlClicksService;

    @Test
    public void urlClicksService_ShouldSaveUrlClick() {

        //Arrange
        String shortCode = "abc123";
        String ip = "192.493.0.1";
        String userAgent = "J-Unit-Test";


        //Act
        urlClicksService.logClick(shortCode, ip, userAgent);

        //Assert
        ArgumentCaptor<UrlClicks> argumentCaptor = ArgumentCaptor.forClass(UrlClicks.class);

        verify(urlClicksRepository).save(argumentCaptor.capture());

        UrlClicks saved = argumentCaptor.getValue();

        Assertions.assertEquals(shortCode, saved.getShortCode());
        Assertions.assertEquals(ip, saved.getIpAddress());
        Assertions.assertEquals(userAgent, saved.getUserAgent());
        Assertions.assertNotNull(saved.getClickedAt());

    }

    @Test
    public void urlClicksService_ShouldReturnShortenUrlAnalytics() {

        //Arrange
        String shortCode = "abc123";

        ShortUrl shortUrl = ShortUrl.builder()
                .shortCode(shortCode)
                .clickCount(5)
                .id(1L)
                .originalUrl("https://www.youtube.com/watch?v=A29fvYHNfqw&list=RDA29fvYHNfqw&start_radio=1")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(2))
                .clickCount(0).build();

        DailyClick dailyClick1 = Mockito.mock(DailyClick.class);
        DailyClick dailyClick2 = Mockito.mock(DailyClick.class);

        TopUserAgent topUserAgent = new TopUserAgent(
                "Chrome",
                2L
        );

        Mockito.when(shortUrlRepository.findByShortCode(shortCode)).thenReturn(Optional.ofNullable(shortUrl));
        Mockito.when(urlClicksRepository.getDailyClicks(shortCode)).thenReturn(List.of(dailyClick1, dailyClick2));
        Mockito.when(urlClicksRepository.getTopUserAgents(shortCode)).thenReturn(List.of(topUserAgent));
        Mockito.when(urlClicksRepository.getTotalClicks(shortCode)).thenReturn(5);

        //Act
        ShortenUrlAnalytics shortenUrlAnalytics = urlClicksService.getStats(shortCode);

        //Assert
        Assertions.assertNotNull(shortenUrlAnalytics);
        Assertions.assertEquals(2L, shortenUrlAnalytics.dailyClicks().size());
        Assertions.assertEquals(1L, shortenUrlAnalytics.topUsers().size());
        Assertions.assertEquals(5, shortenUrlAnalytics.totalClicks());


    }

    @Test
    public void urlClicksService_ShouldThrowURLNotFoundException() {

        String shortCode = "fsf3x";

        Mockito.when(shortUrlRepository.findByShortCode(shortCode)).thenReturn(Optional.empty());

        URLNotFoundException thrown = Assertions.assertThrows(URLNotFoundException.class, () -> urlClicksService.getStats(shortCode));

        Assertions.assertTrue(thrown.getMessage().contains(shortCode));
        verify(shortUrlRepository).findByShortCode(shortCode);

    }

}
