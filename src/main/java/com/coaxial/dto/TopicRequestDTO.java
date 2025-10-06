package com.coaxial.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TopicRequestDTO {
    
    @NotBlank(message = "Topic name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Course type ID is required")
    private Long courseTypeId;
    
    @NotNull(message = "Relationship ID is required")
    private Long relationshipId;
    
    @Min(value = 0, message = "Display order cannot be negative")
    private Integer displayOrder = 0;
    
    private Boolean isActive = true;
    
    // Constructors
    public TopicRequestDTO() {}
    
    public TopicRequestDTO(String name, String description, Long courseTypeId, Long relationshipId) {
        this.name = name;
        this.description = description;
        this.courseTypeId = courseTypeId;
        this.relationshipId = relationshipId;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Long getCourseTypeId() { return courseTypeId; }
    public void setCourseTypeId(Long courseTypeId) { this.courseTypeId = courseTypeId; }
    
    public Long getRelationshipId() { return relationshipId; }
    public void setRelationshipId(Long relationshipId) { this.relationshipId = relationshipId; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}