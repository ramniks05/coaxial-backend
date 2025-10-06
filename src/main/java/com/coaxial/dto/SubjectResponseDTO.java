package com.coaxial.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.coaxial.entity.CourseType;

public class SubjectResponseDTO {
    
    private Long id;
    private String name;
    private String description;
    private CourseType courseType;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    // Relationship data (simplified to avoid loops)
    private List<ClassSubjectSummaryDTO> classSubjects;
    private List<ExamSubjectSummaryDTO> examSubjects;
    private List<CourseSubjectSummaryDTO> courseSubjects;
    
    // Constructors
    public SubjectResponseDTO() {}
    
    public SubjectResponseDTO(Long id, String name, String description, CourseType courseType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.courseType = courseType;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public CourseType getCourseType() { return courseType; }
    public void setCourseType(CourseType courseType) { this.courseType = courseType; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    
    public List<ClassSubjectSummaryDTO> getClassSubjects() { return classSubjects; }
    public void setClassSubjects(List<ClassSubjectSummaryDTO> classSubjects) { this.classSubjects = classSubjects; }
    
    public List<ExamSubjectSummaryDTO> getExamSubjects() { return examSubjects; }
    public void setExamSubjects(List<ExamSubjectSummaryDTO> examSubjects) { this.examSubjects = examSubjects; }
    
    public List<CourseSubjectSummaryDTO> getCourseSubjects() { return courseSubjects; }
    public void setCourseSubjects(List<CourseSubjectSummaryDTO> courseSubjects) { this.courseSubjects = courseSubjects; }
}
