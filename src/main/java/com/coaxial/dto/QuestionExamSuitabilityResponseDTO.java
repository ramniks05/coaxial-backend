package com.coaxial.dto;

import com.coaxial.entity.QuestionExamSuitability;

public class QuestionExamSuitabilityResponseDTO {
    
    private Long id;
    private Long masterExamId;
    private String masterExamName;
    // simplified MasterExam: no code
    private String suitabilityLevel;
    private String notes;
    private String createdAt;
    private String createdByName;
    
    // Constructors
    public QuestionExamSuitabilityResponseDTO() {}
    
    public QuestionExamSuitabilityResponseDTO(QuestionExamSuitability suitability) {
        this.id = suitability.getId();
        this.masterExamId = suitability.getMasterExam().getId();
        this.masterExamName = suitability.getMasterExam().getExamName();
        // no exam code in simplified MasterExam
        this.suitabilityLevel = suitability.getSuitabilityLevel();
        this.notes = suitability.getNotes();
        this.createdAt = suitability.getCreatedAt() != null ? suitability.getCreatedAt().toString() : null;
        this.createdByName = suitability.getCreatedBy() != null ? suitability.getCreatedBy().getFullName() : null;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getMasterExamId() { return masterExamId; }
    public void setMasterExamId(Long masterExamId) { this.masterExamId = masterExamId; }
    
    public String getMasterExamName() { return masterExamName; }
    public void setMasterExamName(String masterExamName) { this.masterExamName = masterExamName; }
    
    // no getter/setter for exam code
    
    public String getSuitabilityLevel() { return suitabilityLevel; }
    public void setSuitabilityLevel(String suitabilityLevel) { this.suitabilityLevel = suitabilityLevel; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
}
