package com.coaxial.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple health check controller for Railway deployment
 */
@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class HealthController {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    /**
     * Root endpoint - Railway health check
     */
    @GetMapping
    public ResponseEntity<?> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Coaxial Backend API is running");
        response.put("port", serverPort);
        response.put("profile", activeProfile);
        response.put("swagger", "/swagger-ui.html");
        response.put("apiDocs", "/v3/api-docs");
        response.put("health", "/actuator/health");
        return ResponseEntity.ok(response);
    }

    /**
     * Health endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Coaxial Backend");
        return ResponseEntity.ok(response);
    }

    /**
     * Status endpoint for Railway
     */
    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "application", "coaxial-backend",
            "version", "1.0.0"
        ));
    }
}
