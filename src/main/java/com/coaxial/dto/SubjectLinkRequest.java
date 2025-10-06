package com.coaxial.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public class SubjectLinkRequest {

	// Existing subject to link
	@NotNull(message = "subjectId is required")
	private Long subjectId;

	// Optional validation: frontend may send expected courseTypeId; we'll validate it matches
	private Long courseTypeId;

	// Linkage targets (one required based on subject's courseType structure)
	private Long classId;   // Academic
	private Long examId;    // Competitive
	private Long courseId;  // Professional

	// Optional linkage attributes
	private Integer displayOrder = 0;
	private Boolean isActive = true;
	private BigDecimal weightage;     // for exam subjects (optional)
	private Boolean isCompulsory;     // for course subjects (optional)

	public Long getSubjectId() { return subjectId; }
	public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

	public Long getCourseTypeId() { return courseTypeId; }
	public void setCourseTypeId(Long courseTypeId) { this.courseTypeId = courseTypeId; }

	public Long getClassId() { return classId; }
	public void setClassId(Long classId) { this.classId = classId; }

	public Long getExamId() { return examId; }
	public void setExamId(Long examId) { this.examId = examId; }

	public Long getCourseId() { return courseId; }
	public void setCourseId(Long courseId) { this.courseId = courseId; }

	public Integer getDisplayOrder() { return displayOrder; }
	public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

	public Boolean getIsActive() { return isActive; }
	public void setIsActive(Boolean isActive) { this.isActive = isActive; }

	public BigDecimal getWeightage() { return weightage; }
	public void setWeightage(BigDecimal weightage) { this.weightage = weightage; }

	public Boolean getIsCompulsory() { return isCompulsory; }
	public void setIsCompulsory(Boolean isCompulsory) { this.isCompulsory = isCompulsory; }
}


