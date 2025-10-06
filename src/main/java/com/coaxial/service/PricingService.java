package com.coaxial.service;

import com.coaxial.entity.ClassEntity;
import com.coaxial.entity.Course;
import com.coaxial.entity.Exam;
import com.coaxial.entity.PricingConfiguration;
import com.coaxial.enums.SubscriptionLevel;
import com.coaxial.repository.PricingConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service for managing pricing information at different subscription levels
 * Provides pricing for Course level and Class/Exam level subscriptions
 */
@Service
public class PricingService {
    
    private static final Logger logger = LoggerFactory.getLogger(PricingService.class);
    
    @Autowired
    private PricingConfigurationRepository pricingConfigRepository;
    
    /**
     * Get pricing information for a class (Class level subscription)
     */
    public Map<String, Object> getClassPricing(ClassEntity classEntity) {
        // First check if there's a custom pricing configuration
        Optional<PricingConfiguration> config = pricingConfigRepository
                .findByEntityTypeAndEntityId("CLASS", classEntity.getId());
        
        if (config.isPresent() && config.get().getIsActive()) {
            logger.info("Using custom pricing configuration for class: {}", classEntity.getName());
            return buildPricingFromConfig(config.get());
        }
        
        // Fallback to default pricing logic
        logger.info("Using default pricing logic for class: {}", classEntity.getName());
        return getDefaultClassPricing(classEntity);
    }
    
    /**
     * Get pricing information for an exam (Exam level subscription)
     */
    public Map<String, Object> getExamPricing(Exam exam) {
        // First check if there's a custom pricing configuration
        Optional<PricingConfiguration> config = pricingConfigRepository
                .findByEntityTypeAndEntityId("EXAM", exam.getId());
        
        if (config.isPresent() && config.get().getIsActive()) {
            logger.info("Using custom pricing configuration for exam: {}", exam.getName());
            return buildPricingFromConfig(config.get());
        }
        
        // Fallback to default pricing logic
        logger.info("Using default pricing logic for exam: {}", exam.getName());
        return getDefaultExamPricing(exam);
    }
    
    /**
     * Get pricing information for a course (Course level subscription)
     */
    public Map<String, Object> getCoursePricing(Course course) {
        // First check if there's a custom pricing configuration
        Optional<PricingConfiguration> config = pricingConfigRepository
                .findByEntityTypeAndEntityId("COURSE", course.getId());
        
        if (config.isPresent() && config.get().getIsActive()) {
            logger.info("Using custom pricing configuration for course: {}", course.getName());
            return buildPricingFromConfig(config.get());
        }
        
        // Fallback to default pricing logic
        logger.info("Using default pricing logic for course: {}", course.getName());
        return getDefaultCoursePricing(course);
    }
    
    /**
     * Build pricing map from database configuration
     */
    private Map<String, Object> buildPricingFromConfig(PricingConfiguration config) {
        Map<String, Object> pricing = new HashMap<>();
        Map<String, Integer> amounts = new HashMap<>();
        Map<String, String> discounts = new HashMap<>();
        Map<String, Integer> durations = new HashMap<>();
        
        // Convert BigDecimal to Integer (rupees)
        amounts.put("monthly", config.getMonthlyPrice().intValue());
        amounts.put("quarterly", config.getQuarterlyPrice().intValue());
        amounts.put("yearly", config.getYearlyPrice().intValue());
        
        discounts.put("quarterly", config.getQuarterlyDiscountPercent() + "% OFF");
        discounts.put("yearly", config.getYearlyDiscountPercent() + "% OFF");
        
        durations.put("monthly", config.getMonthlyDurationDays());
        durations.put("quarterly", config.getQuarterlyDurationDays());
        durations.put("yearly", config.getYearlyDurationDays());
        
        pricing.put("amounts", amounts);
        pricing.put("discounts", discounts);
        pricing.put("durations", durations);
        pricing.put("subscriptionLevel", config.getEntityType());
        pricing.put("entityId", config.getEntityId());
        pricing.put("entityName", config.getEntityName());
        
        logger.info("Built pricing from config for {}: {}", config.getEntityName(), amounts);
        
        return pricing;
    }
    
