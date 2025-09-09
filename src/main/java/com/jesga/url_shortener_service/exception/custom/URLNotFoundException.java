package com.jesga.url_shortener_service.exception.custom;

public class URLNotFoundException extends RuntimeException {
    public URLNotFoundException(String shortCode) {
        super("URL with code " + shortCode + " not found.");
    }
}
