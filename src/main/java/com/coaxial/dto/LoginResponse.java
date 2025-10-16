package com.coaxial.dto;

import com.coaxial.entity.User;

public class LoginResponse {
    
    private String token;
    private String type = "Bearer";
    private User user;
    private String message;
    private boolean success = true;

    public LoginResponse() {}

    public LoginResponse(String token, User user) {
        this(token, user, "Login successful");
    }

    public LoginResponse(String token, User user, String message) {
        this.token = token;
        this.user = user;
        this.message = message;
        this.success = true;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
