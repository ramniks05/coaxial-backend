package com.coaxial.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.coaxial.entity.CourseType;

public class SubjectRequestDTO {
    
    @NotBlank(message = "Subject name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Course type is required")
    private CourseType courseType;
    
    @Min(value = 0, message = "Display order cannot be negative")
    private Integer displayOrder = 0;
    
    private Boolean isActive = true;
    
    // Constructors
    public SubjectRequestDTO() {}
    
    public SubjectRequestDTO(String name, String description, CourseType courseType) {
        this.name = name;
        this.description = description;
        this.courseType = courseType;
    }
    
    // Getters and Setters
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
}
