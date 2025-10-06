package com.coaxial.dto;

import jakarta.validation.constraints.NotNull;

public class QuestionExamSuitabilityRequestDTO {
    
    @NotNull(message = "Master exam ID is required")
    private Long masterExamId;
    
    private String suitabilityLevel = "MEDIUM"; // HIGH, MEDIUM, LOW
    private String notes;
    
    // Constructors
    public QuestionExamSuitabilityRequestDTO() {}
    
    public QuestionExamSuitabilityRequestDTO(Long masterExamId, String suitabilityLevel) {
        this.masterExamId = masterExamId;
        this.suitabilityLevel = suitabilityLevel;
    }
    
    // Getters and Setters
    public Long getMasterExamId() { return masterExamId; }
    public void setMasterExamId(Long masterExamId) { this.masterExamId = masterExamId; }
    
    public String getSuitabilityLevel() { return suitabilityLevel; }
    public void setSuitabilityLevel(String suitabilityLevel) { this.suitabilityLevel = suitabilityLevel; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
