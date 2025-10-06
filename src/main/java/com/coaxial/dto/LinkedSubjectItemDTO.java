package com.coaxial.dto;

import java.time.LocalDateTime;

public class LinkedSubjectItemDTO {
    private String linkageType; // CLASS | EXAM | COURSE
    private Long linkageId;

    private Long subjectId;
    private String subjectName;
    private String subjectDescription;

    private Long courseTypeId;
    private String courseTypeName;
    private String structureType;

    private Long courseId;
    private String courseName;

    private Long classId; // when CLASS
    private String className;

    private Long examId; // when EXAM
    private String examName;

    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public String getLinkageType() { return linkageType; }
    public void setLinkageType(String linkageType) { this.linkageType = linkageType; }

    public Long getLinkageId() { return linkageId; }
    public void setLinkageId(Long linkageId) { this.linkageId = linkageId; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getSubjectDescription() { return subjectDescription; }
    public void setSubjectDescription(String subjectDescription) { this.subjectDescription = subjectDescription; }

    public Long getCourseTypeId() { return courseTypeId; }
    public void setCourseTypeId(Long courseTypeId) { this.courseTypeId = courseTypeId; }

    public String getCourseTypeName() { return courseTypeName; }
    public void setCourseTypeName(String courseTypeName) { this.courseTypeName = courseTypeName; }

    public String getStructureType() { return structureType; }
    public void setStructureType(String structureType) { this.structureType = structureType; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}


