package com.coaxial.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coaxial.dto.QuestionFilterRequestDTO;
import com.coaxial.dto.QuestionRequestDTO;
import com.coaxial.dto.QuestionResponseDTO;
import com.coaxial.service.QuestionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/master-data/questions")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://127.0.0.1:3000", "http://127.0.0.1:3001"}, allowCredentials = "true")
public class QuestionController {
    
    private final QuestionService questionService;
    
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }
    
    // Get all questions
    @GetMapping
    public ResponseEntity<List<QuestionResponseDTO>> getAllQuestions() {
        List<QuestionResponseDTO> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }
    
    // Get questions by chapter ID
    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsByChapterId(@PathVariable Long chapterId) {
        List<QuestionResponseDTO> questions = questionService.getQuestionsByChapterId(chapterId);
        return ResponseEntity.ok(questions);
    }
    
    // Get questions by module ID
    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsByModuleId(@PathVariable Long moduleId) {
        List<QuestionResponseDTO> questions = questionService.getQuestionsByModuleId(moduleId);
        return ResponseEntity.ok(questions);
    }
    
    // Get questions by topic ID
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsByTopicId(@PathVariable Long topicId) {
        List<QuestionResponseDTO> questions = questionService.getQuestionsByTopicId(topicId);
        return ResponseEntity.ok(questions);
    }
    
    // Get questions by subject ID
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsBySubjectId(@PathVariable Long subjectId) {
        List<QuestionResponseDTO> questions = questionService.getQuestionsBySubjectId(subjectId);
        return ResponseEntity.ok(questions);
    }
    
    // Get questions with filters
    @GetMapping("/filter")
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsWithFilters(
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String questionType,
            @RequestParam(required = false) String difficultyLevel,
            @RequestParam(required = false) Long chapterId,
            @RequestParam(required = false) Long moduleId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) Long subjectId) {
        
        List<QuestionResponseDTO> questions = questionService.getQuestionsWithFilters(
                isActive, questionType, difficultyLevel, chapterId, moduleId, topicId, subjectId);
        return ResponseEntity.ok(questions);
    }
    
    // Get questions with pagination
    @GetMapping("/paginated")
    public ResponseEntity<Page<QuestionResponseDTO>> getQuestionsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<QuestionResponseDTO> questions = questionService.getQuestionsPaginated(pageable);
        return ResponseEntity.ok(questions);
    }
    
    // Get question by ID
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDTO> getQuestionById(@PathVariable Long id) {
        Optional<QuestionResponseDTO> question = questionService.getQuestionById(id);
        return question.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    // Create new question
    @PostMapping
    public ResponseEntity<?> createQuestion(@Valid @RequestBody QuestionRequestDTO questionRequestDTO) {
        try {
            QuestionResponseDTO createdQuestion = questionService.createQuestion(questionRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdQuestion);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // Update question
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long id, 
                                                             @Valid @RequestBody QuestionRequestDTO questionRequestDTO) {
        try {
            QuestionResponseDTO updatedQuestion = questionService.updateQuestion(id, questionRequestDTO);
            return ResponseEntity.ok(updatedQuestion);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // Delete question
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteQuestion(@PathVariable Long id) {
        try {
            questionService.deleteQuestion(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Question deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Check if question exists
    @GetMapping("/{id}/exists")
    public ResponseEntity<Map<String, Boolean>> questionExists(@PathVariable Long id) {
        boolean exists = questionService.existsById(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
    
    // Test endpoint for debugging
    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        try {
            System.out.println("Testing database connection...");
            List<QuestionResponseDTO> questions = questionService.getAllQuestions();
            System.out.println("Found " + questions.size() + " questions in database");
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "OK");
            response.put("totalQuestions", questions.size());
            response.put("message", "Database connection successful");
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Test endpoint error: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Database connection failed: " + e.getMessage());
            error.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // Get question statistics
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getQuestionStatistics() {
        List<QuestionResponseDTO> allQuestions = questionService.getAllQuestions();
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalQuestions", allQuestions.size());
        
        // Count by question type
        Map<String, Long> questionTypeCount = allQuestions.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        QuestionResponseDTO::getQuestionType,
                        java.util.stream.Collectors.counting()));
        statistics.put("questionTypeCount", questionTypeCount);
        
        // Count by difficulty level
        Map<String, Long> difficultyCount = allQuestions.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        QuestionResponseDTO::getDifficultyLevel,
                        java.util.stream.Collectors.counting()));
        statistics.put("difficultyCount", difficultyCount);
        
        // Count by active status
        Map<String, Long> activeCount = allQuestions.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        q -> q.getIsActive() ? "Active" : "Inactive",
                        java.util.stream.Collectors.counting()));
        statistics.put("activeCount", activeCount);
        
        return ResponseEntity.ok(statistics);
    }
    
    // Enhanced filtering endpoints
    
    // Advanced filtering with all criteria
    @PostMapping("/filter")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<?> getQuestionsWithEnhancedFilters(
            @Valid @RequestBody QuestionFilterRequestDTO filter) {
        try {
            System.out.println("Received filter request: " + filter.toString());
            
            // Validate marks range
            if (filter.getMinMarks() != null && filter.getMaxMarks() != null 
                && filter.getMinMarks() > filter.getMaxMarks()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Min marks cannot be greater than max marks"));
            }
            
            // Validate pagination parameters
            if (filter.getPage() == null) filter.setPage(0);
            if (filter.getSize() == null) filter.setSize(20);
            if (filter.getSize() > 100) filter.setSize(100);
            
            Page<QuestionResponseDTO> questions = questionService.getQuestionsWithEnhancedFilters(filter);
            System.out.println("Successfully returned " + questions.getContent().size() + " questions");
            
            return ResponseEntity.ok(questions);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Unexpected error in filter endpoint: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "An error occurred while filtering questions: " + e.getMessage());
            error.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // Filter by exam suitability
    @GetMapping("/by-exam-suitability")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsByExamSuitability(
            @RequestParam List<Long> examIds,
            @RequestParam(required = false) List<String> suitabilityLevels) {
        try {
            List<QuestionResponseDTO> questions = questionService.getQuestionsByExamSuitability(examIds, suitabilityLevels);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Filter previously asked questions
    @GetMapping("/previously-asked")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<QuestionResponseDTO>> getPreviouslyAskedQuestions(
            @RequestParam List<Long> examIds,
            @RequestParam(required = false) List<Integer> appearedYears,
            @RequestParam(required = false) List<String> examSessions,
            @RequestParam(required = false) Integer minMarksInExam,
            @RequestParam(required = false) Integer maxMarksInExam,
            @RequestParam(required = false) List<String> questionNumbers,
            @RequestParam(required = false) List<String> examDifficulties) {
        try {
            List<QuestionResponseDTO> questions = questionService.getPreviouslyAskedQuestions(
                examIds, appearedYears, examSessions, minMarksInExam, 
                maxMarksInExam, questionNumbers, examDifficulties);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Filter by academic level
    @GetMapping("/by-academic-level")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsByAcademicLevel(
            @RequestParam Long courseTypeId,
            @RequestParam Long relationshipId) {
        try {
            List<QuestionResponseDTO> questions = questionService.getQuestionsByAcademicLevel(courseTypeId, relationshipId);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Filter by hierarchy
    @GetMapping("/by-hierarchy")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsByHierarchy(
            @RequestParam(required = false) Long courseTypeId,
            @RequestParam(required = false) Long relationshipId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) Long moduleId,
            @RequestParam(required = false) Long chapterId) {
        try {
            List<QuestionResponseDTO> questions = questionService.getQuestionsByHierarchy(
                courseTypeId, relationshipId, subjectId, topicId, moduleId, chapterId);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Legacy enhanced filtering endpoint with query parameters
    @GetMapping("/filter-advanced")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<?> getQuestionsWithAdvancedFilters(
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String questionType,
            @RequestParam(required = false) String difficultyLevel,
            @RequestParam(required = false) Integer minMarks,
            @RequestParam(required = false) Integer maxMarks,
            @RequestParam(required = false) Long courseTypeId,
            @RequestParam(required = false) Long relationshipId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) Long moduleId,
            @RequestParam(required = false) Long chapterId,
            @RequestParam(required = false) List<Long> examIds,
            @RequestParam(required = false) List<String> suitabilityLevels,
            @RequestParam(required = false) List<Integer> appearedYears,
            @RequestParam(required = false) String questionTextSearch,
            @RequestParam(required = false) String explanationSearch) {
        try {
            QuestionFilterRequestDTO filter = new QuestionFilterRequestDTO();
            filter.setIsActive(isActive);
            filter.setQuestionType(questionType);
            filter.setDifficultyLevel(difficultyLevel);
            filter.setMinMarks(minMarks);
            filter.setMaxMarks(maxMarks);
            filter.setCourseTypeId(courseTypeId);
            filter.setRelationshipId(relationshipId);
            filter.setSubjectId(subjectId);
            filter.setTopicId(topicId);
            filter.setModuleId(moduleId);
            filter.setChapterId(chapterId);
            filter.setExamIds(examIds);
            filter.setSuitabilityLevels(suitabilityLevels);
            filter.setAppearedYears(appearedYears);
            filter.setQuestionTextSearch(questionTextSearch);
            filter.setExplanationSearch(explanationSearch);
            
            List<QuestionResponseDTO> questions = questionService.getQuestionsWithEnhancedFilters(filter).getContent();
            return ResponseEntity.ok(questions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An error occurred while filtering questions"));
        }
    }
}
