package com.coaxial.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.QuestionExamHistory;


@Repository
public interface QuestionExamHistoryRepository extends JpaRepository<QuestionExamHistory, Long> {
    
    // Find by question
    List<QuestionExamHistory> findByQuestionIdAndIsActiveTrueOrderByAppearedYearCreatedAtDesc(Long questionId);
    List<QuestionExamHistory> findByQuestionIdOrderByAppearedYearCreatedAtDesc(Long questionId);
    
    // Find by master exam
    List<QuestionExamHistory> findByMasterExamIdAndIsActiveTrueOrderByAppearedYearCreatedAtDesc(Long masterExamId);
    List<QuestionExamHistory> findByMasterExamIdOrderByAppearedYearCreatedAtDesc(Long masterExamId);
    
    // Find by year (Integer)
    List<QuestionExamHistory> findByAppearedYearIdAndIsActiveTrueOrderByCreatedAtDesc(Long appearedYearId);
    List<QuestionExamHistory> findByAppearedYearIdOrderByCreatedAtDesc(Long appearedYearId);
    
    // Find by master exam and year (Integer)
    List<QuestionExamHistory> findByMasterExamIdAndAppearedYearIdAndIsActiveTrueOrderByCreatedAtDesc(Long masterExamId, Long appearedYearId);
    List<QuestionExamHistory> findByMasterExamIdAndAppearedYearIdOrderByCreatedAtDesc(Long masterExamId, Long appearedYearId);
    
    // Find by question and master exam
    List<QuestionExamHistory> findByQuestionIdAndMasterExamIdAndIsActiveTrueOrderByAppearedYearCreatedAtDesc(Long questionId, Long masterExamId);
    List<QuestionExamHistory> findByQuestionIdAndMasterExamIdOrderByAppearedYearCreatedAtDesc(Long questionId, Long masterExamId);
    
    // Find by question and year (Integer)
    List<QuestionExamHistory> findByQuestionIdAndAppearedYearIdAndIsActiveTrueOrderByCreatedAtDesc(Long questionId, Long appearedYearId);
    List<QuestionExamHistory> findByQuestionIdAndAppearedYearIdOrderByCreatedAtDesc(Long questionId, Long appearedYearId);
    
    // Find by active status
    List<QuestionExamHistory> findByIsActiveOrderByCreatedAtDesc(Boolean isActive);
    
    // Count methods
    long countByQuestionIdAndIsActiveTrue(Long questionId);
    long countByMasterExamIdAndIsActiveTrue(Long masterExamId);
    long countByAppearedYearIdAndIsActiveTrue(Long appearedYearId);
    long countByMasterExamIdAndAppearedYearIdAndIsActiveTrue(Long masterExamId, Long appearedYearId);
    
    // Check existence
    boolean existsByQuestionIdAndMasterExamIdAndAppearedYearIdAndIsActiveTrue(Long questionId, Long masterExamId, Long appearedYearId);
    boolean existsByQuestionIdAndMasterExamIdAndAppearedYearIdAndIsActiveTrueAndIdNot(Long questionId, Long masterExamId, Long appearedYearId, Long id);
}
