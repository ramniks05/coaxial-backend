package com.coaxial.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SubmitAnswerRequestDTO {
    
    @NotBlank(message = "Session ID is required")
    private String sessionId;
    
    @NotNull(message = "Question ID is required")
    private Long questionId;
    
    private Long selectedOptionId;  // Can be null for skipped questions
    
    public SubmitAnswerRequestDTO() {}
    
    public SubmitAnswerRequestDTO(String sessionId, Long questionId, Long selectedOptionId) {
        this.sessionId = sessionId;
        this.questionId = questionId;
        this.selectedOptionId = selectedOptionId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public Long getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    
    public Long getSelectedOptionId() {
        return selectedOptionId;
    }
    
    public void setSelectedOptionId(Long selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }
}

