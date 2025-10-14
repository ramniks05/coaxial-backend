package com.coaxial.controller;

import com.coaxial.dto.*;
import com.coaxial.service.TestExecutionService;
import com.coaxial.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/student/tests")
@PreAuthorize("hasRole('STUDENT')")
@Tag(name = "Test Execution", description = "APIs for students to start, take, and submit tests")
public class TestExecutionController {
    
    private static final Logger logger = LoggerFactory.getLogger(TestExecutionController.class);
    
    @Autowired
    private TestExecutionService testExecutionService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Start a test - creates session and attempt
     */
    @Operation(
        summary = "Start a test",
        description = "Validates subscription access and creates a new test session and attempt for the student"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Test started successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or no access to test"),
        @ApiResponse(responseCode = "403", description = "Subscription required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{testId}/start")
    public ResponseEntity<?> startTest(
            @PathVariable Long testId,
            @RequestBody StartTestRequestDTO request,
            Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            TestSessionResponseDTO response = testExecutionService.startTest(testId, studentId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to start test {}: {}", testId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error starting test {}", testId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to start test"));
        }
    }
    
    /**
     * Get test questions for active session
     */
    @Operation(
        summary = "Get test questions",
        description = "Retrieves all questions for an active test session (without correct answers)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Questions retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid session"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{testId}/questions")
    public ResponseEntity<?> getTestQuestions(
            @PathVariable Long testId,
            @Parameter(description = "Session ID from start test response")
            @RequestParam String sessionId,
            Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            List<TestQuestionDTO> questions = testExecutionService.getTestQuestions(testId, sessionId, studentId);
            return ResponseEntity.ok(questions);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to get questions for test {}: {}", testId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting questions for test {}", testId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get test questions"));
        }
    }
    
    /**
     * Submit answer for a question
     */
    @Operation(
        summary = "Submit answer for a question",
        description = "Records student's answer for a specific question in the test"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Answer submitted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or session"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{testId}/submit-answer")
    public ResponseEntity<?> submitAnswer(
            @PathVariable Long testId,
            @Valid @RequestBody SubmitAnswerRequestDTO request,
            Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            testExecutionService.submitAnswer(testId, request, studentId);
            return ResponseEntity.ok(Map.of("message", "Answer submitted successfully"));
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to submit answer for test {}: {}", testId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error submitting answer for test {}", testId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to submit answer"));
        }
    }
    
    /**
     * Submit/End test
     */
    @Operation(
        summary = "Submit test",
        description = "Ends the test session, calculates results, and returns the test result"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Test submitted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid session"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{testId}/submit")
    public ResponseEntity<?> submitTest(
            @PathVariable Long testId,
            @Parameter(description = "Session ID from start test response")
            @RequestParam String sessionId,
            Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            TestResultDTO result = testExecutionService.submitTest(testId, sessionId, studentId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to submit test {}: {}", testId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error submitting test {}", testId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to submit test"));
        }
    }
    
    /**
     * Get test result
     */
    @Operation(
        summary = "Get test result",
        description = "Retrieves the result of a submitted test attempt"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Result retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid attempt or test not submitted"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{testId}/result/{attemptId}")
    public ResponseEntity<?> getTestResult(
            @PathVariable Long testId,
            @PathVariable Long attemptId,
            Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            TestResultDTO result = testExecutionService.getTestResult(testId, attemptId, studentId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to get result for test {}, attempt {}: {}", testId, attemptId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting result for test {}, attempt {}", testId, attemptId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get test result"));
        }
    }
    
    /**
     * Get active session for a test
     */
    @Operation(
        summary = "Get active test session",
        description = "Retrieves the current active session for a test if one exists"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active session retrieved or no active session"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{testId}/active-session")
    public ResponseEntity<?> getActiveSession(
            @PathVariable Long testId,
            Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            TestSessionResponseDTO session = testExecutionService.getActiveSession(testId, studentId);
            if (session != null) {
                return ResponseEntity.ok(session);
            } else {
                return ResponseEntity.ok(Map.of("hasActiveSession", false));
            }
        } catch (Exception e) {
            logger.error("Error getting active session for test {}", testId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get active session"));
        }
    }
    
    /**
     * Abandon/Cancel an active test session
     */
    @Operation(
        summary = "Abandon test session",
        description = "Cancels the current active session without submitting. Use this to start a new attempt."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Session abandoned successfully"),
        @ApiResponse(responseCode = "400", description = "No active session found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{testId}/abandon-session")
    public ResponseEntity<?> abandonSession(
            @PathVariable Long testId,
            Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            testExecutionService.abandonSession(testId, studentId);
            return ResponseEntity.ok(Map.of("message", "Session abandoned successfully. You can now start a new attempt."));
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to abandon session for test {}: {}", testId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error abandoning session for test {}", testId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to abandon session"));
        }
    }
    
    /**
     * Get all test attempts for a specific test
     */
    @Operation(
        summary = "Get test attempts history",
        description = "Retrieves all submitted attempts for a specific test by the student"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attempts retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{testId}/attempts")
    public ResponseEntity<?> getTestAttempts(
            @PathVariable Long testId,
            Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            List<TestResultDTO> attempts = testExecutionService.getTestAttempts(testId, studentId);
            return ResponseEntity.ok(attempts);
        } catch (Exception e) {
            logger.error("Error getting attempts for test {}", testId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get test attempts"));
        }
    }
    
    /**
     * Get all test attempts for the student across all tests
     */
    @Operation(
        summary = "Get all test attempts",
        description = "Retrieves all submitted test attempts for the current student"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attempts retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/attempts")
    public ResponseEntity<?> getAllAttempts(Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            List<TestResultDTO> attempts = testExecutionService.getStudentAttempts(studentId);
            return ResponseEntity.ok(attempts);
        } catch (Exception e) {
            logger.error("Error getting all attempts for student", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get test attempts"));
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

