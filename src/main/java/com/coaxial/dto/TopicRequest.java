package com.coaxial.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TopicRequest {
    
    @NotBlank(message = "Topic name is required")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Course type ID is required")
    private Long courseTypeId;
    
    // Direct relationship IDs (only one should be provided based on courseTypeId)
    private Long classSubjectId;    // For Academic topics (courseTypeId = 1)
    private Long examSubjectId;     // For Competitive topics (courseTypeId = 2)
    private Long courseSubjectId;   // For Professional topics (courseTypeId = 3)
    
    // Alternative: filter by class/exam/course to find the subject relationship
    private Long classId;           // Find ClassSubject by classId and subjectId
    private Long examId;            // Find ExamSubject by examId and subjectId
    private Long courseId;          // Find CourseSubject by courseId and subjectId
    private Long subjectId;         // Used with classId/examId/courseId to find relationship
    
    private Integer displayOrder = 0;
    
    private Boolean isActive = true;
    
    // Constructors
    public TopicRequest() {}
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Long getCourseTypeId() { return courseTypeId; }
    public void setCourseTypeId(Long courseTypeId) { this.courseTypeId = courseTypeId; }
    
    public Long getClassSubjectId() { return classSubjectId; }
    public void setClassSubjectId(Long classSubjectId) { this.classSubjectId = classSubjectId; }
    
    public Long getExamSubjectId() { return examSubjectId; }
    public void setExamSubjectId(Long examSubjectId) { this.examSubjectId = examSubjectId; }
    
    public Long getCourseSubjectId() { return courseSubjectId; }
    public void setCourseSubjectId(Long courseSubjectId) { this.courseSubjectId = courseSubjectId; }
    
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }
    
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
