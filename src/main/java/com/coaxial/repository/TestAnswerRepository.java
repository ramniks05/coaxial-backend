package com.coaxial.repository;

import com.coaxial.entity.TestAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestAnswerRepository extends JpaRepository<TestAnswer, Long> {

    // Find all answers for an attempt
    @Query("SELECT ta FROM TestAnswer ta WHERE ta.testAttempt.id = :attemptId ORDER BY ta.id")
    List<TestAnswer> findByAttemptId(@Param("attemptId") Long attemptId);
    
    // Find answer for a specific question in an attempt
    @Query("SELECT ta FROM TestAnswer ta WHERE ta.testAttempt.id = :attemptId AND ta.question.id = :questionId")
    Optional<TestAnswer> findByAttemptIdAndQuestionId(@Param("attemptId") Long attemptId, @Param("questionId") Long questionId);
    
    // Find all answers for a student's test
    @Query("SELECT ta FROM TestAnswer ta WHERE ta.testAttempt.test.id = :testId AND ta.testAttempt.student.id = :studentId ORDER BY ta.testAttempt.attemptNumber DESC, ta.id")
    List<TestAnswer> findByTestIdAndStudentId(@Param("testId") Long testId, @Param("studentId") Long studentId);
    
    // Count answered questions in an attempt
    @Query("SELECT COUNT(ta) FROM TestAnswer ta WHERE ta.testAttempt.id = :attemptId AND ta.isAnswered = true")
    Long countAnsweredQuestions(@Param("attemptId") Long attemptId);
    
    // Count correct answers in an attempt
    @Query("SELECT COUNT(ta) FROM TestAnswer ta WHERE ta.testAttempt.id = :attemptId AND ta.isCorrect = true")
    Long countCorrectAnswers(@Param("attemptId") Long attemptId);
    
    // Count wrong answers in an attempt
    @Query("SELECT COUNT(ta) FROM TestAnswer ta WHERE ta.testAttempt.id = :attemptId AND ta.isAnswered = true AND ta.isCorrect = false")
    Long countWrongAnswers(@Param("attemptId") Long attemptId);
}

