package com.coaxial.controller;

import com.coaxial.dto.StudentQuestionResponseDTO;
import com.coaxial.service.StudentQuestionService;
import com.coaxial.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.Map;

@RestController
@RequestMapping("/api/student/questions")
@PreAuthorize("hasRole('STUDENT')")
@Tag(name = "Student Question Bank", description = "APIs for students to access question bank for practice and learning")
public class StudentQuestionController {
    
    private static final Logger logger = LoggerFactory.getLogger(StudentQuestionController.class);
    
    @Autowired
    private StudentQuestionService questionService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Get accessible questions for student with filters
     */
    @Operation(
        summary = "Get accessible questions",
        description = "Returns questions the student can access based on their active subscriptions, with optional filters"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Questions retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<?> getAccessibleQuestions(
            @Parameter(description = "Filter by subject ID")
            @RequestParam(required = false) Long subjectId,
            @Parameter(description = "Filter by topic ID")
            @RequestParam(required = false) Long topicId,
            @Parameter(description = "Filter by module ID")
            @RequestParam(required = false) Long moduleId,
            @Parameter(description = "Filter by chapter ID")
            @RequestParam(required = false) Long chapterId,
            @Parameter(description = "Filter by question type (MULTIPLE_CHOICE, TRUE_FALSE, etc.)")
            @RequestParam(required = false) String questionType,
            @Parameter(description = "Filter by difficulty level (EASY, MEDIUM, HARD)")
            @RequestParam(required = false) String difficultyLevel,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "asc") String sortDir,
            Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            
            // Validate page size
            if (size > 100) size = 100;
            if (size < 1) size = 20;
            
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<StudentQuestionResponseDTO> questions = questionService.getAccessibleQuestions(
                    studentId, subjectId, topicId, moduleId, chapterId, 
                    questionType, difficultyLevel, pageable);
            
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            logger.error("Error fetching accessible questions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch questions"));
        }
    }
    
    /**
     * Get question by ID
     */
    @Operation(
        summary = "Get question by ID",
        description = "Retrieves a specific question if student has access (shows correct answers and explanation for learning)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Question retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "No access to this question"),
        @ApiResponse(responseCode = "404", description = "Question not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionById(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            StudentQuestionResponseDTO question = questionService.getQuestionById(id, studentId);
            return ResponseEntity.ok(question);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to get question {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting question {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get question"));
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

