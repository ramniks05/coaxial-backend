package com.coaxial.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coaxial.dto.ExamRequest;
import com.coaxial.dto.ExamResponse;
import com.coaxial.service.ExamService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/master-data/exams")
@PreAuthorize("hasRole('ADMIN')")
public class ExamController {
    
    @Autowired
    private ExamService examService;
    
    // Get all exams with filters and pagination
    @GetMapping
    public ResponseEntity<Page<ExamResponse>> getExams(
            @RequestParam(required = false) Long courseTypeId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ExamResponse> exams = examService.getExams(courseTypeId, courseId, isActive, search, pageable);
        return ResponseEntity.ok(exams);
    }
    
    // Get all exams without pagination
    @GetMapping("/all")
    public ResponseEntity<List<ExamResponse>> getAllExams(
            @RequestParam(required = false) Long courseTypeId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String search) {
        
        List<ExamResponse> exams = examService.getAllExams(courseTypeId, courseId, isActive, search);
        return ResponseEntity.ok(exams);
    }
    
    // Get exam by ID
    @GetMapping("/{id}")
    public ResponseEntity<ExamResponse> getExamById(@PathVariable Long id) {
        ExamResponse exam = examService.getExamById(id);
        return ResponseEntity.ok(exam);
    }
    
    // Standardized endpoint for exams by course with pagination
    @GetMapping("/by-course")
    public ResponseEntity<Page<ExamResponse>> getExamsByCourse(
            @RequestParam Long courseTypeId,
            @RequestParam Long courseId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ExamResponse> exams = examService.getExamsByCourse(courseTypeId, courseId, active, search, pageable);
        return ResponseEntity.ok(exams);
    }
    
    // Create new exam
    @PostMapping
    public ResponseEntity<ExamResponse> createExam(@Valid @RequestBody ExamRequest examRequest) {
        ExamResponse exam = examService.createExam(examRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(exam);
    }
    
    // Update exam
    @PutMapping("/{id}")
    public ResponseEntity<ExamResponse> updateExam(@PathVariable Long id, 
                                                  @Valid @RequestBody ExamRequest examRequest) {
        ExamResponse exam = examService.updateExam(id, examRequest);
        return ResponseEntity.ok(exam);
    }
    
    // Delete exam
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Exam deleted successfully");
        return ResponseEntity.ok(response);
    }
}
