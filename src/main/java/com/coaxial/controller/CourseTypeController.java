package com.coaxial.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;

import com.coaxial.entity.CourseType;
import com.coaxial.service.CourseTypeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/master-data/course-types")
public class CourseTypeController {
    
    @Autowired
    private CourseTypeService courseTypeService;
    
    @GetMapping
    public ResponseEntity<Page<CourseType>> getCourseTypes(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CourseType> courseTypes = courseTypeService.getCourseTypes(active, search, pageable);
        return ResponseEntity.ok(courseTypes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CourseType> getCourseTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(courseTypeService.getCourseTypeById(id));
    }
    
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CourseType> createCourseType(@Valid @RequestBody CourseType courseType) {
        return ResponseEntity.ok(courseTypeService.createCourseType(courseType));
    }
    
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CourseType> updateCourseType(
            @PathVariable Long id,
            @Valid @RequestBody CourseType courseType) {
        return ResponseEntity.ok(courseTypeService.updateCourseType(id, courseType));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourseType(@PathVariable Long id) {
        courseTypeService.deleteCourseType(id);
        return ResponseEntity.ok().build();
    }
}
