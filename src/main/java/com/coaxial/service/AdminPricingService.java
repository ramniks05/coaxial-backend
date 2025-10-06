package com.coaxial.service;

import com.coaxial.dto.*;
import com.coaxial.entity.*;
import com.coaxial.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for admin pricing management
 * Handles dropdown data retrieval and pricing configuration operations
 */
@Service
@Transactional
public class AdminPricingService {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminPricingService.class);
    
    @Autowired
    private PricingConfigurationRepository pricingConfigRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private ClassRepository classRepository;
    
    @Autowired
    private ExamRepository examRepository;
    
    // ==================== DROPDOWN DATA METHODS ====================
    
    /**
     * Get course dropdown data for admin pricing management
     */
    public List<CourseDropdownDTO> getCourseDropdownData() {
        logger.info("Retrieving course dropdown data for admin pricing management");
        
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(this::convertToCourseDropdownDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get class dropdown data (optionally filtered by course)
     */
    public List<ClassDropdownDTO> getClassDropdownData(Long courseId) {
        logger.info("Retrieving class dropdown data for courseId: {}", courseId);
        
        List<ClassEntity> classes;
        if (courseId != null) {
            classes = classRepository.findByCourseId(courseId);
        } else {
            classes = classRepository.findAll();
        }
        
        return classes.stream()
                .map(this::convertToClassDropdownDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get exam dropdown data for admin pricing management
     */
    public List<ExamDropdownDTO> getExamDropdownData() {
        logger.info("Retrieving exam dropdown data for admin pricing management");
        
        List<Exam> exams = examRepository.findAll();
        return exams.stream()
                .map(this::convertToExamDropdownDTO)
                .collect(Collectors.toList());
    }
    
    // ==================== PRICING CONFIGURATION METHODS ====================
    
    /**
     * Set pricing for selected course
     */
    public PricingConfiguration setCoursePricing(CoursePricingRequest request) {
        logger.info("Setting pricing for course ID: {}, Name: {}", request.getCourseId(), request.getCourseName());
        
        // Validate course exists
        courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + request.getCourseId()));
        
        // Create or update pricing configuration
        PricingConfiguration config = pricingConfigRepository
                .findByEntityTypeAndEntityId("COURSE", request.getCourseId())
                .orElse(new PricingConfiguration());
        
        config.setEntityType("COURSE");
        config.setEntityId(request.getCourseId());
        config.setEntityName(request.getCourseName());
        config.setMonthlyPrice(request.getMonthlyPrice());
        config.setQuarterlyPrice(request.getQuarterlyPrice());
        config.setYearlyPrice(request.getYearlyPrice());
        config.setMonthlyDurationDays(request.getMonthlyDurationDays());
        config.setQuarterlyDurationDays(request.getQuarterlyDurationDays());
        config.setYearlyDurationDays(request.getYearlyDurationDays());
        config.setQuarterlyDiscountPercent(request.getQuarterlyDiscountPercent());
        config.setYearlyDiscountPercent(request.getYearlyDiscountPercent());
        config.setIsActive(request.getIsActive());
        
        PricingConfiguration savedConfig = pricingConfigRepository.save(config);
        logger.info("Course pricing configuration saved with ID: {}", savedConfig.getId());
        
        // If cascade to classes is enabled, update all classes in this course
        if (request.getCascadeToClasses()) {
            logger.info("Cascading pricing to classes in course: {}", request.getCourseName());
            updateClassesInCourse(request.getCourseId(), request);
        }
        
        return savedConfig;
    }
    
    /**
     * Set pricing for selected class
     */
    public PricingConfiguration setClassPricing(ClassPricingRequest request) {
        logger.info("Setting pricing for class ID: {}, Name: {}", request.getClassId(), request.getClassName());
        
        // Validate class exists
        classRepository.findById(request.getClassId())
                .orElseThrow(() -> new IllegalArgumentException("Class not found with ID: " + request.getClassId()));
        
        // Create or update pricing configuration
        PricingConfiguration config = pricingConfigRepository
                .findByEntityTypeAndEntityId("CLASS", request.getClassId())
                .orElse(new PricingConfiguration());
        
        config.setEntityType("CLASS");
        config.setEntityId(request.getClassId());
        config.setEntityName(request.getClassName());
        config.setMonthlyPrice(request.getMonthlyPrice());
        config.setQuarterlyPrice(request.getQuarterlyPrice());
        config.setYearlyPrice(request.getYearlyPrice());
        config.setMonthlyDurationDays(request.getMonthlyDurationDays());
        config.setQuarterlyDurationDays(request.getQuarterlyDurationDays());
        config.setYearlyDurationDays(request.getYearlyDurationDays());
        config.setQuarterlyDiscountPercent(request.getQuarterlyDiscountPercent());
        config.setYearlyDiscountPercent(request.getYearlyDiscountPercent());
        config.setIsActive(request.getIsActive());
        
        PricingConfiguration savedConfig = pricingConfigRepository.save(config);
        logger.info("Class pricing configuration saved with ID: {}", savedConfig.getId());
        
        return savedConfig;
    }
    
    /**
     * Set pricing for selected exam
     */
    public PricingConfiguration setExamPricing(ExamPricingRequest request) {
        logger.info("Setting pricing for exam ID: {}, Name: {}", request.getExamId(), request.getExamName());
        
        // Validate exam exists
        examRepository.findById(request.getExamId())
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with ID: " + request.getExamId()));
        
        // Create or update pricing configuration
        PricingConfiguration config = pricingConfigRepository
                .findByEntityTypeAndEntityId("EXAM", request.getExamId())
                .orElse(new PricingConfiguration());
        
        config.setEntityType("EXAM");
        config.setEntityId(request.getExamId());
        config.setEntityName(request.getExamName());
        config.setMonthlyPrice(request.getMonthlyPrice());
        config.setQuarterlyPrice(request.getQuarterlyPrice());
        config.setYearlyPrice(request.getYearlyPrice());
        config.setMonthlyDurationDays(request.getMonthlyDurationDays());
        config.setQuarterlyDurationDays(request.getQuarterlyDurationDays());
        config.setYearlyDurationDays(request.getYearlyDurationDays());
        config.setQuarterlyDiscountPercent(request.getQuarterlyDiscountPercent());
        config.setYearlyDiscountPercent(request.getYearlyDiscountPercent());
        config.setIsActive(request.getIsActive());
        
        PricingConfiguration savedConfig = pricingConfigRepository.save(config);
        logger.info("Exam pricing configuration saved with ID: {}", savedConfig.getId());
        
        return savedConfig;
    }
    
    /**
     * Get current pricing configuration for entity
     */
    public PricingConfiguration getCurrentPricing(String entityType, Long entityId) {
        logger.info("Retrieving current pricing for entity type: {}, ID: {}", entityType, entityId);
        
        return pricingConfigRepository
                .findByEntityTypeAndEntityId(entityType, entityId)
                .orElse(null);
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Convert Course entity to CourseDropdownDTO
     */
    private CourseDropdownDTO convertToCourseDropdownDTO(Course course) {
        CourseDropdownDTO dto = new CourseDropdownDTO();
        dto.setId(course.getId());
        dto.setName(course.getName());
        dto.setCourseTypeName(course.getCourseType() != null ? course.getCourseType().getName() : "Unknown");
        dto.setDescription(course.getDescription());
        
        // Check if pricing exists
        Optional<PricingConfiguration> existingPricing = pricingConfigRepository
                .findByEntityTypeAndEntityId("COURSE", course.getId());
        
        dto.setHasExistingPricing(existingPricing.isPresent());
        dto.setCurrentPricingStatus(existingPricing.isPresent() ? 
                (existingPricing.get().getIsActive() ? "SET" : "INACTIVE") : "NOT_SET");
        
        return dto;
    }
    
    /**
     * Convert ClassEntity to ClassDropdownDTO
     */
    private ClassDropdownDTO convertToClassDropdownDTO(ClassEntity classEntity) {
        ClassDropdownDTO dto = new ClassDropdownDTO();
        dto.setId(classEntity.getId());
        dto.setName(classEntity.getName());
        dto.setCourseId(classEntity.getCourse() != null ? classEntity.getCourse().getId() : null);
        dto.setCourseName(classEntity.getCourse() != null ? classEntity.getCourse().getName() : "Unknown");
        dto.setDescription(classEntity.getDescription());
        
        // Check if pricing exists
        Optional<PricingConfiguration> existingPricing = pricingConfigRepository
                .findByEntityTypeAndEntityId("CLASS", classEntity.getId());
        
        dto.setHasExistingPricing(existingPricing.isPresent());
        dto.setCurrentPricingStatus(existingPricing.isPresent() ? 
                (existingPricing.get().getIsActive() ? "SET" : "INACTIVE") : "NOT_SET");
        
        return dto;
    }
    
    /**
     * Convert Exam entity to ExamDropdownDTO
     */
    private ExamDropdownDTO convertToExamDropdownDTO(Exam exam) {
        ExamDropdownDTO dto = new ExamDropdownDTO();
        dto.setId(exam.getId());
        dto.setName(exam.getName());
        dto.setDescription(exam.getDescription());
        
        // Check if pricing exists
        Optional<PricingConfiguration> existingPricing = pricingConfigRepository
                .findByEntityTypeAndEntityId("EXAM", exam.getId());
        
        dto.setHasExistingPricing(existingPricing.isPresent());
        dto.setCurrentPricingStatus(existingPricing.isPresent() ? 
                (existingPricing.get().getIsActive() ? "SET" : "INACTIVE") : "NOT_SET");
        
        return dto;
    }
    
    /**
     * Update all classes in a course with pricing (for cascade functionality)
     */
    private void updateClassesInCourse(Long courseId, CoursePricingRequest request) {
        List<ClassEntity> classes = classRepository.findByCourseId(courseId);
        logger.info("Found {} classes in course {} to update pricing", classes.size(), courseId);
        
        for (ClassEntity classEntity : classes) {
            // Calculate class pricing (divide course pricing by number of classes)
            BigDecimal classMonthlyPrice = request.getMonthlyPrice()
                    .divide(BigDecimal.valueOf(classes.size()), 2, RoundingMode.HALF_UP);
            BigDecimal classQuarterlyPrice = request.getQuarterlyPrice()
                    .divide(BigDecimal.valueOf(classes.size()), 2, RoundingMode.HALF_UP);
            BigDecimal classYearlyPrice = request.getYearlyPrice()
                    .divide(BigDecimal.valueOf(classes.size()), 2, RoundingMode.HALF_UP);
            
            PricingConfiguration classConfig = pricingConfigRepository
                    .findByEntityTypeAndEntityId("CLASS", classEntity.getId())
                    .orElse(new PricingConfiguration());
            
            classConfig.setEntityType("CLASS");
            classConfig.setEntityId(classEntity.getId());
            classConfig.setEntityName(classEntity.getName());
            classConfig.setMonthlyPrice(classMonthlyPrice);
            classConfig.setQuarterlyPrice(classQuarterlyPrice);
            classConfig.setYearlyPrice(classYearlyPrice);
            classConfig.setMonthlyDurationDays(request.getMonthlyDurationDays());
            classConfig.setQuarterlyDurationDays(request.getQuarterlyDurationDays());
            classConfig.setYearlyDurationDays(request.getYearlyDurationDays());
            classConfig.setQuarterlyDiscountPercent(request.getQuarterlyDiscountPercent());
            classConfig.setYearlyDiscountPercent(request.getYearlyDiscountPercent());
            classConfig.setIsActive(request.getIsActive());
            
            pricingConfigRepository.save(classConfig);
            logger.info("Updated pricing for class: {} (ID: {})", classEntity.getName(), classEntity.getId());
        }
    }
    
    // ==================== ADDITIONAL UTILITY METHODS ====================
    
    /**
     * Get all pricing configurations with filters
     */
    public List<PricingConfiguration> getAllPricingConfigurations(String entityType, Boolean isActive) {
        logger.info("Retrieving pricing configurations with filters - entityType: {}, isActive: {}", entityType, isActive);
        
        if (entityType != null && isActive != null) {
            return pricingConfigRepository.findByEntityTypeAndIsActiveTrue(entityType);
        } else if (entityType != null) {
            return pricingConfigRepository.findByEntityType(entityType);
        } else if (isActive != null) {
            return pricingConfigRepository.findPricingConfigurationsWithFilters(null, isActive);
        } else {
            return pricingConfigRepository.findAll();
        }
    }
    
    /**
     * Check if pricing configuration exists for entity
     */
    public boolean pricingConfigurationExists(String entityType, Long entityId) {
        return pricingConfigRepository.existsByEntityTypeAndEntityId(entityType, entityId);
    }
    
    /**
     * Get pricing configuration summary by entity type
     */
    public List<Object[]> getPricingConfigurationSummary() {
        logger.info("Retrieving pricing configuration summary");
        return pricingConfigRepository.getPricingConfigurationSummary();
    }
    
    /**
     * Deactivate pricing configuration
     */
    public PricingConfiguration deactivatePricing(String entityType, Long entityId) {
        logger.info("Deactivating pricing for entity type: {}, ID: {}", entityType, entityId);
        
        PricingConfiguration config = pricingConfigRepository
                .findByEntityTypeAndEntityId(entityType, entityId)
                .orElseThrow(() -> new IllegalArgumentException("Pricing configuration not found"));
        
        config.setIsActive(false);
        return pricingConfigRepository.save(config);
    }
    
    /**
     * Activate pricing configuration
     */
    public PricingConfiguration activatePricing(String entityType, Long entityId) {
        logger.info("Activating pricing for entity type: {}, ID: {}", entityType, entityId);
        
        PricingConfiguration config = pricingConfigRepository
                .findByEntityTypeAndEntityId(entityType, entityId)
                .orElseThrow(() -> new IllegalArgumentException("Pricing configuration not found"));
        
        config.setIsActive(true);
        return pricingConfigRepository.save(config);
    }
}
