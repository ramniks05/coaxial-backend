package com.coaxial.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.coaxial.dto.ExamSubjectRequestDTO;
import com.coaxial.dto.ExamSubjectResponseDTO;
import com.coaxial.service.ExamSubjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/master-data/exam-subjects")
@PreAuthorize("hasRole('ADMIN')")
public class ExamSubjectController {
    
    @Autowired
    private ExamSubjectService examSubjectService;
    
    // Add subjects to exam
    @PostMapping
    public ResponseEntity<List<ExamSubjectResponseDTO>> addSubjectsToExam(
            @Valid @RequestBody ExamSubjectRequestDTO request) {
        List<ExamSubjectResponseDTO> examSubjects = examSubjectService.addSubjectsToExam(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(examSubjects);
    }
    
    // Get all exam-subject relationships
    @GetMapping
    public ResponseEntity<List<ExamSubjectResponseDTO>> getAllExamSubjects() {
        List<ExamSubjectResponseDTO> subjects = examSubjectService.getAllExamSubjects();
        return ResponseEntity.ok(subjects);
    }
    
    // Public endpoint for testing
    @GetMapping("/public")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ExamSubjectResponseDTO>> getAllExamSubjectsPublic() {
        List<ExamSubjectResponseDTO> subjects = examSubjectService.getAllExamSubjects();
        return ResponseEntity.ok(subjects);
    }
    
    // Get subjects for exam
    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<ExamSubjectResponseDTO>> getSubjectsForExam(@PathVariable Long examId) {
        List<ExamSubjectResponseDTO> subjects = examSubjectService.getSubjectsForExam(examId);
        return ResponseEntity.ok(subjects);
    }
    
    // Get exams for subject
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<ExamSubjectResponseDTO>> getExamsForSubject(@PathVariable Long subjectId) {
        List<ExamSubjectResponseDTO> exams = examSubjectService.getExamsForSubject(subjectId);
        return ResponseEntity.ok(exams);
    }
    
    // Update exam-subject relationship
    @PutMapping("/{id}")
    public ResponseEntity<ExamSubjectResponseDTO> updateExamSubject(@PathVariable Long id, 
                                                                  @Valid @RequestBody ExamSubjectRequestDTO request) {
        ExamSubjectResponseDTO examSubject = examSubjectService.updateExamSubject(id, request);
        return ResponseEntity.ok(examSubject);
    }
    
    // Remove subject from exam
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeSubjectFromExam(@PathVariable Long id) {
        examSubjectService.removeSubjectFromExam(id);
        return ResponseEntity.noContent().build();
    }
    
    // Bulk remove subjects from exam
    @DeleteMapping("/exam/{examId}/subjects")
    public ResponseEntity<Void> removeSubjectsFromExam(@PathVariable Long examId, 
                                                      @RequestParam List<Long> subjectIds) {
        examSubjectService.removeSubjectsFromExam(examId, subjectIds);
        return ResponseEntity.noContent().build();
    }
}
