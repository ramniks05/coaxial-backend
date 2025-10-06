package com.coaxial.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.Course;
import com.coaxial.entity.CourseType;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Find by course type
    List<Course> findByCourseType(CourseType courseType);

    // Find by course type and active status
    List<Course> findByCourseTypeAndIsActive(CourseType courseType, Boolean isActive);
    
    // Find active courses
    List<Course> findByIsActive(Boolean isActive);
    
    // Search courses by name - Simple JPA method
    List<Course> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
    
    // Find courses with filters - Simple JPA method
    List<Course> findByCourseTypeAndIsActiveAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        CourseType courseType, Boolean isActive, String name, String description);
    
    // Count courses by course type
    long countByCourseType(CourseType courseType);
    
    // Count active courses
    long countByIsActive(Boolean isActive);
    
    // Find courses by display order
    List<Course> findByCourseTypeOrderByDisplayOrderAsc(CourseType courseType);
    
    // Check if course name exists within course type
    boolean existsByNameAndCourseType(String name, CourseType courseType);
    
    // Check if course name exists within course type (excluding specific ID)
    boolean existsByNameAndCourseTypeAndIdNot(String name, CourseType courseType, Long id);
    
    // Paginated methods
    Page<Course> findByCourseType(CourseType courseType, Pageable pageable);
    Page<Course> findByCourseTypeAndIsActive(CourseType courseType, Boolean isActive, Pageable pageable);
    Page<Course> findByCourseTypeAndNameContainingIgnoreCase(CourseType courseType, String name, Pageable pageable);
    Page<Course> findByCourseTypeAndIsActiveAndNameContainingIgnoreCase(CourseType courseType, Boolean isActive, String name, Pageable pageable);
}