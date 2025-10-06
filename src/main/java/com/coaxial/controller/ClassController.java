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

import com.coaxial.dto.ClassRequest;
import com.coaxial.dto.ClassResponse;
import com.coaxial.service.ClassService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/master-data/classes")
@PreAuthorize("hasRole('ADMIN')")
public class ClassController {
    
    @Autowired
    private ClassService classService;
    
    // Get all classes with filters and pagination
    @GetMapping
    public ResponseEntity<Page<ClassResponse>> getClasses(
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
        
        Page<ClassResponse> classes = classService.getClasses(courseTypeId, courseId, isActive, search, pageable);
        return ResponseEntity.ok(classes);
    }
    
    // Get all classes without pagination
    @GetMapping("/all")
    public ResponseEntity<List<ClassResponse>> getAllClasses(
            @RequestParam(required = false) Long courseTypeId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String search) {
        
        List<ClassResponse> classes = classService.getAllClasses(courseTypeId, courseId, isActive, search);
        return ResponseEntity.ok(classes);
    }
    
    // Get class by ID
    @GetMapping("/{id}")
    public ResponseEntity<ClassResponse> getClassById(@PathVariable Long id) {
        ClassResponse classEntity = classService.getClassById(id);
        return ResponseEntity.ok(classEntity);
    }
    
    // Standardized endpoint for classes by course with pagination
    @GetMapping("/by-course")
    public ResponseEntity<Page<ClassResponse>> getClassesByCourse(
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
        
        Page<ClassResponse> classes = classService.getClassesByCourse(courseTypeId, courseId, active, search, pageable);
        return ResponseEntity.ok(classes);
    }
    
    // Create new class
    @PostMapping
    public ResponseEntity<ClassResponse> createClass(@Valid @RequestBody ClassRequest classRequest) {
        ClassResponse classEntity = classService.createClass(classRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(classEntity);
    }
    
    // Update class
    @PutMapping("/{id}")
    public ResponseEntity<ClassResponse> updateClass(@PathVariable Long id, 
                                                    @Valid @RequestBody ClassRequest classRequest) {
        ClassResponse classEntity = classService.updateClass(id, classRequest);
        return ResponseEntity.ok(classEntity);
    }
    
    // Delete class
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Class deleted successfully");
        return ResponseEntity.ok(response);
    }
}

