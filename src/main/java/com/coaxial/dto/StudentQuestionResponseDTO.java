package com.coaxial.dto;

import java.util.List;

/**
 * Student-friendly question response DTO
 * Shows questions with correct answers and explanations for learning/practice
 */
public class StudentQuestionResponseDTO {
    
    private Long id;
    private String questionText;
    private String questionType;
    private String difficultyLevel;
    private Integer marks;
    private String explanation;
    
    // Chapter information
    private Long chapterId;
    private String chapterName;
    
    // Module information
    private Long moduleId;
    private String moduleName;
    
    // Topic information
    private Long topicId;
    private String topicName;
    
    // Subject information
    private Long subjectId;
    private String subjectName;
    
    // Question options with correct answer indication
    private List<StudentQuestionOptionDTO> options;
    
    public StudentQuestionResponseDTO() {}
    
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
    
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    
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
    
    public List<StudentQuestionOptionDTO> getOptions() { return options; }
    public void setOptions(List<StudentQuestionOptionDTO> options) { this.options = options; }
    
    /**
     * Nested class for question options (student version)
     */
    public static class StudentQuestionOptionDTO {
        private Long optionId;
        private String optionText;
        private Boolean isCorrect;  // Show correct answer for learning
        
        public StudentQuestionOptionDTO() {}
        
        public StudentQuestionOptionDTO(Long optionId, String optionText, Boolean isCorrect) {
            this.optionId = optionId;
            this.optionText = optionText;
            this.isCorrect = isCorrect;
        }
        
        public Long getOptionId() { return optionId; }
        public void setOptionId(Long optionId) { this.optionId = optionId; }
        
        public String getOptionText() { return optionText; }
        public void setOptionText(String optionText) { this.optionText = optionText; }
        
        public Boolean getIsCorrect() { return isCorrect; }
        public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
    }
}

