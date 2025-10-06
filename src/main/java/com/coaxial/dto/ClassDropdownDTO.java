package com.coaxial.dto;

/**
 * DTO for class dropdown data in admin pricing management
 */
public class ClassDropdownDTO {
    
    private Long id;
    private String name;
    private Long courseId;
    private String courseName;
    private String description;
    private Boolean hasExistingPricing;
    private String currentPricingStatus; // "SET", "DEFAULT", "NOT_SET"
    
    // Constructors
    public ClassDropdownDTO() {}
    
    public ClassDropdownDTO(Long id, String name, Long courseId, String courseName, String description) {
        this.id = id;
        this.name = name;
        this.courseId = courseId;
        this.courseName = courseName;
        this.description = description;
        this.hasExistingPricing = false;
        this.currentPricingStatus = "NOT_SET";
    }
    
    public ClassDropdownDTO(Long id, String name, Long courseId, String courseName, String description,
                           Boolean hasExistingPricing, String currentPricingStatus) {
        this.id = id;
        this.name = name;
        this.courseId = courseId;
        this.courseName = courseName;
        this.description = description;
        this.hasExistingPricing = hasExistingPricing;
        this.currentPricingStatus = currentPricingStatus;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Long getCourseId() {
        return courseId;
    }
    
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getHasExistingPricing() {
        return hasExistingPricing;
    }
    
    public void setHasExistingPricing(Boolean hasExistingPricing) {
        this.hasExistingPricing = hasExistingPricing;
    }
    
    public String getCurrentPricingStatus() {
        return currentPricingStatus;
    }
    
    public void setCurrentPricingStatus(String currentPricingStatus) {
        this.currentPricingStatus = currentPricingStatus;
    }
    
    @Override
    public String toString() {
        return "ClassDropdownDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", description='" + description + '\'' +
                ", hasExistingPricing=" + hasExistingPricing +
                ", currentPricingStatus='" + currentPricingStatus + '\'' +
                '}';
    }
}
