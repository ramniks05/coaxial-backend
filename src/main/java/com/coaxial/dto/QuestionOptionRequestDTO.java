package com.coaxial.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class QuestionOptionRequestDTO {
    
    @NotBlank(message = "Option text is required")
    private String optionText;
    
    private String optionLetter; // A, B, C, D, etc.
    
    private Boolean isCorrect = false;
    
    @Min(value = 0, message = "Display order cannot be negative")
    private Integer displayOrder = 0;
    
    // Constructors
    public QuestionOptionRequestDTO() {}
    
    public QuestionOptionRequestDTO(String optionText, String optionLetter) {
        this.optionText = optionText;
        this.optionLetter = optionLetter;
    }
    
    public QuestionOptionRequestDTO(String optionText, String optionLetter, Boolean isCorrect) {
        this.optionText = optionText;
        this.optionLetter = optionLetter;
        this.isCorrect = isCorrect;
    }
    
    // Getters and Setters
    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }
    
    public String getOptionLetter() { return optionLetter; }
    public void setOptionLetter(String optionLetter) { this.optionLetter = optionLetter; }
    
    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}
