package com.coaxial.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.coaxial.dto.TestQuestionRequestDTO;
import com.coaxial.dto.TestQuestionSummaryDTO;
import com.coaxial.dto.TestRequestDTO;
import com.coaxial.dto.TestResponseDTO;
import com.coaxial.enums.TestCreationMode;
import com.coaxial.enums.TestLevel;
import com.coaxial.service.TestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/master-data/tests")
@CrossOrigin(origins = "*")
@Tag(name = "Test Management", description = "APIs for managing tests in dual-mode system (EXAM_BASED and CONTENT_BASED)")
public class TestController {

    @Autowired
    private TestService testService;

    @Operation(
        summary = "Create a new test",
        description = "Create test in EXAM_BASED mode (general exam practice) or CONTENT_BASED mode (linked to course content hierarchy)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Test created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or validation failed"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestResponseDTO> createTest(@Valid @RequestBody TestRequestDTO request) {
        TestResponseDTO created = testService.createTest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
        summary = "Get all tests with optional filters",
        description = "Retrieve tests with optional filtering by creation mode, test level, and content hierarchy"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved tests"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<List<TestResponseDTO>> getAllTests(
            @Parameter(description = "Filter by test creation mode (EXAM_BASED or CONTENT_BASED)")
            @RequestParam(required = false) TestCreationMode testCreationMode,
            @Parameter(description = "Filter by test level (CLASS_EXAM, SUBJECT, MODULE, CHAPTER)")
            @RequestParam(required = false) TestLevel testLevel,
            @Parameter(description = "Filter by master exam ID")
            @RequestParam(required = false) Long masterExamId,
            @Parameter(description = "Filter by course type ID (1=Academic, 2=Competitive, 3=Professional)")
            @RequestParam(required = false) Long courseTypeId,
            @Parameter(description = "Filter by course ID")
            @RequestParam(required = false) Long courseId,
            @Parameter(description = "Filter by class ID")
            @RequestParam(required = false) Long classId,
            @Parameter(description = "Filter by exam ID")
            @RequestParam(required = false) Long examId,
            @Parameter(description = "Filter by subject linkage ID")
            @RequestParam(required = false) Long subjectLinkageId,
            @Parameter(description = "Filter by topic ID")
            @RequestParam(required = false) Long topicId,
            @Parameter(description = "Filter by module ID")
            @RequestParam(required = false) Long moduleId,
            @Parameter(description = "Filter by chapter ID")
            @RequestParam(required = false) Long chapterId,
            @Parameter(description = "Filter by published status")
            @RequestParam(required = false) Boolean isPublished) {
        return ResponseEntity.ok(testService.getTestsWithFilters(
            testCreationMode, testLevel, masterExamId, courseTypeId, courseId,
            classId, examId, subjectLinkageId, topicId, moduleId, chapterId, isPublished));
    }

    @Operation(
        summary = "Get test by ID",
        description = "Retrieve a single test with all details and resolved names"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved test"),
        @ApiResponse(responseCode = "404", description = "Test not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<TestResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(testService.getByIdAsDTO(id));
    }

    @Operation(
        summary = "Update an existing test",
        description = "Update test in EXAM_BASED or CONTENT_BASED mode with full validation"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Test updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or validation failed"),
        @ApiResponse(responseCode = "404", description = "Test not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestResponseDTO> update(@PathVariable Long id, @Valid @RequestBody TestRequestDTO request) {
        return ResponseEntity.ok(testService.updateTest(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        testService.deleteTest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/questions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TestQuestionSummaryDTO>> getQuestions(@PathVariable Long id) {
        return ResponseEntity.ok(testService.getQuestions(id));
    }

    @PostMapping("/{id}/questions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestQuestionSummaryDTO> addQuestion(
            @PathVariable Long id,
            @Valid @RequestBody TestQuestionRequestDTO question) {
        TestQuestionSummaryDTO added = testService.addQuestion(id, question);
        return ResponseEntity.status(HttpStatus.CREATED).body(added);
    }

    @PutMapping("/{id}/questions/{qId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TestQuestionSummaryDTO> updateQuestion(
            @PathVariable Long id,
            @PathVariable Long qId,
            @Valid @RequestBody TestQuestionRequestDTO question) {
        return ResponseEntity.ok(testService.updateQuestion(id, qId, question));
    }

    @DeleteMapping("/{id}/questions/{qId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long id,
            @PathVariable Long qId) {
        testService.removeQuestion(id, qId);
        return ResponseEntity.noContent().build();
    }
}


