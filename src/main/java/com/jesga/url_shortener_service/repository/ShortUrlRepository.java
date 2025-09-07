package com.jesga.url_shortener_service.repository;

import com.jesga.url_shortener_service.entities.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    public Optional<ShortUrl> findByShortCode(String shortCode);

}
