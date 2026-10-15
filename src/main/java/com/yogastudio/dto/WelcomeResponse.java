package com.yogastudio.dto;

import java.util.List;

/**
 * Raspunsul pentru endpoint-ul de welcome (GET /api).
 * Un record pastreaza ordinea campurilor in JSON, spre deosebire de Map.of().
 */
public record WelcomeResponse(
        String appName,
        String version,
        String status,
        String message,
        List<String> availableEndpoints,
        String authentication,
        String liveSince
) {
}