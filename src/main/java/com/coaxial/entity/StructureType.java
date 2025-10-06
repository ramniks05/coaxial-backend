package com.coaxial.entity;

public enum StructureType {
    ACADEMIC("Academic"),
    COMPETITIVE("Competitive"),
    CERTIFICATION("Certification"),
    PROFESSIONAL("Professional");
    
    private final String displayName;
    
    StructureType(String name) {
        this.displayName = name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
