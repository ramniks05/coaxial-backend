package com.coaxial.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.lang.NonNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts Railway's DATABASE_URL (postgresql:// format) to JDBC format
 * This runs before Spring Boot reads datasource properties
 */
public class DatabaseConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String databaseUrl = environment.getProperty("DB_URL");
        
        // If DB_URL is in postgresql:// format, convert to JDBC format
        if (databaseUrl != null && databaseUrl.startsWith("postgresql://")) {
            try {
                URI dbUri = new URI(databaseUrl);
                
                String username = dbUri.getUserInfo() != null ? dbUri.getUserInfo().split(":")[0] : null;
                String password = dbUri.getUserInfo() != null && dbUri.getUserInfo().split(":").length > 1 
                    ? dbUri.getUserInfo().split(":")[1] : null;
                String host = dbUri.getHost();
                int port = dbUri.getPort() != -1 ? dbUri.getPort() : 5432;
                String database = dbUri.getPath() != null ? dbUri.getPath().replaceFirst("/", "") : "railway";
                
                // Build JDBC URL
                String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
                
                // Override properties
                Map<String, Object> properties = new HashMap<>();
                properties.put("spring.datasource.url", jdbcUrl);
                if (username != null && environment.getProperty("spring.datasource.username") == null) {
                    properties.put("spring.datasource.username", username);
                }
                if (password != null && environment.getProperty("spring.datasource.password") == null) {
                    properties.put("spring.datasource.password", password);
                }
                
                environment.getPropertySources().addFirst(
                    new MapPropertySource("databaseUrlConfig", properties)
                );
                
            } catch (URISyntaxException e) {
                System.err.println("Failed to parse DB_URL: " + e.getMessage());
            }
        }
    }
}

