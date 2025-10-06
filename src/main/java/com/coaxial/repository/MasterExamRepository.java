package com.coaxial.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.MasterExam;

@Repository
public interface MasterExamRepository extends JpaRepository<MasterExam, Long> {
    
    // Find by exam name
    List<MasterExam> findByExamNameContainingIgnoreCaseAndIsActiveTrueOrderByExamNameAsc(String examName);
    List<MasterExam> findByExamNameContainingIgnoreCaseOrderByExamNameAsc(String examName);
    
    // Simplified repository – only by name and basic active filters
    // (code/type/body removed in simplified MasterExam)
    
    // Find by active status
    List<MasterExam> findByIsActiveOrderByExamNameAsc(Boolean isActive);
    
    // Find all active exams
    List<MasterExam> findByIsActiveTrueOrderByExamNameAsc();
    
    // Check existence (code removed)
    boolean existsByExamNameAndIsActiveTrue(String examName);
    boolean existsByExamNameAndIsActiveTrueAndIdNot(String examName, Long id);
}
