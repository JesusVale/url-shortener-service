package com.jesga.url_shortener_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jesga.url_shortener_service.dto.analytics.DailyClick;
import com.jesga.url_shortener_service.dto.analytics.ShortenUrlAnalytics;
import com.jesga.url_shortener_service.dto.analytics.TopUserAgent;
import com.jesga.url_shortener_service.entities.ShortUrl;
import com.jesga.url_shortener_service.service.ShortUrlService;
import com.jesga.url_shortener_service.service.UrlClicksService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ShortUrlController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class ShortUrlControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ShortUrlService shortUrlService;

    @MockitoBean
    UrlClicksService clicksLog;

    @Test
    public void shortenUrl_ReturnsString() throws Exception {

        ShortUrl shortUrl1 = ShortUrl.builder()
                .id(1L)
                .originalUrl("https://example.com")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(2))
                .shortCode("abcd1234")
                .clickCount(0).build();

        Mockito.when(shortUrlService.shorten(anyString())).thenReturn(shortUrl1);

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\":\"https://example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("http://localhost:8081/abcd1234"));

    }

    @Test
    public void shortenUrl_WhenUrlInvalid() throws Exception {

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\":\"ewfdsfdvd\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("URL Invalid"));

    }

    @Test
    public void redirect_ShouldRedirectToUrl() throws Exception {

        String shortCode = "abcd1234";
        ShortUrl shortUrlSaved = ShortUrl.builder()
                .id(1L)
                .originalUrl("https://example.com")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(2))
                .shortCode(shortCode)
                .clickCount(0).build();

        String userAgent = "Chrome";
        String remoteAddr = "127.0.0.1";

        Mockito.when(shortUrlService.getOriginalUrl(shortCode)).thenReturn(shortUrlSaved);

        mockMvc.perform(get("/" + shortCode)
                        .header("User-Agent", userAgent)
                        .with(request -> {
                            request.setRemoteAddr(remoteAddr);
                            return request;
                        }))
                .andExpect(status().isFound())
                .andExpect(header().string("Location","https://example.com"));

        Mockito.verify(clicksLog).logClick(shortCode, remoteAddr, userAgent);
    }

    @Test
    public void getStatsUrl_ReturnsAnalytics() throws Exception {

        String shortCode = "abcd1234";

        TopUserAgent topUserAgent = new TopUserAgent(
                "Chrome",
                2L
        );

        ShortenUrlAnalytics shortenUrlAnalytics = new ShortenUrlAnalytics(
                5,
                    List.of(),
                    List.of(topUserAgent)
        );

        Mockito.when(clicksLog.getStats(shortCode)).thenReturn(shortenUrlAnalytics);

        mockMvc.perform(get("/stats/" + shortCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalClicks").value(5))
                .andExpect(jsonPath("$.topUsers[0].userAgent").value("Chrome"));

        Mockito.verify(clicksLog).getStats(shortCode);

    }

}






