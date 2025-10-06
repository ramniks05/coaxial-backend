package com.coaxial.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CourseSubjectRequestDTO {
    
    @NotNull(message = "Course ID is required")
    private Long courseId;
    
    @NotNull(message = "Subject IDs are required")
    private List<Long> subjectIds;
    
    private Boolean isCompulsory = true;
    
    @Min(value = 0, message = "Display order cannot be negative")
    private Integer displayOrder = 0;
    
    // Constructors
    public CourseSubjectRequestDTO() {}
    
    public CourseSubjectRequestDTO(Long courseId, List<Long> subjectIds) {
        this.courseId = courseId;
        this.subjectIds = subjectIds;
    }
    
    // Getters and Setters
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    
    public List<Long> getSubjectIds() { return subjectIds; }
    public void setSubjectIds(List<Long> subjectIds) { this.subjectIds = subjectIds; }
    
    public Boolean getIsCompulsory() { return isCompulsory; }
    public void setIsCompulsory(Boolean isCompulsory) { this.isCompulsory = isCompulsory; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}
