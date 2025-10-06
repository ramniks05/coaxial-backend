package com.coaxial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.Test;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    
    // Find active tests
    List<Test> findByIsActiveTrue();
    
    // Find published tests
    List<Test> findByIsPublishedTrue();
    
    // Find tests by MasterExam
    List<Test> findByMasterExamIdAndIsActiveTrue(Long masterExamId);
    
    // Find tests by MasterExam (all status)
    List<Test> findByMasterExamId(Long masterExamId);
    
    // Find tests by multiple MasterExams
    List<Test> findByMasterExamIdInAndIsActiveTrue(List<Long> masterExamIds);
    
    // Find tests by test type
    List<Test> findByTestTypeAndIsActiveTrue(String testType);
    
    // Find tests within date range
    List<Test> findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndIsActiveTrue(
        java.time.LocalDateTime currentTime, java.time.LocalDateTime currentTime2);
        
    // Find tests by created by user
    List<Test> findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(com.coaxial.entity.User createdBy);
}


