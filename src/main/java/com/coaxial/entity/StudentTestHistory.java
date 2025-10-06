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
@Table(name = "student_test_histories", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "test_id", "attempt_number"}))
public class StudentTestHistory {
    
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
    
    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber = 1;
    
    @Column(name = "test_name", length = 200)
    private String testName; // Store test name for historical reference
    
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;
    
    @Column(name = "total_questions")
    private Integer totalQuestions;
    
    @Column(name = "answered_questions")
    private Integer answeredQuestions = 0;
    
    @Column(name = "correct_answers")
    private Integer correctAnswers = 0;
    
    @Column(name = "wrong_answers")
    private Integer wrongAnswers = 0;
    
    @Column(name = "unanswered_questions")
    private Integer unansweredQuestions = 0;
    
    @Column(name = "total_marks_obtained")
    private Double totalMarksObtained = 0.0;
    
    @Column(name = "total_marks_available")
    private Double totalMarksAvailable = 0.0;
    
    @Column(name = "percentage")
    private Double percentage = 0.0;
    
    @Column(name = "is_passed")
    private Boolean isPassed = false;
    
    @Column(name = "rank_in_test")
    private Integer rankInTest; // Student's rank in this test
    
    @Column(name = "total_students")
    private Integer totalStudents; // Total students who took this test
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public StudentTestHistory() {}
    
    public StudentTestHistory(User student, Test test, Integer attemptNumber) {
        this.student = student;
        this.test = test;
        this.attemptNumber = attemptNumber;
        this.testName = test.getTestName();
        this.startedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    
    public Test getTest() { return test; }
    public void setTest(Test test) { this.test = test; }
    
    public Integer getAttemptNumber() { return attemptNumber; }
    public void setAttemptNumber(Integer attemptNumber) { this.attemptNumber = attemptNumber; }
    
    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public Integer getTimeTakenSeconds() { return timeTakenSeconds; }
    public void setTimeTakenSeconds(Integer timeTakenSeconds) { this.timeTakenSeconds = timeTakenSeconds; }
    
    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }
    
    public Integer getAnsweredQuestions() { return answeredQuestions; }
    public void setAnsweredQuestions(Integer answeredQuestions) { this.answeredQuestions = answeredQuestions; }
    
    public Integer getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(Integer correctAnswers) { this.correctAnswers = correctAnswers; }
    
    public Integer getWrongAnswers() { return wrongAnswers; }
    public void setWrongAnswers(Integer wrongAnswers) { this.wrongAnswers = wrongAnswers; }
    
    public Integer getUnansweredQuestions() { return unansweredQuestions; }
    public void setUnansweredQuestions(Integer unansweredQuestions) { this.unansweredQuestions = unansweredQuestions; }
    
    public Double getTotalMarksObtained() { return totalMarksObtained; }
    public void setTotalMarksObtained(Double totalMarksObtained) { this.totalMarksObtained = totalMarksObtained; }
    
    public Double getTotalMarksAvailable() { return totalMarksAvailable; }
    public void setTotalMarksAvailable(Double totalMarksAvailable) { this.totalMarksAvailable = totalMarksAvailable; }
    
    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }
    
    public Boolean getIsPassed() { return isPassed; }
    public void setIsPassed(Boolean isPassed) { this.isPassed = isPassed; }
    
    public Integer getRankInTest() { return rankInTest; }
    public void setRankInTest(Integer rankInTest) { this.rankInTest = rankInTest; }
    
    public Integer getTotalStudents() { return totalStudents; }
    public void setTotalStudents(Integer totalStudents) { this.totalStudents = totalStudents; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // Helper methods
    public long getDurationInSeconds() {
        if (completedAt != null && startedAt != null) {
            return java.time.Duration.between(startedAt, completedAt).getSeconds();
        }
        return 0;
    }
    
    public void calculateResults() {
        if (totalMarksAvailable > 0) {
            this.percentage = (totalMarksObtained / totalMarksAvailable) * 100;
        }
        
        if (test != null && test.getPassingMarks() != null) {
            this.isPassed = totalMarksObtained >= test.getPassingMarks();
        }
    }
}
