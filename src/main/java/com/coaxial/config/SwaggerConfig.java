package com.coaxial.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Value("${spring.application.version:1.0.0}")
    private String applicationVersion;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    public OpenAPI customOpenAPI() {
        // Determine server order based on active profile
        List<Server> servers;
        if ("prod".equals(activeProfile)) {
            // Production: Railway URL first
            servers = List.of(
                    new Server()
                            .url("https://coaxial-backend-production.up.railway.app")
                            .description("Railway Production Server"),
                    new Server()
                            .url("http://localhost:8080")
                            .description("Development Server")
            );
        } else {
            // Development: localhost first
            servers = List.of(
                    new Server()
                            .url("http://localhost:8080")
                            .description("Development Server"),
                    new Server()
                            .url("https://coaxial-backend-production.up.railway.app")
                            .description("Railway Production Server")
            );
        }

        return new OpenAPI()
                .info(new Info()
                        .title("Coaxial Learning Management System API")
                        .description("REST API for Coaxial Learning Management System - Backend service for React frontend")
                        .version(applicationVersion)
                        .contact(new Contact()
                                .name("Coaxial Development Team")
                                .email("dev@coaxial.com")
                                .url("https://coaxial.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(servers)
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token")));
    }
}
