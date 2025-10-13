package com.coaxial.enums;

public enum TestLevel {
    CLASS_EXAM("Class/Exam Level"),
    SUBJECT("Subject Level"),
    MODULE("Module Level"),
    CHAPTER("Chapter Level");

    private final String displayName;

    TestLevel(String displayName) {
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

