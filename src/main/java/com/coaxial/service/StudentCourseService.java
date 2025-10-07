package com.coaxial.service;

import com.coaxial.dto.CourseResponse;
import com.coaxial.dto.StudentCourseResponseDTO;
import com.coaxial.entity.Course;
import com.coaxial.entity.ClassEntity;
import com.coaxial.entity.Exam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for student course operations
 * Provides student-friendly course information and enrollment data
 */
@Service
@Transactional(readOnly = true)
public class StudentCourseService {
    
    private static final Logger logger = LoggerFactory.getLogger(StudentCourseService.class);
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private PricingService pricingService;
    
    /**
     * Get all available courses for students with pagination
     */
    public Page<StudentCourseResponseDTO> getCoursesForStudents(
            Long courseTypeId, 
            Boolean isActive, 
            String search, 
            Long studentId,
            Pageable pageable) {
        
        logger.info("Fetching courses for student {} with filters - courseTypeId: {}, isActive: {}, search: {}", 
                   studentId, courseTypeId, isActive, search);
        
        // Get courses using existing service
        Page<CourseResponse> courses = courseService.getCourses(courseTypeId, isActive, search, pageable);
        
        // Convert to student-friendly DTOs
        List<StudentCourseResponseDTO> studentCourses = courses.getContent().stream()
                .map(courseResponse -> convertToStudentCourseDTO(courseResponse, studentId))
                .collect(Collectors.toList());
        
        logger.info("Found {} courses for student {}", studentCourses.size(), studentId);
        
        return new PageImpl<>(studentCourses, pageable, courses.getTotalElements());
    }
    
    /**
     * Get all available courses for students without pagination
     */
    public List<StudentCourseResponseDTO> getAllCoursesForStudents(
            Long courseTypeId, 
            Boolean isActive, 
            String search,
            Long studentId) {
        
        logger.info("Fetching all courses for student {} with filters", studentId);
        
        List<CourseResponse> courses = courseService.getAllCourses(courseTypeId, isActive, search);
        
        return courses.stream()
                .map(courseResponse -> convertToStudentCourseDTO(courseResponse, studentId))
                .collect(Collectors.toList());
    }
    
    /**
     * Get course by ID for students
     */
    public StudentCourseResponseDTO getCourseForStudent(Long courseId, Long studentId) {
        logger.info("Fetching course {} for student {}", courseId, studentId);
        
        CourseResponse courseResponse = courseService.getCourseById(courseId);
        
        if (!courseResponse.getIsActive()) {
            throw new IllegalArgumentException("Course is not available");
        }
        
        return convertToStudentCourseDTO(courseResponse, studentId);
    }
    
    /**
     * Get courses by course type for students
     */
    public Page<StudentCourseResponseDTO> getCoursesByCourseTypeForStudents(
            Long courseTypeId, 
            Boolean active, 
            String search, 
            Long studentId,
            Pageable pageable) {
        
        logger.info("Fetching courses by course type {} for student {}", courseTypeId, studentId);
        
        Page<CourseResponse> courses = courseService.getCoursesByCourseType(courseTypeId, active, search, pageable);
        
        List<StudentCourseResponseDTO> studentCourses = courses.getContent().stream()
                .map(courseResponse -> convertToStudentCourseDTO(courseResponse, studentId))
                .collect(Collectors.toList());
        
        return new PageImpl<>(studentCourses, pageable, courses.getTotalElements());
    }
    
