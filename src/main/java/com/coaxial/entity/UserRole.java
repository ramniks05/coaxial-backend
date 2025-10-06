package com.coaxial.entity;

public enum UserRole {
    ADMIN("Administrator"),
    INSTRUCTOR("Instructor"),
    STUDENT("Student");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
