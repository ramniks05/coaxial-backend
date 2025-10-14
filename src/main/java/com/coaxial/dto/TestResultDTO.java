package com.coaxial.dto;

import java.time.LocalDateTime;

public class TestResultDTO {
    private Long attemptId;
    private Long testId;
    private String testName;
    private Integer attemptNumber;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private Integer timeTakenSeconds;
    private Integer totalQuestions;
    private Integer answeredQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Integer unansweredQuestions;
    private Double totalMarksObtained;
    private Double totalMarksAvailable;
    private Double percentage;
    private Boolean isPassed;
    private Double passingMarks;
    
    public TestResultDTO() {}
    
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
    
    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
    
    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
    
    public Integer getTimeTakenSeconds() {
        return timeTakenSeconds;
    }
    
    public void setTimeTakenSeconds(Integer timeTakenSeconds) {
        this.timeTakenSeconds = timeTakenSeconds;
    }
    
    public Integer getTotalQuestions() {
        return totalQuestions;
    }
    
    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
    
    public Integer getAnsweredQuestions() {
        return answeredQuestions;
    }
    
    public void setAnsweredQuestions(Integer answeredQuestions) {
        this.answeredQuestions = answeredQuestions;
    }
    
    public Integer getCorrectAnswers() {
        return correctAnswers;
    }
    
    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
    
    public Integer getWrongAnswers() {
        return wrongAnswers;
    }
    
    public void setWrongAnswers(Integer wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }
    
    public Integer getUnansweredQuestions() {
        return unansweredQuestions;
    }
    
    public void setUnansweredQuestions(Integer unansweredQuestions) {
        this.unansweredQuestions = unansweredQuestions;
    }
    
    public Double getTotalMarksObtained() {
        return totalMarksObtained;
    }
    
    public void setTotalMarksObtained(Double totalMarksObtained) {
        this.totalMarksObtained = totalMarksObtained;
    }
    
    public Double getTotalMarksAvailable() {
        return totalMarksAvailable;
    }
    
    public void setTotalMarksAvailable(Double totalMarksAvailable) {
        this.totalMarksAvailable = totalMarksAvailable;
    }
    
    public Double getPercentage() {
        return percentage;
    }
    
    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
    
    public Boolean getIsPassed() {
        return isPassed;
    }
    
    public void setIsPassed(Boolean isPassed) {
        this.isPassed = isPassed;
    }
    
    public Double getPassingMarks() {
        return passingMarks;
    }
    
    public void setPassingMarks(Double passingMarks) {
        this.passingMarks = passingMarks;
    }
}

