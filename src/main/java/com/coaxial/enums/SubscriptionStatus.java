package com.coaxial.enums;

public enum SubscriptionStatus {
    PENDING("Pending"),
    ACTIVE("Active"),
    EXPIRED("Expired"),
    CANCELLED("Cancelled");

    private final String displayName;

    SubscriptionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

