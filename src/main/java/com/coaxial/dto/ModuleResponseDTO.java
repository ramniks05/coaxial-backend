package com.coaxial.dto;

import com.coaxial.entity.Module;

public class ModuleResponseDTO {
    
    private Long id;
    private String name;
    private String description;
    private Long topicId;
    private String topicName;
    private Long subjectId;
    private String subjectName;
    private String subjectType;
    private Integer displayOrder;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
    private String createdByName;
    private String updatedByName;
    
    // Additional fields for enhanced response
    private Long courseId;
    private String courseName;
    private Long courseTypeId;
    private String courseTypeName;
    private String structureType;
    private Long classId;
    private String className;
    private Long examId;
    private String examName;
    
    // Constructors
    public ModuleResponseDTO() {}
    
    public ModuleResponseDTO(Module module) {
        this.id = module.getId();
        this.name = module.getName();
        this.description = module.getDescription();
        this.topicId = module.getTopic().getId();
        this.topicName = module.getTopic().getName();
        // Subject information is no longer directly accessible from Topic entity
        // These fields will need to be populated separately if needed
        this.displayOrder = module.getDisplayOrder();
        this.isActive = module.getIsActive();
        this.createdAt = module.getCreatedAt() != null ? module.getCreatedAt().toString() : null;
        this.updatedAt = module.getUpdatedAt() != null ? module.getUpdatedAt().toString() : null;
        
        if (module.getCreatedBy() != null) {
            this.createdByName = module.getCreatedBy().getFullName();
        }
        if (module.getUpdatedBy() != null) {
            this.updatedByName = module.getUpdatedBy().getFullName();
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
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
    
    // Additional field getters and setters
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public Long getCourseTypeId() { return courseTypeId; }
    public void setCourseTypeId(Long courseTypeId) { this.courseTypeId = courseTypeId; }
    
    public String getCourseTypeName() { return courseTypeName; }
    public void setCourseTypeName(String courseTypeName) { this.courseTypeName = courseTypeName; }
    
    public String getStructureType() { return structureType; }
    public void setStructureType(String structureType) { this.structureType = structureType; }
    
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }
    
    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }
}
