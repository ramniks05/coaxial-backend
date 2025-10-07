package com.coaxial.controller;

import com.coaxial.dto.CourseCatalogueResponse;
import com.coaxial.service.CourseCatalogueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Public Course Catalogue Controller
 * Provides course catalogue without authentication
 * Returns different structures based on course type:
 * - Academic: Class objects
 * - Competitive: Exam objects
 * - Professional: Course objects
 */
@RestController
@RequestMapping("/api/public/course-catalogue")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PublicCourseCatalogueController {
    
    private static final Logger logger = LoggerFactory.getLogger(PublicCourseCatalogueController.class);
    
    @Autowired
    private CourseCatalogueService catalogueService;
    
    /**
     * Get combined course catalogue (all types in one response)
     * GET /api/public/course-catalogue/all
     * 
     * Returns all three types together:
     * - classes[] (Academic)
     * - exams[] (Competitive)
     * - courses[] (Professional)
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllCourseCatalogue() {
        try {
            logger.info("Public request for combined course catalogue");
            
            CourseCatalogueResponse catalogue = catalogueService.getAllCourseCatalogue();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("courseType", catalogue.getCourseType());
            response.put("description", catalogue.getDescription());
            
            // Add all three types
            if (catalogue.getClasses() != null) {
                response.put("classes", catalogue.getClasses());
                response.put("classCount", catalogue.getClasses().size());
            }
            if (catalogue.getExams() != null) {
                response.put("exams", catalogue.getExams());
                response.put("examCount", catalogue.getExams().size());
            }
            if (catalogue.getCourses() != null) {
                response.put("courses", catalogue.getCourses());
                response.put("courseCount", catalogue.getCourses().size());
            }
            
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            logger.info("Returning combined catalogue with {} classes, {} exams, {} courses", 
                catalogue.getClasses() != null ? catalogue.getClasses().size() : 0,
                catalogue.getExams() != null ? catalogue.getExams().size() : 0,
                catalogue.getCourses() != null ? catalogue.getCourses().size() : 0);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching combined course catalogue", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "success", false,
                        "message", "Error fetching combined course catalogue: " + e.getMessage()
                    ));
        }
    }
    
    /**
     * Get course catalogue by course type
     * GET /api/public/course-catalogue?courseTypeId=1
     * 
     * Returns:
     * - Academic (courseTypeId=1): Array of Class objects
     * - Competitive (courseTypeId=2): Array of Exam objects
     * - Professional (courseTypeId=3): Array of Course objects
     */
    @GetMapping
    public ResponseEntity<?> getCourseCatalogue(
            @RequestParam(required = false) Long courseTypeId) {
        try {
            logger.info("Public request for course catalogue, courseTypeId: {}", courseTypeId);
            
            if (courseTypeId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                            "success", false,
                            "message", "courseTypeId is required. Use 1=Academic, 2=Competitive, 3=Professional, or use /all for combined"
                        ));
            }
            
            CourseCatalogueResponse catalogue = catalogueService.getCourseCatalogue(courseTypeId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("courseTypeId", catalogue.getCourseTypeId());
            response.put("courseType", catalogue.getCourseType());
            response.put("description", catalogue.getDescription());
            
            // Add appropriate data based on course type
            if (catalogue.getClasses() != null && !catalogue.getClasses().isEmpty()) {
                logger.info("Adding {} classes to response", catalogue.getClasses().size());
                response.put("classes", catalogue.getClasses());
                response.put("count", catalogue.getClasses().size());
            } else if (catalogue.getExams() != null && !catalogue.getExams().isEmpty()) {
                logger.info("Adding {} exams to response", catalogue.getExams().size());
                response.put("exams", catalogue.getExams());
                response.put("count", catalogue.getExams().size());
            } else if (catalogue.getCourses() != null && !catalogue.getCourses().isEmpty()) {
                logger.info("Adding {} courses to response", catalogue.getCourses().size());
                response.put("courses", catalogue.getCourses());
                response.put("count", catalogue.getCourses().size());
            } else {
                logger.warn("No data found in catalogue response. Classes: {}, Exams: {}, Courses: {}", 
                    catalogue.getClasses() != null ? catalogue.getClasses().size() : "null",
                    catalogue.getExams() != null ? catalogue.getExams().size() : "null",
                    catalogue.getCourses() != null ? catalogue.getCourses().size() : "null");
                response.put("count", 0);
                response.put("message", "No data found for this course type");
            }
            
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            logger.info("Returning catalogue for courseType: {} with {} items", 
                catalogue.getCourseType(), response.get("count"));
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid course type ID: {}", courseTypeId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "success", false,
                        "message", e.getMessage()
                    ));
        } catch (Exception e) {
            logger.error("Error fetching course catalogue", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "success", false,
                        "message", "Error fetching course catalogue: " + e.getMessage()
                    ));
        }
    }
    
    /**
     * Get all course types available
     * GET /api/public/course-catalogue/types
     */
    @GetMapping("/types")
    public ResponseEntity<?> getCourseTypes() {
        try {
            logger.info("Public request for course types");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("courseTypes", java.util.Arrays.asList(
                Map.of("id", 1, "name", "Academic", "description", "Class-based academic curriculum"),
                Map.of("id", 2, "name", "Competitive", "description", "Competitive exam preparation"),
                Map.of("id", 3, "name", "Professional", "description", "Professional skill development")
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching course types", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "success", false,
                        "message", "Error fetching course types: " + e.getMessage()
                    ));
        }
    }
    
    /**
     * Debug endpoint to check what data exists
     * GET /api/public/course-catalogue/debug?courseTypeId=1
     */
    @GetMapping("/debug")
    public ResponseEntity<?> debugCatalogue(@RequestParam Long courseTypeId) {
        try {
            logger.info("Debug request for courseTypeId: {}", courseTypeId);
            
            Map<String, Object> debug = catalogueService.debugDataForCourseType(courseTypeId);
            
            return ResponseEntity.ok(debug);
            
        } catch (Exception e) {
            logger.error("Error in debug endpoint", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "success", false,
                        "message", "Error: " + e.getMessage()
                    ));
        }
    }
}

