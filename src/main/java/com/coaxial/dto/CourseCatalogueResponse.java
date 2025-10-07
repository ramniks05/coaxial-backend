package com.coaxial.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Public Course Catalogue Response
 * Returns different structures based on course type
 */
public class CourseCatalogueResponse {
    
    private Long courseTypeId;
    private String courseType; // Academic, Competitive, Professional
    private String description;
    
    // Academic: Class-based response
    private List<ClassCatalogueItem> classes;
    
    // Competitive: Exam-based response
    private List<ExamCatalogueItem> exams;
    
    // Professional: Course-based response
    private List<CourseCatalogueItem> courses;
    
    // Constructors
    public CourseCatalogueResponse() {}
    
    // Getters and Setters
    public Long getCourseTypeId() {
        return courseTypeId;
    }
    
    public void setCourseTypeId(Long courseTypeId) {
        this.courseTypeId = courseTypeId;
    }
    
    public String getCourseType() {
        return courseType;
    }
    
    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<ClassCatalogueItem> getClasses() {
        return classes;
    }
    
    public void setClasses(List<ClassCatalogueItem> classes) {
        this.classes = classes;
    }
    
    public List<ExamCatalogueItem> getExams() {
        return exams;
    }
    
    public void setExams(List<ExamCatalogueItem> exams) {
        this.exams = exams;
    }
    
    public List<CourseCatalogueItem> getCourses() {
        return courses;
    }
    
    public void setCourses(List<CourseCatalogueItem> courses) {
        this.courses = courses;
    }
    
    // Inner class for Academic - Class Item
    public static class ClassCatalogueItem {
        private Long id;
        private Long courseId;
        private String courseName;
        private String className;
        private String description;
        private String level; // Beginner, Intermediate, Advanced
        private String duration; // "1 academic year"
        private Integer subjectCount;
        private Integer topicCount;
        private Integer moduleCount;
        private Integer chapterCount;
        private Integer questionCount;
        private List<SubjectInfo> subjects;
        private PricingInfo pricing;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public Long getCourseId() { return courseId; }
        public void setCourseId(Long courseId) { this.courseId = courseId; }
        
        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
        
        public Integer getSubjectCount() { return subjectCount; }
        public void setSubjectCount(Integer subjectCount) { this.subjectCount = subjectCount; }
        
        public Integer getTopicCount() { return topicCount; }
        public void setTopicCount(Integer topicCount) { this.topicCount = topicCount; }
        
        public Integer getModuleCount() { return moduleCount; }
        public void setModuleCount(Integer moduleCount) { this.moduleCount = moduleCount; }
        
        public Integer getChapterCount() { return chapterCount; }
        public void setChapterCount(Integer chapterCount) { this.chapterCount = chapterCount; }
        
        public Integer getQuestionCount() { return questionCount; }
        public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
        
        public List<SubjectInfo> getSubjects() { return subjects; }
        public void setSubjects(List<SubjectInfo> subjects) { this.subjects = subjects; }
        
        public PricingInfo getPricing() { return pricing; }
        public void setPricing(PricingInfo pricing) { this.pricing = pricing; }
    }
    
    // Inner class for Competitive - Exam Item
    public static class ExamCatalogueItem {
        private Long id;
        private Long courseId;
        private String courseName;
        private String examName;
        private String description;
        private String difficulty; // Easy, Medium, Hard
        private Integer questionCount;
        private Integer subjectCount;
        private Integer topicCount;
        private List<String> topicsList;
        private List<SubjectInfo> subjects;
        private PricingInfo pricing;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public Long getCourseId() { return courseId; }
        public void setCourseId(Long courseId) { this.courseId = courseId; }
        
        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        
        public String getExamName() { return examName; }
        public void setExamName(String examName) { this.examName = examName; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
        
        public Integer getQuestionCount() { return questionCount; }
        public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
        
        public Integer getSubjectCount() { return subjectCount; }
        public void setSubjectCount(Integer subjectCount) { this.subjectCount = subjectCount; }
        
        public Integer getTopicCount() { return topicCount; }
        public void setTopicCount(Integer topicCount) { this.topicCount = topicCount; }
        
        public List<String> getTopicsList() { return topicsList; }
        public void setTopicsList(List<String> topicsList) { this.topicsList = topicsList; }
        
        public List<SubjectInfo> getSubjects() { return subjects; }
        public void setSubjects(List<SubjectInfo> subjects) { this.subjects = subjects; }
        
        public PricingInfo getPricing() { return pricing; }
        public void setPricing(PricingInfo pricing) { this.pricing = pricing; }
    }
    
    // Inner class for Professional - Course Item
    public static class CourseCatalogueItem {
        private Long id;
        private String courseName;
        private String description;
        private String level; // Beginner, Intermediate, Advanced
        private String duration;
        private Integer moduleCount;
        private Integer chapterCount;
        private Integer projectCount;
        private Integer topicCount;
        private List<String> skillsCovered;
        private PricingInfo pricing;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
        
