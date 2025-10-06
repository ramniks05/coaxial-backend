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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "question_options")
public class QuestionOption {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @NotBlank(message = "Option text is required")
    @Column(name = "option_text", columnDefinition = "TEXT", nullable = false)
    private String optionText;
    
    @Column(name = "option_letter", length = 2)
    private String optionLetter; // A, B, C, D, etc.
    
    @Column(name = "is_correct")
    private Boolean isCorrect = false;
    
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public QuestionOption() {}
    
    public QuestionOption(Question question, String optionText, String optionLetter) {
        this.question = question;
        this.optionText = optionText;
        this.optionLetter = optionLetter;
    }
    
    public QuestionOption(Question question, String optionText, String optionLetter, Boolean isCorrect) {
        this.question = question;
        this.optionText = optionText;
        this.optionLetter = optionLetter;
        this.isCorrect = isCorrect;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
    
    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }
    
    public String getOptionLetter() { return optionLetter; }
    public void setOptionLetter(String optionLetter) { this.optionLetter = optionLetter; }
    
    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
