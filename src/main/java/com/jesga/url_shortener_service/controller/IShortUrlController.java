package com.jesga.url_shortener_service.controller;

import com.jesga.url_shortener_service.dto.ShortenUrlRequest;
import com.jesga.url_shortener_service.dto.analytics.ShortenUrlAnalytics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface IShortUrlController {

    @Operation(
            summary = "Shorten a URL",
            description = "Accepts a full URL and returns a shortened version"
    )
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Url successfully shortened",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(type = "string", example = "https://short.ly/abc123")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid url",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Example invalid url",
                                    value = """
                                            {
                                                "message": "URL Invalid",
                                                "status": 400,
                                                "timestamp": "2025-09-17T21:13:57.1113709"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/shorten")
    ResponseEntity<String> shortenUrl(@Valid @RequestBody ShortenUrlRequest urlRequest);


    @Operation(
            summary = "Redirects to the original URL from a short code",
            description = """
                    Given a short code, this endpoint attempts to find the original URL
                    and redirects the client to it.
                    """,
            parameters = {
                    @Parameter(
                            name = "shortCode",
                            description = "The short code associated with the original URL",
                            required = true,
                            example = "abc123"
                    )
            }
    )
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "302",
                    description = "Redirection successful. The Location header contains the original URL",
                    headers = {
                            @Header(name = "Location", description = "The original URL the client is redirected to")
                    }
            ),
            @ApiResponse( responseCode = "410",
                    description = "The short code exists but the URL has expired",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Example expired URL",
                                    value = """
                                            {
                                                "message": "The URL with code 1 has expired",
                                                "status": 410,
                                                "timestamp": "2025-09-13T19:39:39.2145699"
                                            }
                                            """
                            )
                    )

            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No URL was found for the provided short code",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Example URL not found",
                                    value = """
                                            {
                                                "message": "URL with code 15343 not found.",
                                                "status": 404,
                                                "timestamp": "2025-09-17T21:29:01.9356024"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{shortCode}")
    ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request);

    @Operation(
            summary = "Retrieves stats of a url by short code",
            description = """
                    Returns analytics data for the given short code, including:
                    - Total clicks
                    - Daily click counts
                    - Most common user agents
                    """,
            parameters = {
                    @Parameter(
                            name = "shortCode",
                            description = "Thr short code to retrieve the stats for",
                            required = true,
                            example = "abc123"
                    )
            }
    )
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Stats retrieves successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Example stats",
                                    value = """
                                            {
                                                "totalClicks": 1,
                                                "dailyClicks": [
                                                    {
                                                        "date": "2025-09-08",
                                                        "totalClicks": 1
                                                    }
                                                ],
                                                "topUsers": [
                                                    {
                                                        "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/ (KHTML, like Gecko) Chrome Safari",
                                                        "totalClicks": 1
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Url not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Example URL not found",
                                    value = """
                                            {
                                                "message": "URL with code 15343 not found.",
                                                "status": 404,
                                                "timestamp": "2025-09-17T21:29:01.9356024"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/stats/{shortCode}")
    public ResponseEntity<ShortenUrlAnalytics> getStatsUrl(@PathVariable String shortCode);

}
