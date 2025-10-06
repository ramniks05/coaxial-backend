package com.coaxial.dto;

import java.time.LocalDateTime;

public class CourseSubjectSummaryDTO {
    
    private Long id;
    private Long courseId;
    private String courseName;
    private Long subjectId;
    private String subjectName;
    private Boolean isCompulsory;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    
    // Constructors
    public CourseSubjectSummaryDTO() {}
    
    public CourseSubjectSummaryDTO(Long id, Long courseId, String courseName, Long subjectId, String subjectName) {
        this.id = id;
        this.courseId = courseId;
        this.courseName = courseName;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    
    public Boolean getIsCompulsory() { return isCompulsory; }
    public void setIsCompulsory(Boolean isCompulsory) { this.isCompulsory = isCompulsory; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
