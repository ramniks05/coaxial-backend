package com.coaxial.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.coaxial.repository")
public class DatabaseConfig {
    // Database configuration will be handled by application.properties
}
