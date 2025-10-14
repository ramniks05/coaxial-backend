package com.coaxial.service;

import com.coaxial.dto.*;
import com.coaxial.entity.*;
import com.coaxial.enums.TestSessionStatus;
import com.coaxial.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TestExecutionService {
    
    private static final Logger logger = LoggerFactory.getLogger(TestExecutionService.class);
    
    @Autowired
    private TestRepository testRepository;
    
    @Autowired
    private TestSessionRepository testSessionRepository;
    
    @Autowired
    private TestAttemptRepository testAttemptRepository;
    
    @Autowired
    private TestAnswerRepository testAnswerRepository;
    
    @Autowired
    private TestQuestionRepository testQuestionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TestService testService;
    
    /**
     * Start a new test - creates session and attempt
     */
    @Transactional
    public TestSessionResponseDTO startTest(Long testId, Long studentId, StartTestRequestDTO request) {
        // Get test
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found"));
        
        // Validate test is active and published
        if (!test.getIsActive() || !test.getIsPublished()) {
            throw new IllegalArgumentException("Test is not available");
        }
        
        // Validate student has access (subscription check)
        if (!testService.hasStudentAccessToTest(studentId, testId)) {
            throw new IllegalArgumentException("You do not have access to this test. Please purchase a subscription.");
        }
        
        // Get student
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        
        // Check if student already has an active session for this test
        testSessionRepository.findActiveSessionByTestAndStudent(testId, studentId)
                .ifPresent(session -> {
                    throw new IllegalArgumentException("You already have an active session for this test. Please continue or submit the existing attempt.");
                });
        
        // Check attempt limit
        Long attemptCount = testAttemptRepository.countAttemptsByTestAndStudent(testId, studentId);
        if (test.getMaxAttempts() != null && attemptCount >= test.getMaxAttempts()) {
            throw new IllegalArgumentException("You have reached the maximum number of attempts (" + test.getMaxAttempts() + ") for this test");
        }
        
        // Create session
        TestSession session = new TestSession();
        session.setTest(test);
        session.setStudent(student);
        session.setSessionId(UUID.randomUUID().toString());
        session.setStatus(TestSessionStatus.STARTED);
        session.setStartedAt(LocalDateTime.now());
        session.setIpAddress(request.getIpAddress());
        session.setUserAgent(request.getUserAgent());
        session.setIsActive(true);
        
        TestSession savedSession = testSessionRepository.save(session);
        logger.info("Created test session {} for test {} and student {}", savedSession.getSessionId(), testId, studentId);
        
        // Create attempt
        TestAttempt attempt = new TestAttempt();
        attempt.setTest(test);
        attempt.setStudent(student);
        attempt.setAttemptNumber(attemptCount.intValue() + 1);
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setIsSubmitted(false);
        attempt.setIsActive(true);
        
        // Get total questions
        List<TestQuestion> testQuestions = testQuestionRepository.findByTestIdOrderByQuestionOrderAsc(testId);
        attempt.setTotalQuestions(testQuestions.size());
        attempt.setTotalMarksAvailable(test.getTotalMarks() != null ? test.getTotalMarks().doubleValue() : 0.0);
        
        TestAttempt savedAttempt = testAttemptRepository.save(attempt);
        logger.info("Created test attempt {} (attempt number {}) for test {} and student {}", 
                savedAttempt.getId(), savedAttempt.getAttemptNumber(), testId, studentId);
        
        // Initialize answers for all questions
        for (TestQuestion tq : testQuestions) {
            TestAnswer answer = new TestAnswer();
            answer.setTestAttempt(savedAttempt);
            answer.setQuestion(tq.getQuestion());
            answer.setIsAnswered(false);
            answer.setIsCorrect(false);
            answer.setMarksObtained(0.0);
            testAnswerRepository.save(answer);
        }
        
        // Build response
        TestSessionResponseDTO response = new TestSessionResponseDTO();
        response.setSessionId(savedSession.getSessionId());
        response.setAttemptId(savedAttempt.getId());
        response.setTestId(test.getId());
        response.setTestName(test.getTestName());
        response.setTimeLimitMinutes(test.getTimeLimitMinutes());
        response.setTotalQuestions(testQuestions.size());
        response.setAttemptNumber(savedAttempt.getAttemptNumber());
        response.setStartedAt(savedSession.getStartedAt());
        response.setExpiresAt(savedSession.getStartedAt().plusMinutes(test.getTimeLimitMinutes()));
        response.setStatus(savedSession.getStatus().name());
        response.setNegativeMarking(test.getNegativeMarking());
        response.setNegativeMarkPercentage(test.getNegativeMarkPercentage());
        response.setAllowReview(test.getAllowReview());
        response.setShowCorrectAnswers(test.getShowCorrectAnswers());
        response.setAllowSkip(test.getAllowSkip());
        
        return response;
    }
    
    /**
     * Get test questions for active session
     */
    @Transactional(readOnly = true)
    public List<TestQuestionDTO> getTestQuestions(Long testId, String sessionId, Long studentId) {
        // Validate session
        TestSession session = testSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid session"));
        
        // Validate session belongs to student and test
        if (!session.getStudent().getId().equals(studentId) || !session.getTest().getId().equals(testId)) {
            throw new IllegalArgumentException("Invalid session for this test and student");
        }
        
        // Validate session is active
        if (!session.getStatus().equals(TestSessionStatus.STARTED) && 
            !session.getStatus().equals(TestSessionStatus.IN_PROGRESS)) {
            throw new IllegalArgumentException("Session is not active");
        }
        
        // Get test questions
        List<TestQuestion> testQuestions = testQuestionRepository.findByTestIdOrderByQuestionOrderAsc(testId);
        
        // Convert to DTO (don't show correct answers or explanations during test)
        return testQuestions.stream()
                .map(this::toQuestionDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Submit answer for a question
     */
    @Transactional
    public void submitAnswer(Long testId, SubmitAnswerRequestDTO request, Long studentId) {
        // Validate session
        TestSession session = testSessionRepository.findBySessionId(request.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid session"));
        
        // Validate session belongs to student
        if (!session.getStudent().getId().equals(studentId)) {
            throw new IllegalArgumentException("Invalid session for this student");
        }
        
        // Validate session is active
        if (!session.getStatus().equals(TestSessionStatus.STARTED) && 
            !session.getStatus().equals(TestSessionStatus.IN_PROGRESS)) {
            throw new IllegalArgumentException("Session is not active");
        }
        
        // Update session status to IN_PROGRESS if still STARTED
        if (session.getStatus().equals(TestSessionStatus.STARTED)) {
            session.setStatus(TestSessionStatus.IN_PROGRESS);
            testSessionRepository.save(session);
        }
        
        // Get active attempt
        TestAttempt attempt = testAttemptRepository.findActiveAttempt(testId, studentId)
                .orElseThrow(() -> new IllegalArgumentException("No active attempt found"));
        
        // Find or create answer
        TestAnswer answer = testAnswerRepository.findByAttemptIdAndQuestionId(attempt.getId(), request.getQuestionId())
                .orElse(null);
        
        if (answer == null) {
            throw new IllegalArgumentException("Question not found in this test");
        }
        
        // Update answer
        answer.setSelectedOptionId(request.getSelectedOptionId());
        
        if (request.getSelectedOptionId() != null) {
            // Check if answer is correct
            Question question = answer.getQuestion();
            boolean isCorrect = question.getOptions().stream()
                    .anyMatch(opt -> opt.getId().equals(request.getSelectedOptionId()) && opt.getIsCorrect());
            answer.setIsCorrect(isCorrect);
            answer.setIsAnswered(true);
            
            // Calculate marks - find TestQuestion by testId and questionId
            Double marksForQuestion = testQuestionRepository.findByTestIdAndQuestionId(testId, request.getQuestionId())
                    .map(tq -> tq.getMarks().doubleValue())
                    .orElse(0.0);
            
            if (isCorrect) {
                answer.setMarksObtained(marksForQuestion);
            } else {
                // Apply negative marking if enabled
                Test test = session.getTest();
                if (test.getNegativeMarking() != null && test.getNegativeMarking()) {
                    Double negativeMarks = marksForQuestion * (test.getNegativeMarkPercentage() != null ? test.getNegativeMarkPercentage() : 0.25);
                    answer.setMarksObtained(-negativeMarks);
                } else {
                    answer.setMarksObtained(0.0);
                }
            }
            
            answer.markAsAnswered();
        } else {
            // Question skipped
            answer.setIsAnswered(false);
            answer.setIsCorrect(false);
            answer.setMarksObtained(0.0);
        }
        
        testAnswerRepository.save(answer);
        logger.info("Saved answer for question {} in attempt {}", request.getQuestionId(), attempt.getId());
    }
    
    /**
     * Submit/End test
     */
    @Transactional
    public TestResultDTO submitTest(Long testId, String sessionId, Long studentId) {
        // Validate session
        TestSession session = testSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid session"));
        
        // Validate session belongs to student
        if (!session.getStudent().getId().equals(studentId) || !session.getTest().getId().equals(testId)) {
            throw new IllegalArgumentException("Invalid session for this test and student");
        }
        
        // Get active attempt
        TestAttempt attempt = testAttemptRepository.findActiveAttempt(testId, studentId)
                .orElseThrow(() -> new IllegalArgumentException("No active attempt found"));
        
        // Mark session as completed
        session.setStatus(TestSessionStatus.COMPLETED);
        session.setEndedAt(LocalDateTime.now());
        testSessionRepository.save(session);
        
        // Calculate time taken
        long timeTaken = java.time.Duration.between(attempt.getStartedAt(), LocalDateTime.now()).getSeconds();
        attempt.setTimeTakenSeconds((int) timeTaken);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setIsSubmitted(true);
        
        // Calculate results
        List<TestAnswer> answers = testAnswerRepository.findByAttemptId(attempt.getId());
        
        int answeredCount = 0;
        int correctCount = 0;
        int wrongCount = 0;
        double totalMarks = 0.0;
        
        for (TestAnswer answer : answers) {
            if (answer.getIsAnswered()) {
                answeredCount++;
                if (answer.getIsCorrect()) {
                    correctCount++;
                } else {
                    wrongCount++;
                }
            }
            totalMarks += (answer.getMarksObtained() != null ? answer.getMarksObtained() : 0.0);
        }
        
        attempt.setAnsweredQuestions(answeredCount);
        attempt.setCorrectAnswers(correctCount);
        attempt.setWrongAnswers(wrongCount);
        attempt.setUnansweredQuestions(attempt.getTotalQuestions() - answeredCount);
        attempt.setTotalMarksObtained(totalMarks);
        
        // Calculate percentage and pass/fail
        attempt.calculateResults();
        
        testAttemptRepository.save(attempt);
        logger.info("Submitted test attempt {} for test {} and student {}", attempt.getId(), testId, studentId);
        
        // Build result DTO
        return toResultDTO(attempt);
    }
    
    /**
     * Get test result
     */
    @Transactional(readOnly = true)
    public TestResultDTO getTestResult(Long testId, Long attemptId, Long studentId) {
        TestAttempt attempt = testAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found"));
        
        // Validate attempt belongs to student and test
        if (!attempt.getStudent().getId().equals(studentId) || !attempt.getTest().getId().equals(testId)) {
            throw new IllegalArgumentException("Invalid attempt");
        }
        
        // Validate attempt is submitted
        if (!attempt.getIsSubmitted()) {
            throw new IllegalArgumentException("Test not yet submitted");
        }
        
        return toResultDTO(attempt);
    }
    
    /**
     * Get active session for a test
     */
    @Transactional(readOnly = true)
    public TestSessionResponseDTO getActiveSession(Long testId, Long studentId) {
        return testSessionRepository.findActiveSessionByTestAndStudent(testId, studentId)
                .map(session -> {
                    TestAttempt attempt = testAttemptRepository.findActiveAttempt(testId, studentId)
                            .orElse(null);
                    
                    if (attempt == null) {
                        return null;
                    }
                    
                    Test test = session.getTest();
                    
                    TestSessionResponseDTO response = new TestSessionResponseDTO();
                    response.setSessionId(session.getSessionId());
                    response.setAttemptId(attempt.getId());
                    response.setTestId(test.getId());
                    response.setTestName(test.getTestName());
                    response.setTimeLimitMinutes(test.getTimeLimitMinutes());
                    response.setTotalQuestions(attempt.getTotalQuestions());
                    response.setAttemptNumber(attempt.getAttemptNumber());
                    response.setStartedAt(session.getStartedAt());
                    response.setExpiresAt(session.getStartedAt().plusMinutes(test.getTimeLimitMinutes()));
                    response.setStatus(session.getStatus().name());
                    response.setNegativeMarking(test.getNegativeMarking());
                    response.setNegativeMarkPercentage(test.getNegativeMarkPercentage());
                    response.setAllowReview(test.getAllowReview());
                    response.setShowCorrectAnswers(test.getShowCorrectAnswers());
                    response.setAllowSkip(test.getAllowSkip());
                    
                    return response;
                })
                .orElse(null);
    }
    
    /**
     * Abandon/Cancel active session without submitting
     */
    @Transactional
    public void abandonSession(Long testId, Long studentId) {
        // Find active session
        TestSession session = testSessionRepository.findActiveSessionByTestAndStudent(testId, studentId)
                .orElseThrow(() -> new IllegalArgumentException("No active session found for this test"));
        
        // Mark session as abandoned
        session.setStatus(TestSessionStatus.ABANDONED);
        session.setEndedAt(LocalDateTime.now());
        testSessionRepository.save(session);
        
        // Mark attempt as inactive (not submitted)
        testAttemptRepository.findActiveAttempt(testId, studentId)
                .ifPresent(attempt -> {
                    attempt.setIsActive(false);
                    testAttemptRepository.save(attempt);
                });
        
        logger.info("Abandoned session {} for test {} and student {}", session.getSessionId(), testId, studentId);
    }
    
    /**
     * Get all submitted attempts for a specific test by student
     */
    @Transactional(readOnly = true)
    public List<TestResultDTO> getTestAttempts(Long testId, Long studentId) {
        return testAttemptRepository.findByTestIdAndStudentId(testId, studentId).stream()
                .filter(TestAttempt::getIsSubmitted)
                .map(this::toResultDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all submitted attempts for a student across all tests
     */
    @Transactional(readOnly = true)
    public List<TestResultDTO> getStudentAttempts(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        
        return testAttemptRepository.findByStudentOrderByStartedAtDesc(student).stream()
                .filter(TestAttempt::getIsSubmitted)
                .map(this::toResultDTO)
                .collect(Collectors.toList());
    }
    
    // Helper methods
    
    private TestQuestionDTO toQuestionDTO(TestQuestion testQuestion) {
        Question q = testQuestion.getQuestion();
        
        TestQuestionDTO dto = new TestQuestionDTO();
        dto.setQuestionId(q.getId());
        dto.setQuestionText(q.getQuestionText());
        dto.setQuestionType(q.getQuestionType());
        dto.setQuestionOrder(testQuestion.getQuestionOrder());
        dto.setMarks(testQuestion.getMarks().doubleValue());
        dto.setImageUrl(null); // Image URL not in Question entity
        
        // Convert options (don't reveal correct answer during test)
        List<TestQuestionDTO.OptionDTO> options = q.getOptions().stream()
                .map(opt -> new TestQuestionDTO.OptionDTO(
                        opt.getId(),
                        opt.getOptionText(),
                        null // Option image URL not in QuestionOption entity
                ))
                .collect(Collectors.toList());
        
        dto.setOptions(options);
        
        return dto;
    }
    
    private TestResultDTO toResultDTO(TestAttempt attempt) {
        TestResultDTO dto = new TestResultDTO();
        dto.setAttemptId(attempt.getId());
        dto.setTestId(attempt.getTest().getId());
        dto.setTestName(attempt.getTest().getTestName());
        dto.setAttemptNumber(attempt.getAttemptNumber());
        dto.setStartedAt(attempt.getStartedAt());
        dto.setSubmittedAt(attempt.getSubmittedAt());
        dto.setTimeTakenSeconds(attempt.getTimeTakenSeconds());
        dto.setTotalQuestions(attempt.getTotalQuestions());
        dto.setAnsweredQuestions(attempt.getAnsweredQuestions());
        dto.setCorrectAnswers(attempt.getCorrectAnswers());
        dto.setWrongAnswers(attempt.getWrongAnswers());
        dto.setUnansweredQuestions(attempt.getUnansweredQuestions());
        dto.setTotalMarksObtained(attempt.getTotalMarksObtained());
        dto.setTotalMarksAvailable(attempt.getTotalMarksAvailable());
        dto.setPercentage(attempt.getPercentage());
        dto.setIsPassed(attempt.getIsPassed());
        dto.setPassingMarks(attempt.getTest().getPassingMarks() != null ? attempt.getTest().getPassingMarks().doubleValue() : null);
        return dto;
    }
}

