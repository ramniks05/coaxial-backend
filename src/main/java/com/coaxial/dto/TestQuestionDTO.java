package com.coaxial.dto;

import java.util.List;

public class TestQuestionDTO {
    private Long questionId;
    private String questionText;
    private String questionType;
    private Integer questionOrder;
    private Double marks;
    private List<OptionDTO> options;
    private String imageUrl;
    private String explanation;  // Only show after test is submitted
    
    public TestQuestionDTO() {}
    
    public Long getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public String getQuestionType() {
        return questionType;
    }
    
    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }
    
    public Integer getQuestionOrder() {
        return questionOrder;
    }
    
    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }
    
    public Double getMarks() {
        return marks;
    }
    
    public void setMarks(Double marks) {
        this.marks = marks;
    }
    
    public List<OptionDTO> getOptions() {
        return options;
    }
    
    public void setOptions(List<OptionDTO> options) {
        this.options = options;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getExplanation() {
        return explanation;
    }
    
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
    
    public static class OptionDTO {
        private Long optionId;
        private String optionText;
        private String optionImageUrl;
        
        public OptionDTO() {}
        
        public OptionDTO(Long optionId, String optionText, String optionImageUrl) {
            this.optionId = optionId;
            this.optionText = optionText;
            this.optionImageUrl = optionImageUrl;
        }
        
        public Long getOptionId() {
            return optionId;
        }
        
        public void setOptionId(Long optionId) {
            this.optionId = optionId;
        }
        
        public String getOptionText() {
            return optionText;
        }
        
        public void setOptionText(String optionText) {
            this.optionText = optionText;
        }
        
        public String getOptionImageUrl() {
            return optionImageUrl;
        }
        
        public void setOptionImageUrl(String optionImageUrl) {
            this.optionImageUrl = optionImageUrl;
        }
    }
}

