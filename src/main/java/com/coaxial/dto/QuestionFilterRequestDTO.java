package com.coaxial.dto;

import java.time.LocalDateTime;
import java.util.List;

public class QuestionFilterRequestDTO {
    
    // Basic filters
    private Boolean isActive;
    private String questionType;
    private String difficultyLevel;
    private Integer minMarks;
    private Integer maxMarks;
    
    // Hierarchy filters
    private Long courseTypeId;        // 1=Academic, 2=Competitive, 3=Professional
    private Long relationshipId;      // classSubjectId, examSubjectId, courseSubjectId
    private Long subjectId;
    private Long topicId;
    private Long moduleId;
    private Long chapterId;
    
    // Exam suitability filters
    private List<Long> examIds;           // Specific exams
    private List<String> suitabilityLevels; // HIGH, MEDIUM, LOW
    private List<String> examTypes;       // ENTRANCE, BOARD, etc.
    private List<String> conductingBodies; // NTA, CBSE, etc.
    
    // Exam history filters
    private List<Integer> appearedYears;  // 2020, 2021, etc.
    private List<String> examSessions;    // "JEE Main 2023"
    private Integer minMarksInExam;
    private Integer maxMarksInExam;
    private List<String> questionNumbers; // "Q1", "Q25"
    private List<String> examDifficulties; // Difficulty as appeared in exam
    
    // Search filters
    private String questionTextSearch;
    private String explanationSearch;
    
    // Date filters
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
    
    // Pagination
    private Integer page = 0;
    private Integer size = 20;
    
    // Constructors
    public QuestionFilterRequestDTO() {}
    
    // Getters and Setters
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
    
    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    
    public Integer getMinMarks() { return minMarks; }
    public void setMinMarks(Integer minMarks) { this.minMarks = minMarks; }
    
    public Integer getMaxMarks() { return maxMarks; }
    public void setMaxMarks(Integer maxMarks) { this.maxMarks = maxMarks; }
    
    public Long getCourseTypeId() { return courseTypeId; }
    public void setCourseTypeId(Long courseTypeId) { this.courseTypeId = courseTypeId; }
    
    public Long getRelationshipId() { return relationshipId; }
    public void setRelationshipId(Long relationshipId) { this.relationshipId = relationshipId; }
    
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    
    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    
    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
    
    public Long getChapterId() { return chapterId; }
    public void setChapterId(Long chapterId) { this.chapterId = chapterId; }
    
    public List<Long> getExamIds() { return examIds; }
    public void setExamIds(List<Long> examIds) { this.examIds = examIds; }
    
    public List<String> getSuitabilityLevels() { return suitabilityLevels; }
    public void setSuitabilityLevels(List<String> suitabilityLevels) { this.suitabilityLevels = suitabilityLevels; }
    
    public List<String> getExamTypes() { return examTypes; }
    public void setExamTypes(List<String> examTypes) { this.examTypes = examTypes; }
    
    public List<String> getConductingBodies() { return conductingBodies; }
    public void setConductingBodies(List<String> conductingBodies) { this.conductingBodies = conductingBodies; }
    
    public List<Integer> getAppearedYears() { return appearedYears; }
    public void setAppearedYears(List<Integer> appearedYears) { this.appearedYears = appearedYears; }
    
    public List<String> getExamSessions() { return examSessions; }
    public void setExamSessions(List<String> examSessions) { this.examSessions = examSessions; }
    
    public Integer getMinMarksInExam() { return minMarksInExam; }
    public void setMinMarksInExam(Integer minMarksInExam) { this.minMarksInExam = minMarksInExam; }
    
    public Integer getMaxMarksInExam() { return maxMarksInExam; }
    public void setMaxMarksInExam(Integer maxMarksInExam) { this.maxMarksInExam = maxMarksInExam; }
    
    public List<String> getQuestionNumbers() { return questionNumbers; }
    public void setQuestionNumbers(List<String> questionNumbers) { this.questionNumbers = questionNumbers; }
    
    public List<String> getExamDifficulties() { return examDifficulties; }
    public void setExamDifficulties(List<String> examDifficulties) { this.examDifficulties = examDifficulties; }
    
    public String getQuestionTextSearch() { return questionTextSearch; }
    public void setQuestionTextSearch(String questionTextSearch) { this.questionTextSearch = questionTextSearch; }
    
    public String getExplanationSearch() { return explanationSearch; }
    public void setExplanationSearch(String explanationSearch) { this.explanationSearch = explanationSearch; }
    
    public LocalDateTime getCreatedAfter() { return createdAfter; }
    public void setCreatedAfter(LocalDateTime createdAfter) { this.createdAfter = createdAfter; }
    
    public LocalDateTime getCreatedBefore() { return createdBefore; }
    public void setCreatedBefore(LocalDateTime createdBefore) { this.createdBefore = createdBefore; }
    
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
}
