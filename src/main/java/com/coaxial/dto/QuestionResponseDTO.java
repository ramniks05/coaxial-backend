package com.coaxial.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.coaxial.entity.Question;

public class QuestionResponseDTO {
    
    private Long id;
    private String questionText;
    private String questionType;
    private String difficultyLevel;
    private Integer marks;
    private Integer negativeMarks;
    private Integer timeLimitSeconds;
    private String explanation;
    private Boolean isActive;
    
    // Chapter information
    private Long chapterId;
    private String chapterName;
    
    // Module information
    private Long moduleId;
    private String moduleName;
    
    // Topic information
    private Long topicId;
    private String topicName;
    
    // Subject information (resolved through Topic)
    private Long subjectId;
    private String subjectName;
    private String subjectType;
    
    // Additional references for test creation
    private Long courseTypeId;
    private String courseTypeName;
    private Long relationshipId;
    private String relationshipType;
    
    private Integer displayOrder;
    private String createdAt;
    private String updatedAt;
    private String createdByName;
    private String updatedByName;
    
    // Question options
    private List<QuestionOptionResponseDTO> options;
    
    // Exam tagging
    private List<QuestionExamHistoryResponseDTO> examHistories;
    private List<QuestionExamSuitabilityResponseDTO> examSuitabilities;
    
    // Constructors
    public QuestionResponseDTO() {}
    
    public QuestionResponseDTO(Question question) {
        this.id = question.getId();
        this.questionText = question.getQuestionText();
        this.questionType = question.getQuestionType();
        this.difficultyLevel = question.getDifficultyLevel();
        this.marks = question.getMarks();
        this.negativeMarks = question.getNegativeMarks();
        this.timeLimitSeconds = question.getTimeLimitSeconds();
        this.explanation = question.getExplanation();
        this.isActive = question.getIsActive();
        
        // Chapter information
        if (question.getChapter() != null) {
            this.chapterId = question.getChapter().getId();
            this.chapterName = question.getChapter().getName();
        }
        
        // Module information
        this.moduleId = question.getModuleId();
        
        // Topic information
        this.topicId = question.getTopicId();
        
        // Subject information (will be populated by service)
        this.subjectId = question.getSubjectId();
        
        // Additional references
        this.courseTypeId = question.getCourseTypeId();
        this.courseTypeName = question.getCourseTypeName();
        this.relationshipId = question.getRelationshipId();
        this.relationshipType = question.getRelationshipType();
        
        this.displayOrder = question.getDisplayOrder();
        this.createdAt = question.getCreatedAt() != null ? question.getCreatedAt().toString() : null;
        this.updatedAt = question.getUpdatedAt() != null ? question.getUpdatedAt().toString() : null;
        
        if (question.getCreatedBy() != null) {
            this.createdByName = question.getCreatedBy().getFullName();
        }
        if (question.getUpdatedBy() != null) {
            this.updatedByName = question.getUpdatedBy().getFullName();
        }
        
        // Question options
        if (question.getOptions() != null) {
            this.options = question.getOptions().stream()
                    .map(QuestionOptionResponseDTO::new)
                    .collect(Collectors.toList());
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
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
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Long getChapterId() { return chapterId; }
    public void setChapterId(Long chapterId) { this.chapterId = chapterId; }
    
    public String getChapterName() { return chapterName; }
    public void setChapterName(String chapterName) { this.chapterName = chapterName; }
    
    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
    
    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    
    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    
    public String getTopicName() { return topicName; }
    public void setTopicName(String topicName) { this.topicName = topicName; }
    
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    
    public String getSubjectType() { return subjectType; }
    public void setSubjectType(String subjectType) { this.subjectType = subjectType; }
    
    public Long getCourseTypeId() { return courseTypeId; }
    public void setCourseTypeId(Long courseTypeId) { this.courseTypeId = courseTypeId; }
    
    public String getCourseTypeName() { return courseTypeName; }
    public void setCourseTypeName(String courseTypeName) { this.courseTypeName = courseTypeName; }
    
    public Long getRelationshipId() { return relationshipId; }
    public void setRelationshipId(Long relationshipId) { this.relationshipId = relationshipId; }
    
    public String getRelationshipType() { return relationshipType; }
    public void setRelationshipType(String relationshipType) { this.relationshipType = relationshipType; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    
    public String getUpdatedByName() { return updatedByName; }
    public void setUpdatedByName(String updatedByName) { this.updatedByName = updatedByName; }
    
    public List<QuestionOptionResponseDTO> getOptions() { return options; }
    public void setOptions(List<QuestionOptionResponseDTO> options) { this.options = options; }
    
    public List<QuestionExamHistoryResponseDTO> getExamHistories() { return examHistories; }
    public void setExamHistories(List<QuestionExamHistoryResponseDTO> examHistories) { this.examHistories = examHistories; }
    
    public List<QuestionExamSuitabilityResponseDTO> getExamSuitabilities() { return examSuitabilities; }
    public void setExamSuitabilities(List<QuestionExamSuitabilityResponseDTO> examSuitabilities) { this.examSuitabilities = examSuitabilities; }
}
