package com.coaxial.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.ClassEntity;
import com.coaxial.entity.Course;
import com.coaxial.entity.CourseType;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    
    // Find by course
    List<ClassEntity> findByCourse(Course course);
    List<ClassEntity> findByCourseAndIsActive(Course course, Boolean isActive);
    List<ClassEntity> findByCourseOrderByDisplayOrderAsc(Course course);
    List<ClassEntity> findByCourseId(Long courseId);
    List<ClassEntity> findByCourseIdAndIsActive(Long courseId, Boolean isActive);
    
    // Find by active status
    List<ClassEntity> findByIsActive(Boolean isActive);
    
    // Find by course type
    List<ClassEntity> findByCourseCourseType(CourseType courseType);
    
    // Search by name or description
    List<ClassEntity> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
    
    // Count methods
    long countByCourse(Course course);
    long countByIsActive(Boolean isActive);
    
    // Check existence
    boolean existsByNameAndCourse(String name, Course course);
    boolean existsByNameAndCourseAndIdNot(String name, Course course, Long id);
    
    // Paginated methods
    Page<ClassEntity> findByCourse(Course course, Pageable pageable);
    Page<ClassEntity> findByCourseAndIsActive(Course course, Boolean isActive, Pageable pageable);
    Page<ClassEntity> findByCourseCourseType(CourseType courseType, Pageable pageable);
    Page<ClassEntity> findByIsActive(Boolean isActive, Pageable pageable);
}