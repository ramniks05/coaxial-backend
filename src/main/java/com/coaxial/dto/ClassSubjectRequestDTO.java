package com.coaxial.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ClassSubjectRequestDTO {
    
    @NotNull(message = "Class ID is required")
    private Long classId;
    
    @NotNull(message = "Subject IDs are required")
    private List<Long> subjectIds;
    
    private Boolean isCompulsory = true;
    
    @Min(value = 0, message = "Display order cannot be negative")
    private Integer displayOrder = 0;
    
    // Constructors
    public ClassSubjectRequestDTO() {}
    
    public ClassSubjectRequestDTO(Long classId, List<Long> subjectIds) {
        this.classId = classId;
        this.subjectIds = subjectIds;
    }
    
    // Getters and Setters
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    
    public List<Long> getSubjectIds() { return subjectIds; }
    public void setSubjectIds(List<Long> subjectIds) { this.subjectIds = subjectIds; }
    
    public Boolean getIsCompulsory() { return isCompulsory; }
    public void setIsCompulsory(Boolean isCompulsory) { this.isCompulsory = isCompulsory; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}
