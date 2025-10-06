package com.coaxial.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Student-friendly Course Response DTO
 * Enhanced with student-specific information and learning paths
 */
public class StudentCourseResponseDTO {
    
    private Long id;
    private String name;
    private String description;
    private Long courseTypeId;
    private String courseTypeName;
    private String structureType;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    
    // Student-friendly enhancements
    private String studentFriendlyName;
    private String learningPathDescription;
    private List<String> features;
    private String subscriptionLevel;
    private Boolean isSubscribed;
    private String enrollmentStatus;
    
    // Pricing and subscription information
    private Map<String, Object> coursePricing;
    private List<SubscriptionOption> subscriptionOptions;
    
    // Hierarchical data for students
    private List<ClassSummary> classes;
    private List<ExamSummary> exams;
    private List<SubjectSummary> subjects;
    private CourseStats stats;
    
    // Constructors
    public StudentCourseResponseDTO() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Long getCourseTypeId() { return courseTypeId; }
    public void setCourseTypeId(Long courseTypeId) { this.courseTypeId = courseTypeId; }
    
    public String getCourseTypeName() { return courseTypeName; }
    public void setCourseTypeName(String courseTypeName) { this.courseTypeName = courseTypeName; }
    
    public String getStructureType() { return structureType; }
    public void setStructureType(String structureType) { this.structureType = structureType; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getStudentFriendlyName() { return studentFriendlyName; }
    public void setStudentFriendlyName(String studentFriendlyName) { this.studentFriendlyName = studentFriendlyName; }
    
    public String getLearningPathDescription() { return learningPathDescription; }
    public void setLearningPathDescription(String learningPathDescription) { this.learningPathDescription = learningPathDescription; }
    
    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }
    
    public String getSubscriptionLevel() { return subscriptionLevel; }
    public void setSubscriptionLevel(String subscriptionLevel) { this.subscriptionLevel = subscriptionLevel; }
    
    public Boolean getIsSubscribed() { return isSubscribed; }
    public void setIsSubscribed(Boolean isSubscribed) { this.isSubscribed = isSubscribed; }
    
    public String getEnrollmentStatus() { return enrollmentStatus; }
    public void setEnrollmentStatus(String enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }
    
    public List<ClassSummary> getClasses() { return classes; }
    public void setClasses(List<ClassSummary> classes) { this.classes = classes; }
    
    public List<ExamSummary> getExams() { return exams; }
    public void setExams(List<ExamSummary> exams) { this.exams = exams; }
    
    public List<SubjectSummary> getSubjects() { return subjects; }
    public void setSubjects(List<SubjectSummary> subjects) { this.subjects = subjects; }
    
    public CourseStats getStats() { return stats; }
    public void setStats(CourseStats stats) { this.stats = stats; }
    
    public Map<String, Object> getCoursePricing() { return coursePricing; }
    public void setCoursePricing(Map<String, Object> coursePricing) { this.coursePricing = coursePricing; }
    
    public List<SubscriptionOption> getSubscriptionOptions() { return subscriptionOptions; }
    public void setSubscriptionOptions(List<SubscriptionOption> subscriptionOptions) { this.subscriptionOptions = subscriptionOptions; }
    
    // Inner classes for student-friendly summaries
    public static class ClassSummary {
        private Long id;
        private String name;
        private String description;
        private Integer studentCount;
        private Boolean isEnrolled;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Integer getStudentCount() { return studentCount; }
        public void setStudentCount(Integer studentCount) { this.studentCount = studentCount; }
        
        public Boolean getIsEnrolled() { return isEnrolled; }
        public void setIsEnrolled(Boolean isEnrolled) { this.isEnrolled = isEnrolled; }
    }
    
    public static class ExamSummary {
        private Long id;
        private String name;
        private String description;
        private Integer questionCount;
        private Integer timeLimit;
        private Boolean isAttempted;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Integer getQuestionCount() { return questionCount; }
        public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
        
        public Integer getTimeLimit() { return timeLimit; }
        public void setTimeLimit(Integer timeLimit) { this.timeLimit = timeLimit; }
        
        public Boolean getIsAttempted() { return isAttempted; }
        public void setIsAttempted(Boolean isAttempted) { this.isAttempted = isAttempted; }
    }
    
    public static class SubjectSummary {
        private Long id;
        private String name;
        private String description;
        private Integer topicCount;
        private Integer moduleCount;
        private Boolean isAccessible;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Integer getTopicCount() { return topicCount; }
        public void setTopicCount(Integer topicCount) { this.topicCount = topicCount; }
        
        public Integer getModuleCount() { return moduleCount; }
        public void setModuleCount(Integer moduleCount) { this.moduleCount = moduleCount; }
        
        public Boolean getIsAccessible() { return isAccessible; }
        public void setIsAccessible(Boolean isAccessible) { this.isAccessible = isAccessible; }
    }
    
    public static class CourseStats {
        private Integer totalClasses;
        private Integer totalExams;
        private Integer totalSubjects;
        private Integer totalTopics;
        private Integer totalModules;
        private Integer completedClasses;
        private Integer completedExams;
        private Double progressPercentage;
        
        // Getters and Setters
        public Integer getTotalClasses() { return totalClasses; }
        public void setTotalClasses(Integer totalClasses) { this.totalClasses = totalClasses; }
        
        public Integer getTotalExams() { return totalExams; }
        public void setTotalExams(Integer totalExams) { this.totalExams = totalExams; }
        
        public Integer getTotalSubjects() { return totalSubjects; }
        public void setTotalSubjects(Integer totalSubjects) { this.totalSubjects = totalSubjects; }
        
        public Integer getTotalTopics() { return totalTopics; }
        public void setTotalTopics(Integer totalTopics) { this.totalTopics = totalTopics; }
        
        public Integer getTotalModules() { return totalModules; }
        public void setTotalModules(Integer totalModules) { this.totalModules = totalModules; }
        
        public Integer getCompletedClasses() { return completedClasses; }
        public void setCompletedClasses(Integer completedClasses) { this.completedClasses = completedClasses; }
        
        public Integer getCompletedExams() { return completedExams; }
        public void setCompletedExams(Integer completedExams) { this.completedExams = completedExams; }
        
        public Double getProgressPercentage() { return progressPercentage; }
        public void setProgressPercentage(Double progressPercentage) { this.progressPercentage = progressPercentage; }
    }
    
    // Inner class for subscription options
    public static class SubscriptionOption {
        private String level;
        private String levelName;
        private Long entityId;
        private String entityName;
        private Map<String, Integer> pricing;
        private Map<String, String> discounts;
        private String description;
        private Boolean isRecommended;
        
        // Getters and Setters
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        
        public String getLevelName() { return levelName; }
        public void setLevelName(String levelName) { this.levelName = levelName; }
        
        public Long getEntityId() { return entityId; }
        public void setEntityId(Long entityId) { this.entityId = entityId; }
        
        public String getEntityName() { return entityName; }
        public void setEntityName(String entityName) { this.entityName = entityName; }
        
        public Map<String, Integer> getPricing() { return pricing; }
        public void setPricing(Map<String, Integer> pricing) { this.pricing = pricing; }
        
        public Map<String, String> getDiscounts() { return discounts; }
        public void setDiscounts(Map<String, String> discounts) { this.discounts = discounts; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Boolean getIsRecommended() { return isRecommended; }
        public void setIsRecommended(Boolean isRecommended) { this.isRecommended = isRecommended; }
    }
}
