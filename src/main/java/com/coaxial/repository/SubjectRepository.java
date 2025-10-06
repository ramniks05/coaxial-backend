package com.coaxial.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.CourseType;
import com.coaxial.entity.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    
    // Find by course type
    List<Subject> findByCourseTypeAndIsActiveTrueOrderByDisplayOrderAsc(CourseType courseType);
    List<Subject> findByCourseTypeOrderByDisplayOrderAsc(CourseType courseType);
    List<Subject> findByCourseTypeAndIsActiveTrue(CourseType courseType);
    List<Subject> findByCourseTypeIdAndIsActiveTrueOrderByDisplayOrderAsc(Long courseTypeId);
    List<Subject> findByCourseTypeIdOrderByDisplayOrderAsc(Long courseTypeId);
    
    // Find by name and course type
    Optional<Subject> findByNameAndCourseType(String name, CourseType courseType);
    Optional<Subject> findByNameAndCourseTypeAndIdNot(String name, CourseType courseType, Long id);
    
    // Find by active status
    List<Subject> findByIsActiveTrueOrderByDisplayOrderAsc();
    List<Subject> findByIsActiveFalseOrderByDisplayOrderAsc();
    List<Subject> findAllByOrderByDisplayOrderAsc();
    
    // Check existence
    boolean existsByNameAndCourseType(String name, CourseType courseType);
    boolean existsByNameAndCourseTypeAndIdNot(String name, CourseType courseType, Long id);
    
    // Find subjects by course type through CourseSubject relationship
    List<Subject> findByCourseSubjectsCourseCourseType(CourseType courseType);
    
    // Enhanced filtering methods
    List<Subject> findByCourseSubjectsCourseCourseTypeIdAndIsActiveOrderByDisplayOrderAsc(Long courseTypeId, Boolean isActive);
    List<Subject> findByCourseSubjectsCourseCourseTypeIdOrderByDisplayOrderAsc(Long courseTypeId);
    List<Subject> findByCourseSubjectsCourseIdAndIsActiveOrderByDisplayOrderAsc(Long courseId, Boolean isActive);
    List<Subject> findByCourseSubjectsCourseIdOrderByDisplayOrderAsc(Long courseId);
    
    // Search functionality
    List<Subject> findByNameContainingIgnoreCaseAndIsActiveTrueOrderByDisplayOrderAsc(String name);
    List<Subject> findByDescriptionContainingIgnoreCaseAndIsActiveTrueOrderByDisplayOrderAsc(String description);
    List<Subject> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsActiveTrueOrderByDisplayOrderAsc(String name, String description);
    
    // Class-based filtering for combined filters
    List<Subject> findByClassSubjectsClassEntityIdAndIsActiveOrderByDisplayOrderAsc(Long classId, Boolean isActive);
    List<Subject> findByClassSubjectsClassEntityIdOrderByDisplayOrderAsc(Long classId);
    
    // Exam-based filtering for combined filters
    List<Subject> findByExamSubjectsExamIdAndIsActiveOrderByDisplayOrderAsc(Long examId, Boolean isActive);
    List<Subject> findByExamSubjectsExamIdOrderByDisplayOrderAsc(Long examId);
    
    // Course type filtering for combined filters
    List<Subject> findByCourseTypeAndIsActiveOrderByDisplayOrderAsc(CourseType courseType, Boolean isActive);
    List<Subject> findByCourseTypeIdAndIsActiveOrderByDisplayOrderAsc(Long courseTypeId, Boolean isActive);
}