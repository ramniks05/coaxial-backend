package com.coaxial.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "question_exam_suitabilities", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"question_id", "master_exam_id"}))
public class QuestionExamSuitability {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_exam_id", nullable = false)
    private MasterExam masterExam;
    
    @Column(name = "suitability_level", length = 20)
    private String suitabilityLevel = "MEDIUM"; // HIGH, MEDIUM, LOW
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; // Additional notes about suitability
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    // Constructors
    public QuestionExamSuitability() {}
    
    public QuestionExamSuitability(Question question, MasterExam masterExam, String suitabilityLevel) {
        this.question = question;
        this.masterExam = masterExam;
        this.suitabilityLevel = suitabilityLevel;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
    
    public MasterExam getMasterExam() { return masterExam; }
    public void setMasterExam(MasterExam masterExam) { this.masterExam = masterExam; }
    
    public String getSuitabilityLevel() { return suitabilityLevel; }
    public void setSuitabilityLevel(String suitabilityLevel) { this.suitabilityLevel = suitabilityLevel; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}
