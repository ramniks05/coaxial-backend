package com.coaxial.enums;

public enum SubscriptionLevel {
    CLASS("Class"),
    EXAM("Exam"),
    COURSE("Course");

    private final String displayName;

    SubscriptionLevel(String displayName) {
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
