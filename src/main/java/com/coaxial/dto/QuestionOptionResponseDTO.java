package com.coaxial.dto;

import com.coaxial.entity.QuestionOption;

public class QuestionOptionResponseDTO {
    
    private Long id;
    private String optionText;
    private String optionLetter;
    private Boolean isCorrect;
    private Integer displayOrder;
    private String createdAt;
    
    // Constructors
    public QuestionOptionResponseDTO() {}
    
    public QuestionOptionResponseDTO(QuestionOption option) {
        this.id = option.getId();
        this.optionText = option.getOptionText();
        this.optionLetter = option.getOptionLetter();
        this.isCorrect = option.getIsCorrect();
        this.displayOrder = option.getDisplayOrder();
        this.createdAt = option.getCreatedAt() != null ? option.getCreatedAt().toString() : null;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }
    
    public String getOptionLetter() { return optionLetter; }
    public void setOptionLetter(String optionLetter) { this.optionLetter = optionLetter; }
    
    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
