package com.coaxial.controller;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coaxial.dto.HealthResponse;
import com.coaxial.dto.HealthResponse.DatabaseStatus;

@RestController
@RequestMapping("/api")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> healthCheck() {
        HealthResponse response = new HealthResponse();
        
        // Check database connectivity
        DatabaseStatus dbStatus = checkDatabaseConnectivity();
        response.setDatabase(dbStatus);
        
        // If database is down, set overall status to DOWN
        if (!"UP".equals(dbStatus.getStatus())) {
            response.setStatus("DOWN");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    private DatabaseStatus checkDatabaseConnectivity() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) { // 5 second timeout
                return new DatabaseStatus("UP", "Database connection successful");
            } else {
                return new DatabaseStatus("DOWN", "Database connection invalid");
            }
        } catch (SQLException e) {
            return new DatabaseStatus("DOWN", "Database connection failed: " + e.getMessage());
        }
    }
}
