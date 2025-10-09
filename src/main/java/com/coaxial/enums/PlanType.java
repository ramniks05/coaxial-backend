package com.coaxial.enums;

public enum PlanType {
    MONTHLY("Monthly", 30),
    QUARTERLY("Quarterly", 90),
    YEARLY("Yearly", 365);

    private final String displayName;
    private final int defaultDays;

    PlanType(String displayName, int defaultDays) {
        this.displayName = displayName;
        this.defaultDays = defaultDays;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDefaultDays() {
        return defaultDays;
    }

    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Get PlanType from duration days (approximate match)
     */
    public static PlanType fromDurationDays(Integer durationDays) {
        if (durationDays == null) {
            return null;
        }
        
        // Exact or close match
        if (durationDays <= 31) {
            return MONTHLY;
        } else if (durationDays <= 100) {
            return QUARTERLY;
        } else {
            return YEARLY;
        }
    }
}

