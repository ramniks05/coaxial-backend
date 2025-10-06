package com.coaxial.dto;

/**
 * DTO for course dropdown data in admin pricing management
 */
public class CourseDropdownDTO {
    
    private Long id;
    private String name;
    private String courseTypeName;
    private String description;
    private Boolean hasExistingPricing;
    private String currentPricingStatus; // "SET", "DEFAULT", "NOT_SET"
    
    // Constructors
    public CourseDropdownDTO() {}
    
    public CourseDropdownDTO(Long id, String name, String courseTypeName, String description) {
        this.id = id;
        this.name = name;
        this.courseTypeName = courseTypeName;
        this.description = description;
        this.hasExistingPricing = false;
        this.currentPricingStatus = "NOT_SET";
    }
    
    public CourseDropdownDTO(Long id, String name, String courseTypeName, String description, 
                           Boolean hasExistingPricing, String currentPricingStatus) {
        this.id = id;
        this.name = name;
        this.courseTypeName = courseTypeName;
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
    
    public String getCourseTypeName() {
        return courseTypeName;
    }
    
    public void setCourseTypeName(String courseTypeName) {
        this.courseTypeName = courseTypeName;
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
        return "CourseDropdownDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", courseTypeName='" + courseTypeName + '\'' +
                ", description='" + description + '\'' +
                ", hasExistingPricing=" + hasExistingPricing +
                ", currentPricingStatus='" + currentPricingStatus + '\'' +
                '}';
    }
}
