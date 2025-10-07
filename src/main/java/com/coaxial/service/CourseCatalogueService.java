package com.coaxial.service;

import com.coaxial.dto.CourseCatalogueResponse;
import com.coaxial.dto.CourseCatalogueResponse.*;
import com.coaxial.entity.*;
import com.coaxial.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for public course catalogue
 * Returns different structures based on course type
 */
@Service
@Transactional(readOnly = true)
public class CourseCatalogueService {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseCatalogueService.class);
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private ClassRepository classRepository;
    
    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private PricingConfigurationRepository pricingConfigRepository;
    
    @Autowired
    private CourseTypeRepository courseTypeRepository;
    
    @Autowired
    private ClassSubjectRepository classSubjectRepository;
    
    @Autowired
    private ExamSubjectRepository examSubjectRepository;
    
    @Autowired
    private CourseSubjectRepository courseSubjectRepository;
    
    @Autowired
    private TopicRepository topicRepository;
    
    @Autowired
    private ModuleRepository moduleRepository;
    
    @Autowired
    private ChapterRepository chapterRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    /**
     * Get course catalogue by course type
     */
    public CourseCatalogueResponse getCourseCatalogue(Long courseTypeId) {
        logger.info("Getting course catalogue for courseTypeId: {}", courseTypeId);
        
        CourseType courseType = courseTypeRepository.findById(courseTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Course type not found"));
        
        CourseCatalogueResponse response = new CourseCatalogueResponse();
        response.setCourseTypeId(courseTypeId);
        response.setCourseType(courseType.getName());
        response.setDescription(courseType.getDescription());
        
        
        if (courseTypeId == 1) {
            response.setClasses(buildAcademicClassCatalogue(courseTypeId));
        } else if (courseTypeId == 2) {
            response.setExams(buildCompetitiveExamCatalogue(courseTypeId));
        } else if (courseTypeId == 3) {
            response.setCourses(buildProfessionalCourseCatalogue(courseTypeId));
        }
        
        return response;
    }
    
    /**
     * Get combined catalogue for all course types
     */
    public CourseCatalogueResponse getAllCourseCatalogue() {
        logger.info("Getting combined course catalogue for all course types");
        
        CourseCatalogueResponse response = new CourseCatalogueResponse();
        response.setCourseType("All Course Types");
        response.setDescription("Combined catalogue with Academic classes, Competitive exams, and Professional courses");
        
        // Build all three catalogues
        List<ClassCatalogueItem> academicClasses = buildAcademicClassCatalogue(1L); // Academic
        List<ExamCatalogueItem> competitiveExams = buildCompetitiveExamCatalogue(2L); // Competitive
        List<CourseCatalogueItem> professionalCourses = buildProfessionalCourseCatalogue(3L); // Professional
        
        response.setClasses(academicClasses);
        response.setExams(competitiveExams);
        response.setCourses(professionalCourses);
        
        logger.info("Combined catalogue: {} classes, {} exams, {} courses", 
                academicClasses.size(), competitiveExams.size(), professionalCourses.size());
        
        return response;
    }
    
    /**
     * Build Academic course catalogue (Class-based)
     */
    private List<ClassCatalogueItem> buildAcademicClassCatalogue(Long courseTypeId) {
        logger.info("Building Academic class catalogue for courseTypeId: {}", courseTypeId);
        
        // Get course type entity
        CourseType courseType = courseTypeRepository.findById(courseTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Course type not found"));
        
        logger.info("CourseType found: id={}, name={}", courseType.getId(), courseType.getName());
        
        // Find all courses for this course type
        List<Course> courses = courseRepository.findByCourseType(courseType);
        logger.info("Found {} courses for courseType: {}", courses.size(), courseType.getName());
        
        // Get all classes from these courses
        List<ClassEntity> allClasses = new ArrayList<>();
        for (Course course : courses) {
            List<ClassEntity> courseClasses = classRepository.findByCourseId(course.getId());
            logger.info("Course '{}' (ID:{}) has {} classes", course.getName(), course.getId(), courseClasses.size());
            allClasses.addAll(courseClasses);
        }
        
        logger.info("Total classes found: {}", allClasses.size());
        
        // Filter active classes
        List<ClassEntity> activeClasses = allClasses.stream()
                .filter(c -> c.getIsActive())
                .collect(Collectors.toList());
        
        logger.info("Active classes: {}", activeClasses.size());
        
        if (activeClasses.isEmpty()) {
            logger.warn("No active classes found for courseTypeId: {}. Total classes: {}, Active: {}", 
                    courseTypeId, allClasses.size(), activeClasses.size());
        }
        
        return activeClasses.stream()
                .map(this::convertToClassCatalogueItem)
                .collect(Collectors.toList());
    }
    
    /**
     * Build Competitive course catalogue (Exam-based)
     */
    private List<ExamCatalogueItem> buildCompetitiveExamCatalogue(Long courseTypeId) {
        logger.info("Building Competitive exam catalogue for courseTypeId: {}", courseTypeId);
        
        // Get course type entity
        CourseType courseType = courseTypeRepository.findById(courseTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Course type not found"));
        
        logger.info("CourseType found: id={}, name={}", courseType.getId(), courseType.getName());
        
        // Find all courses for this course type
        List<Course> courses = courseRepository.findByCourseType(courseType);
        logger.info("Found {} courses for courseType: {}", courses.size(), courseType.getName());
        
        // Get all exams from these courses
        List<Exam> allExams = new ArrayList<>();
        for (Course course : courses) {
            List<Exam> courseExams = examRepository.findByCourseId(course.getId());
            logger.info("Course '{}' (ID:{}) has {} exams", course.getName(), course.getId(), courseExams.size());
            allExams.addAll(courseExams);
        }
        
        logger.info("Total exams found: {}", allExams.size());
        
        // Filter active exams
        List<Exam> activeExams = allExams.stream()
                .filter(e -> e.getIsActive())
                .collect(Collectors.toList());
        
        logger.info("Active exams: {}", activeExams.size());
        
        if (activeExams.isEmpty()) {
            logger.warn("No active exams found for courseTypeId: {}. Total exams: {}, Active: {}", 
                    courseTypeId, allExams.size(), activeExams.size());
        }
        
        return activeExams.stream()
                .map(this::convertToExamCatalogueItem)
                .collect(Collectors.toList());
    }
    
    /**
     * Build Professional course catalogue (Course-based)
     */
    private List<CourseCatalogueItem> buildProfessionalCourseCatalogue(Long courseTypeId) {
        logger.info("Building Professional course catalogue for courseTypeId: {}", courseTypeId);
        
        // Get course type entity
        CourseType courseType = courseTypeRepository.findById(courseTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Course type not found"));
        
        // Find all courses for this course type
        List<Course> courses = courseRepository.findByCourseTypeAndIsActive(courseType, true);
        logger.info("Found {} courses for courseType: {}", courses.size(), courseType.getName());
        
        if (courses.isEmpty()) {
            logger.warn("No courses found for courseTypeId: {}. Check if courses exist with this course type.", courseTypeId);
        }
        
        return courses.stream()
                .map(this::convertToCourseCatalogueItem)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert ClassEntity to ClassCatalogueItem
     */
    private ClassCatalogueItem convertToClassCatalogueItem(ClassEntity classEntity) {
        ClassCatalogueItem item = new ClassCatalogueItem();
        
        item.setId(classEntity.getId());
        item.setCourseId(classEntity.getCourse() != null ? classEntity.getCourse().getId() : null);
        item.setCourseName(classEntity.getCourse() != null ? classEntity.getCourse().getName() : "");
        item.setClassName(classEntity.getName());
        item.setDescription(classEntity.getDescription());
        item.setLevel("Beginner"); // TODO: Extract from description or add to entity
        item.setDuration("1 academic year"); // TODO: Make dynamic
        
        // Get pricing from pricing_configurations table
        item.setPricing(getEntityPricing("CLASS", classEntity.getId()));
        
        // Get actual counts from database using relationshipId
        Long courseTypeId = classEntity.getCourse() != null && 
                           classEntity.getCourse().getCourseType() != null ? 
                           classEntity.getCourse().getCourseType().getId() : 1L;
        
        List<ClassSubject> classSubjects = classSubjectRepository.findByClassEntityIdAndIsActive(classEntity.getId(), true);
        item.setSubjectCount(classSubjects != null ? classSubjects.size() : 0);
        
        // Count topics, modules, chapters, questions across all classSubjects
        int totalTopics = 0;
        int totalModules = 0;
        int totalChapters = 0;
        int totalQuestions = 0;
        
        List<SubjectInfo> subjectInfoList = new ArrayList<>();
        
        if (classSubjects == null || classSubjects.isEmpty()) {
            // Set defaults when no subjects
            item.setTopicCount(0);
            item.setModuleCount(0);
            item.setChapterCount(0);
            item.setQuestionCount(0);
            item.setSubjects(new ArrayList<>());
            return item;
        }
        
        for (ClassSubject classSubject : classSubjects) {
            // Find topics using relationshipId = classSubject.id AND courseTypeId
            List<Topic> topics = topicRepository.findByCourseTypeIdAndRelationshipIdAndIsActiveOrderByDisplayOrderAsc(
                    courseTypeId, classSubject.getId(), true);
            
            int subjectTopics = topics.size();
            int subjectModules = 0;
            int subjectChapters = 0;
            int subjectQuestions = 0;
            
            for (Topic topic : topics) {
                List<com.coaxial.entity.Module> modules = moduleRepository.findByTopicIdAndIsActiveOrderByDisplayOrderAsc(topic.getId(), true);
                subjectModules += modules.size();
                
                for (com.coaxial.entity.Module module : modules) {
                    List<Chapter> chapters = chapterRepository.findByModuleIdAndIsActiveOrderByDisplayOrderAsc(module.getId(), true);
                    subjectChapters += chapters.size();
                    
                    for (Chapter chapter : chapters) {
                        long questionCount = questionRepository.findByChapterIdAndIsActiveOrderByDisplayOrderAsc(chapter.getId(), true).size();
                        subjectQuestions += questionCount;
                    }
                }
            }
            
            totalTopics += subjectTopics;
            totalModules += subjectModules;
            totalChapters += subjectChapters;
            totalQuestions += subjectQuestions;
            
            // Build subject info
            if (classSubject.getSubject() != null) {
                SubjectInfo subjectInfo = new SubjectInfo();
                subjectInfo.setId(classSubject.getSubject().getId());
                subjectInfo.setName(classSubject.getSubject().getName());
                subjectInfo.setTopicCount(subjectTopics);
                subjectInfo.setModuleCount(subjectModules);
                subjectInfo.setChapterCount(subjectChapters);
                subjectInfo.setQuestionCount(subjectQuestions);
                subjectInfoList.add(subjectInfo);
            }
        }
        
        item.setTopicCount(totalTopics != 0 ? totalTopics : 0);
        item.setModuleCount(totalModules != 0 ? totalModules : 0);
        item.setChapterCount(totalChapters != 0 ? totalChapters : 0);
        item.setQuestionCount(totalQuestions != 0 ? totalQuestions : 0);
        item.setSubjects(subjectInfoList != null && !subjectInfoList.isEmpty() ? subjectInfoList : new ArrayList<>());
        
        logger.debug("Class {}: {} subjects, {} topics, {} modules, {} chapters, {} questions",
                classEntity.getName(), classSubjects.size(), totalTopics, totalModules, totalChapters, totalQuestions);
        
        return item;
    }
    
    /**
     * Convert Exam to ExamCatalogueItem
     */
    private ExamCatalogueItem convertToExamCatalogueItem(Exam exam) {
        ExamCatalogueItem item = new ExamCatalogueItem();
        
        item.setId(exam.getId());
        item.setCourseId(exam.getCourse() != null ? exam.getCourse().getId() : null);
        item.setCourseName(exam.getCourse() != null ? exam.getCourse().getName() : "");
        item.setExamName(exam.getName());
        item.setDescription(exam.getDescription());
        item.setDifficulty("Medium"); // TODO: Add to entity or calculate
        
        // Get pricing from pricing_configurations table
        item.setPricing(getEntityPricing("EXAM", exam.getId()));
        
        // Get actual counts from database using relationshipId
        Long courseTypeId = exam.getCourse() != null && 
                           exam.getCourse().getCourseType() != null ? 
                           exam.getCourse().getCourseType().getId() : 2L;
        
        List<ExamSubject> examSubjects = examSubjectRepository.findByExamId(exam.getId());
        item.setSubjectCount(examSubjects != null ? examSubjects.size() : 0);
        
        // Count topics, modules, chapters, questions across all examSubjects
        int totalTopics = 0;
        int totalModules = 0;
        int totalChapters = 0;
        int totalQuestions = 0;
        
        List<SubjectInfo> subjectInfoList = new ArrayList<>();
        List<String> topicNames = new ArrayList<>();
        
        if (examSubjects == null || examSubjects.isEmpty()) {
            // Set defaults when no subjects
            item.setTopicCount(0);
            item.setQuestionCount(0);
            item.setTopicsList(new ArrayList<>());
            item.setSubjects(new ArrayList<>());
            return item;
        }
        
        for (ExamSubject examSubject : examSubjects) {
            // Find topics using relationshipId = examSubject.id AND courseTypeId
            List<Topic> topics = topicRepository.findByCourseTypeIdAndRelationshipIdAndIsActiveOrderByDisplayOrderAsc(
                    courseTypeId, examSubject.getId(), true);
            
            int subjectTopics = topics.size();
            int subjectModules = 0;
            int subjectChapters = 0;
            int subjectQuestions = 0;
            
            for (Topic topic : topics) {
                topicNames.add(topic.getName());
                List<com.coaxial.entity.Module> modules = moduleRepository.findByTopicIdAndIsActiveOrderByDisplayOrderAsc(topic.getId(), true);
                subjectModules += modules.size();
                
                for (com.coaxial.entity.Module module : modules) {
                    List<Chapter> chapters = chapterRepository.findByModuleIdAndIsActiveOrderByDisplayOrderAsc(module.getId(), true);
                    subjectChapters += chapters.size();
                    
                    for (Chapter chapter : chapters) {
                        long questionCount = questionRepository.findByChapterIdAndIsActiveOrderByDisplayOrderAsc(chapter.getId(), true).size();
                        subjectQuestions += questionCount;
                    }
                }
            }
            
            totalTopics += subjectTopics;
            totalModules += subjectModules;
            totalChapters += subjectChapters;
            totalQuestions += subjectQuestions;
            
            // Build subject info
            if (examSubject.getSubject() != null) {
                SubjectInfo subjectInfo = new SubjectInfo();
                subjectInfo.setId(examSubject.getSubject().getId());
                subjectInfo.setName(examSubject.getSubject().getName());
                subjectInfo.setTopicCount(subjectTopics);
                subjectInfo.setModuleCount(subjectModules);
                subjectInfo.setChapterCount(subjectChapters);
                subjectInfo.setQuestionCount(subjectQuestions);
                subjectInfoList.add(subjectInfo);
            }
        }
        
        item.setTopicCount(totalTopics != 0 ? totalTopics : 0);
        item.setQuestionCount(totalQuestions != 0 ? totalQuestions : 0);
        item.setTopicsList(topicNames != null && !topicNames.isEmpty() ? topicNames : new ArrayList<>());
        item.setSubjects(subjectInfoList != null && !subjectInfoList.isEmpty() ? subjectInfoList : new ArrayList<>());
        
        logger.debug("Exam {}: {} subjects, {} topics, {} questions",
                exam.getName(), examSubjects.size(), totalTopics, totalQuestions);
        
        return item;
    }
    
    /**
     * Convert Course to CourseCatalogueItem
     */
    private CourseCatalogueItem convertToCourseCatalogueItem(Course course) {
        CourseCatalogueItem item = new CourseCatalogueItem();
        
        item.setId(course.getId());
        item.setCourseName(course.getName());
        item.setDescription(course.getDescription());
        item.setLevel("Intermediate"); // TODO: Add to entity
        item.setDuration("6 months"); // TODO: Make dynamic
        
        // Get pricing from pricing_configurations table
        item.setPricing(getEntityPricing("COURSE", course.getId()));
        
        // Get actual counts from database using relationshipId
        Long courseTypeId = course.getCourseType() != null ? course.getCourseType().getId() : 3L;
        
        List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseId(course.getId());
        
        // Count topics, modules, chapters across all courseSubjects
        int totalTopics = 0;
        int totalModules = 0;
        int totalChapters = 0;
        List<String> skills = new ArrayList<>();
        
        if (courseSubjects == null || courseSubjects.isEmpty()) {
            // Set defaults when no subjects
            item.setTopicCount(0);
            item.setModuleCount(0);
            item.setChapterCount(0);
            item.setProjectCount(0);
            item.setSkillsCovered(new ArrayList<>());
            return item;
        }
        
        for (CourseSubject courseSubject : courseSubjects) {
            // Find topics using relationshipId = courseSubject.id AND courseTypeId
            List<Topic> topics = topicRepository.findByCourseTypeIdAndRelationshipIdAndIsActiveOrderByDisplayOrderAsc(
                    courseTypeId, courseSubject.getId(), true);
            
            totalTopics += topics.size();
            
            for (Topic topic : topics) {
                skills.add(topic.getName());
                List<com.coaxial.entity.Module> modules = moduleRepository.findByTopicIdAndIsActiveOrderByDisplayOrderAsc(topic.getId(), true);
                totalModules += modules.size();
                
                for (com.coaxial.entity.Module module : modules) {
                    List<Chapter> chapters = chapterRepository.findByModuleIdAndIsActiveOrderByDisplayOrderAsc(module.getId(), true);
                    totalChapters += chapters.size();
                }
            }
        }
        
        item.setTopicCount(totalTopics != 0 ? totalTopics : 0);
        item.setModuleCount(totalModules != 0 ? totalModules : 0);
        item.setChapterCount(totalChapters != 0 ? totalChapters : 0);
        item.setProjectCount(0); // TODO: Add project count if applicable
        item.setSkillsCovered(skills != null && !skills.isEmpty() ? skills : new ArrayList<>());
        
        logger.debug("Course {}: {} topics, {} modules, {} chapters",
                course.getName(), totalTopics, totalModules, totalChapters);
        
        return item;
    }
    
    /**
     * Get pricing for entity (Class, Exam, or Course)
     */
    private PricingInfo getEntityPricing(String entityType, Long entityId) {
        PricingInfo pricing = new PricingInfo();
        
        Optional<PricingConfiguration> configOpt = pricingConfigRepository
                .findByEntityTypeAndEntityId(entityType, entityId);
        
        if (configOpt.isPresent() && configOpt.get().getIsActive()) {
            PricingConfiguration config = configOpt.get();
            
            pricing.setMonthlyPrice(config.getMonthlyPrice() != null ? config.getMonthlyPrice() : BigDecimal.ZERO);
            pricing.setQuarterlyPrice(config.getQuarterlyPrice() != null ? config.getQuarterlyPrice() : BigDecimal.ZERO);
            pricing.setYearlyPrice(config.getYearlyPrice() != null ? config.getYearlyPrice() : BigDecimal.ZERO);
            
            pricing.setMonthlyDiscountPercent(config.getMonthlyDiscountPercent() != null ? config.getMonthlyDiscountPercent() : 0);
            pricing.setQuarterlyDiscountPercent(config.getQuarterlyDiscountPercent() != null ? config.getQuarterlyDiscountPercent() : 0);
            pricing.setYearlyDiscountPercent(config.getYearlyDiscountPercent() != null ? config.getYearlyDiscountPercent() : 0);
            
            // Calculate final prices
            pricing.setMonthlyFinalPrice(calculateFinalPrice(pricing.getMonthlyPrice(), pricing.getMonthlyDiscountPercent()));
            pricing.setQuarterlyFinalPrice(calculateFinalPrice(pricing.getQuarterlyPrice(), pricing.getQuarterlyDiscountPercent()));
            pricing.setYearlyFinalPrice(calculateFinalPrice(pricing.getYearlyPrice(), pricing.getYearlyDiscountPercent()));
            
            // Discounts map
            Map<String, String> discounts = new HashMap<>();
            discounts.put("monthly", pricing.getMonthlyDiscountPercent() + "% OFF");
            discounts.put("quarterly", pricing.getQuarterlyDiscountPercent() + "% OFF");
            discounts.put("yearly", pricing.getYearlyDiscountPercent() + "% OFF");
            pricing.setDiscounts(discounts);
            
            // Offer validity
            pricing.setOfferValidity(buildOfferValidity(config));
            
            logger.debug("Pricing found for {} ID {}: Monthly={}, Quarterly={}, Yearly={}", 
                    entityType, entityId, pricing.getMonthlyPrice(), pricing.getQuarterlyPrice(), pricing.getYearlyPrice());
        } else {
            logger.warn("No active pricing found for {} ID {}. Using default pricing.", entityType, entityId);
            
            // Default pricing when no configuration exists
            pricing.setMonthlyPrice(BigDecimal.valueOf(999));
            pricing.setQuarterlyPrice(BigDecimal.valueOf(2697));
            pricing.setYearlyPrice(BigDecimal.valueOf(9592));
            pricing.setMonthlyDiscountPercent(0);
            pricing.setQuarterlyDiscountPercent(10);
            pricing.setYearlyDiscountPercent(20);
            pricing.setMonthlyFinalPrice(BigDecimal.valueOf(999));
            pricing.setQuarterlyFinalPrice(BigDecimal.valueOf(2697));
            pricing.setYearlyFinalPrice(BigDecimal.valueOf(9592));
            
            Map<String, String> discounts = new HashMap<>();
            discounts.put("monthly", "0% OFF");
            discounts.put("quarterly", "10% OFF");
            discounts.put("yearly", "20% OFF");
            pricing.setDiscounts(discounts);
        }
        
        return pricing;
    }
    
    /**
     * Build offer validity information
     */
    private OfferValidity buildOfferValidity(PricingConfiguration config) {
        OfferValidity validity = new OfferValidity();
        
        // Use monthly offer dates (since all tiers have same dates from bulk update)
        if (config.getMonthlyOfferValidFrom() != null && config.getMonthlyOfferValidTo() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            validity.setValidFrom(config.getMonthlyOfferValidFrom().format(formatter));
            validity.setValidTo(config.getMonthlyOfferValidTo().format(formatter));
            
            // Check if offer is currently active
            LocalDateTime now = LocalDateTime.now();
            validity.setIsActive(
                now.isAfter(config.getMonthlyOfferValidFrom()) && 
                now.isBefore(config.getMonthlyOfferValidTo())
            );
        } else {
            validity.setValidFrom(null);
            validity.setValidTo(null);
            validity.setIsActive(false);
        }
        
        return validity;
    }
    
    /**
     * Calculate final price after discount
     */
    private BigDecimal calculateFinalPrice(BigDecimal basePrice, Integer discountPercent) {
        if (basePrice == null || discountPercent == null || discountPercent == 0) {
            return basePrice;
        }
        BigDecimal discount = basePrice.multiply(BigDecimal.valueOf(discountPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return basePrice.subtract(discount);
    }
    
    /**
     * Debug method to check what data exists
     */
    public Map<String, Object> debugDataForCourseType(Long courseTypeId) {
        Map<String, Object> debug = new HashMap<>();
        
        // Get course type
        CourseType courseType = courseTypeRepository.findById(courseTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Course type not found"));
        
        debug.put("courseTypeId", courseTypeId);
        debug.put("courseTypeName", courseType.getName());
        debug.put("structureType", courseType.getStructureType());
        
        // Count courses
        List<Course> courses = courseRepository.findByCourseType(courseType);
        debug.put("totalCourses", courses.size());
        
        List<Map<String, Object>> courseDetails = new ArrayList<>();
        for (Course course : courses) {
            Map<String, Object> courseInfo = new HashMap<>();
            courseInfo.put("courseId", course.getId());
            courseInfo.put("courseName", course.getName());
            courseInfo.put("isActive", course.getIsActive());
            
            // Count classes/exams based on course type
            if ("Academic".equalsIgnoreCase(courseType.getName())) {
                List<ClassEntity> classes = classRepository.findByCourseId(course.getId());
                courseInfo.put("classCount", classes.size());
                
                List<Map<String, Object>> classDetails = new ArrayList<>();
                for (ClassEntity classEntity : classes) {
                    Map<String, Object> classInfo = new HashMap<>();
                    classInfo.put("classId", classEntity.getId());
                    classInfo.put("className", classEntity.getName());
                    classInfo.put("isActive", classEntity.getIsActive());
                    
                    // Count subjects
                    List<ClassSubject> classSubjects = classSubjectRepository.findByClassEntityId(classEntity.getId());
                    classInfo.put("subjectCount", classSubjects.size());
                    
                    // Check pricing
                    boolean hasPricing = pricingConfigRepository.existsByEntityTypeAndEntityId("CLASS", classEntity.getId());
                    classInfo.put("hasPricing", hasPricing);
                    
                    classDetails.add(classInfo);
                }
                courseInfo.put("classes", classDetails);
                
            } else if ("Competitive".equalsIgnoreCase(courseType.getName())) {
                List<Exam> exams = examRepository.findByCourseId(course.getId());
                courseInfo.put("examCount", exams.size());
                
                List<Map<String, Object>> examDetails = new ArrayList<>();
                for (Exam exam : exams) {
                    Map<String, Object> examInfo = new HashMap<>();
                    examInfo.put("examId", exam.getId());
                    examInfo.put("examName", exam.getName());
                    examInfo.put("isActive", exam.getIsActive());
                    
                    // Count subjects
                    List<ExamSubject> examSubjects = examSubjectRepository.findByExamId(exam.getId());
                    examInfo.put("subjectCount", examSubjects.size());
                    
                    // Check pricing
                    boolean hasPricing = pricingConfigRepository.existsByEntityTypeAndEntityId("EXAM", exam.getId());
                    examInfo.put("hasPricing", hasPricing);
                    
                    examDetails.add(examInfo);
                }
                courseInfo.put("exams", examDetails);
                
            } else {
                // Professional
                List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseId(course.getId());
                courseInfo.put("subjectCount", courseSubjects.size());
                
                // Check pricing
                boolean hasPricing = pricingConfigRepository.existsByEntityTypeAndEntityId("COURSE", course.getId());
                courseInfo.put("hasPricing", hasPricing);
            }
            
            courseDetails.add(courseInfo);
        }
        debug.put("courses", courseDetails);
        
        return debug;
    }
    
}

