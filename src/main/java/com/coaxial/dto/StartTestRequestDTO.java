package com.coaxial.dto;

public class StartTestRequestDTO {
    private String ipAddress;
    private String userAgent;
    
    public StartTestRequestDTO() {}
    
    public StartTestRequestDTO(String ipAddress, String userAgent) {
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}

