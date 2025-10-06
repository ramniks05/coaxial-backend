package com.coaxial.dto;

/**
 * DTO for exam dropdown data in admin pricing management
 */
public class ExamDropdownDTO {
    
    private Long id;
    private String name;
    private String description;
    private Boolean hasExistingPricing;
    private String currentPricingStatus; // "SET", "DEFAULT", "NOT_SET"
    
    // Constructors
    public ExamDropdownDTO() {}
    
    public ExamDropdownDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.hasExistingPricing = false;
        this.currentPricingStatus = "NOT_SET";
    }
    
    public ExamDropdownDTO(Long id, String name, String description,
                          Boolean hasExistingPricing, String currentPricingStatus) {
        this.id = id;
        this.name = name;
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
        return "ExamDropdownDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", hasExistingPricing=" + hasExistingPricing +
                ", currentPricingStatus='" + currentPricingStatus + '\'' +
                '}';
    }
}
