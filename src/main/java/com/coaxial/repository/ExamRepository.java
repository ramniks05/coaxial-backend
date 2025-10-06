package com.coaxial.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.Course;
import com.coaxial.entity.CourseType;
import com.coaxial.entity.Exam;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    
    // Find by course
    List<Exam> findByCourse(Course course);
    List<Exam> findByCourseAndIsActive(Course course, Boolean isActive);
    List<Exam> findByCourseOrderByDisplayOrderAsc(Course course);
    
    // Find by active status
    List<Exam> findByIsActive(Boolean isActive);
    
    // Find by course type
    List<Exam> findByCourseCourseType(CourseType courseType);
    
    // Search by name or description
    List<Exam> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
    
    // Check existence
    boolean existsByNameAndCourse(String name, Course course);
    boolean existsByNameAndCourseAndIdNot(String name, Course course, Long id);
    
    // Custom query to fetch exam with course and course type
    @Query("SELECT e FROM Exam e JOIN FETCH e.course c JOIN FETCH c.courseType WHERE e.id = :id")
    Exam findByIdWithCourseAndCourseType(Long id);
    
    // Custom query to fetch all exams with course and course type
    @Query("SELECT e FROM Exam e JOIN FETCH e.course c JOIN FETCH c.courseType")
    List<Exam> findAllWithCourseAndCourseType();
    
    // Find exam entities by course ID
    List<Exam> findByCourseId(Long courseId);
    
    // Paginated methods
    Page<Exam> findByCourse(Course course, Pageable pageable);
    Page<Exam> findByCourseAndIsActive(Course course, Boolean isActive, Pageable pageable);
    Page<Exam> findByCourseCourseType(CourseType courseType, Pageable pageable);
    Page<Exam> findByIsActive(Boolean isActive, Pageable pageable);
}