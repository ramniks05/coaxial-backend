package com.coaxial.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.coaxial.enums.TestCreationMode;
import com.coaxial.enums.TestLevel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tests")
public class Test {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Test name is required")
    @Column(name = "test_name", nullable = false, length = 200)
    private String testName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;
    
    @NotNull(message = "Time limit is required")
    @Min(value = 1, message = "Time limit must be at least 1 minute")
    @Column(name = "time_limit_minutes", nullable = false)
    private Integer timeLimitMinutes;
    
    @Column(name = "total_marks")
    private Integer totalMarks;
    
    @Column(name = "passing_marks")
    private Integer passingMarks;
    
    @Column(name = "negative_marking")
    private Boolean negativeMarking = false;
    
    @Column(name = "negative_mark_percentage")
    private Double negativeMarkPercentage = 0.25; // 25% of marks deducted for wrong answer
    
    @Column(name = "max_attempts")
    private Integer maxAttempts = 1; // How many times student can take this test
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "is_published")
    private Boolean isPublished = false;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "display_order")
    private Integer displayOrder = 0;

    // Extended configuration
    @Column(name = "test_type", length = 50)
    private String testType; // e.g., PRACTICE, MOCK, SECTIONAL

    @Column(name = "allow_review")
    private Boolean allowReview = true;

    @Column(name = "show_correct_answers")
    private Boolean showCorrectAnswers = false;

    @Column(name = "shuffle_questions")
    private Boolean shuffleQuestions = false;

    @Column(name = "shuffle_options")
    private Boolean shuffleOptions = false;

    @Column(name = "allow_skip")
    private Boolean allowSkip = true;

    @Column(name = "time_per_question")
    private Integer timePerQuestion = 0; // seconds; 0 means disabled
    
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
    
    // Master exam association for test creation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_exam_id")
    private MasterExam masterExam;
    
    // Dual-mode test system: EXAM_BASED vs CONTENT_BASED
    @Enumerated(EnumType.STRING)
    @Column(name = "test_creation_mode", length = 20)
    private TestCreationMode testCreationMode = TestCreationMode.EXAM_BASED;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "test_level", length = 20)
    private TestLevel testLevel;
    
    // Content hierarchy linkage (for CONTENT_BASED mode)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_type_id")
    private CourseType courseType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;
    
    @Column(name = "subject_linkage_id")
    private Long subjectLinkageId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;
    
    // Relationships
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TestQuestion> testQuestions = new ArrayList<>();
    
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TestSession> testSessions = new ArrayList<>();
    
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TestAttempt> testAttempts = new ArrayList<>();
    
    // Constructors
    public Test() {}
    
    public Test(String testName, Integer timeLimitMinutes) {
        this.testName = testName;
        this.timeLimitMinutes = timeLimitMinutes;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    
    public Integer getTimeLimitMinutes() { return timeLimitMinutes; }
    public void setTimeLimitMinutes(Integer timeLimitMinutes) { this.timeLimitMinutes = timeLimitMinutes; }
    
    public Integer getTotalMarks() { return totalMarks; }
    public void setTotalMarks(Integer totalMarks) { this.totalMarks = totalMarks; }
    
    public Integer getPassingMarks() { return passingMarks; }
    public void setPassingMarks(Integer passingMarks) { this.passingMarks = passingMarks; }
    
    public Boolean getNegativeMarking() { return negativeMarking; }
    public void setNegativeMarking(Boolean negativeMarking) { this.negativeMarking = negativeMarking; }
    
    public Double getNegativeMarkPercentage() { return negativeMarkPercentage; }
    public void setNegativeMarkPercentage(Double negativeMarkPercentage) { this.negativeMarkPercentage = negativeMarkPercentage; }
    
    public Integer getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(Integer maxAttempts) { this.maxAttempts = maxAttempts; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Boolean getIsPublished() { return isPublished; }
    public void setIsPublished(Boolean isPublished) { this.isPublished = isPublished; }
    
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public String getTestType() { return testType; }
    public void setTestType(String testType) { this.testType = testType; }

    public Boolean getAllowReview() { return allowReview; }
    public void setAllowReview(Boolean allowReview) { this.allowReview = allowReview; }

    public Boolean getShowCorrectAnswers() { return showCorrectAnswers; }
    public void setShowCorrectAnswers(Boolean showCorrectAnswers) { this.showCorrectAnswers = showCorrectAnswers; }

    public Boolean getShuffleQuestions() { return shuffleQuestions; }
    public void setShuffleQuestions(Boolean shuffleQuestions) { this.shuffleQuestions = shuffleQuestions; }

    public Boolean getShuffleOptions() { return shuffleOptions; }
    public void setShuffleOptions(Boolean shuffleOptions) { this.shuffleOptions = shuffleOptions; }

    public Boolean getAllowSkip() { return allowSkip; }
    public void setAllowSkip(Boolean allowSkip) { this.allowSkip = allowSkip; }

    public Integer getTimePerQuestion() { return timePerQuestion; }
    public void setTimePerQuestion(Integer timePerQuestion) { this.timePerQuestion = timePerQuestion; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    
    public User getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(User updatedBy) { this.updatedBy = updatedBy; }
    
    public MasterExam getMasterExam() { return masterExam; }
    public void setMasterExam(MasterExam masterExam) { this.masterExam = masterExam; }
    
    public List<TestQuestion> getTestQuestions() { return testQuestions; }
    public void setTestQuestions(List<TestQuestion> testQuestions) { this.testQuestions = testQuestions; }
    
    public List<TestSession> getTestSessions() { return testSessions; }
    public void setTestSessions(List<TestSession> testSessions) { this.testSessions = testSessions; }
    
    public List<TestAttempt> getTestAttempts() { return testAttempts; }
    public void setTestAttempts(List<TestAttempt> testAttempts) { this.testAttempts = testAttempts; }
    
    // Dual-mode system getters and setters
    public TestCreationMode getTestCreationMode() { return testCreationMode; }
    public void setTestCreationMode(TestCreationMode testCreationMode) { this.testCreationMode = testCreationMode; }
    
    public TestLevel getTestLevel() { return testLevel; }
    public void setTestLevel(TestLevel testLevel) { this.testLevel = testLevel; }
    
    public CourseType getCourseType() { return courseType; }
    public void setCourseType(CourseType courseType) { this.courseType = courseType; }
    
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    
    public ClassEntity getClassEntity() { return classEntity; }
    public void setClassEntity(ClassEntity classEntity) { this.classEntity = classEntity; }
    
    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }
    
    public Long getSubjectLinkageId() { return subjectLinkageId; }
    public void setSubjectLinkageId(Long subjectLinkageId) { this.subjectLinkageId = subjectLinkageId; }
    
    public Topic getTopic() { return topic; }
    public void setTopic(Topic topic) { this.topic = topic; }
    
    public Module getModule() { return module; }
    public void setModule(Module module) { this.module = module; }
    
    public Chapter getChapter() { return chapter; }
    public void setChapter(Chapter chapter) { this.chapter = chapter; }
}
