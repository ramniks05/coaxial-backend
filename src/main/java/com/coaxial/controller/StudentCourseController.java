package com.coaxial.controller;

import com.coaxial.dto.StudentCourseResponseDTO;
import com.coaxial.service.StudentCourseService;
import com.coaxial.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Student Course Controller
 * 
 * Provides course information and enrollment data for student users.
 * Students can browse available courses, view course details, and check
 * their enrollment status and progress.
 * 
 * @author Coaxial Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/student/courses")
@PreAuthorize("hasRole('STUDENT')")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://127.0.0.1:3000", "http://127.0.0.1:3001"}, allowCredentials = "true")
public class StudentCourseController {

    private static final Logger logger = LoggerFactory.getLogger(StudentCourseController.class);

    @Autowired
    private StudentCourseService studentCourseService;

    @Autowired
    private UserService userService;

    /**
     * Get all available courses for students with pagination
     * 
     * Returns a paginated list of courses that students can access.
     * Includes enrollment status, subscription information, and progress data.
     * 
     * @param courseTypeId Filter by course type ID
     * @param isActive Filter by active status (default: true)
     * @param search Search term for course name/description
     * @param page Page number (default: 0)
     * @param size Page size (default: 10)
     * @param sortBy Sort field (default: name)
     * @param sortDir Sort direction (default: asc)
     * @param authentication Current student authentication
     * @return ResponseEntity containing paginated course list
     */
    @GetMapping
    public ResponseEntity<?> getCourses(
            @RequestParam(required = false) Long courseTypeId,
            @RequestParam(required = false, defaultValue = "true") Boolean isActive,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Authentication authentication) {
        
        try {
            Long studentId = getCurrentStudentId(authentication);
            logger.info("Student {} requesting courses with filters - courseTypeId: {}, isActive: {}, search: '{}', page: {}, size: {}", 
                       studentId, courseTypeId, isActive, search, page, size);

            // Validate pagination parameters
            if (size > 50) size = 50; // Limit page size
            if (page < 0) page = 0;

            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<StudentCourseResponseDTO> courses = studentCourseService.getCoursesForStudents(
                courseTypeId, isActive, search, studentId, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("courses", courses.getContent());
            response.put("currentPage", courses.getNumber());
            response.put("totalPages", courses.getTotalPages());
            response.put("totalElements", courses.getTotalElements());
            response.put("size", courses.getSize());
            response.put("studentId", studentId);
            response.put("timestamp", java.time.LocalDateTime.now().toString());

            logger.info("Returning {} courses for student {} (page {}/{})", 
                       courses.getContent().size(), studentId, page + 1, courses.getTotalPages());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error fetching courses for student", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch courses: " + e.getMessage()));
        }
    }

    /**
     * Get all available courses for students without pagination
     * 
     * Returns a complete list of courses for dropdowns or summary views.
     * 
     * @param courseTypeId Filter by course type ID
     * @param isActive Filter by active status (default: true)
     * @param search Search term for course name/description
     * @param authentication Current student authentication
     * @return ResponseEntity containing course list
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllCourses(
            @RequestParam(required = false) Long courseTypeId,
            @RequestParam(required = false, defaultValue = "true") Boolean isActive,
            @RequestParam(required = false) String search,
            Authentication authentication) {
        
        try {
            Long studentId = getCurrentStudentId(authentication);
            logger.info("Student {} requesting all courses with filters", studentId);

            List<StudentCourseResponseDTO> courses = studentCourseService.getAllCoursesForStudents(
                courseTypeId, isActive, search, studentId);

            Map<String, Object> response = new HashMap<>();
            response.put("courses", courses);
            response.put("totalCount", courses.size());
            response.put("studentId", studentId);
            response.put("timestamp", java.time.LocalDateTime.now().toString());

            logger.info("Returning {} total courses for student {}", courses.size(), studentId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error fetching all courses for student", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch courses: " + e.getMessage()));
        }
    }

    /**
     * Get course by ID for students
     * 
     * Returns detailed information about a specific course including
     * enrollment status, progress, and available content.
     * 
     * @param id Course ID
     * @param authentication Current student authentication
     * @return ResponseEntity containing course details
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id, 
                                         Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            logger.info("Student {} requesting course with ID: {}", studentId, id);

            if (id == null || id <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid course ID"));
            }

            StudentCourseResponseDTO course = studentCourseService.getCourseForStudent(id, studentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("course", course);
            response.put("studentId", studentId);
            response.put("timestamp", java.time.LocalDateTime.now().toString());

            logger.info("Returning course {} for student {}", id, studentId);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Course not found or not available for ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching course {} for student", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch course: " + e.getMessage()));
        }
    }

    /**
     * Get courses by course type for students
     * 
     * Returns courses filtered by specific course type (Academic, Competitive, Professional).
     * 
     * @param courseTypeId Course type ID (required)
     * @param active Filter by active status (default: true)
     * @param search Search term for course name/description
     * @param page Page number (default: 0)
     * @param size Page size (default: 10)
     * @param sortBy Sort field (default: name)
     * @param sortDir Sort direction (default: asc)
     * @param authentication Current student authentication
     * @return ResponseEntity containing paginated course list
     */
    @GetMapping("/by-course-type")
    public ResponseEntity<?> getCoursesByCourseType(
            @RequestParam Long courseTypeId,
            @RequestParam(required = false, defaultValue = "true") Boolean active,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Authentication authentication) {
        
        try {
            Long studentId = getCurrentStudentId(authentication);
            logger.info("Student {} requesting courses by course type {}", studentId, courseTypeId);

            if (courseTypeId == null || courseTypeId <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid course type ID"));
            }

            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<StudentCourseResponseDTO> courses = studentCourseService.getCoursesByCourseTypeForStudents(
                courseTypeId, active, search, studentId, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("courses", courses.getContent());
            response.put("courseTypeId", courseTypeId);
            response.put("currentPage", courses.getNumber());
            response.put("totalPages", courses.getTotalPages());
            response.put("totalElements", courses.getTotalElements());
            response.put("studentId", studentId);
            response.put("timestamp", java.time.LocalDateTime.now().toString());

            logger.info("Returning {} courses of type {} for student {}", 
                       courses.getContent().size(), courseTypeId, studentId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error fetching courses by course type {} for student", courseTypeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch courses: " + e.getMessage()));
        }
    }

    /**
     * Get student's course dashboard summary
     * 
     * Returns a summary of student's course enrollment, progress, and recommendations.
     * 
     * @param authentication Current student authentication
     * @return ResponseEntity containing dashboard summary
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getCourseDashboard(Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            logger.info("Student {} requesting course dashboard", studentId);

            // Get all courses for the student
            List<StudentCourseResponseDTO> allCourses = studentCourseService.getAllCoursesForStudents(
                null, true, null, studentId);

            // Calculate dashboard statistics
            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("totalAvailableCourses", allCourses.size());
            dashboard.put("enrolledCourses", allCourses.stream()
                .filter(course -> "ENROLLED".equals(course.getEnrollmentStatus()))
                .count());
            dashboard.put("subscribedCourses", allCourses.stream()
                .filter(StudentCourseResponseDTO::getIsSubscribed)
                .count());
            
            // Group by course type
            Map<String, Long> coursesByType = allCourses.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    StudentCourseResponseDTO::getCourseTypeName,
                    java.util.stream.Collectors.counting()
                ));
            dashboard.put("coursesByType", coursesByType);

            // Get recommended courses (courses not enrolled)
            List<StudentCourseResponseDTO> recommendedCourses = allCourses.stream()
                .filter(course -> !"ENROLLED".equals(course.getEnrollmentStatus()))
                .limit(5)
                .collect(java.util.stream.Collectors.toList());
            dashboard.put("recommendedCourses", recommendedCourses);

            dashboard.put("studentId", studentId);
            dashboard.put("timestamp", java.time.LocalDateTime.now().toString());

            logger.info("Returning course dashboard for student {}", studentId);
            return ResponseEntity.ok(dashboard);

        } catch (Exception e) {
            logger.error("Error fetching course dashboard for student", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch course dashboard: " + e.getMessage()));
        }
    }

    /**
     * Get subscription pricing for a specific course
     * 
     * @param courseId Course ID
     * @param authentication Current student authentication
     * @return ResponseEntity containing pricing information
     */
    @GetMapping("/{id}/pricing")
    public ResponseEntity<?> getCoursePricing(@PathVariable("id") Long courseId, 
                                            Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            logger.info("Student {} requesting pricing for course {}", studentId, courseId);

            StudentCourseResponseDTO course = studentCourseService.getCourseForStudent(courseId, studentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("courseId", courseId);
            response.put("courseName", course.getName());
            response.put("coursePricing", course.getCoursePricing());
            response.put("subscriptionOptions", course.getSubscriptionOptions());
            response.put("studentId", studentId);
            response.put("timestamp", java.time.LocalDateTime.now().toString());

            logger.info("Returning pricing information for course {} to student {}", courseId, studentId);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Course not found or not available for ID: {}", courseId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching pricing for course {}", courseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch pricing: " + e.getMessage()));
        }
    }

    /**
     * Get current student ID from authentication
     * 
     * @param authentication Current authentication context
     * @return Student ID
     * @throws RuntimeException if student not found
     */
    private Long getCurrentStudentId(Authentication authentication) {
        String username = authentication.getName();
        return userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"))
                .getId();
    }
}
