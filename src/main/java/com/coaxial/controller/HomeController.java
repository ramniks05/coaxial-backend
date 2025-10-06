package com.coaxial.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Home", description = "Home and health check endpoints")
public class HomeController {

    @GetMapping("/")
    @Operation(summary = "Welcome message", description = "Returns a welcome message for the Coaxial LMS")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Welcome message returned successfully")
    })
    public String home() {
        return "Welcome to Coaxial Learning Management System!";
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Returns the health status of the application")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application is healthy")
    })
    public String health() {
        return "Application is running successfully!";
    }

}
