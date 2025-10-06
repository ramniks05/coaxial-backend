package com.coaxial.enums;

public enum TestSessionStatus {
    STARTED("Started"),
    IN_PROGRESS("In Progress"),
    PAUSED("Paused"),
    COMPLETED("Completed"),
    TIMEOUT("Timeout"),
    ABANDONED("Abandoned"),
    TERMINATED("Terminated");
    
    private final String displayName;
    
    TestSessionStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
