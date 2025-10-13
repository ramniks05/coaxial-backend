package com.coaxial.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.Test;
import com.coaxial.enums.TestCreationMode;
import com.coaxial.enums.TestLevel;

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
    
    // ========== DUAL-MODE TEST SYSTEM QUERY METHODS ==========
    
    // Filter by creation mode
    List<Test> findByTestCreationMode(TestCreationMode mode);
    List<Test> findByTestCreationModeAndIsPublished(TestCreationMode mode, Boolean isPublished);
    Page<Test> findByTestCreationMode(TestCreationMode mode, Pageable pageable);
    
    // Filter by test level
    List<Test> findByTestLevel(TestLevel level);
    List<Test> findByTestLevelAndIsPublished(TestLevel level, Boolean isPublished);
    Page<Test> findByTestLevel(TestLevel level, Pageable pageable);
    
    // Filter by content hierarchy - CourseType
    List<Test> findByCourseTypeId(Long courseTypeId);
    Page<Test> findByCourseTypeId(Long courseTypeId, Pageable pageable);
    
    // Filter by content hierarchy - Course
    List<Test> findByCourseId(Long courseId);
    Page<Test> findByCourseId(Long courseId, Pageable pageable);
    
    // Filter by content hierarchy - ClassEntity
    List<Test> findByClassEntityId(Long classId);
    Page<Test> findByClassEntityId(Long classId, Pageable pageable);
    
    // Filter by content hierarchy - Exam
    List<Test> findByExamId(Long examId);
    Page<Test> findByExamId(Long examId, Pageable pageable);
    
    // Filter by content hierarchy - Subject Linkage
    List<Test> findBySubjectLinkageId(Long subjectLinkageId);
    Page<Test> findBySubjectLinkageId(Long subjectLinkageId, Pageable pageable);
    
    // Filter by content hierarchy - Topic
    List<Test> findByTopicId(Long topicId);
    Page<Test> findByTopicId(Long topicId, Pageable pageable);
    
    // Filter by content hierarchy - Module
    List<Test> findByModuleId(Long moduleId);
    Page<Test> findByModuleId(Long moduleId, Pageable pageable);
    
    // Filter by content hierarchy - Chapter
    List<Test> findByChapterId(Long chapterId);
    Page<Test> findByChapterId(Long chapterId, Pageable pageable);
    
    // Combined filters
    Page<Test> findByTestCreationModeAndMasterExamId(TestCreationMode mode, Long masterExamId, Pageable pageable);
    Page<Test> findByTestLevelAndCourseTypeId(TestLevel level, Long courseTypeId, Pageable pageable);
    Page<Test> findByTestCreationModeAndTestLevel(TestCreationMode mode, TestLevel level, Pageable pageable);
}
