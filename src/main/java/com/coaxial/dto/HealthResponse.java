package com.coaxial.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class HealthResponse {
    
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;
    
    private DatabaseStatus database;
    
    private DatabaseStatus redis;
    
    private String version;

    public HealthResponse() {
        this.status = "UP";
        this.timestamp = LocalDateTime.now();
        this.version = "1.0.0";
    }

    public HealthResponse(String status, DatabaseStatus database) {
        this();
        this.status = status;
        this.database = database;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public DatabaseStatus getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseStatus database) {
        this.database = database;
    }

    public DatabaseStatus getRedis() {
        return redis;
    }

    public void setRedis(DatabaseStatus redis) {
        this.redis = redis;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public static class DatabaseStatus {
        private String status;
        private String message;

        public DatabaseStatus() {}

        public DatabaseStatus(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
