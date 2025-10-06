package com.coaxial.dto;

import com.coaxial.entity.Topic;

public class TopicResponseDTO {
    
    private Long id;
    private String name;
    private String description;
    private Integer displayOrder;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
    private String createdByName;
    private String updatedByName;
    
    // Enhanced relationship information
    private Long courseTypeId;
    private String courseTypeName;
    private Long relationshipId;
    private String relationshipType;
    
    // Course information
    private Long courseId;
    private String courseName;
    
    // Class information (for Academic courses)
    private Long classId;
    private String className;
    
    // Exam information (for Competitive courses)
    private Long examId;
    private String examName;
    
    // Linkage information
    private Long linkageId;
    private String linkageType;
    
    // Master subject information
    private Long subjectId;
    private String subjectName;
    
    // Constructors
    public TopicResponseDTO() {}
    
    public TopicResponseDTO(Topic topic) {
        // Topic ID is now composite - set to null or create composite ID if needed
        // this.id = Long.parseLong(topic.getCourseTypeId() + "" + topic.getRelationshipId());
        this.id = null; // Topic now uses composite key (courseTypeId + relationshipId)
        this.name = topic.getName();
        this.description = topic.getDescription();
        
        // Set basic topic information
        
        this.displayOrder = topic.getDisplayOrder();
        this.isActive = topic.getIsActive();
        this.createdAt = topic.getCreatedAt() != null ? topic.getCreatedAt().toString() : null;
        this.updatedAt = topic.getUpdatedAt() != null ? topic.getUpdatedAt().toString() : null;
        
        if (topic.getCreatedBy() != null) {
            this.createdByName = topic.getCreatedBy().getFullName();
        }
        if (topic.getUpdatedBy() != null) {
            this.updatedByName = topic.getUpdatedBy().getFullName();
        }
        
        // Set enhanced relationship information
        this.courseTypeId = topic.getCourseTypeId();
        this.courseTypeName = topic.getCourseTypeName();
        this.relationshipId = topic.getRelationshipId();
        this.relationshipType = topic.getRelationshipType();
    }
    
    // Helper methods for context information
    public String getContextType() {
        return courseTypeName;
    }
    
    public String getContextName() {
        return courseTypeName;
    }
    
    public Long getContextId() {
        return relationshipId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    
    public String getUpdatedByName() { return updatedByName; }
    public void setUpdatedByName(String updatedByName) { this.updatedByName = updatedByName; }
    
    // Enhanced relationship getters and setters
    public Long getCourseTypeId() { return courseTypeId; }
    public void setCourseTypeId(Long courseTypeId) { this.courseTypeId = courseTypeId; }
    
    public String getCourseTypeName() { return courseTypeName; }
    public void setCourseTypeName(String courseTypeName) { this.courseTypeName = courseTypeName; }
    
    public Long getRelationshipId() { return relationshipId; }
    public void setRelationshipId(Long relationshipId) { this.relationshipId = relationshipId; }
    
    public String getRelationshipType() { return relationshipType; }
    public void setRelationshipType(String relationshipType) { this.relationshipType = relationshipType; }
    
    // Course information getters and setters
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    // Class information getters and setters
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    
    // Exam information getters and setters
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }
    
    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }
    
    // Linkage information getters and setters
    public Long getLinkageId() { return linkageId; }
    public void setLinkageId(Long linkageId) { this.linkageId = linkageId; }
    
    public String getLinkageType() { return linkageType; }
    public void setLinkageType(String linkageType) { this.linkageType = linkageType; }
    
    // Master subject information getters and setters
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
}
