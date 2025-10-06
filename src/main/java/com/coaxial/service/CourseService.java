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

import com.coaxial.dto.CourseRequest;
import com.coaxial.dto.CourseResponse;
import com.coaxial.entity.Course;
import com.coaxial.entity.CourseType;
import com.coaxial.entity.User;
import com.coaxial.repository.CourseRepository;
import com.coaxial.repository.CourseTypeRepository;

@Service
@Transactional
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CourseTypeRepository courseTypeRepository;
    
    // Create new course
    public CourseResponse createCourse(CourseRequest courseRequest) {
        // Validate course type exists
        CourseType courseType = courseTypeRepository.findById(courseRequest.getCourseType().getId())
            .orElseThrow(() -> (RuntimeException) new IllegalArgumentException("Course type not found"));
        
        // Check if course name already exists within the course type
        if (courseRepository.existsByNameAndCourseType(courseRequest.getName(), courseType)) {
            throw new IllegalArgumentException("Course name already exists in this course type");
        }
        
        Course course = new Course();
        course.setName(courseRequest.getName());
        course.setDescription(courseRequest.getDescription());
        course.setCourseType(courseType);
        course.setDisplayOrder(courseRequest.getDisplayOrder());
        course.setIsActive(courseRequest.getIsActive());
        course.setCreatedBy(getCurrentUser());
        
        Course savedCourse = courseRepository.save(course);
        return convertToResponse(savedCourse);
    }
    
    // Helper method to get courses list using simplified JPA methods
    private List<Course> getAllCoursesList(Long courseTypeId, Boolean isActive, String search) {
        if (courseTypeId != null && isActive != null && search != null && !search.isEmpty()) {
            CourseType courseType = courseTypeRepository.findById(courseTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Course type not found"));
            // First get courses by course type and active status, then filter by search
            List<Course> courses = courseRepository.findByCourseTypeAndIsActive(courseType, isActive);
            return courses.stream()
                    .filter(course -> course.getName().toLowerCase().contains(search.toLowerCase()) ||
                                     course.getDescription().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        } else if (courseTypeId != null && isActive != null) {
            CourseType courseType = courseTypeRepository.findById(courseTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Course type not found"));
            return courseRepository.findByCourseTypeAndIsActive(courseType, isActive);
        } else if (courseTypeId != null) {
            CourseType courseType = courseTypeRepository.findById(courseTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Course type not found"));
            return courseRepository.findByCourseType(courseType);
        } else if (isActive != null) {
            return courseRepository.findByIsActive(isActive);
        } else if (search != null && !search.isEmpty()) {
            return courseRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search);
        } else {
            return courseRepository.findAll();
        }
    }
    
    // Get all courses with pagination and filters
    @Transactional(readOnly = true)
    public Page<CourseResponse> getCourses(Long courseTypeId, Boolean isActive, String search, Pageable pageable) {
        List<Course> allCourses = getAllCoursesList(courseTypeId, isActive, search);
        
        // Manual pagination since we're using simple JPA methods
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allCourses.size());
        List<Course> pageContent = allCourses.subList(start, end);
        
        Page<Course> courses = new PageImpl<>(pageContent, pageable, allCourses.size());
        return courses.map(this::convertToResponse);
    }
    
    // Get all courses without pagination
    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses(Long courseTypeId, Boolean isActive, String search) {
        List<Course> courses;
        
        if (courseTypeId != null && isActive != null && search != null && !search.isEmpty()) {
            CourseType courseType = courseTypeRepository.findById(courseTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Course type not found"));
            // First get courses by course type and active status, then filter by search
            List<Course> filteredCourses = courseRepository.findByCourseTypeAndIsActive(courseType, isActive);
            courses = filteredCourses.stream()
                    .filter(course -> course.getName().toLowerCase().contains(search.toLowerCase()) ||
                                     course.getDescription().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        } else if (courseTypeId != null && isActive != null) {
            CourseType courseType = courseTypeRepository.findById(courseTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Course type not found"));
            courses = courseRepository.findByCourseTypeAndIsActive(courseType, isActive);
        } else if (courseTypeId != null) {
            CourseType courseType = courseTypeRepository.findById(courseTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Course type not found"));
            courses = courseRepository.findByCourseType(courseType);
        } else if (isActive != null) {
            courses = courseRepository.findByIsActive(isActive);
        } else if (search != null && !search.isEmpty()) {
            courses = courseRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search);
        } else {
            courses = courseRepository.findAll();
        }
        
        return courses.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    // Get course by ID
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> (RuntimeException) new IllegalArgumentException("Course not found with id: " + id));
        return convertToResponse(course);
    }
    
    // Update course
    public CourseResponse updateCourse(Long id, CourseRequest courseRequest) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> (RuntimeException) new IllegalArgumentException("Course not found with id: " + id));
        
        // Validate course type exists
        CourseType courseType = courseTypeRepository.findById(courseRequest.getCourseType().getId())
            .orElseThrow(() -> (RuntimeException) new IllegalArgumentException("Course type not found"));
        
        // Check if course name is being changed and if it already exists
        if (!course.getName().equals(courseRequest.getName()) && 
            courseRepository.existsByNameAndCourseTypeAndIdNot(courseRequest.getName(), courseType, id)) {
            throw new IllegalArgumentException("Course name already exists in this course type");
        }
        
        // Update fields
        course.setName(courseRequest.getName());
        course.setDescription(courseRequest.getDescription());
        course.setCourseType(courseType);
        course.setDisplayOrder(courseRequest.getDisplayOrder());
        course.setIsActive(courseRequest.getIsActive());
        course.setUpdatedBy(getCurrentUser());
        
        Course updatedCourse = courseRepository.save(course);
        return convertToResponse(updatedCourse);
    }
    
    // Delete course
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new IllegalArgumentException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }
    
    // Get courses by course type
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByCourseType(Long courseTypeId) {
        CourseType courseType = courseTypeRepository.findById(courseTypeId)
            .orElseThrow(() -> (RuntimeException) new IllegalArgumentException("Course type not found"));
        
        List<Course> courses = courseRepository.findByCourseTypeOrderByDisplayOrderAsc(courseType);
        return courses.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    // Paginated method for standardized endpoints
    @Transactional(readOnly = true)
    public Page<CourseResponse> getCoursesByCourseType(Long courseTypeId, Boolean active, String search, Pageable pageable) {
        CourseType courseType = courseTypeRepository.findById(courseTypeId)
            .orElseThrow(() -> new IllegalArgumentException("Course type not found"));
        
        Page<Course> courses;
        if (active != null && search != null && !search.isEmpty()) {
            courses = courseRepository.findByCourseTypeAndIsActiveAndNameContainingIgnoreCase(
                courseType, active, search, pageable);
        } else if (active != null) {
            courses = courseRepository.findByCourseTypeAndIsActive(courseType, active, pageable);
        } else if (search != null && !search.isEmpty()) {
            courses = courseRepository.findByCourseTypeAndNameContainingIgnoreCase(courseType, search, pageable);
        } else {
            courses = courseRepository.findByCourseType(courseType, pageable);
        }
        
        return courses.map(this::convertToResponse);
    }
    
    // Convert Course entity to CourseResponse DTO
    private CourseResponse convertToResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setName(course.getName());
        response.setDescription(course.getDescription());
        response.setDisplayOrder(course.getDisplayOrder());
        response.setIsActive(course.getIsActive());
        response.setCreatedAt(course.getCreatedAt());
        response.setUpdatedAt(course.getUpdatedAt());
        
        // Safely access courseType to get name and id without causing proxy serialization issues
        try {
            CourseType courseType = course.getCourseType();
            if (courseType != null) {
                response.setCourseTypeId(courseType.getId());
                response.setCourseTypeName(courseType.getName());
                if (courseType.getStructureType() != null) {
                    response.setStructureType(courseType.getStructureType().name());
                }
            }
        } catch (Exception e) {
            // If there's any issue accessing the courseType, just leave it null
            // This prevents ByteBuddyInterceptor errors
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