    /**
     * Convert CourseResponse to StudentCourseResponseDTO
     */
    private StudentCourseResponseDTO convertToStudentCourseDTO(CourseResponse courseResponse, Long studentId) {
        StudentCourseResponseDTO dto = new StudentCourseResponseDTO();
        
        // Basic course information
        dto.setId(courseResponse.getId());
        dto.setName(courseResponse.getName());
        dto.setDescription(courseResponse.getDescription());
        dto.setCourseTypeId(courseResponse.getCourseTypeId());
        dto.setCourseTypeName(courseResponse.getCourseTypeName());
        dto.setStructureType(courseResponse.getStructureType());
        dto.setDisplayOrder(courseResponse.getDisplayOrder());
        dto.setCreatedAt(courseResponse.getCreatedAt());
        
        // Student-friendly enhancements
        dto.setStudentFriendlyName(getStudentFriendlyName(courseResponse));
        dto.setLearningPathDescription(getLearningPathDescription(courseResponse.getStructureType()));
        dto.setFeatures(getCourseFeatures(courseResponse.getStructureType()));
        
        // Enrollment and subscription information
        dto.setIsSubscribed(checkStudentSubscription(studentId, courseResponse.getId()));
        dto.setEnrollmentStatus(getEnrollmentStatus(studentId, courseResponse.getId()));
        dto.setSubscriptionLevel(getRequiredSubscriptionLevel(courseResponse.getStructureType()));
        
        // Hierarchical data for students
        dto.setClasses(convertToClassSummaries(courseResponse.getClasses(), studentId));
        dto.setExams(convertToExamSummaries(courseResponse.getExams(), studentId));
        dto.setSubjects(convertToSubjectSummaries(courseResponse.getSubjects(), studentId));
        
        // Course statistics
        dto.setStats(calculateCourseStats(courseResponse, studentId));
        
        // Pricing and subscription information
        dto.setCoursePricing(getCoursePricingInfo(courseResponse));
        dto.setSubscriptionOptions(getSubscriptionOptions(courseResponse));
        
        return dto;
    }
    
    /**
     * Get student-friendly course name
     */
    private String getStudentFriendlyName(CourseResponse course) {
        String typeName = course.getCourseTypeName();
        if (typeName != null) {
            switch (typeName.toUpperCase()) {
                case "ACADEMIC":
                    return course.getName() + " - Academic Program";
                case "COMPETITIVE":
                    return course.getName() + " - Competitive Exam";
                case "PROFESSIONAL":
                    return course.getName() + " - Professional Course";
                default:
                    return course.getName();
            }
        }
        return course.getName();
    }
    
    /**
     * Get learning path description
     */
    private String getLearningPathDescription(String structureType) {
        if (structureType != null) {
            switch (structureType.toUpperCase()) {
                case "ACADEMIC":
                    return "Structured academic learning with classes, subjects, topics, modules, and chapters";
                case "COMPETITIVE":
                    return "Exam-focused preparation with practice tests and competitive strategies";
                case "PROFESSIONAL":
                    return "Professional skill development with industry-relevant courses";
                default:
                    return "Comprehensive learning path";
            }
        }
        return "Comprehensive learning path";
    }
    
    /**
     * Get course features
     */
    private List<String> getCourseFeatures(String structureType) {
        if (structureType != null) {
            switch (structureType.toUpperCase()) {
                case "ACADEMIC":
                    return List.of("Class-based learning", "Subject-wise organization", 
                                  "Topic and module breakdown", "Chapter-wise content");
                case "COMPETITIVE":
                    return List.of("Exam-focused content", "Practice tests", 
                                  "Previous year papers", "Mock examinations");
                case "PROFESSIONAL":
                    return List.of("Industry-relevant courses", "Skill-based learning", 
                                  "Practical applications", "Professional development");
                default:
                    return List.of("Comprehensive learning", "Structured content");
            }
        }
        return List.of("Comprehensive learning", "Structured content");
    }
    
    /**
     * Check if student has subscription for this course
     */
    private Boolean checkStudentSubscription(Long studentId, Long courseId) {
        // TODO: Implement actual subscription check
        // This would typically query the subscription service
        return false; // Placeholder
    }
    
    /**
     * Get enrollment status
     */
    private String getEnrollmentStatus(Long studentId, Long courseId) {
        // TODO: Implement actual enrollment status check
        // This would typically query the enrollment service
        return "NOT_ENROLLED"; // Placeholder
    }
    
    /**
     * Get required subscription level
     */
    private String getRequiredSubscriptionLevel(String structureType) {
        if (structureType != null) {
            switch (structureType.toUpperCase()) {
                case "ACADEMIC":
                    return "ACADEMIC";
                case "COMPETITIVE":
                    return "COMPETITIVE";
                case "PROFESSIONAL":
                    return "PROFESSIONAL";
                default:
                    return "BASIC";
            }
        }
        return "BASIC";
    }
    
