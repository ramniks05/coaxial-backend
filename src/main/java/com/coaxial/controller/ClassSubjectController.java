package com.coaxial.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RestController;

import com.coaxial.dto.ClassSubjectRequest;
import com.coaxial.dto.ClassSubjectResponse;
import com.coaxial.service.ClassSubjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/master-data/class-subjects")
@PreAuthorize("hasRole('ADMIN')")
public class ClassSubjectController {
    
    @Autowired
    private ClassSubjectService classSubjectService;
    
    // Add subject to class
    @PostMapping
    public ResponseEntity<ClassSubjectResponse> addSubjectToClass(@Valid @RequestBody ClassSubjectRequest request) {
        ClassSubjectResponse classSubject = classSubjectService.addSubjectToClass(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(classSubject);
    }
    
    // Get all class-subject relationships
    @GetMapping
    public ResponseEntity<List<ClassSubjectResponse>> getAllClassSubjects() {
        List<ClassSubjectResponse> subjects = classSubjectService.getAllClassSubjects();
        return ResponseEntity.ok(subjects);
    }
    
    // Public endpoint for testing
    @GetMapping("/public")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ClassSubjectResponse>> getAllClassSubjectsPublic() {
        List<ClassSubjectResponse> subjects = classSubjectService.getAllClassSubjects();
        return ResponseEntity.ok(subjects);
    }
    
    // Get subjects for class
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<ClassSubjectResponse>> getSubjectsForClass(@PathVariable Long classId) {
        List<ClassSubjectResponse> subjects = classSubjectService.getSubjectsForClass(classId);
        return ResponseEntity.ok(subjects);
    }
    
    // Get classes for subject
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<ClassSubjectResponse>> getClassesForSubject(@PathVariable Long subjectId) {
        List<ClassSubjectResponse> classes = classSubjectService.getClassesForSubject(subjectId);
        return ResponseEntity.ok(classes);
    }
    
    // Update class-subject relationship
    @PutMapping("/{id}")
    public ResponseEntity<ClassSubjectResponse> updateClassSubject(@PathVariable Long id, 
                                                                  @Valid @RequestBody ClassSubjectRequest request) {
        ClassSubjectResponse classSubject = classSubjectService.updateClassSubject(id, request);
        return ResponseEntity.ok(classSubject);
    }
    
    // Remove subject from class
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> removeSubjectFromClass(@PathVariable Long id) {
        classSubjectService.removeSubjectFromClass(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Subject removed from class successfully");
        return ResponseEntity.ok(response);
    }
}

