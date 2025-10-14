package com.coaxial.repository;

import com.coaxial.entity.TestAttempt;
import com.coaxial.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {

    // Find attempts by test and student
    @Query("SELECT ta FROM TestAttempt ta WHERE ta.test.id = :testId AND ta.student.id = :studentId ORDER BY ta.attemptNumber DESC")
    List<TestAttempt> findByTestIdAndStudentId(@Param("testId") Long testId, @Param("studentId") Long studentId);
    
    // Find specific attempt
    @Query("SELECT ta FROM TestAttempt ta WHERE ta.test.id = :testId AND ta.student.id = :studentId AND ta.attemptNumber = :attemptNumber")
    Optional<TestAttempt> findByTestIdAndStudentIdAndAttemptNumber(@Param("testId") Long testId, @Param("studentId") Long studentId, @Param("attemptNumber") Integer attemptNumber);
    
    // Find latest attempt
    @Query("SELECT ta FROM TestAttempt ta WHERE ta.test.id = :testId AND ta.student.id = :studentId ORDER BY ta.attemptNumber DESC")
    Optional<TestAttempt> findLatestAttempt(@Param("testId") Long testId, @Param("studentId") Long studentId);
    
    // Find all attempts for a student
    List<TestAttempt> findByStudentOrderByStartedAtDesc(User student);
    
    // Find submitted attempts
    @Query("SELECT ta FROM TestAttempt ta WHERE ta.test.id = :testId AND ta.student.id = :studentId AND ta.isSubmitted = true ORDER BY ta.attemptNumber DESC")
    List<TestAttempt> findSubmittedAttempts(@Param("testId") Long testId, @Param("studentId") Long studentId);
    
    // Count attempts for a test by student
    @Query("SELECT COUNT(ta) FROM TestAttempt ta WHERE ta.test.id = :testId AND ta.student.id = :studentId")
    Long countAttemptsByTestAndStudent(@Param("testId") Long testId, @Param("studentId") Long studentId);
    
    // Find active (not submitted) attempt
    @Query("SELECT ta FROM TestAttempt ta WHERE ta.test.id = :testId AND ta.student.id = :studentId AND ta.isSubmitted = false AND ta.isActive = true ORDER BY ta.attemptNumber DESC")
    Optional<TestAttempt> findActiveAttempt(@Param("testId") Long testId, @Param("studentId") Long studentId);
    
    // Get best score for a test
    @Query("SELECT ta FROM TestAttempt ta WHERE ta.test.id = :testId AND ta.student.id = :studentId AND ta.isSubmitted = true ORDER BY ta.totalMarksObtained DESC")
    Optional<TestAttempt> findBestAttempt(@Param("testId") Long testId, @Param("studentId") Long studentId);
}

