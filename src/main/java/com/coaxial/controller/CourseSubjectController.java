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

import com.coaxial.dto.CourseSubjectRequestDTO;
import com.coaxial.dto.CourseSubjectResponseDTO;
import com.coaxial.service.CourseSubjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/master-data/course-subjects")
@PreAuthorize("hasRole('ADMIN')")
public class CourseSubjectController {
    
    @Autowired
    private CourseSubjectService courseSubjectService;
    
    // Add subjects to course
    @PostMapping
    public ResponseEntity<List<CourseSubjectResponseDTO>> addSubjectsToCourse(
            @Valid @RequestBody CourseSubjectRequestDTO request) {
        List<CourseSubjectResponseDTO> courseSubjects = courseSubjectService.addSubjectsToCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(courseSubjects);
    }
    
    // Get all course-subject relationships
    @GetMapping
    public ResponseEntity<List<CourseSubjectResponseDTO>> getAllCourseSubjects() {
        List<CourseSubjectResponseDTO> subjects = courseSubjectService.getAllCourseSubjects();
        return ResponseEntity.ok(subjects);
    }
    
    // Public endpoint for testing
    @GetMapping("/public")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<CourseSubjectResponseDTO>> getAllCourseSubjectsPublic() {
        List<CourseSubjectResponseDTO> subjects = courseSubjectService.getAllCourseSubjects();
        return ResponseEntity.ok(subjects);
    }
    
    // Get subjects for course
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CourseSubjectResponseDTO>> getSubjectsForCourse(@PathVariable Long courseId) {
        List<CourseSubjectResponseDTO> subjects = courseSubjectService.getSubjectsForCourse(courseId);
        return ResponseEntity.ok(subjects);
    }
    
    // Get courses for subject
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<CourseSubjectResponseDTO>> getCoursesForSubject(@PathVariable Long subjectId) {
        List<CourseSubjectResponseDTO> courses = courseSubjectService.getCoursesForSubject(subjectId);
        return ResponseEntity.ok(courses);
    }
    
    // Update course-subject relationship
    @PutMapping("/{id}")
    public ResponseEntity<CourseSubjectResponseDTO> updateCourseSubject(@PathVariable Long id, 
                                                                      @Valid @RequestBody CourseSubjectRequestDTO request) {
        CourseSubjectResponseDTO courseSubject = courseSubjectService.updateCourseSubject(id, request);
        return ResponseEntity.ok(courseSubject);
    }
    
    // Remove subject from course
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeSubjectFromCourse(@PathVariable Long id) {
        courseSubjectService.removeSubjectFromCourse(id);
        return ResponseEntity.noContent().build();
    }
    
    // Bulk remove subjects from course
    @DeleteMapping("/course/{courseId}/subjects")
    public ResponseEntity<Void> removeSubjectsFromCourse(@PathVariable Long courseId, 
                                                        @RequestParam List<Long> subjectIds) {
        courseSubjectService.removeSubjectsFromCourse(courseId, subjectIds);
        return ResponseEntity.noContent().build();
    }
}
