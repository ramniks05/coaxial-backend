package com.coaxial.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "student_performances", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "test_id"}))
public class StudentPerformance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;
    
    @Column(name = "total_attempts")
    private Integer totalAttempts = 0;
    
    @Column(name = "best_score")
    private Double bestScore = 0.0;
    
    @Column(name = "best_percentage")
    private Double bestPercentage = 0.0;
    
    @Column(name = "average_score")
    private Double averageScore = 0.0;
    
    @Column(name = "average_percentage")
    private Double averagePercentage = 0.0;
    
    @Column(name = "total_time_spent_seconds")
    private Integer totalTimeSpentSeconds = 0;
    
    @Column(name = "average_time_per_question")
    private Double averageTimePerQuestion = 0.0;
    
    @Column(name = "total_questions_attempted")
    private Integer totalQuestionsAttempted = 0;
    
    @Column(name = "total_correct_answers")
    private Integer totalCorrectAnswers = 0;
    
    @Column(name = "total_wrong_answers")
    private Integer totalWrongAnswers = 0;
    
    @Column(name = "accuracy_percentage")
    private Double accuracyPercentage = 0.0;
    
    @Column(name = "improvement_trend")
    private String improvementTrend = "STABLE"; // IMPROVING, DECLINING, STABLE
    
    @Column(name = "last_attempt_date")
    private LocalDateTime lastAttemptDate;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public StudentPerformance() {}
    
    public StudentPerformance(User student, Test test) {
        this.student = student;
        this.test = test;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    
    public Test getTest() { return test; }
    public void setTest(Test test) { this.test = test; }
    
    public Integer getTotalAttempts() { return totalAttempts; }
    public void setTotalAttempts(Integer totalAttempts) { this.totalAttempts = totalAttempts; }
    
    public Double getBestScore() { return bestScore; }
    public void setBestScore(Double bestScore) { this.bestScore = bestScore; }
    
    public Double getBestPercentage() { return bestPercentage; }
    public void setBestPercentage(Double bestPercentage) { this.bestPercentage = bestPercentage; }
    
    public Double getAverageScore() { return averageScore; }
    public void setAverageScore(Double averageScore) { this.averageScore = averageScore; }
    
    public Double getAveragePercentage() { return averagePercentage; }
    public void setAveragePercentage(Double averagePercentage) { this.averagePercentage = averagePercentage; }
    
    public Integer getTotalTimeSpentSeconds() { return totalTimeSpentSeconds; }
    public void setTotalTimeSpentSeconds(Integer totalTimeSpentSeconds) { this.totalTimeSpentSeconds = totalTimeSpentSeconds; }
    
    public Double getAverageTimePerQuestion() { return averageTimePerQuestion; }
    public void setAverageTimePerQuestion(Double averageTimePerQuestion) { this.averageTimePerQuestion = averageTimePerQuestion; }
    
    public Integer getTotalQuestionsAttempted() { return totalQuestionsAttempted; }
    public void setTotalQuestionsAttempted(Integer totalQuestionsAttempted) { this.totalQuestionsAttempted = totalQuestionsAttempted; }
    
    public Integer getTotalCorrectAnswers() { return totalCorrectAnswers; }
    public void setTotalCorrectAnswers(Integer totalCorrectAnswers) { this.totalCorrectAnswers = totalCorrectAnswers; }
    
    public Integer getTotalWrongAnswers() { return totalWrongAnswers; }
    public void setTotalWrongAnswers(Integer totalWrongAnswers) { this.totalWrongAnswers = totalWrongAnswers; }
    
    public Double getAccuracyPercentage() { return accuracyPercentage; }
    public void setAccuracyPercentage(Double accuracyPercentage) { this.accuracyPercentage = accuracyPercentage; }
    
    public String getImprovementTrend() { return improvementTrend; }
    public void setImprovementTrend(String improvementTrend) { this.improvementTrend = improvementTrend; }
    
    public LocalDateTime getLastAttemptDate() { return lastAttemptDate; }
    public void setLastAttemptDate(LocalDateTime lastAttemptDate) { this.lastAttemptDate = lastAttemptDate; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public void calculateAccuracy() {
        if (totalQuestionsAttempted > 0) {
            this.accuracyPercentage = (double) totalCorrectAnswers / totalQuestionsAttempted * 100;
        }
    }
    
    public void calculateAverageTimePerQuestion() {
        if (totalQuestionsAttempted > 0) {
            this.averageTimePerQuestion = (double) totalTimeSpentSeconds / totalQuestionsAttempted;
        }
    }
    
    public void updatePerformance(Double score, Double percentage, Integer timeSpent, Integer correct, Integer wrong) {
        this.totalAttempts++;
        this.lastAttemptDate = LocalDateTime.now();
        
        // Update best scores
        if (score > this.bestScore) {
            this.bestScore = score;
        }
        if (percentage > this.bestPercentage) {
            this.bestPercentage = percentage;
        }
        
        // Update totals
        this.totalTimeSpentSeconds += timeSpent;
        this.totalCorrectAnswers += correct;
        this.totalWrongAnswers += wrong;
        this.totalQuestionsAttempted += (correct + wrong);
        
        // Recalculate averages
        this.averageScore = (this.averageScore * (totalAttempts - 1) + score) / totalAttempts;
        this.averagePercentage = (this.averagePercentage * (totalAttempts - 1) + percentage) / totalAttempts;
        
        // Recalculate derived metrics
        calculateAccuracy();
        calculateAverageTimePerQuestion();
    }
}