    /**
     * Get default class pricing (fallback logic)
     */
    private Map<String, Object> getDefaultClassPricing(ClassEntity classEntity) {
        Map<String, Object> pricing = new HashMap<>();
        
        // Calculate pricing based on class level (1-10, 11-12, etc.)
        int classLevel = extractClassLevel(classEntity.getName());
        
        Map<String, Integer> amounts = new HashMap<>();
        Map<String, String> discounts = new HashMap<>();
        
        if (classLevel >= 1 && classLevel <= 10) {
            // Primary/Middle school pricing
            amounts.put("monthly", 299);
            amounts.put("quarterly", 807); // 10% discount
            amounts.put("yearly", 2870);   // 20% discount
        } else if (classLevel >= 11 && classLevel <= 12) {
            // High school pricing
            amounts.put("monthly", 499);
            amounts.put("quarterly", 1347); // 10% discount
            amounts.put("yearly", 4792);    // 20% discount
        } else {
            // Default pricing
            amounts.put("monthly", 399);
            amounts.put("quarterly", 1077); // 10% discount
            amounts.put("yearly", 3832);    // 20% discount
        }
        
        discounts.put("quarterly", "10% OFF");
        discounts.put("yearly", "20% OFF");
        
        pricing.put("amounts", amounts);
        pricing.put("discounts", discounts);
        pricing.put("subscriptionLevel", SubscriptionLevel.CLASS.toString());
        pricing.put("entityId", classEntity.getId());
        pricing.put("entityName", classEntity.getName());
        
        logger.info("Generated default class pricing for {}: {}", classEntity.getName(), amounts);
        
        return pricing;
    }
    
    /**
     * Get default exam pricing (fallback logic)
     */
    private Map<String, Object> getDefaultExamPricing(Exam exam) {
        Map<String, Object> pricing = new HashMap<>();
        
        Map<String, Integer> amounts = new HashMap<>();
        Map<String, String> discounts = new HashMap<>();
        
        // Competitive exam pricing (typically higher)
        amounts.put("monthly", 799);
        amounts.put("quarterly", 2157); // 10% discount
        amounts.put("yearly", 7672);    // 20% discount
        
        discounts.put("quarterly", "10% OFF");
        discounts.put("yearly", "20% OFF");
        
        pricing.put("amounts", amounts);
        pricing.put("discounts", discounts);
        pricing.put("subscriptionLevel", SubscriptionLevel.EXAM.toString());
        pricing.put("entityId", exam.getId());
        pricing.put("entityName", exam.getName());
        
        logger.info("Generated default exam pricing for {}: {}", exam.getName(), amounts);
        
        return pricing;
    }
    
    /**
     * Get default course pricing (fallback logic)
     */
    private Map<String, Object> getDefaultCoursePricing(Course course) {
        Map<String, Object> pricing = new HashMap<>();
        
        Map<String, Integer> amounts = new HashMap<>();
        Map<String, String> discounts = new HashMap<>();
        
        // Course pricing varies by type
        String courseType = course.getCourseType() != null ? course.getCourseType().getName() : "Unknown";
        
        if ("Academic".equalsIgnoreCase(courseType)) {
            // Academic course pricing (comprehensive)
            amounts.put("monthly", 1299);
            amounts.put("quarterly", 3507); // 10% discount
            amounts.put("yearly", 12472);   // 20% discount
        } else if ("Competitive".equalsIgnoreCase(courseType)) {
            // Competitive exam course pricing
            amounts.put("monthly", 1999);
            amounts.put("quarterly", 5397); // 10% discount
            amounts.put("yearly", 19192);   // 20% discount
        } else if ("Professional".equalsIgnoreCase(courseType)) {
            // Professional course pricing
            amounts.put("monthly", 1599);
            amounts.put("quarterly", 4317); // 10% discount
            amounts.put("yearly", 15352);   // 20% discount
        } else {
            // Default pricing
            amounts.put("monthly", 999);
            amounts.put("quarterly", 2697); // 10% discount
            amounts.put("yearly", 9592);    // 20% discount
        }
        
        discounts.put("quarterly", "10% OFF");
        discounts.put("yearly", "20% OFF");
        
        pricing.put("amounts", amounts);
        pricing.put("discounts", discounts);
        pricing.put("subscriptionLevel", SubscriptionLevel.COURSE.toString());
        pricing.put("entityId", course.getId());
        pricing.put("entityName", course.getName());
        
        logger.info("Generated default course pricing for {}: {}", course.getName(), amounts);
        
        return pricing;
    }
    
    /**
     * Extract class level from class name (e.g., "Class 10" -> 10)
     */
    private int extractClassLevel(String className) {
        try {
            // Extract number from class name
            String[] parts = className.split(" ");
            for (String part : parts) {
                if (part.matches("\\d+")) {
                    return Integer.parseInt(part);
                }
            }
        } catch (Exception e) {
            logger.warn("Could not extract class level from: {}", className);
        }
        return 1; // Default to class 1
    }
}
