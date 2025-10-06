package com.coaxial.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coaxial.dto.ClassRequest;
import com.coaxial.dto.ClassResponse;
import com.coaxial.entity.ClassEntity;
import com.coaxial.entity.Course;
import com.coaxial.entity.CourseType;
import com.coaxial.entity.User;
import com.coaxial.repository.ClassRepository;
import com.coaxial.repository.CourseRepository;

@Service
@Transactional
public class ClassService {
    
    @Autowired
    private ClassRepository classRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    // Create new class
    public ClassResponse createClass(ClassRequest classRequest) {
        // Validate course exists
        Course course = courseRepository.findById(classRequest.getCourseId())
            .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        
        // Check if class name already exists within the course
        if (classRepository.existsByNameAndCourse(classRequest.getName(), course)) {
            throw new IllegalArgumentException("Class name already exists in this course");
        }
        
        ClassEntity classEntity = new ClassEntity();
        classEntity.setName(classRequest.getName());
        classEntity.setDescription(classRequest.getDescription());
        classEntity.setCourse(course);
        classEntity.setDisplayOrder(classRequest.getDisplayOrder());
        classEntity.setIsActive(classRequest.getIsActive());
        classEntity.setCreatedBy(getCurrentUser());
        
        ClassEntity savedClass = classRepository.save(classEntity);
        return convertToResponse(savedClass);
    }
    
