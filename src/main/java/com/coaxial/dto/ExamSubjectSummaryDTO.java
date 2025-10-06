package com.coaxial.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExamSubjectSummaryDTO {
    
    private Long id;
    private Long examId;
    private String examName;
    private Long subjectId;
    private String subjectName;
    private BigDecimal weightage;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    
    // Constructors
    public ExamSubjectSummaryDTO() {}
    
    public ExamSubjectSummaryDTO(Long id, Long examId, String examName, Long subjectId, String subjectName) {
        this.id = id;
        this.examId = examId;
        this.examName = examName;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }
    
    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }
    
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    
    public BigDecimal getWeightage() { return weightage; }
    public void setWeightage(BigDecimal weightage) { this.weightage = weightage; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
