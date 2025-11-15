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
        
        // Read DB_URL directly from system environment variables (available earlier)
        String databaseUrl = System.getenv("DB_URL");
        
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
                
                // Override properties - always set these to ensure they take precedence
                Map<String, Object> properties = new HashMap<>();
                properties.put("spring.datasource.url", jdbcUrl);
                
                // Use DB_USERNAME and DB_PASSWORD from environment if available, otherwise use parsed values
                String dbUsername = System.getenv("DB_USERNAME");
                String dbPassword = System.getenv("DB_PASSWORD");
                
                if (dbUsername != null && !dbUsername.isEmpty()) {
                    properties.put("spring.datasource.username", dbUsername);
                } else if (username != null) {
                    properties.put("spring.datasource.username", username);
                }
                
                if (dbPassword != null && !dbPassword.isEmpty()) {
                    properties.put("spring.datasource.password", dbPassword);
                } else if (password != null) {
                    properties.put("spring.datasource.password", password);
                }
                
                // Add as first property source to ensure highest priority
                environment.getPropertySources().addFirst(
                    new MapPropertySource("databaseUrlConfig", properties)
                );
                
                System.out.println("DatabaseConfig: Converted DB_URL to JDBC format: " + jdbcUrl);
                
            } catch (URISyntaxException e) {
                System.err.println("Failed to parse DB_URL: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (databaseUrl != null) {
            // DB_URL is already in JDBC format, use it directly
            Map<String, Object> properties = new HashMap<>();
            properties.put("spring.datasource.url", databaseUrl);
            
            String dbUsername = System.getenv("DB_USERNAME");
            String dbPassword = System.getenv("DB_PASSWORD");
            
            if (dbUsername != null && !dbUsername.isEmpty()) {
                properties.put("spring.datasource.username", dbUsername);
            }
            if (dbPassword != null && !dbPassword.isEmpty()) {
                properties.put("spring.datasource.password", dbPassword);
            }
            
            environment.getPropertySources().addFirst(
                new MapPropertySource("databaseUrlConfig", properties)
            );
            
            System.out.println("DatabaseConfig: Using DB_URL directly: " + databaseUrl);
        }
    }
}