    // Helper method to get classes list using simplified JPA methods
    private List<ClassEntity> getAllClassesList(Long courseTypeId, Long courseId, Boolean isActive, String search) {
        // Handle courseTypeId filtering first
        if (courseTypeId != null) {
            CourseType courseType = courseRepository.findById(courseTypeId)
                .map(course -> course.getCourseType())
                .orElse(null);
            if (courseType == null) {
                return List.of(); // Return empty list if courseType not found
            }
            
            // Get all classes for courses of this type
            List<ClassEntity> classesByType = classRepository.findByCourseCourseType(courseType);
            
            // Apply additional filters
            if (courseId != null) {
                classesByType = classesByType.stream()
                    .filter(c -> c.getCourse().getId().equals(courseId))
                    .collect(Collectors.toList());
            }
            if (isActive != null) {
                classesByType = classesByType.stream()
                    .filter(c -> c.getIsActive().equals(isActive))
                    .collect(Collectors.toList());
            }
            if (search != null && !search.isEmpty()) {
                classesByType = classesByType.stream()
                    .filter(c -> c.getName().toLowerCase().contains(search.toLowerCase()) || 
                               (c.getDescription() != null && c.getDescription().toLowerCase().contains(search.toLowerCase())))
                    .collect(Collectors.toList());
            }
            return classesByType;
        }
        
        // Original filtering logic for when courseTypeId is not provided
        if (courseId != null && isActive != null && search != null && !search.isEmpty()) {
            // For complex filtering, we need to get all classes and filter manually
            List<ClassEntity> allClasses = classRepository.findByCourseIdAndIsActive(courseId, isActive);
            return allClasses.stream()
                .filter(c -> c.getName().toLowerCase().contains(search.toLowerCase()) || 
                           (c.getDescription() != null && c.getDescription().toLowerCase().contains(search.toLowerCase())))
                .collect(Collectors.toList());
        } else if (courseId != null && isActive != null) {
            return classRepository.findByCourseIdAndIsActive(courseId, isActive);
        } else if (courseId != null) {
            return classRepository.findByCourseId(courseId);
        } else if (isActive != null) {
            return classRepository.findByIsActive(isActive);
        } else if (search != null && !search.isEmpty()) {
            return classRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search);
        } else {
            return classRepository.findAll();
        }
    }
    
    // Get all classes with pagination and filters
    @Transactional(readOnly = true)
    public Page<ClassResponse> getClasses(Long courseTypeId, Long courseId, Boolean isActive, String search, Pageable pageable) {
        List<ClassEntity> allClasses = getAllClassesList(courseTypeId, courseId, isActive, search);
        
        // Manual pagination since we're using simple JPA methods
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allClasses.size());
        List<ClassEntity> pageContent = allClasses.subList(start, end);
        
        Page<ClassEntity> classes = new PageImpl<>(pageContent, pageable, allClasses.size());
        return classes.map(this::convertToResponse);
    }
    
    // Get all classes without pagination
    @Transactional(readOnly = true)
    public List<ClassResponse> getAllClasses(Long courseTypeId, Long courseId, Boolean isActive, String search) {
        List<ClassEntity> classes = getAllClassesList(courseTypeId, courseId, isActive, search);
        return classes.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    // Get class by ID
    @Transactional(readOnly = true)
    public ClassResponse getClassById(Long id) {
        ClassEntity classEntity = classRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Class not found with id: " + id));
        return convertToResponse(classEntity);
    }
    
    // Get classes by course
    @Transactional(readOnly = true)
    public List<ClassResponse> getClassesByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        
        List<ClassEntity> classes = classRepository.findByCourseOrderByDisplayOrderAsc(course);
        return classes.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    // Paginated method for standardized endpoints
    @Transactional(readOnly = true)
    public Page<ClassResponse> getClassesByCourse(Long courseTypeId, Long courseId, Boolean active, String search, Pageable pageable) {
        return getClasses(courseTypeId, courseId, active, search, pageable);
    }
    
    // Update class
    public ClassResponse updateClass(Long id, ClassRequest classRequest) {
        ClassEntity classEntity = classRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Class not found with id: " + id));
        
        // Validate course exists
        Course course = courseRepository.findById(classRequest.getCourseId())
            .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        
        // Check if class name is being changed and if it already exists
        if (!classEntity.getName().equals(classRequest.getName()) && 
            classRepository.existsByNameAndCourseAndIdNot(classRequest.getName(), course, id)) {
            throw new IllegalArgumentException("Class name already exists in this course");
        }
        
        // Update fields
        classEntity.setName(classRequest.getName());
        classEntity.setDescription(classRequest.getDescription());
        classEntity.setCourse(course);
        classEntity.setDisplayOrder(classRequest.getDisplayOrder());
        classEntity.setIsActive(classRequest.getIsActive());
        classEntity.setUpdatedBy(getCurrentUser());
        
        ClassEntity updatedClass = classRepository.save(classEntity);
        return convertToResponse(updatedClass);
    }
    
    // Delete class
    public void deleteClass(Long id) {
        if (!classRepository.existsById(id)) {
            throw new IllegalArgumentException("Class not found with id: " + id);
        }
        classRepository.deleteById(id);
    }
    
    // Convert ClassEntity to ClassResponse DTO
    private ClassResponse convertToResponse(ClassEntity classEntity) {
        ClassResponse response = new ClassResponse();
        response.setId(classEntity.getId());
        response.setName(classEntity.getName());
        response.setDescription(classEntity.getDescription());
        response.setDisplayOrder(classEntity.getDisplayOrder());
        response.setIsActive(classEntity.getIsActive());
        response.setCreatedAt(classEntity.getCreatedAt());
        response.setUpdatedAt(classEntity.getUpdatedAt());
        
        // Safely access course and courseType to avoid lazy loading issues
        try {
            Course course = classEntity.getCourse();
            if (course != null) {
                response.setCourseId(course.getId());
                response.setCourseName(course.getName());
                
                CourseType courseType = course.getCourseType();
                if (courseType != null) {
                    response.setCourseTypeName(courseType.getName());
                    if (courseType.getStructureType() != null) {
                        response.setStructureType(courseType.getStructureType().name());
                    }
                }
            }
        } catch (Exception e) {
            // If there's any issue accessing the course/courseType, just leave them null
            // This prevents ByteBuddyInterceptor errors
        }
        
        // Safely access user references to avoid lazy loading issues
        try {
            User createdBy = classEntity.getCreatedBy();
            if (createdBy != null) {
                response.setCreatedByName(createdBy.getFullName());
            }
            
            User updatedBy = classEntity.getUpdatedBy();
            if (updatedBy != null) {
                response.setUpdatedByName(updatedBy.getFullName());
            }
        } catch (Exception e) {
            // If there's any issue accessing user references, just leave them null
        }
        
        // Safely access classSubjects to avoid lazy loading issues
        try {
            response.setSubjectCount(classEntity.getClassSubjects().size());
        } catch (Exception e) {
            response.setSubjectCount(0);
        }
        
        return response;
    }
    
    // Get current authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
}

