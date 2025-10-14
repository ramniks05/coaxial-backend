package com.coaxial.dto;

import java.time.LocalDateTime;

public class TestSessionResponseDTO {
    private String sessionId;
    private Long attemptId;
    private Long testId;
    private String testName;
    private Integer timeLimitMinutes;
    private Integer totalQuestions;
    private Integer attemptNumber;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private String status;
    private Boolean negativeMarking;
    private Double negativeMarkPercentage;
    private Boolean allowReview;
    private Boolean showCorrectAnswers;
    private Boolean allowSkip;
    
    public TestSessionResponseDTO() {}
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public Long getAttemptId() {
        return attemptId;
    }
    
    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }
    
    public Long getTestId() {
        return testId;
    }
    
    public void setTestId(Long testId) {
        this.testId = testId;
    }
    
    public String getTestName() {
        return testName;
    }
    
    public void setTestName(String testName) {
        this.testName = testName;
    }
    
    public Integer getTimeLimitMinutes() {
        return timeLimitMinutes;
    }
    
    public void setTimeLimitMinutes(Integer timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }
    
    public Integer getTotalQuestions() {
        return totalQuestions;
    }
    
    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
    
    public Integer getAttemptNumber() {
        return attemptNumber;
    }
    
    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Boolean getNegativeMarking() {
        return negativeMarking;
    }
    
    public void setNegativeMarking(Boolean negativeMarking) {
        this.negativeMarking = negativeMarking;
    }
    
    public Double getNegativeMarkPercentage() {
        return negativeMarkPercentage;
    }
    
    public void setNegativeMarkPercentage(Double negativeMarkPercentage) {
        this.negativeMarkPercentage = negativeMarkPercentage;
    }
    
    public Boolean getAllowReview() {
        return allowReview;
    }
    
    public void setAllowReview(Boolean allowReview) {
        this.allowReview = allowReview;
    }
    
    public Boolean getShowCorrectAnswers() {
        return showCorrectAnswers;
    }
    
    public void setShowCorrectAnswers(Boolean showCorrectAnswers) {
        this.showCorrectAnswers = showCorrectAnswers;
    }
    
    public Boolean getAllowSkip() {
        return allowSkip;
    }
    
    public void setAllowSkip(Boolean allowSkip) {
        this.allowSkip = allowSkip;
    }
}

