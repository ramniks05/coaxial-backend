package com.coaxial.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class QuestionRequestDTO {
    
    @NotBlank(message = "Question text is required")
    private String questionText;
    
    private String questionType = "MULTIPLE_CHOICE"; // MULTIPLE_CHOICE, TRUE_FALSE, FILL_BLANK, ESSAY
    
    private String difficultyLevel = "MEDIUM"; // EASY, MEDIUM, HARD
    
    @Min(value = 1, message = "Marks must be at least 1")
    private Integer marks = 1;
    
    @Min(value = 0, message = "Negative marks cannot be negative")
    private Integer negativeMarks = 0;
    
    @Min(value = 1, message = "Time limit must be at least 1 second")
    private Integer timeLimitSeconds;
    
    private String explanation;
    
    @NotNull(message = "Chapter ID is required")
    private Long chapterId;
    
    // Additional references for flexible test creation
    private Long courseTypeId; // 1=Academic, 2=Competitive, 3=Professional
    private Long relationshipId; // classSubjectId, examSubjectId, or courseSubjectId
    private Long topicId; // Direct topic reference
    private Long moduleId; // Direct module reference
    private Long subjectId; // Direct subject reference
    
    @Min(value = 0, message = "Display order cannot be negative")
    private Integer displayOrder = 0;
    
    private Boolean isActive = true;
    
    // Question options (for multiple choice questions)
    private List<QuestionOptionRequestDTO> options;
    
    // Exam tagging - detailed histories, simple suitabilities (IDs only)
    private List<QuestionExamHistoryRequestDTO> examHistories;
    private List<Long> examSuitabilities; // e.g., [1,2,3]
    
    // Constructors
    public QuestionRequestDTO() {}
    
    public QuestionRequestDTO(String questionText, Long chapterId) {
        this.questionText = questionText;
        this.chapterId = chapterId;
    }
    
    // Getters and Setters
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    
    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
    
    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    
    public Integer getMarks() { return marks; }
    public void setMarks(Integer marks) { this.marks = marks; }
    
    public Integer getNegativeMarks() { return negativeMarks; }
    public void setNegativeMarks(Integer negativeMarks) { this.negativeMarks = negativeMarks; }
    
    public Integer getTimeLimitSeconds() { return timeLimitSeconds; }
    public void setTimeLimitSeconds(Integer timeLimitSeconds) { this.timeLimitSeconds = timeLimitSeconds; }
    
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    
    public Long getChapterId() { return chapterId; }
    public void setChapterId(Long chapterId) { this.chapterId = chapterId; }
    
    public Long getCourseTypeId() { return courseTypeId; }
    public void setCourseTypeId(Long courseTypeId) { this.courseTypeId = courseTypeId; }
    
    public Long getRelationshipId() { return relationshipId; }
    public void setRelationshipId(Long relationshipId) { this.relationshipId = relationshipId; }
    
    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    
    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
    
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public List<QuestionOptionRequestDTO> getOptions() { return options; }
    public void setOptions(List<QuestionOptionRequestDTO> options) { this.options = options; }
    
    public List<QuestionExamHistoryRequestDTO> getExamHistories() { return examHistories; }
    public void setExamHistories(List<QuestionExamHistoryRequestDTO> examHistories) { this.examHistories = examHistories; }
    
    public List<Long> getExamSuitabilities() { return examSuitabilities; }
    public void setExamSuitabilities(List<Long> examSuitabilities) { this.examSuitabilities = examSuitabilities; }
}
