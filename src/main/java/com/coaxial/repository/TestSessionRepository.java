package com.coaxial.repository;

import com.coaxial.entity.TestSession;
import com.coaxial.entity.User;
import com.coaxial.enums.TestSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestSessionRepository extends JpaRepository<TestSession, Long> {

    // Find session by session ID
    Optional<TestSession> findBySessionId(String sessionId);
    
    // Find active session for a test and student
    @Query("SELECT ts FROM TestSession ts WHERE ts.test.id = :testId AND ts.student.id = :studentId AND ts.status IN ('STARTED', 'IN_PROGRESS', 'PAUSED') AND ts.isActive = true ORDER BY ts.startedAt DESC")
    Optional<TestSession> findActiveSessionByTestAndStudent(@Param("testId") Long testId, @Param("studentId") Long studentId);
    
    // Find all sessions for a student
    List<TestSession> findByStudentOrderByStartedAtDesc(User student);
    
    // Find sessions by status
    List<TestSession> findByStatusOrderByStartedAtDesc(TestSessionStatus status);
    
    // Find sessions for a test
    @Query("SELECT ts FROM TestSession ts WHERE ts.test.id = :testId ORDER BY ts.startedAt DESC")
    List<TestSession> findByTestId(@Param("testId") Long testId);
    
    // Find sessions by student and status
    @Query("SELECT ts FROM TestSession ts WHERE ts.student.id = :studentId AND ts.status = :status ORDER BY ts.startedAt DESC")
    List<TestSession> findByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") TestSessionStatus status);
    
    // Find timed-out sessions (for cleanup)
    @Query("SELECT ts FROM TestSession ts WHERE ts.status IN ('STARTED', 'IN_PROGRESS') AND ts.startedAt < :timeoutThreshold AND ts.isActive = true")
    List<TestSession> findTimedOutSessions(@Param("timeoutThreshold") LocalDateTime timeoutThreshold);
    
    // Count active sessions for a student
    @Query("SELECT COUNT(ts) FROM TestSession ts WHERE ts.student.id = :studentId AND ts.status IN ('STARTED', 'IN_PROGRESS', 'PAUSED') AND ts.isActive = true")
    Long countActiveSessionsByStudent(@Param("studentId") Long studentId);
}

