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
@Table(name = "question_exam_histories", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"question_id", "master_exam_id", "appeared_year_id"}))
public class QuestionExamHistory {
    
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
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appeared_year_id", nullable = false)
    private MasterYear appearedYear; // Reference to master_years
    
    @Column(name = "appeared_session", length = 100)
    private String appearedSession; // e.g., "JEE Main 2023", "NEET 2024"
    
    @Column(name = "marks_in_exam")
    private Integer marksInExam; // Marks this question carried in the exam
    
    @Column(name = "question_number_in_exam", length = 20)
    private String questionNumberInExam; // e.g., "Q1", "Q25", "Section A-3"
    
    @Column(name = "difficulty_in_exam", length = 20)
    private String difficultyInExam; // Difficulty level as it appeared in the exam
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; // Additional notes about this question in this exam
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    // Constructors
    public QuestionExamHistory() {}
    
    public QuestionExamHistory(Question question, MasterExam masterExam, MasterYear appearedYear) {
        this.question = question;
        this.masterExam = masterExam;
        this.appearedYear = appearedYear;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
    
    public MasterExam getMasterExam() { return masterExam; }
    public void setMasterExam(MasterExam masterExam) { this.masterExam = masterExam; }
    
    public MasterYear getAppearedYear() { return appearedYear; }
    public void setAppearedYear(MasterYear appearedYear) { this.appearedYear = appearedYear; }
    
    public String getAppearedSession() { return appearedSession; }
    public void setAppearedSession(String appearedSession) { this.appearedSession = appearedSession; }
    
    public Integer getMarksInExam() { return marksInExam; }
    public void setMarksInExam(Integer marksInExam) { this.marksInExam = marksInExam; }
    
    public String getQuestionNumberInExam() { return questionNumberInExam; }
    public void setQuestionNumberInExam(String questionNumberInExam) { this.questionNumberInExam = questionNumberInExam; }
    
    public String getDifficultyInExam() { return difficultyInExam; }
    public void setDifficultyInExam(String difficultyInExam) { this.difficultyInExam = difficultyInExam; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}
