package com.coaxial.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ModuleRequestDTO {
    
    @NotBlank(message = "Module name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Topic ID is required")
    private Long topicId;
    
    @Min(value = 0, message = "Display order cannot be negative")
    private Integer displayOrder = 0;
    
    private Boolean isActive = true;
    
    // Constructors
    public ModuleRequestDTO() {}
    
    public ModuleRequestDTO(String name, String description, Long topicId) {
        this.name = name;
        this.description = description;
        this.topicId = topicId;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
