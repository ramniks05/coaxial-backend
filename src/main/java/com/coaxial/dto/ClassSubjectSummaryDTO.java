package com.coaxial.dto;

import java.time.LocalDateTime;

public class ClassSubjectSummaryDTO {
    
    private Long id;
    private Long classId;
    private String className;
    private Long subjectId;
    private String subjectName;
    private Boolean isCompulsory;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    
    // Constructors
    public ClassSubjectSummaryDTO() {}
    
    public ClassSubjectSummaryDTO(Long id, Long classId, String className, Long subjectId, String subjectName) {
        this.id = id;
        this.classId = classId;
        this.className = className;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    
    public Boolean getIsCompulsory() { return isCompulsory; }
    public void setIsCompulsory(Boolean isCompulsory) { this.isCompulsory = isCompulsory; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
