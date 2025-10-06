package com.coaxial.controller;

import com.coaxial.dto.SubjectResponseDTO;
import com.coaxial.dto.TestResponseDTO;
import com.coaxial.service.StudentDashboardService;
import com.coaxial.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student/dashboard")
@PreAuthorize("hasRole('STUDENT')")
public class StudentDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(StudentDashboardController.class);

    @Autowired
    private StudentDashboardService dashboardService;

    @Autowired
    private UserService userService;

    /**
     * Get dashboard summary
     */
    @GetMapping("/summary")
    public ResponseEntity<?> getDashboardSummary(Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            Object summary = dashboardService.getDashboardSummary(studentId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            logger.error("Error fetching dashboard summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch dashboard summary"));
        }
    }

    /**
     * Get accessible subjects for the student
     */
    @GetMapping("/subjects")
    public ResponseEntity<?> getAccessibleSubjects(Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            List<SubjectResponseDTO> subjects = dashboardService.getAccessibleSubjects(studentId);
            return ResponseEntity.ok(subjects);
        } catch (Exception e) {
            logger.error("Error fetching accessible subjects", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch accessible subjects"));
        }
    }

    /**
     * Get accessible tests for the student
     */
    @GetMapping("/tests")
    public ResponseEntity<?> getAccessibleTests(Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            List<TestResponseDTO> tests = dashboardService.getAccessibleTests(studentId);
            return ResponseEntity.ok(tests);
        } catch (Exception e) {
            logger.error("Error fetching accessible tests", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch accessible tests"));
        }
    }

    /**
     * Check if student has access to specific content
     */
    @GetMapping("/check-access")
    public ResponseEntity<?> checkContentAccess(@RequestParam String subscriptionLevel,
                                               @RequestParam Long entityId,
                                               Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            
            com.coaxial.enums.SubscriptionLevel level = com.coaxial.enums.SubscriptionLevel.valueOf(subscriptionLevel.toUpperCase());
            boolean hasAccess = dashboardService.hasAccessToContent(studentId, level, entityId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("hasAccess", hasAccess);
            response.put("studentId", studentId);
            response.put("subscriptionLevel", level);
            response.put("entityId", entityId);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid access check request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid subscription level"));
        } catch (Exception e) {
            logger.error("Error checking content access", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to check content access"));
        }
    }

    /**
     * Check if student has access to specific test
     */
    @GetMapping("/test-access/{testId}")
    public ResponseEntity<?> checkTestAccess(@PathVariable Long testId, Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            boolean hasAccess = dashboardService.hasAccessToTest(studentId, testId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("hasAccess", hasAccess);
            response.put("studentId", studentId);
            response.put("testId", testId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error checking test access for test {}", testId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to check test access"));
        }
    }

    /**
     * Get student profile information
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getStudentProfile(Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            var student = userService.getUserById(studentId);
            
            if (student.isPresent()) {
                Map<String, Object> profile = new HashMap<>();
                profile.put("id", student.get().getId());
                profile.put("username", student.get().getUsername());
                profile.put("email", student.get().getEmail());
                profile.put("firstName", student.get().getFirstName());
                profile.put("lastName", student.get().getLastName());
                profile.put("phoneNumber", student.get().getPhoneNumber());
                profile.put("dateOfBirth", student.get().getDateOfBirth());
                profile.put("address", student.get().getAddress());
                profile.put("profileImageUrl", student.get().getProfileImageUrl());
                profile.put("createdAt", student.get().getCreatedAt());
                profile.put("lastLoginAt", student.get().getLastLoginAt());
                
                return ResponseEntity.ok(profile);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching student profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch student profile"));
        }
    }

    /**
     * Get current student ID from authentication
     */
    private Long getCurrentStudentId(Authentication authentication) {
        String username = authentication.getName();
        return userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"))
                .getId();
    }
}
