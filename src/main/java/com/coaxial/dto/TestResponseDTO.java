package com.coaxial.dto;

import java.time.LocalDateTime;

import com.coaxial.enums.TestCreationMode;
import com.coaxial.enums.TestLevel;

public class TestResponseDTO {

    private Long id;
    private String name;
    private String testName; // For TestService compatibility
    private String description;
    private String instructions;
    private Integer durationMinutes;
    private Integer timeLimitMinutes; // For TestService compatibility
    private Double totalMarks;
    private Double passingMarks;
    private Boolean negativeMarking;
    private Double negativeMarkPercentage;
    private Integer maxAttempts;
    private Boolean isActive;
    private Boolean isPublished;
    private java.time.LocalDate startDate;
    private java.time.LocalDate endDate;
    private String testType;
    private Boolean allowReview;
    private Boolean showCorrectAnswers;
    private Boolean shuffleQuestions;
    private Boolean shuffleOptions;
    private Boolean allowSkip;
    private Integer timePerQuestion;
    private Long masterExamId; // For TestService compatibility
    private String masterExamName; // For TestService compatibility
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Dual-mode test system fields
    private TestCreationMode testCreationMode;
    private TestLevel testLevel;
    private Long courseTypeId;
    private String courseTypeName;
    private Long courseId;
    private String courseName;
    private Long classId;
    private String className;
    private Long examId;
    private String examName;
    private Long subjectLinkageId;
    private String subjectName;
    private Long topicId;
    private String topicName;
    private Long moduleId;
    private String moduleName;
    private Long chapterId;
    private String chapterName;
    
    private java.util.List<com.coaxial.dto.TestQuestionSummaryDTO> questions;

    // Constructors
    public TestResponseDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Double getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Double totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Double getPassingMarks() {
        return passingMarks;
    }

    public void setPassingMarks(Double passingMarks) {
        this.passingMarks = passingMarks;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    // Additional getters and setters for TestService compatibility
    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Integer getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(Integer timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
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

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public java.time.LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(java.time.LocalDate startDate) {
        this.startDate = startDate;
    }

    public java.time.LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(java.time.LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
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

    public Boolean getShuffleQuestions() {
        return shuffleQuestions;
    }

    public void setShuffleQuestions(Boolean shuffleQuestions) {
        this.shuffleQuestions = shuffleQuestions;
    }

    public Boolean getShuffleOptions() {
        return shuffleOptions;
    }

    public void setShuffleOptions(Boolean shuffleOptions) {
        this.shuffleOptions = shuffleOptions;
    }

    public Boolean getAllowSkip() {
        return allowSkip;
    }

    public void setAllowSkip(Boolean allowSkip) {
        this.allowSkip = allowSkip;
    }

    public Integer getTimePerQuestion() {
        return timePerQuestion;
    }

    public void setTimePerQuestion(Integer timePerQuestion) {
        this.timePerQuestion = timePerQuestion;
    }

    public Long getMasterExamId() {
        return masterExamId;
    }

    public void setMasterExamId(Long masterExamId) {
        this.masterExamId = masterExamId;
    }

    public String getMasterExamName() {
        return masterExamName;
    }

    public void setMasterExamName(String masterExamName) {
        this.masterExamName = masterExamName;
    }

    public java.util.List<com.coaxial.dto.TestQuestionSummaryDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(java.util.List<com.coaxial.dto.TestQuestionSummaryDTO> questions) {
        this.questions = questions;
    }
    
    // Dual-mode system getters and setters
    public TestCreationMode getTestCreationMode() { return testCreationMode; }
    public void setTestCreationMode(TestCreationMode testCreationMode) { this.testCreationMode = testCreationMode; }
    
    public TestLevel getTestLevel() { return testLevel; }
    public void setTestLevel(TestLevel testLevel) { this.testLevel = testLevel; }
    
    public Long getCourseTypeId() { return courseTypeId; }
    public void setCourseTypeId(Long courseTypeId) { this.courseTypeId = courseTypeId; }
    
    public String getCourseTypeName() { return courseTypeName; }
    public void setCourseTypeName(String courseTypeName) { this.courseTypeName = courseTypeName; }
    
    public Long getSubjectLinkageId() { return subjectLinkageId; }
    public void setSubjectLinkageId(Long subjectLinkageId) { this.subjectLinkageId = subjectLinkageId; }
    
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    
    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    
    public String getTopicName() { return topicName; }
    public void setTopicName(String topicName) { this.topicName = topicName; }
    
    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
    
    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    
    public Long getChapterId() { return chapterId; }
    public void setChapterId(Long chapterId) { this.chapterId = chapterId; }
    
    public String getChapterName() { return chapterName; }
    public void setChapterName(String chapterName) { this.chapterName = chapterName; }
}