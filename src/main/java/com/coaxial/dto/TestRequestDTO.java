package com.coaxial.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TestRequestDTO {
    @NotBlank(message = "Test name is required")
    private String testName;

    private String description;

    private String instructions;

    @NotNull(message = "Time limit is required")
    @Min(value = 1, message = "Time limit must be at least 1 minute")
    private Integer timeLimitMinutes;

    private Integer totalMarks;

    private Integer passingMarks;

    private Boolean negativeMarking = false;

    private Double negativeMarkPercentage = 0.25;

    private Integer maxAttempts = 1;

    private Boolean isActive = true;

    private Boolean isPublished = false;

    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "Master exam ID is required")
    private Long masterExamId;

    // Optional: initial questions
    private List<com.coaxial.dto.TestQuestionRequestDTO> questions;

    // Extended configuration
    private String testType; // PRACTICE, MOCK, etc.
    private Boolean allowReview = true;
    private Boolean showCorrectAnswers = false;
    private Boolean shuffleQuestions = false;
    private Boolean shuffleOptions = false;
    private Boolean allowSkip = true;
    private Integer timePerQuestion = 0;

    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public Integer getTimeLimitMinutes() { return timeLimitMinutes; }
    public void setTimeLimitMinutes(Integer timeLimitMinutes) { this.timeLimitMinutes = timeLimitMinutes; }

    public Integer getTotalMarks() { return totalMarks; }
    public void setTotalMarks(Integer totalMarks) { this.totalMarks = totalMarks; }

    public Integer getPassingMarks() { return passingMarks; }
    public void setPassingMarks(Integer passingMarks) { this.passingMarks = passingMarks; }

    public Boolean getNegativeMarking() { return negativeMarking; }
    public void setNegativeMarking(Boolean negativeMarking) { this.negativeMarking = negativeMarking; }

    public Double getNegativeMarkPercentage() { return negativeMarkPercentage; }
    public void setNegativeMarkPercentage(Double negativeMarkPercentage) { this.negativeMarkPercentage = negativeMarkPercentage; }

    public Integer getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(Integer maxAttempts) { this.maxAttempts = maxAttempts; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getIsPublished() { return isPublished; }
    public void setIsPublished(Boolean isPublished) { this.isPublished = isPublished; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Long getMasterExamId() { return masterExamId; }
    public void setMasterExamId(Long masterExamId) { this.masterExamId = masterExamId; }

    public List<com.coaxial.dto.TestQuestionRequestDTO> getQuestions() { return questions; }
    public void setQuestions(List<com.coaxial.dto.TestQuestionRequestDTO> questions) { this.questions = questions; }

    public String getTestType() { return testType; }
    public void setTestType(String testType) { this.testType = testType; }
    public Boolean getAllowReview() { return allowReview; }
    public void setAllowReview(Boolean allowReview) { this.allowReview = allowReview; }
    public Boolean getShowCorrectAnswers() { return showCorrectAnswers; }
    public void setShowCorrectAnswers(Boolean showCorrectAnswers) { this.showCorrectAnswers = showCorrectAnswers; }
    public Boolean getShuffleQuestions() { return shuffleQuestions; }
    public void setShuffleQuestions(Boolean shuffleQuestions) { this.shuffleQuestions = shuffleQuestions; }
    public Boolean getShuffleOptions() { return shuffleOptions; }
    public void setShuffleOptions(Boolean shuffleOptions) { this.shuffleOptions = shuffleOptions; }
    public Boolean getAllowSkip() { return allowSkip; }
    public void setAllowSkip(Boolean allowSkip) { this.allowSkip = allowSkip; }
    public Integer getTimePerQuestion() { return timePerQuestion; }
    public void setTimePerQuestion(Integer timePerQuestion) { this.timePerQuestion = timePerQuestion; }
}


