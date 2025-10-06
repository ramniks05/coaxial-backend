package com.coaxial.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ExamSubjectRequestDTO {
    
    @NotNull(message = "Exam ID is required")
    private Long examId;
    
    @NotNull(message = "Subject IDs are required")
    private List<Long> subjectIds;
    
    @DecimalMin(value = "0.0", message = "Weightage cannot be negative")
    private BigDecimal weightage = new BigDecimal("100.00");
    
    @Min(value = 0, message = "Display order cannot be negative")
    private Integer displayOrder = 0;
    
    // Constructors
    public ExamSubjectRequestDTO() {}
    
    public ExamSubjectRequestDTO(Long examId, List<Long> subjectIds) {
        this.examId = examId;
        this.subjectIds = subjectIds;
    }
    
    // Getters and Setters
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }
    
    public List<Long> getSubjectIds() { return subjectIds; }
    public void setSubjectIds(List<Long> subjectIds) { this.subjectIds = subjectIds; }
    
    public BigDecimal getWeightage() { return weightage; }
    public void setWeightage(BigDecimal weightage) { this.weightage = weightage; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}
