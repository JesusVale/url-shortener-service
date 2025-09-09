package com.jesga.url_shortener_service.exception.custom;

public class ExpiredUrlException extends RuntimeException{

    public ExpiredUrlException(String shortCode) {

        super("The URL with code " + shortCode + " has expired");

    }

}
