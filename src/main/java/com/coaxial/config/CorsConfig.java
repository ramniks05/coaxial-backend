package com.coaxial.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Build list of allowed origin patterns
        List<String> originPatterns = new ArrayList<>();
        
        // Always allow localhost for development
        originPatterns.addAll(Arrays.asList(
            "http://localhost:*",
            "https://localhost:*",
            "http://127.0.0.1:*",
            "https://127.0.0.1:*"
        ));
        
        // Always allow Railway backend domain (for Swagger to work)
        originPatterns.add("https://*.up.railway.app");
        
        // Add specific origins from configuration
        if (allowedOrigins != null && !allowedOrigins.trim().isEmpty() 
            && !allowedOrigins.equals("https://yourdomain.com")) {
            String[] origins = allowedOrigins.split(",");
            for (String origin : origins) {
                String trimmed = origin.trim();
                if (!trimmed.isEmpty() && !trimmed.equals("https://yourdomain.com")) {
                    originPatterns.add(trimmed);
                }
            }
        }
        
        configuration.setAllowedOriginPatterns(originPatterns);
        
        // Set allowed methods
        configuration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
        
        // Set allowed headers - more comprehensive
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Cache-Control",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Allow credentials
        configuration.setAllowCredentials(true);
        
        // Set exposed headers
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Cache-Control",
            "Content-Type",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        
        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
