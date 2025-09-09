package com.jesga.url_shortener_service.repository;

import com.jesga.url_shortener_service.dto.analytics.DailyClick;
import com.jesga.url_shortener_service.dto.analytics.TopUserAgent;
import com.jesga.url_shortener_service.entities.UrlClicks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

@Repository
public interface UrlClicksRepository extends JpaRepository<UrlClicks, Long> {

    public Optional<UrlClicks> findByShortCode(String shortCode);

    @Query("""
            SELECT COUNT(U)
            FROM UrlClicks U
            WHERE U.shortCode = :shortCode
            """)
    public Integer getTotalClicks(String shortCode);

    @Query(value = """
            SELECT
            CAST(U.clicked_at AS DATE) as date,
            COUNT(*) as totalClicks
            FROM url_clicks U
            WHERE U.short_code = :shortCode
            GROUP BY CAST(U.clicked_at AS DATE)
            ORDER BY CAST(U.clicked_at AS DATE)
            """, nativeQuery = true)
    public List<DailyClick> getDailyClicks(String shortCode);

    @Query(value = """
            SELECT
            U.user_agent as userAgent,
            COUNT(*) as totalClicks
            FROM url_clicks U
            WHERE U.short_code = :shortCode
            GROUP BY userAgent
            """, nativeQuery = true)
    public List<TopUserAgent> getTopUserAgents(String shortCode);

}