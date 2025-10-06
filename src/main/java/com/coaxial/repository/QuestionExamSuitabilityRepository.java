package com.coaxial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.QuestionExamSuitability;

@Repository
public interface QuestionExamSuitabilityRepository extends JpaRepository<QuestionExamSuitability, Long> {
    
    // Find by question
    List<QuestionExamSuitability> findByQuestionIdAndIsActiveTrueOrderBySuitabilityLevelAsc(Long questionId);
    List<QuestionExamSuitability> findByQuestionIdOrderBySuitabilityLevelAsc(Long questionId);
    
    // Find by master exam
    List<QuestionExamSuitability> findByMasterExamIdAndIsActiveTrueOrderBySuitabilityLevelAsc(Long masterExamId);
    List<QuestionExamSuitability> findByMasterExamIdOrderBySuitabilityLevelAsc(Long masterExamId);
    
    // Find by suitability level
    List<QuestionExamSuitability> findBySuitabilityLevelAndIsActiveTrueOrderByCreatedAtDesc(String suitabilityLevel);
    List<QuestionExamSuitability> findBySuitabilityLevelOrderByCreatedAtDesc(String suitabilityLevel);
    
    // Find by question and master exam
    List<QuestionExamSuitability> findByQuestionIdAndMasterExamIdAndIsActiveTrueOrderByCreatedAtDesc(Long questionId, Long masterExamId);
    List<QuestionExamSuitability> findByQuestionIdAndMasterExamIdOrderByCreatedAtDesc(Long questionId, Long masterExamId);
    
    // Find by question and suitability level
    List<QuestionExamSuitability> findByQuestionIdAndSuitabilityLevelAndIsActiveTrueOrderByCreatedAtDesc(Long questionId, String suitabilityLevel);
    List<QuestionExamSuitability> findByQuestionIdAndSuitabilityLevelOrderByCreatedAtDesc(Long questionId, String suitabilityLevel);
    
    // Find by master exam and suitability level
    List<QuestionExamSuitability> findByMasterExamIdAndSuitabilityLevelAndIsActiveTrueOrderByCreatedAtDesc(Long masterExamId, String suitabilityLevel);
    List<QuestionExamSuitability> findByMasterExamIdAndSuitabilityLevelOrderByCreatedAtDesc(Long masterExamId, String suitabilityLevel);
    
    // Find by active status
    List<QuestionExamSuitability> findByIsActiveOrderByCreatedAtDesc(Boolean isActive);
    
    // Count methods
    long countByQuestionIdAndIsActiveTrue(Long questionId);
    long countByMasterExamIdAndIsActiveTrue(Long masterExamId);
    long countBySuitabilityLevelAndIsActiveTrue(String suitabilityLevel);
    long countByQuestionIdAndSuitabilityLevelAndIsActiveTrue(Long questionId, String suitabilityLevel);
    long countByMasterExamIdAndSuitabilityLevelAndIsActiveTrue(Long masterExamId, String suitabilityLevel);
    
    // Check existence
    boolean existsByQuestionIdAndMasterExamIdAndIsActiveTrue(Long questionId, Long masterExamId);
    boolean existsByQuestionIdAndMasterExamIdAndIsActiveTrueAndIdNot(Long questionId, Long masterExamId, Long id);
}
