package com.coaxial.service;

import com.coaxial.dto.StudentQuestionResponseDTO;
import com.coaxial.dto.StudentQuestionResponseDTO.StudentQuestionOptionDTO;
import com.coaxial.entity.Question;
import com.coaxial.entity.StudentSubscription;
import com.coaxial.enums.SubscriptionLevel;
import com.coaxial.repository.QuestionRepository;
import com.coaxial.repository.StudentSubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StudentQuestionService {
    
    private static final Logger logger = LoggerFactory.getLogger(StudentQuestionService.class);
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private StudentSubscriptionRepository subscriptionRepository;
    
    /**
     * Get questions accessible to student based on their subscriptions with filters
     * Uses courseTypeId and linkageId (relationshipId) to filter questions
     */
    public Page<StudentQuestionResponseDTO> getAccessibleQuestions(
            Long studentId,
            Long courseTypeId,
            Long linkageId,
            Long topicId,
            Long moduleId,
            Long chapterId,
            String questionType,
            String difficultyLevel,
            Pageable pageable) {
        
        // Get student's active subscriptions
        List<StudentSubscription> subscriptions = subscriptionRepository.findActiveSubscriptionsByStudentId(studentId);
        
        if (subscriptions.isEmpty()) {
            logger.warn("Student {} has no active subscriptions", studentId);
            return Page.empty(pageable);
        }
        
        // Get all questions matching filters
        List<Question> allQuestions = questionRepository.findAll();
        
        // Filter questions based on subscriptions
        List<Question> accessibleQuestions = allQuestions.stream()
                .filter(q -> q.getIsActive())
                .filter(q -> hasAccessToQuestion(studentId, q, subscriptions))
                .filter(q -> applyFilters(q, courseTypeId, linkageId, topicId, moduleId, chapterId, questionType, difficultyLevel))
                .collect(Collectors.toList());
        
        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), accessibleQuestions.size());
        
        List<StudentQuestionResponseDTO> questionDTOs = accessibleQuestions.subList(start, end).stream()
                .map(this::toStudentDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(questionDTOs, pageable, accessibleQuestions.size());
    }
    
    /**
     * Get question by ID (if student has access)
     */
    public StudentQuestionResponseDTO getQuestionById(Long questionId, Long studentId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));
        
        // Get student's active subscriptions
        List<StudentSubscription> subscriptions = subscriptionRepository.findActiveSubscriptionsByStudentId(studentId);
        
        // Check if student has access
        if (!hasAccessToQuestion(studentId, question, subscriptions)) {
            throw new IllegalArgumentException("You do not have access to this question. Please purchase a subscription.");
        }
        
        return toStudentDTO(question);
    }
    
    /**
     * Check if student has access to a question based on their subscriptions
     */
    private boolean hasAccessToQuestion(Long studentId, Question question, List<StudentSubscription> subscriptions) {
        LocalDateTime now = LocalDateTime.now();
        
        // Question must have chapter
        if (question.getChapter() == null) {
            return false;
        }
        
        // Get chapter's parent hierarchy
        Long chapterCourseTypeId = question.getCourseTypeId();
        Long chapterRelationshipId = question.getRelationshipId(); // classSubjectId or examSubjectId
        
        for (StudentSubscription sub : subscriptions) {
            // Skip inactive or expired subscriptions
            if (!sub.getIsActive() || (sub.getEndDate() != null && sub.getEndDate().isBefore(now))) {
                continue;
            }
            
            // Course subscription - grants access to all questions in that course
            if (sub.getSubscriptionLevel() == SubscriptionLevel.COURSE) {
                // Check if question belongs to this course
                // Questions are linked via courseTypeId and relationshipId
                if (chapterRelationshipId != null && chapterRelationshipId.equals(sub.getEntityId())) {
                    return true;
                }
            }
            
            // Class subscription - grants access to class-specific questions
            if (sub.getSubscriptionLevel() == SubscriptionLevel.CLASS) {
                // Check if question's relationshipId matches classSubjectId linked to this class
                // This is simplified - in reality you'd need to check if relationshipId belongs to this class
                if (chapterRelationshipId != null && chapterCourseTypeId != null && chapterCourseTypeId == 1L) {
                    // Academic course type - check class subscription
                    return true; // Simplified - should check actual class linkage
                }
            }
            
            // Exam subscription - grants access to exam-specific questions
            if (sub.getSubscriptionLevel() == SubscriptionLevel.EXAM) {
                // Check if question's relationshipId matches examSubjectId linked to this exam
                if (chapterRelationshipId != null && chapterCourseTypeId != null && chapterCourseTypeId == 2L) {
                    // Competitive course type - check exam subscription
                    return true; // Simplified - should check actual exam linkage
                }
            }
        }
        
        return false; // No valid subscription found
    }
    
    /**
     * Apply filters to question
     * Uses courseTypeId and linkageId (relationshipId) for subject filtering
     */
    private boolean applyFilters(Question q, Long courseTypeId, Long linkageId, Long topicId, 
                                 Long moduleId, Long chapterId, String questionType, String difficultyLevel) {
        // Filter by courseTypeId and linkageId (relationshipId)
        if (courseTypeId != null && !courseTypeId.equals(q.getCourseTypeId())) {
            return false;
        }
        if (linkageId != null && !linkageId.equals(q.getRelationshipId())) {
            return false;
        }
        if (topicId != null && !topicId.equals(q.getTopicId())) {
            return false;
        }
        if (moduleId != null && !moduleId.equals(q.getModuleId())) {
            return false;
        }
        if (chapterId != null && (q.getChapter() == null || !chapterId.equals(q.getChapter().getId()))) {
            return false;
        }
        if (questionType != null && !questionType.equalsIgnoreCase(q.getQuestionType())) {
            return false;
        }
        if (difficultyLevel != null && !difficultyLevel.equalsIgnoreCase(q.getDifficultyLevel())) {
            return false;
        }
        return true;
    }
    
    /**
     * Convert Question to Student DTO
     */
    private StudentQuestionResponseDTO toStudentDTO(Question question) {
        StudentQuestionResponseDTO dto = new StudentQuestionResponseDTO();
        
        dto.setId(question.getId());
        dto.setQuestionText(question.getQuestionText());
        dto.setQuestionType(question.getQuestionType());
        dto.setDifficultyLevel(question.getDifficultyLevel());
        dto.setMarks(question.getMarks());
        dto.setExplanation(question.getExplanation());
        
        // Chapter info
        if (question.getChapter() != null) {
            dto.setChapterId(question.getChapter().getId());
            dto.setChapterName(question.getChapter().getName());
            
            // Module info
            if (question.getChapter().getModule() != null) {
                dto.setModuleId(question.getChapter().getModule().getId());
                dto.setModuleName(question.getChapter().getModule().getName());
                
                // Topic info
                if (question.getChapter().getModule().getTopic() != null) {
                    dto.setTopicId(question.getChapter().getModule().getTopic().getId());
                    dto.setTopicName(question.getChapter().getModule().getTopic().getName());
                }
            }
        }
        
        // Subject info
        dto.setSubjectId(question.getSubjectId());
        
        // Convert options (show correct answers for learning)
        List<StudentQuestionOptionDTO> options = question.getOptions().stream()
                .map(opt -> new StudentQuestionOptionDTO(
                        opt.getId(),
                        opt.getOptionText(),
                        opt.getIsCorrect()
                ))
                .collect(Collectors.toList());
        dto.setOptions(options);
        
        return dto;
    }
}

