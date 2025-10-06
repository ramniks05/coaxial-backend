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

import com.coaxial.dto.CourseRequest;
import com.coaxial.dto.CourseResponse;
import com.coaxial.service.CourseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/master-data/courses")
@PreAuthorize("hasRole('ADMIN')")
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    // Get all courses with filters and pagination
    @GetMapping
    public ResponseEntity<Page<CourseResponse>> getCourses(
            @RequestParam(required = false) Long courseTypeId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CourseResponse> courses = courseService.getCourses(courseTypeId, isActive, search, pageable);
        return ResponseEntity.ok(courses);
    }
    
    // Get all courses without pagination
    @GetMapping("/all")
    public ResponseEntity<List<CourseResponse>> getAllCourses(
            @RequestParam(required = false) Long courseTypeId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String search) {
        
        List<CourseResponse> courses = courseService.getAllCourses(courseTypeId, isActive, search);
        return ResponseEntity.ok(courses);
    }
    
    // Get course by ID
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        CourseResponse course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }
    
    // Standardized endpoint for courses by course type with pagination
    @GetMapping("/by-course-type")
    public ResponseEntity<Page<CourseResponse>> getCoursesByCourseType(
            @RequestParam Long courseTypeId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CourseResponse> courses = courseService.getCoursesByCourseType(courseTypeId, active, search, pageable);
        return ResponseEntity.ok(courses);
    }
    
    // Create new course
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest courseRequest) {
        CourseResponse course = courseService.createCourse(courseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }
    
    // Update course
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Long id, 
                                                      @Valid @RequestBody CourseRequest courseRequest) {
        CourseResponse course = courseService.updateCourse(id, courseRequest);
        return ResponseEntity.ok(course);
    }
    
    // Delete course
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Course deleted successfully");
        return ResponseEntity.ok(response);
    }
}
