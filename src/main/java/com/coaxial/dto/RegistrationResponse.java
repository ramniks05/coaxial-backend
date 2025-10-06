package com.coaxial.dto;

import com.coaxial.entity.User;

public class RegistrationResponse {
    
    private boolean success;
    private String message;
    private User user;
    
    // Constructors
    public RegistrationResponse() {}
    
    public RegistrationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public RegistrationResponse(boolean success, String message, User user) {
        this.success = success;
        this.message = message;
        this.user = user;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
}