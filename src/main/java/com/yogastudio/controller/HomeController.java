package com.yogastudio.controller;

import com.yogastudio.dto.WelcomeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Punctul de intrare in API: spune ce aplicatie ruleaza si ce endpoint-uri exista.
 * Sta pe /api (nu pe /) pentru ca / serveste aplicatia React.
 */
@RestController
public class HomeController {

    private static final List<String> AVAILABLE_ENDPOINTS = List.of(
            "/health",
            "/api/classes",
            "/api/instructors",
            "/api/students",
            "/api/bookings",
            "/api/assistant?question=...",
            "/swagger-ui/index.html"
    );

    private static final String AUTHENTICATION =
            "HTTP Basic: GET e public, POST/PUT/PATCH/DELETE cer autentificare";

    private final WelcomeResponse welcome;

    public HomeController(
            @Value("${app.info.name}") String appName,
            @Value("${app.info.version}") String version,
            @Value("${app.info.description}") String description,
            @Value("${app.info.live-since}") String liveSince) {
        this.welcome = new WelcomeResponse(
                appName,
                version,
                "Running",
                description,
                AVAILABLE_ENDPOINTS,
                AUTHENTICATION,
                liveSince
        );
    }

    @GetMapping("/api")
    public WelcomeResponse welcome() {
        return welcome;
    }
}