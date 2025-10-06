package com.coaxial.controller;

import com.coaxial.dto.*;
import com.coaxial.entity.PricingConfiguration;
import com.coaxial.service.AdminPricingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Admin Pricing Management
 * Provides endpoints for dropdown data and pricing configuration operations
 */
@RestController
@RequestMapping("/api/admin/pricing")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminPricingController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminPricingController.class);
    
    @Autowired
    private AdminPricingService adminPricingService;
    
    // ==================== DROPDOWN DATA ENDPOINTS ====================
    
    /**
     * Get dropdown data for course selection
     * GET /api/admin/pricing/dropdowns/courses
     */
    @GetMapping("/dropdowns/courses")
    public ResponseEntity<?> getCourseDropdownData() {
        try {
            logger.info("Admin requesting course dropdown data");
            List<CourseDropdownDTO> courses = adminPricingService.getCourseDropdownData();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", courses,
                "message", "Course dropdown data retrieved successfully",
                "count", courses.size()
            ));
        } catch (Exception e) {
            logger.error("Error retrieving course dropdown data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false, 
                    "message", "Error retrieving course dropdown data: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Get dropdown data for class selection (filtered by course)
     * GET /api/admin/pricing/dropdowns/classes?courseId=1
     */
    @GetMapping("/dropdowns/classes")
    public ResponseEntity<?> getClassDropdownData(
            @RequestParam(required = false) Long courseId) {
        try {
            logger.info("Admin requesting class dropdown data for courseId: {}", courseId);
            List<ClassDropdownDTO> classes = adminPricingService.getClassDropdownData(courseId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", classes,
                "message", "Class dropdown data retrieved successfully",
                "count", classes.size(),
                "courseId", courseId
            ));
        } catch (Exception e) {
            logger.error("Error retrieving class dropdown data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false, 
                    "message", "Error retrieving class dropdown data: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Get dropdown data for exam selection
     * GET /api/admin/pricing/dropdowns/exams
     */
    @GetMapping("/dropdowns/exams")
    public ResponseEntity<?> getExamDropdownData() {
        try {
            logger.info("Admin requesting exam dropdown data");
            List<ExamDropdownDTO> exams = adminPricingService.getExamDropdownData();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", exams,
                "message", "Exam dropdown data retrieved successfully",
                "count", exams.size()
            ));
        } catch (Exception e) {
            logger.error("Error retrieving exam dropdown data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false, 
                    "message", "Error retrieving exam dropdown data: " + e.getMessage()
                ));
        }
    }
    
    // ==================== PRICING CONFIGURATION ENDPOINTS ====================
    
    /**
     * Set pricing for selected course
     * POST /api/admin/pricing/set-course-pricing
     */
    @PostMapping("/set-course-pricing")
    public ResponseEntity<?> setCoursePricing(@Valid @RequestBody CoursePricingRequest request) {
        try {
            logger.info("Admin setting course pricing for courseId: {}", request.getCourseId());
            PricingConfiguration config = adminPricingService.setCoursePricing(request);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", config,
                "message", "Course pricing set successfully"
            ));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid course pricing request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "success", false, 
                    "message", e.getMessage()
                ));
        } catch (Exception e) {
            logger.error("Error setting course pricing", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false, 
                    "message", "Error setting course pricing: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Set pricing for selected class
     * POST /api/admin/pricing/set-class-pricing
     */
    @PostMapping("/set-class-pricing")
    public ResponseEntity<?> setClassPricing(@Valid @RequestBody ClassPricingRequest request) {
        try {
            logger.info("Admin setting class pricing for classId: {}", request.getClassId());
            PricingConfiguration config = adminPricingService.setClassPricing(request);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", config,
                "message", "Class pricing set successfully"
            ));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid class pricing request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "success", false, 
                    "message", e.getMessage()
                ));
        } catch (Exception e) {
            logger.error("Error setting class pricing", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false, 
                    "message", "Error setting class pricing: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Set pricing for selected exam
     * POST /api/admin/pricing/set-exam-pricing
     */
    @PostMapping("/set-exam-pricing")
    public ResponseEntity<?> setExamPricing(@Valid @RequestBody ExamPricingRequest request) {
        try {
            logger.info("Admin setting exam pricing for examId: {}", request.getExamId());
            PricingConfiguration config = adminPricingService.setExamPricing(request);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", config,
                "message", "Exam pricing set successfully"
            ));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid exam pricing request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "success", false, 
                    "message", e.getMessage()
                ));
        } catch (Exception e) {
            logger.error("Error setting exam pricing", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false, 
                    "message", "Error setting exam pricing: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Get current pricing for selected entity
     * GET /api/admin/pricing/current-pricing/{entityType}/{entityId}
     */
    @GetMapping("/current-pricing/{entityType}/{entityId}")
    public ResponseEntity<?> getCurrentPricing(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        try {
            logger.info("Admin requesting current pricing for entityType: {}, entityId: {}", entityType, entityId);
            PricingConfiguration config = adminPricingService.getCurrentPricing(entityType, entityId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", config);
            response.put("message", config != null ? 
                "Current pricing retrieved successfully" : 
                "No pricing configuration found for this entity");
            response.put("exists", config != null);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error retrieving current pricing", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false, 
                    "message", "Error retrieving current pricing: " + e.getMessage()
                ));
        }
    }
    
    // ==================== ADDITIONAL MANAGEMENT ENDPOINTS ====================
    
    /**
     * Get all pricing configurations with optional filters
     * GET /api/admin/pricing/configurations?entityType=COURSE&isActive=true
     */
    @GetMapping("/configurations")
    public ResponseEntity<?> getAllPricingConfigurations(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) Boolean isActive) {
        try {
            logger.info("Admin requesting pricing configurations with filters - entityType: {}, isActive: {}", entityType, isActive);
            List<PricingConfiguration> configurations = adminPricingService.getAllPricingConfigurations(entityType, isActive);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", configurations,
                "message", "Pricing configurations retrieved successfully",
                "count", configurations.size(),
                "filters", Map.of("entityType", entityType, "isActive", isActive)
            ));
        } catch (Exception e) {
            logger.error("Error retrieving pricing configurations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false, 
                    "message", "Error retrieving pricing configurations: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Get pricing configuration summary
     * GET /api/admin/pricing/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<?> getPricingConfigurationSummary() {
        try {
            logger.info("Admin requesting pricing configuration summary");
            List<Object[]> summary = adminPricingService.getPricingConfigurationSummary();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", summary,
                "message", "Pricing configuration summary retrieved successfully"
            ));
        } catch (Exception e) {
            logger.error("Error retrieving pricing configuration summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false, 
                    "message", "Error retrieving pricing configuration summary: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Deactivate pricing configuration
     * PUT /api/admin/pricing/deactivate/{entityType}/{entityId}
     */
    @PutMapping("/deactivate/{entityType}/{entityId}")
    public ResponseEntity<?> deactivatePricing(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        try {
            logger.info("Admin deactivating pricing for entityType: {}, entityId: {}", entityType, entityId);
            PricingConfiguration config = adminPricingService.deactivatePricing(entityType, entityId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", config,
                "message", "Pricing configuration deactivated successfully"
            ));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid deactivation request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "success", false, 
                    "message", e.getMessage()
                ));
        } catch (Exception e) {
            logger.error("Error deactivating pricing configuration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false, 
                    "message", "Error deactivating pricing configuration: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Activate pricing configuration
     * PUT /api/admin/pricing/activate/{entityType}/{entityId}
     */
    @PutMapping("/activate/{entityType}/{entityId}")
    public ResponseEntity<?> activatePricing(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        try {
            logger.info("Admin activating pricing for entityType: {}, entityId: {}", entityType, entityId);
            PricingConfiguration config = adminPricingService.activatePricing(entityType, entityId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", config,
                "message", "Pricing configuration activated successfully"
            ));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid activation request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "success", false, 
                    "message", e.getMessage()
                ));
        } catch (Exception e) {
            logger.error("Error activating pricing configuration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false, 
                    "message", "Error activating pricing configuration: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Check if pricing configuration exists for entity
     * GET /api/admin/pricing/exists/{entityType}/{entityId}
     */
    @GetMapping("/exists/{entityType}/{entityId}")
    public ResponseEntity<?> checkPricingExists(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        try {
            logger.info("Admin checking if pricing exists for entityType: {}, entityId: {}", entityType, entityId);
            boolean exists = adminPricingService.pricingConfigurationExists(entityType, entityId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "exists", exists,
                "message", exists ? "Pricing configuration exists" : "No pricing configuration found"
            ));
        } catch (Exception e) {
            logger.error("Error checking pricing existence", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false, 
                    "message", "Error checking pricing existence: " + e.getMessage()
                ));
        }
    }
}
