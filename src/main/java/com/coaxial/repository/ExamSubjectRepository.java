package com.coaxial.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.Exam;
import com.coaxial.entity.ExamSubject;
import com.coaxial.entity.Subject;

@Repository
public interface ExamSubjectRepository extends JpaRepository<ExamSubject, Long> {
    
    // Find by exam
    List<ExamSubject> findByExam(Exam exam);
    List<ExamSubject> findByExamOrderByDisplayOrderAsc(Exam exam);
    List<ExamSubject> findByExamId(Long examId);
    
    // Find by subject
    List<ExamSubject> findBySubject(Subject subject);
    List<ExamSubject> findBySubjectOrderByDisplayOrderAsc(Subject subject);
    List<ExamSubject> findBySubjectId(Long subjectId);
    
    // Find by exam and subject
    Optional<ExamSubject> findByExamAndSubject(Exam exam, Subject subject);
    Optional<ExamSubject> findByExamIdAndSubjectId(Long examId, Long subjectId);
    
    // Find by exam and multiple subjects
    List<ExamSubject> findByExamAndSubjectIn(Exam exam, List<Subject> subjects);
    
    // Count methods
    long countByExam(Exam exam);
    
    // Check existence
    boolean existsByExamAndSubject(Exam exam, Subject subject);
    boolean existsByExamAndSubjectAndIdNot(Exam exam, Subject subject, Long id);
}
