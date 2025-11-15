package com.coaxial.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.lang.NonNull;

/**
 * Converts Railway's DATABASE_URL (postgresql:// format) to JDBC format
 * This runs before Spring Boot reads datasource properties
 */
public class DatabaseConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        // Try multiple environment variable names that Railway might use
        String databaseUrl = System.getenv("DB_URL");
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            databaseUrl = System.getenv("DATABASE_URL");
        }
        
        // Debug logging
        System.out.println("DatabaseConfig: DB_URL env var = " + (System.getenv("DB_URL") != null ? "SET" : "NULL"));
        System.out.println("DatabaseConfig: DATABASE_URL env var = " + (System.getenv("DATABASE_URL") != null ? "SET" : "NULL"));
        if (databaseUrl != null) {
            // Log first 80 chars for debugging (without password)
            String logUrl = databaseUrl.length() > 80 ? databaseUrl.substring(0, 80) + "..." : databaseUrl;
            // Mask password in log
            if (logUrl.contains("@")) {
                int atIndex = logUrl.indexOf("@");
                int colonIndex = logUrl.indexOf(":", logUrl.indexOf("://") + 3);
                if (colonIndex > 0 && colonIndex < atIndex) {
                    logUrl = logUrl.substring(0, colonIndex + 1) + "***@" + logUrl.substring(atIndex + 1);
                }
            }
            System.out.println("DatabaseConfig: Using databaseUrl = " + logUrl);
        } else {
            System.out.println("DatabaseConfig: databaseUrl is NULL - checking PGHOST/PGPORT...");
            // Try using individual PG* variables as fallback
            String pgHost = System.getenv("PGHOST");
            String pgPort = System.getenv("PGPORT");
            String pgDatabase = System.getenv("PGDATABASE");
            String pgUser = System.getenv("PGUSER");
            String pgPassword = System.getenv("PGPASSWORD");
            
            if (pgHost != null && !pgHost.isEmpty()) {
                // Build JDBC URL from individual components
                int port = pgPort != null && !pgPort.isEmpty() ? Integer.parseInt(pgPort) : 5432;
                String db = pgDatabase != null && !pgDatabase.isEmpty() ? pgDatabase : "railway";
                String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", pgHost, port, db);
                
                Map<String, Object> properties = new HashMap<>();
                properties.put("spring.datasource.url", jdbcUrl);
                if (pgUser != null && !pgUser.isEmpty()) {
                    properties.put("spring.datasource.username", pgUser);
                }
                if (pgPassword != null && !pgPassword.isEmpty()) {
                    properties.put("spring.datasource.password", pgPassword);
                }
                
                environment.getPropertySources().addFirst(
                    new MapPropertySource("databaseUrlConfig", properties)
                );
                
                System.out.println("DatabaseConfig: Built JDBC URL from PG* variables: " + jdbcUrl);
                return;
            }
            System.out.println("DatabaseConfig: No DB_URL or PG* variables found - will use defaults from application-prod.properties");
        }
        
        // If DB_URL is in postgresql:// format, convert to JDBC format
        if (databaseUrl != null && !databaseUrl.isEmpty() && databaseUrl.startsWith("postgresql://")) {
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
                System.err.println("URISyntaxException: " + e.getClass().getName() + ": " + e.getMessage());
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

