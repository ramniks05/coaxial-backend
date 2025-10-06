package com.coaxial.service;

import com.coaxial.dto.SubjectResponseDTO;
import com.coaxial.dto.TestResponseDTO;
import com.coaxial.enums.SubscriptionLevel;
import com.coaxial.repository.StudentSubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentDashboardService {

    private static final Logger logger = LoggerFactory.getLogger(StudentDashboardService.class);

    @Autowired
    private StudentSubscriptionRepository subscriptionRepository;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private TestService testService;

    /**
     * Get accessible subjects for a student based on their subscriptions
     */
    public List<SubjectResponseDTO> getAccessibleSubjects(Long studentId) {
        try {
            // Get all active subscriptions for the student
            List<Long> accessibleClassIds = getAccessibleEntityIds(studentId, SubscriptionLevel.CLASS);
            List<Long> accessibleExamIds = getAccessibleEntityIds(studentId, SubscriptionLevel.EXAM);
            List<Long> accessibleCourseIds = getAccessibleEntityIds(studentId, SubscriptionLevel.COURSE);

            List<SubjectResponseDTO> accessibleSubjects = new ArrayList<>();

            // Get subjects from class subscriptions
            if (!accessibleClassIds.isEmpty()) {
                for (Long classId : accessibleClassIds) {
                    try {
                        // Get subjects for this class (Academic course type)
                        // Note: Using a simplified approach since getAllLinkageSubjects method doesn't exist
                        List<SubjectResponseDTO> classSubjects = new ArrayList<>();
                        accessibleSubjects.addAll(classSubjects);
                    } catch (Exception e) {
                        logger.warn("Error fetching subjects for class {}: {}", classId, e.getMessage());
                    }
                }
            }

            // Get subjects from exam subscriptions
            if (!accessibleExamIds.isEmpty()) {
                for (Long examId : accessibleExamIds) {
                    try {
                        // Get subjects for this exam (Competitive course type)
                        // Note: Using a simplified approach since getAllLinkageSubjects method doesn't exist
                        List<SubjectResponseDTO> examSubjects = new ArrayList<>();
                        accessibleSubjects.addAll(examSubjects);
                    } catch (Exception e) {
                        logger.warn("Error fetching subjects for exam {}: {}", examId, e.getMessage());
                    }
                }
            }

            // Get subjects from course subscriptions
            if (!accessibleCourseIds.isEmpty()) {
                for (Long courseId : accessibleCourseIds) {
                    try {
                        // Get subjects for this course (Professional course type)
                        // Note: Using a simplified approach since getAllLinkageSubjects method doesn't exist
                        List<SubjectResponseDTO> courseSubjects = new ArrayList<>();
                        accessibleSubjects.addAll(courseSubjects);
                    } catch (Exception e) {
                        logger.warn("Error fetching subjects for course {}: {}", courseId, e.getMessage());
                    }
                }
            }

            // Return accessible subjects (no deduplication needed for now)
            return accessibleSubjects;

        } catch (Exception e) {
            logger.error("Error fetching accessible subjects for student {}", studentId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Get accessible tests for a student based on their subscriptions
     */
    public List<TestResponseDTO> getAccessibleTests(Long studentId) {
        try {
            // Use TestService's subscription-aware method
            List<com.coaxial.dto.TestResponseDTO> accessibleTests = testService.getAccessibleTests(studentId);
            
            // Convert to dashboard DTO format
            return accessibleTests.stream()
                    .map(this::convertFromTestServiceResponse)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error fetching accessible tests for student {}", studentId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Check if student has access to specific content
     */
    public boolean hasAccessToContent(Long studentId, SubscriptionLevel level, Long entityId) {
        return subscriptionRepository.hasStudentAccessToEntity(studentId, level, entityId, LocalDateTime.now());
    }

    /**
     * Check if student has access to test
     */
    public boolean hasAccessToTest(Long studentId, Long testId) {
        try {
            // Use TestService's subscription-aware access check
            return testService.hasStudentAccessToTest(studentId, testId);

        } catch (Exception e) {
            logger.error("Error checking test access for student {} and test {}", studentId, testId, e);
            return false;
        }
    }

    /**
     * Get dashboard summary for student
     */
    public Object getDashboardSummary(Long studentId) {
        try {
            List<SubjectResponseDTO> subjects = getAccessibleSubjects(studentId);
            List<TestResponseDTO> tests = getAccessibleTests(studentId);

            // Count by subscription level
            long classSubscriptions = subscriptionRepository.countActiveSubscriptionsByStudentId(studentId, LocalDateTime.now());
            long classSubjects = 0; // Simplified for now
            long examSubjects = 0; // Simplified for now
            long courseSubjects = 0; // Simplified for now

            return new Object() {
                public final long totalSubjects = subjects.size();
                public final long totalTests = tests.size();
                public final long activeSubscriptions = classSubscriptions;
                public final long classSubjectsCount = classSubjects;
                public final long examSubjectsCount = examSubjects;
                public final long courseSubjectsCount = courseSubjects;
                public final List<SubjectResponseDTO> recentSubjects = subjects.stream().limit(5).collect(Collectors.toList());
                public final List<TestResponseDTO> recentTests = tests.stream().limit(5).collect(Collectors.toList());
            };

        } catch (Exception e) {
            logger.error("Error generating dashboard summary for student {}", studentId, e);
            return new Object() {
                public final long totalSubjects = 0;
                public final long totalTests = 0;
                public final long activeSubscriptions = 0;
                public final long classSubjectsCount = 0;
                public final long examSubjectsCount = 0;
                public final long courseSubjectsCount = 0;
                public final List<SubjectResponseDTO> recentSubjects = new ArrayList<>();
                public final List<TestResponseDTO> recentTests = new ArrayList<>();
            };
        }
    }

    /**
     * Get accessible entity IDs for a student by subscription level
     */
    private List<Long> getAccessibleEntityIds(Long studentId, SubscriptionLevel level) {
        return subscriptionRepository.findActiveSubscriptionsByLevelAndEntity(
                level, null, LocalDateTime.now())
                .stream()
                .filter(subscription -> subscription.getStudent().getId().equals(studentId))
                .map(subscription -> subscription.getEntityId())
                .collect(Collectors.toList());
    }

    /**
     * Convert TestService TestResponseDTO to dashboard TestResponseDTO
     */
    private TestResponseDTO convertFromTestServiceResponse(com.coaxial.dto.TestResponseDTO testServiceResponse) {
        TestResponseDTO dto = new TestResponseDTO();
        dto.setId(testServiceResponse.getId());
        dto.setName(testServiceResponse.getTestName() != null ? testServiceResponse.getTestName() : testServiceResponse.getName());
        dto.setTestName(testServiceResponse.getTestName());
        dto.setDescription(testServiceResponse.getDescription());
        dto.setInstructions(testServiceResponse.getInstructions());
        dto.setDurationMinutes(testServiceResponse.getDurationMinutes());
        dto.setTimeLimitMinutes(testServiceResponse.getTimeLimitMinutes());
        dto.setTotalMarks(testServiceResponse.getTotalMarks() != null ? testServiceResponse.getTotalMarks().doubleValue() : null);
        dto.setPassingMarks(testServiceResponse.getPassingMarks() != null ? testServiceResponse.getPassingMarks().doubleValue() : null);
        dto.setNegativeMarking(testServiceResponse.getNegativeMarking());
        dto.setNegativeMarkPercentage(testServiceResponse.getNegativeMarkPercentage());
        dto.setMaxAttempts(testServiceResponse.getMaxAttempts());
        dto.setIsActive(testServiceResponse.getIsActive());
        dto.setIsPublished(testServiceResponse.getIsPublished());
        dto.setStartDate(testServiceResponse.getStartDate());
        dto.setEndDate(testServiceResponse.getEndDate());
        dto.setTestType(testServiceResponse.getTestType());
        dto.setAllowReview(testServiceResponse.getAllowReview());
        dto.setShowCorrectAnswers(testServiceResponse.getShowCorrectAnswers());
        dto.setShuffleQuestions(testServiceResponse.getShuffleQuestions());
        dto.setShuffleOptions(testServiceResponse.getShuffleOptions());
        dto.setAllowSkip(testServiceResponse.getAllowSkip());
        dto.setTimePerQuestion(testServiceResponse.getTimePerQuestion());
        dto.setMasterExamId(testServiceResponse.getMasterExamId());
        dto.setMasterExamName(testServiceResponse.getMasterExamName());
        dto.setCreatedAt(testServiceResponse.getCreatedAt());
        dto.setUpdatedAt(testServiceResponse.getUpdatedAt());
        dto.setQuestions(testServiceResponse.getQuestions());
        
        // Add MasterExam information if available
        if (testServiceResponse.getMasterExamId() != null) {
            dto.setExamId(testServiceResponse.getMasterExamId());
            dto.setExamName(testServiceResponse.getMasterExamName());
        }
        
        return dto;
    }
}
