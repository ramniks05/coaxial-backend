package com.coaxial.dto;

import jakarta.validation.constraints.NotNull;

public class ClassSubjectRequest {
    
    @NotNull(message = "Class ID is required")
    private Long classId;
    
    @NotNull(message = "Subject ID is required")
    private Long subjectId;
    
    private Integer displayOrder = 0;
    
    private Boolean isActive = true;
    
    // Constructors
    public ClassSubjectRequest() {}
    
    // Getters and Setters
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}

