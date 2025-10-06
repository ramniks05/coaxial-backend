package com.coaxial.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "questions")
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Question text is required")
    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;
    
    @Column(name = "question_type", length = 50)
    private String questionType = "MULTIPLE_CHOICE"; // MULTIPLE_CHOICE, TRUE_FALSE, FILL_BLANK, ESSAY
    
    @Column(name = "difficulty_level", length = 20)
    private String difficultyLevel = "MEDIUM"; // EASY, MEDIUM, HARD
    
    @Column(name = "marks")
    private Integer marks = 1;
    
    @Column(name = "negative_marks")
    private Integer negativeMarks = 0;
    
    @Column(name = "time_limit_seconds")
    private Integer timeLimitSeconds;
    
    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    // Primary relationship - Question belongs to Chapter
    @NotNull(message = "Chapter is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;
    
    // Additional references for flexible test creation
    @Column(name = "course_type_id")
    private Long courseTypeId; // 1=Academic, 2=Competitive, 3=Professional
    
    @Column(name = "relationship_id")
    private Long relationshipId; // classSubjectId, examSubjectId, or courseSubjectId
    
    @Column(name = "topic_id")
    private Long topicId; // Direct topic reference
    
    @Column(name = "module_id")
    private Long moduleId; // Direct module reference
    
    @Column(name = "subject_id")
    private Long subjectId; // Direct subject reference (resolved from relationship)
    
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
    
    // Question options (for multiple choice questions)
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuestionOption> options = new ArrayList<>();
    
    // Exam tagging relationships
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuestionExamHistory> examHistories = new ArrayList<>();
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuestionExamSuitability> examSuitabilities = new ArrayList<>();
    
    // Constructors
    public Question() {}
    
    public Question(String questionText, Chapter chapter) {
        this.questionText = questionText;
        this.chapter = chapter;
    }
    
    public Question(String questionText, String questionType, Chapter chapter) {
        this.questionText = questionText;
        this.questionType = questionType;
        this.chapter = chapter;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    
    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
    
    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    
    public Integer getMarks() { return marks; }
    public void setMarks(Integer marks) { this.marks = marks; }
    
    public Integer getNegativeMarks() { return negativeMarks; }
    public void setNegativeMarks(Integer negativeMarks) { this.negativeMarks = negativeMarks; }
    
    public Integer getTimeLimitSeconds() { return timeLimitSeconds; }
    public void setTimeLimitSeconds(Integer timeLimitSeconds) { this.timeLimitSeconds = timeLimitSeconds; }
    
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Chapter getChapter() { return chapter; }
    public void setChapter(Chapter chapter) { this.chapter = chapter; }
    
    public Long getCourseTypeId() { return courseTypeId; }
    public void setCourseTypeId(Long courseTypeId) { this.courseTypeId = courseTypeId; }
    
    public Long getRelationshipId() { return relationshipId; }
    public void setRelationshipId(Long relationshipId) { this.relationshipId = relationshipId; }
    
    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    
    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
    
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    
    public User getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(User updatedBy) { this.updatedBy = updatedBy; }
    
    public List<QuestionOption> getOptions() { return options; }
    public void setOptions(List<QuestionOption> options) { this.options = options; }
    
    public List<QuestionExamHistory> getExamHistories() { return examHistories; }
    public void setExamHistories(List<QuestionExamHistory> examHistories) { this.examHistories = examHistories; }
    
    public List<QuestionExamSuitability> getExamSuitabilities() { return examSuitabilities; }
    public void setExamSuitabilities(List<QuestionExamSuitability> examSuitabilities) { this.examSuitabilities = examSuitabilities; }
    
    // Helper methods for context information
    public String getCourseTypeName() {
        if (courseTypeId == 1) return "Academic";
        if (courseTypeId == 2) return "Competitive";
        if (courseTypeId == 3) return "Professional";
        return "Unknown";
    }
    
    public String getRelationshipType() {
        if (courseTypeId == 1) return "ClassSubject";
        if (courseTypeId == 2) return "ExamSubject";
        if (courseTypeId == 3) return "CourseSubject";
        return "Unknown";
    }
}
