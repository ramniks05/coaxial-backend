package com.coaxial.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.Course;
import com.coaxial.entity.CourseSubject;
import com.coaxial.entity.Subject;

@Repository
public interface CourseSubjectRepository extends JpaRepository<CourseSubject, Long> {
    
    // Find by course
    List<CourseSubject> findByCourse(Course course);
    List<CourseSubject> findByCourseOrderByDisplayOrderAsc(Course course);
    List<CourseSubject> findByCourseId(Long courseId);
    
    // Find by subject
    List<CourseSubject> findBySubject(Subject subject);
    List<CourseSubject> findBySubjectOrderByDisplayOrderAsc(Subject subject);
    List<CourseSubject> findBySubjectId(Long subjectId);
    
    // Find by course and subject
    Optional<CourseSubject> findByCourseAndSubject(Course course, Subject subject);
    Optional<CourseSubject> findByCourseIdAndSubjectId(Long courseId, Long subjectId);
    
    // Find by course and multiple subjects
    List<CourseSubject> findByCourseAndSubjectIn(Course course, List<Subject> subjects);
    
    // Count methods
    long countByCourse(Course course);
    
    // Check existence
    boolean existsByCourseAndSubject(Course course, Subject subject);
    boolean existsByCourseAndSubjectAndIdNot(Course course, Subject subject, Long id);
}