    /**
     * Convert class information to student-friendly summaries
     */
    private List<StudentCourseResponseDTO.ClassSummary> convertToClassSummaries(
            List<CourseResponse.ClassInfo> classes, Long studentId) {
        if (classes == null) return List.of();
        
        return classes.stream()
                .map(cls -> {
                    StudentCourseResponseDTO.ClassSummary summary = new StudentCourseResponseDTO.ClassSummary();
                    summary.setId(cls.getId());
                    summary.setName(cls.getName());
                    summary.setDescription(cls.getDescription());
                    summary.setStudentCount(0); // TODO: Get actual student count
                    summary.setIsEnrolled(false); // TODO: Check actual enrollment
                    return summary;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Convert exam information to student-friendly summaries
     */
    private List<StudentCourseResponseDTO.ExamSummary> convertToExamSummaries(
            List<CourseResponse.ExamInfo> exams, Long studentId) {
        if (exams == null) return List.of();
        
        return exams.stream()
                .map(exam -> {
                    StudentCourseResponseDTO.ExamSummary summary = new StudentCourseResponseDTO.ExamSummary();
                    summary.setId(exam.getId());
                    summary.setName(exam.getName());
                    summary.setDescription(exam.getDescription());
                    summary.setQuestionCount(0); // TODO: Get actual question count
                    summary.setTimeLimit(0); // TODO: Get actual time limit
                    summary.setIsAttempted(false); // TODO: Check actual attempt status
                    return summary;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Convert subject information to student-friendly summaries
     */
    private List<StudentCourseResponseDTO.SubjectSummary> convertToSubjectSummaries(
            List<CourseResponse.SubjectInfo> subjects, Long studentId) {
        if (subjects == null) return List.of();
        
        return subjects.stream()
                .map(subject -> {
                    StudentCourseResponseDTO.SubjectSummary summary = new StudentCourseResponseDTO.SubjectSummary();
                    summary.setId(subject.getId());
                    summary.setName(subject.getName());
                    summary.setDescription(subject.getDescription());
                    summary.setTopicCount(0); // TODO: Get actual topic count
                    summary.setModuleCount(0); // TODO: Get actual module count
                    summary.setIsAccessible(false); // TODO: Check actual access
                    return summary;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Calculate course statistics for student
     */
    private StudentCourseResponseDTO.CourseStats calculateCourseStats(CourseResponse course, Long studentId) {
        StudentCourseResponseDTO.CourseStats stats = new StudentCourseResponseDTO.CourseStats();
        
        stats.setTotalClasses(course.getClasses() != null ? course.getClasses().size() : 0);
        stats.setTotalExams(course.getExams() != null ? course.getExams().size() : 0);
        stats.setTotalSubjects(course.getSubjects() != null ? course.getSubjects().size() : 0);
        stats.setTotalTopics(0); // TODO: Calculate actual topic count
        stats.setTotalModules(0); // TODO: Calculate actual module count
        stats.setCompletedClasses(0); // TODO: Get actual completed count
        stats.setCompletedExams(0); // TODO: Get actual completed count
        stats.setProgressPercentage(0.0); // TODO: Calculate actual progress
        
        return stats;
    }
    
    /**
     * Get pricing information for the course
     */
    private Map<String, Object> getCoursePricingInfo(CourseResponse course) {
        try {
            Course courseEntity = getCourseEntityById(course.getId());
            if (courseEntity != null) {
                return pricingService.getCoursePricing(courseEntity);
            }
        } catch (Exception e) {
            logger.error("Error getting course pricing for course {}", course.getId(), e);
        }
        return getDefaultPricing();
    }
    
    /**
     * Get all subscription options (Course level and Class/Exam level)
     */
    private List<StudentCourseResponseDTO.SubscriptionOption> getSubscriptionOptions(CourseResponse course) {
        List<StudentCourseResponseDTO.SubscriptionOption> options = new ArrayList<>();
        
        try {
            Course courseEntity = getCourseEntityById(course.getId());
            if (courseEntity == null) {
                return options;
            }
            
            String courseType = course.getCourseTypeName();
            
            // Course level option (always available and recommended)
            StudentCourseResponseDTO.SubscriptionOption courseOption = new StudentCourseResponseDTO.SubscriptionOption();
            Map<String, Object> coursePricing = pricingService.getCoursePricing(courseEntity);
            
            courseOption.setLevel("COURSE");
            courseOption.setLevelName("Complete Course");
            courseOption.setEntityId(course.getId());
            courseOption.setEntityName(course.getName());
            courseOption.setPricing((Map<String, Integer>) coursePricing.get("amounts"));
            courseOption.setDiscounts((Map<String, String>) coursePricing.get("discounts"));
            courseOption.setDescription("Access to entire course with all subjects, topics, modules, and chapters");
            courseOption.setIsRecommended(true); // Course level is recommended
            options.add(courseOption);
            
            // Class level options (for Academic courses)
            if ("Academic".equalsIgnoreCase(courseType) && course.getClasses() != null) {
                for (CourseResponse.ClassInfo classInfo : course.getClasses()) {
                    StudentCourseResponseDTO.SubscriptionOption classOption = new StudentCourseResponseDTO.SubscriptionOption();
                    
                    // Create a simple ClassEntity for pricing calculation
                    ClassEntity classEntity = createClassEntityFromInfo(classInfo);
                    if (classEntity != null) {
                        Map<String, Object> classPricing = pricingService.getClassPricing(classEntity);
                        
                        classOption.setLevel("CLASS");
                        classOption.setLevelName("Class " + classInfo.getName());
                        classOption.setEntityId(classInfo.getId());
                        classOption.setEntityName(classInfo.getName());
                        classOption.setPricing((Map<String, Integer>) classPricing.get("amounts"));
                        classOption.setDiscounts((Map<String, String>) classPricing.get("discounts"));
                        classOption.setDescription("Access to specific class with all subjects and content");
                        classOption.setIsRecommended(false);
                        options.add(classOption);
                    }
                }
            }
            
            // Exam level options (for Competitive courses)
            if ("Competitive".equalsIgnoreCase(courseType) && course.getExams() != null) {
                for (CourseResponse.ExamInfo examInfo : course.getExams()) {
                    StudentCourseResponseDTO.SubscriptionOption examOption = new StudentCourseResponseDTO.SubscriptionOption();
                    
                    // Create a simple Exam entity for pricing calculation
                    Exam examEntity = createExamEntityFromInfo(examInfo);
                    if (examEntity != null) {
                        Map<String, Object> examPricing = pricingService.getExamPricing(examEntity);
                        
                        examOption.setLevel("EXAM");
                        examOption.setLevelName("Exam " + examInfo.getName());
                        examOption.setEntityId(examInfo.getId());
                        examOption.setEntityName(examInfo.getName());
                        examOption.setPricing((Map<String, Integer>) examPricing.get("amounts"));
                        examOption.setDiscounts((Map<String, String>) examPricing.get("discounts"));
                        examOption.setDescription("Access to specific exam preparation with all subjects and practice tests");
                        examOption.setIsRecommended(false);
                        options.add(examOption);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error getting subscription options for course {}", course.getId(), e);
        }
        
        return options;
    }
    
    /**
     * Get default pricing when pricing service fails
     */
    private Map<String, Object> getDefaultPricing() {
        Map<String, Object> pricing = new HashMap<>();
        Map<String, Integer> amounts = new HashMap<>();
        Map<String, String> discounts = new HashMap<>();
        
        amounts.put("monthly", 999);
        amounts.put("quarterly", 2697);
        amounts.put("yearly", 9592);
        discounts.put("quarterly", "10% OFF");
        discounts.put("yearly", "20% OFF");
        
        pricing.put("amounts", amounts);
        pricing.put("discounts", discounts);
        pricing.put("subscriptionLevel", "COURSE");
        
        return pricing;
    }
    
    /**
     * Get course entity by ID (helper method)
     */
    private Course getCourseEntityById(Long courseId) {
        try {
            // This would typically use CourseService or CourseRepository
            // For now, we'll create a simple Course object
            Course course = new Course();
            course.setId(courseId);
            
            // You would typically fetch this from database
            // For now, we'll use default values
            return course;
        } catch (Exception e) {
            logger.error("Error getting course entity for ID {}", courseId, e);
            return null;
        }
    }
    
    /**
     * Create ClassEntity from CourseResponse.ClassInfo (helper method)
     */
    private ClassEntity createClassEntityFromInfo(CourseResponse.ClassInfo classInfo) {
        try {
            ClassEntity classEntity = new ClassEntity();
            classEntity.setId(classInfo.getId());
            classEntity.setName(classInfo.getName());
            classEntity.setDescription(classInfo.getDescription());
            return classEntity;
        } catch (Exception e) {
            logger.error("Error creating ClassEntity from ClassInfo", e);
            return null;
        }
    }
    
    /**
     * Create Exam entity from CourseResponse.ExamInfo (helper method)
     */
    private Exam createExamEntityFromInfo(CourseResponse.ExamInfo examInfo) {
        try {
            Exam exam = new Exam();
            exam.setId(examInfo.getId());
            exam.setName(examInfo.getName());
            exam.setDescription(examInfo.getDescription());
            return exam;
        } catch (Exception e) {
            logger.error("Error creating Exam entity from ExamInfo", e);
            return null;
        }
    }
}
