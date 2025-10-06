package com.coaxial.dto;

public class TokenRefreshResponse {
    
    private String token;
    private String type = "Bearer";
    private String message;
    private Long expiresIn;

    public TokenRefreshResponse() {}

    public TokenRefreshResponse(String token, String message, Long expiresIn) {
        this.token = token;
        this.message = message;
        this.expiresIn = expiresIn;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
