package com.coaxial.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    // Find by chapter
    List<Question> findByChapterIdAndIsActiveTrueOrderByDisplayOrderAsc(Long chapterId);
    List<Question> findByChapterIdOrderByDisplayOrderAsc(Long chapterId);
    List<Question> findByChapterIdAndIsActiveOrderByDisplayOrderAsc(Long chapterId, Boolean isActive);
    List<Question> findByChapterIdInAndIsActiveTrueOrderByDisplayOrderAsc(List<Long> chapterIds);
    
    // Find by module
    List<Question> findByModuleIdAndIsActiveTrueOrderByDisplayOrderAsc(Long moduleId);
    List<Question> findByModuleIdOrderByDisplayOrderAsc(Long moduleId);
    List<Question> findByModuleIdAndIsActiveOrderByDisplayOrderAsc(Long moduleId, Boolean isActive);
    
    // Find by topic
    List<Question> findByTopicIdAndIsActiveTrueOrderByDisplayOrderAsc(Long topicId);
    List<Question> findByTopicIdOrderByDisplayOrderAsc(Long topicId);
    List<Question> findByTopicIdAndIsActiveOrderByDisplayOrderAsc(Long topicId, Boolean isActive);
    
    // Find by subject
    List<Question> findBySubjectIdAndIsActiveTrueOrderByDisplayOrderAsc(Long subjectId);
    List<Question> findBySubjectIdOrderByDisplayOrderAsc(Long subjectId);
    List<Question> findBySubjectIdAndIsActiveOrderByDisplayOrderAsc(Long subjectId, Boolean isActive);
    
    // Find by course type
    List<Question> findByCourseTypeIdAndIsActiveTrueOrderByDisplayOrderAsc(Long courseTypeId);
    List<Question> findByCourseTypeIdOrderByDisplayOrderAsc(Long courseTypeId);
    List<Question> findByCourseTypeIdAndIsActiveOrderByDisplayOrderAsc(Long courseTypeId, Boolean isActive);
    
    // Find by relationship
    List<Question> findByRelationshipIdAndIsActiveTrueOrderByDisplayOrderAsc(Long relationshipId);
    List<Question> findByRelationshipIdOrderByDisplayOrderAsc(Long relationshipId);
    List<Question> findByRelationshipIdAndIsActiveOrderByDisplayOrderAsc(Long relationshipId, Boolean isActive);
    
    // Find by course type and relationship (for specific class/exam/course)
    List<Question> findByCourseTypeIdAndRelationshipIdAndIsActiveTrueOrderByDisplayOrderAsc(Long courseTypeId, Long relationshipId);
    List<Question> findByCourseTypeIdAndRelationshipIdOrderByDisplayOrderAsc(Long courseTypeId, Long relationshipId);
    List<Question> findByCourseTypeIdAndRelationshipIdAndIsActiveOrderByDisplayOrderAsc(Long courseTypeId, Long relationshipId, Boolean isActive);
    
    // Find by question type
    List<Question> findByQuestionTypeAndIsActiveTrueOrderByDisplayOrderAsc(String questionType);
    List<Question> findByQuestionTypeOrderByDisplayOrderAsc(String questionType);
    List<Question> findByQuestionTypeAndIsActiveOrderByDisplayOrderAsc(String questionType, Boolean isActive);
    
    // Find by difficulty level
    List<Question> findByDifficultyLevelAndIsActiveTrueOrderByDisplayOrderAsc(String difficultyLevel);
    List<Question> findByDifficultyLevelOrderByDisplayOrderAsc(String difficultyLevel);
    List<Question> findByDifficultyLevelAndIsActiveOrderByDisplayOrderAsc(String difficultyLevel, Boolean isActive);
    
    // Find by marks range
    List<Question> findByMarksAndIsActiveTrueOrderByDisplayOrderAsc(Integer marks);
    List<Question> findByMarksOrderByDisplayOrderAsc(Integer marks);
    List<Question> findByMarksAndIsActiveOrderByDisplayOrderAsc(Integer marks, Boolean isActive);
    
    // Find by marks range (between)
    List<Question> findByMarksBetweenAndIsActiveTrueOrderByDisplayOrderAsc(Integer minMarks, Integer maxMarks);
    List<Question> findByMarksBetweenOrderByDisplayOrderAsc(Integer minMarks, Integer maxMarks);
    List<Question> findByMarksBetweenAndIsActiveOrderByDisplayOrderAsc(Integer minMarks, Integer maxMarks, Boolean isActive);
    
    // Find by creation date
    List<Question> findByCreatedAtAfterAndIsActiveTrueOrderByDisplayOrderAsc(LocalDateTime createdAfter);
    
    // Find by active status
    List<Question> findByIsActiveOrderByDisplayOrderAsc(Boolean isActive);
    
    // Search by question text
    List<Question> findByQuestionTextContainingIgnoreCaseAndIsActiveTrueOrderByDisplayOrderAsc(String questionText);
    List<Question> findByQuestionTextContainingIgnoreCaseOrderByDisplayOrderAsc(String questionText);
    List<Question> findByQuestionTextContainingIgnoreCaseAndIsActiveOrderByDisplayOrderAsc(String questionText, Boolean isActive);
    
    // Search by explanation
    List<Question> findByExplanationContainingIgnoreCaseAndIsActiveTrueOrderByDisplayOrderAsc(String explanation);
    List<Question> findByExplanationContainingIgnoreCaseOrderByDisplayOrderAsc(String explanation);
    List<Question> findByExplanationContainingIgnoreCaseAndIsActiveOrderByDisplayOrderAsc(String explanation, Boolean isActive);
    
    // Complex filtering combinations for test creation
    List<Question> findByCourseTypeIdAndRelationshipIdAndQuestionTypeAndDifficultyLevelAndIsActiveTrueOrderByDisplayOrderAsc(
            Long courseTypeId, Long relationshipId, String questionType, String difficultyLevel);
    
    List<Question> findByCourseTypeIdAndRelationshipIdAndQuestionTypeAndIsActiveTrueOrderByDisplayOrderAsc(
            Long courseTypeId, Long relationshipId, String questionType);
    
    List<Question> findByCourseTypeIdAndRelationshipIdAndDifficultyLevelAndIsActiveTrueOrderByDisplayOrderAsc(
            Long courseTypeId, Long relationshipId, String difficultyLevel);
    
    List<Question> findByCourseTypeIdAndRelationshipIdAndMarksBetweenAndIsActiveTrueOrderByDisplayOrderAsc(
            Long courseTypeId, Long relationshipId, Integer minMarks, Integer maxMarks);
    
    // Count methods for test creation
    long countByCourseTypeIdAndRelationshipIdAndIsActiveTrue(Long courseTypeId, Long relationshipId);
    long countByCourseTypeIdAndRelationshipIdAndQuestionTypeAndIsActiveTrue(Long courseTypeId, Long relationshipId, String questionType);
    long countByCourseTypeIdAndRelationshipIdAndDifficultyLevelAndIsActiveTrue(Long courseTypeId, Long relationshipId, String difficultyLevel);
    long countByCourseTypeIdAndRelationshipIdAndMarksBetweenAndIsActiveTrue(Long courseTypeId, Long relationshipId, Integer minMarks, Integer maxMarks);
    
    // Check existence
    boolean existsByQuestionTextAndChapterId(String questionText, Long chapterId);
    boolean existsByQuestionTextAndChapterIdAndIdNot(String questionText, Long chapterId, Long id);
    
    // Enhanced filtering queries
    @Query("SELECT DISTINCT q FROM Question q " +
           "LEFT JOIN FETCH q.examSuitabilities es " +
           "LEFT JOIN FETCH es.masterExam " +
           "LEFT JOIN FETCH q.examHistories eh " +
           "LEFT JOIN FETCH eh.masterExam " +
           "LEFT JOIN FETCH eh.appearedYear " +
           "WHERE (:isActive IS NULL OR q.isActive = :isActive) " +
           "AND (:questionType IS NULL OR q.questionType = :questionType) " +
           "AND (:difficultyLevel IS NULL OR q.difficultyLevel = :difficultyLevel) " +
           "AND (:minMarks IS NULL OR q.marks >= :minMarks) " +
           "AND (:maxMarks IS NULL OR q.marks <= :maxMarks) " +
           "AND (:courseTypeId IS NULL OR q.courseTypeId = :courseTypeId) " +
           "AND (:relationshipId IS NULL OR q.relationshipId = :relationshipId) " +
           "AND (:subjectId IS NULL OR q.subjectId = :subjectId) " +
           "AND (:topicId IS NULL OR q.topicId = :topicId) " +
           "AND (:moduleId IS NULL OR q.moduleId = :moduleId) " +
           "AND (:chapterId IS NULL OR q.chapter.id = :chapterId) " +
           "AND (:createdAfter IS NULL OR q.createdAt >= :createdAfter) " +
           "AND (:createdBefore IS NULL OR q.createdAt <= :createdBefore) " +
           "ORDER BY q.displayOrder ASC")
    List<Question> findQuestionsWithBasicFilters(
            @Param("isActive") Boolean isActive,
            @Param("questionType") String questionType,
            @Param("difficultyLevel") String difficultyLevel,
            @Param("minMarks") Integer minMarks,
            @Param("maxMarks") Integer maxMarks,
            @Param("courseTypeId") Long courseTypeId,
            @Param("relationshipId") Long relationshipId,
            @Param("subjectId") Long subjectId,
            @Param("topicId") Long topicId,
            @Param("moduleId") Long moduleId,
            @Param("chapterId") Long chapterId,
            @Param("createdAfter") LocalDateTime createdAfter,
            @Param("createdBefore") LocalDateTime createdBefore);
    
    // Filter by exam suitability
    @Query("SELECT DISTINCT q FROM Question q " +
           "LEFT JOIN FETCH q.examSuitabilities es " +
           "LEFT JOIN FETCH es.masterExam " +
           "LEFT JOIN FETCH q.examHistories eh " +
           "LEFT JOIN FETCH eh.masterExam " +
           "LEFT JOIN FETCH eh.appearedYear " +
           "WHERE es.masterExam.id IN :examIds " +
           "AND (:suitabilityLevels IS NULL OR es.suitabilityLevel IN :suitabilityLevels) " +
           "ORDER BY q.displayOrder ASC")
    List<Question> findQuestionsByExamSuitability(
            @Param("examIds") List<Long> examIds,
            @Param("suitabilityLevels") List<String> suitabilityLevels);
    
    // Filter by exam history (previously asked questions)
    @Query("SELECT DISTINCT q FROM Question q " +
           "LEFT JOIN FETCH q.examSuitabilities es " +
           "LEFT JOIN FETCH es.masterExam " +
           "LEFT JOIN FETCH q.examHistories eh " +
           "LEFT JOIN FETCH eh.masterExam " +
           "LEFT JOIN FETCH eh.appearedYear " +
           "WHERE eh.masterExam.id IN :examIds " +
           "AND (:appearedYears IS NULL OR eh.appearedYear.yearValue IN :appearedYears) " +
           "AND (:examSessions IS NULL OR eh.appearedSession IN :examSessions) " +
           "AND (:minMarksInExam IS NULL OR eh.marksInExam >= :minMarksInExam) " +
           "AND (:maxMarksInExam IS NULL OR eh.marksInExam <= :maxMarksInExam) " +
           "AND (:questionNumbers IS NULL OR eh.questionNumberInExam IN :questionNumbers) " +
           "AND (:examDifficulties IS NULL OR eh.difficultyInExam IN :examDifficulties) " +
           "ORDER BY q.displayOrder ASC")
    List<Question> findPreviouslyAskedQuestions(
            @Param("examIds") List<Long> examIds,
            @Param("appearedYears") List<Integer> appearedYears,
            @Param("examSessions") List<String> examSessions,
            @Param("minMarksInExam") Integer minMarksInExam,
            @Param("maxMarksInExam") Integer maxMarksInExam,
            @Param("questionNumbers") List<String> questionNumbers,
            @Param("examDifficulties") List<String> examDifficulties);
    
    // Combined filtering query with text search fix
    @Query("SELECT DISTINCT q FROM Question q " +
           "LEFT JOIN q.examSuitabilities es " +
           "LEFT JOIN q.examHistories eh " +
           "WHERE (:isActive IS NULL OR q.isActive = :isActive) " +
           "AND (:questionType IS NULL OR q.questionType = :questionType) " +
           "AND (:difficultyLevel IS NULL OR q.difficultyLevel = :difficultyLevel) " +
           "AND (:minMarks IS NULL OR q.marks >= :minMarks) " +
           "AND (:maxMarks IS NULL OR q.marks <= :maxMarks) " +
           "AND (:courseTypeId IS NULL OR q.courseTypeId = :courseTypeId) " +
           "AND (:relationshipId IS NULL OR q.relationshipId = :relationshipId) " +
           "AND (:subjectId IS NULL OR q.subjectId = :subjectId) " +
           "AND (:topicId IS NULL OR q.topicId = :topicId) " +
           "AND (:moduleId IS NULL OR q.moduleId = :moduleId) " +
           "AND (:chapterId IS NULL OR q.chapter.id = :chapterId) " +
           "AND (:examIds IS NULL OR es.masterExam.id IN :examIds) " +
           "AND (:suitabilityLevels IS NULL OR es.suitabilityLevel IN :suitabilityLevels) " +
           "AND (:appearedYears IS NULL OR eh.appearedYear.yearValue IN :appearedYears) " +
           "ORDER BY q.displayOrder ASC")
    Page<Question> findQuestionsWithEnhancedFilters(
            @Param("isActive") Boolean isActive,
            @Param("questionType") String questionType,
            @Param("difficultyLevel") String difficultyLevel,
            @Param("minMarks") Integer minMarks,
            @Param("maxMarks") Integer maxMarks,
            @Param("courseTypeId") Long courseTypeId,
            @Param("relationshipId") Long relationshipId,
            @Param("subjectId") Long subjectId,
            @Param("topicId") Long topicId,
            @Param("moduleId") Long moduleId,
            @Param("chapterId") Long chapterId,
            @Param("examIds") List<Long> examIds,
            @Param("suitabilityLevels") List<String> suitabilityLevels,
            @Param("appearedYears") List<Integer> appearedYears,
            Pageable pageable);
}
