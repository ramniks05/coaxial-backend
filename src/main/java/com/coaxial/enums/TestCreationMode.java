package com.coaxial.enums;

public enum TestCreationMode {
    EXAM_BASED("Exam Based"),
    CONTENT_BASED("Content Based");

    private final String displayName;

    TestCreationMode(String displayName) {
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