        public Integer getModuleCount() { return moduleCount; }
        public void setModuleCount(Integer moduleCount) { this.moduleCount = moduleCount; }
        
        public Integer getChapterCount() { return chapterCount; }
        public void setChapterCount(Integer chapterCount) { this.chapterCount = chapterCount; }
        
        public Integer getProjectCount() { return projectCount; }
        public void setProjectCount(Integer projectCount) { this.projectCount = projectCount; }
        
        public Integer getTopicCount() { return topicCount; }
        public void setTopicCount(Integer topicCount) { this.topicCount = topicCount; }
        
        public List<String> getSkillsCovered() { return skillsCovered; }
        public void setSkillsCovered(List<String> skillsCovered) { this.skillsCovered = skillsCovered; }
        
        public PricingInfo getPricing() { return pricing; }
        public void setPricing(PricingInfo pricing) { this.pricing = pricing; }
    }
    
    // Shared inner classes
    public static class SubjectInfo {
        private Long id;
        private String name;
        private Integer topicCount;
        private Integer moduleCount;
        private Integer chapterCount;
        private Integer questionCount;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Integer getTopicCount() { return topicCount; }
        public void setTopicCount(Integer topicCount) { this.topicCount = topicCount; }
        
        public Integer getModuleCount() { return moduleCount; }
        public void setModuleCount(Integer moduleCount) { this.moduleCount = moduleCount; }
        
        public Integer getChapterCount() { return chapterCount; }
        public void setChapterCount(Integer chapterCount) { this.chapterCount = chapterCount; }
        
        public Integer getQuestionCount() { return questionCount; }
        public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
    }
    
    public static class PricingInfo {
        private BigDecimal monthlyPrice;
        private BigDecimal quarterlyPrice;
        private BigDecimal yearlyPrice;
        private Integer monthlyDiscountPercent;
        private Integer quarterlyDiscountPercent;
        private Integer yearlyDiscountPercent;
        private BigDecimal monthlyFinalPrice;
        private BigDecimal quarterlyFinalPrice;
        private BigDecimal yearlyFinalPrice;
        private Map<String, String> discounts;
        private OfferValidity offerValidity;
        
        // Getters and Setters
        public BigDecimal getMonthlyPrice() { return monthlyPrice; }
        public void setMonthlyPrice(BigDecimal monthlyPrice) { this.monthlyPrice = monthlyPrice; }
        
        public BigDecimal getQuarterlyPrice() { return quarterlyPrice; }
        public void setQuarterlyPrice(BigDecimal quarterlyPrice) { this.quarterlyPrice = quarterlyPrice; }
        
        public BigDecimal getYearlyPrice() { return yearlyPrice; }
        public void setYearlyPrice(BigDecimal yearlyPrice) { this.yearlyPrice = yearlyPrice; }
        
        public Integer getMonthlyDiscountPercent() { return monthlyDiscountPercent; }
        public void setMonthlyDiscountPercent(Integer monthlyDiscountPercent) { this.monthlyDiscountPercent = monthlyDiscountPercent; }
        
        public Integer getQuarterlyDiscountPercent() { return quarterlyDiscountPercent; }
        public void setQuarterlyDiscountPercent(Integer quarterlyDiscountPercent) { this.quarterlyDiscountPercent = quarterlyDiscountPercent; }
        
        public Integer getYearlyDiscountPercent() { return yearlyDiscountPercent; }
        public void setYearlyDiscountPercent(Integer yearlyDiscountPercent) { this.yearlyDiscountPercent = yearlyDiscountPercent; }
        
        public BigDecimal getMonthlyFinalPrice() { return monthlyFinalPrice; }
        public void setMonthlyFinalPrice(BigDecimal monthlyFinalPrice) { this.monthlyFinalPrice = monthlyFinalPrice; }
        
        public BigDecimal getQuarterlyFinalPrice() { return quarterlyFinalPrice; }
        public void setQuarterlyFinalPrice(BigDecimal quarterlyFinalPrice) { this.quarterlyFinalPrice = quarterlyFinalPrice; }
        
        public BigDecimal getYearlyFinalPrice() { return yearlyFinalPrice; }
        public void setYearlyFinalPrice(BigDecimal yearlyFinalPrice) { this.yearlyFinalPrice = yearlyFinalPrice; }
        
        public Map<String, String> getDiscounts() { return discounts; }
        public void setDiscounts(Map<String, String> discounts) { this.discounts = discounts; }
        
        public OfferValidity getOfferValidity() { return offerValidity; }
        public void setOfferValidity(OfferValidity offerValidity) { this.offerValidity = offerValidity; }
    }
    
    public static class OfferValidity {
        private String validFrom;
        private String validTo;
        private Boolean isActive;
        
        public String getValidFrom() { return validFrom; }
        public void setValidFrom(String validFrom) { this.validFrom = validFrom; }
        
        public String getValidTo() { return validTo; }
        public void setValidTo(String validTo) { this.validTo = validTo; }
        
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }
}

