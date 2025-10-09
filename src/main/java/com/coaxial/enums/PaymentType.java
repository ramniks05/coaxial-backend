package com.coaxial.enums;

public enum PaymentType {
    SUBSCRIPTION("New Subscription"),
    RENEWAL("Subscription Renewal"),
    UPGRADE("Plan Upgrade");

    private final String displayName;

    PaymentType(String displayName) {
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

