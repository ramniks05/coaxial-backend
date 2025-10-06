package com.coaxial.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coaxial.dto.TestQuestionRequestDTO;
import com.coaxial.dto.TestQuestionSummaryDTO;
import com.coaxial.dto.TestRequestDTO;
import com.coaxial.dto.TestResponseDTO;
import com.coaxial.entity.MasterExam;
import com.coaxial.entity.Question;
import com.coaxial.entity.Test;
import com.coaxial.entity.TestQuestion;
import com.coaxial.repository.MasterExamRepository;
import com.coaxial.repository.QuestionRepository;
import com.coaxial.repository.TestQuestionRepository;
import com.coaxial.repository.TestRepository;
import com.coaxial.repository.StudentSubscriptionRepository;
import com.coaxial.enums.SubscriptionLevel;
import com.coaxial.enums.PaymentStatus;

@Service
public class TestService {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private TestQuestionRepository testQuestionRepository;

    @Autowired
    private MasterExamRepository masterExamRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private StudentSubscriptionRepository subscriptionRepository;

    @Transactional
    public TestResponseDTO createTest(TestRequestDTO request) {
        Test test = new Test();
        applyRequestToEntity(request, test);

        MasterExam masterExam = masterExamRepository.findById(request.getMasterExamId())
            .orElseThrow(() -> new IllegalArgumentException("MasterExam not found: " + request.getMasterExamId()));
        test.setMasterExam(masterExam);

        Test saved = testRepository.save(test);

        if (request.getQuestions() != null && !request.getQuestions().isEmpty()) {
            addQuestionsInternal(saved.getId(), request.getQuestions());
        }

        return toResponse(getById(saved.getId()));
    }

    @Transactional(readOnly = true)
    public List<TestResponseDTO> getAll() {
        return testRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get tests accessible to a specific student based on their subscriptions
     */
    @Transactional(readOnly = true)
    public List<TestResponseDTO> getAccessibleTests(Long studentId) {
        return testRepository.findByIsActiveTrue().stream()
                .filter(test -> hasStudentAccessToTest(studentId, test))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get active tests only
     */
    @Transactional(readOnly = true)
    public List<TestResponseDTO> getActiveTests() {
        return testRepository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get published tests only
     */
    @Transactional(readOnly = true)
    public List<TestResponseDTO> getPublishedTests() {
        return testRepository.findByIsPublishedTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get tests by MasterExam
     */
    @Transactional(readOnly = true)
    public List<TestResponseDTO> getTestsByMasterExam(Long masterExamId) {
        return testRepository.findByMasterExamIdAndIsActiveTrue(masterExamId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Check if student has access to a specific test
     */
    @Transactional(readOnly = true)
    public boolean hasStudentAccessToTest(Long studentId, Long testId) {
        Test test = testRepository.findById(testId).orElse(null);
        if (test == null || !test.getIsActive()) {
            return false;
        }
        return hasStudentAccessToTest(studentId, test);
    }

    /**
     * Check if student has access to a specific test entity
     */
    private boolean hasStudentAccessToTest(Long studentId, Test test) {
        // For now, allow access to all active tests
        // This can be enhanced with specific subscription logic
        return test.getIsActive() && test.getIsPublished();
        
        // Future enhancement: Check subscription based on test's linked entities
        // if (test.getMasterExam() != null) {
        //     return subscriptionRepository.hasStudentAccessToEntity(
        //         studentId, SubscriptionLevel.EXAM, test.getMasterExam().getId(), 
        //         java.time.LocalDateTime.now());
        // }
        // Add more subscription checks based on test relationships
    }

    @Transactional(readOnly = true)
    public Test getById(Long id) {
        return testRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Test not found: " + id));
    }

    @Transactional(readOnly = true)
    public TestResponseDTO getByIdAsDTO(Long id) {
        return toResponse(getById(id));
    }

    @Transactional
    public TestResponseDTO updateTest(Long id, TestRequestDTO request) {
        Test test = getById(id);
        applyRequestToEntity(request, test);

        if (request.getMasterExamId() != null) {
            MasterExam masterExam = masterExamRepository.findById(request.getMasterExamId())
                .orElseThrow(() -> new IllegalArgumentException("MasterExam not found: " + request.getMasterExamId()));
            test.setMasterExam(masterExam);
        }

        Test saved = testRepository.save(test);
        return toResponse(saved);
    }

    @Transactional
    public void deleteTest(Long id) {
        if (!testRepository.existsById(id)) {
            throw new IllegalArgumentException("Test not found: " + id);
        }
        testRepository.deleteById(id);
    }

    @Transactional
    public List<TestQuestionSummaryDTO> addQuestions(Long testId, List<TestQuestionRequestDTO> questions) {
        getById(testId); // ensure exists
        return addQuestionsInternal(testId, questions);
    }

    @Transactional(readOnly = true)
    public List<TestQuestionSummaryDTO> getQuestions(Long testId) {
        getById(testId);
        return testQuestionRepository.findByTestIdOrderByQuestionOrderAsc(testId)
            .stream().map(this::toSummary).collect(Collectors.toList());
    }

    @Transactional
    public TestQuestionSummaryDTO addQuestion(Long testId, TestQuestionRequestDTO q) {
        List<TestQuestionRequestDTO> list = new ArrayList<>();
        list.add(q);
        return addQuestionsInternal(testId, list).get(0);
    }

    @Transactional
    public TestQuestionSummaryDTO updateQuestion(Long testId, Long testQuestionId, TestQuestionRequestDTO q) {
        TestQuestion tq = testQuestionRepository.findByIdAndTestId(testQuestionId, testId)
            .orElseThrow(() -> new IllegalArgumentException("TestQuestion not found: " + testQuestionId));

        if (q.getQuestionId() != null && !q.getQuestionId().equals(tq.getQuestion().getId())) {
            Question question = questionRepository.findById(q.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + q.getQuestionId()));
            tq.setQuestion(question);
        }
        if (q.getQuestionOrder() != null) {
            tq.setQuestionOrder(q.getQuestionOrder());
        }
        if (q.getMarks() != null) {
            tq.setMarks(q.getMarks());
        }
        if (q.getNegativeMarks() != null) {
            tq.setNegativeMarks(q.getNegativeMarks());
        }

        TestQuestion saved = testQuestionRepository.save(tq);
        return toSummary(saved);
    }

    @Transactional
    public void removeQuestion(Long testId, Long testQuestionId) {
        long deleted = testQuestionRepository.deleteByIdAndTestId(testQuestionId, testId);
        if (deleted == 0) {
            throw new IllegalArgumentException("TestQuestion not found: " + testQuestionId);
        }
    }

    private List<TestQuestionSummaryDTO> addQuestionsInternal(Long testId, List<TestQuestionRequestDTO> questions) {
        List<TestQuestion> entities = new ArrayList<>();
        Test testRef = new Test();
        testRef.setId(testId);

        for (TestQuestionRequestDTO q : questions) {
            Question question = questionRepository.findById(q.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + q.getQuestionId()));

            TestQuestion tq = new TestQuestion();
            tq.setTest(testRef);
            tq.setQuestion(question);
            tq.setQuestionOrder(q.getQuestionOrder());
            tq.setMarks(q.getMarks());
            tq.setNegativeMarks(q.getNegativeMarks());
            entities.add(tq);
        }

        List<TestQuestion> saved = testQuestionRepository.saveAll(entities);
        return saved.stream().map(this::toSummary).collect(Collectors.toList());
    }

    private void applyRequestToEntity(TestRequestDTO request, Test test) {
        test.setTestName(request.getTestName());
        test.setDescription(request.getDescription());
        test.setInstructions(request.getInstructions());
        test.setTimeLimitMinutes(request.getTimeLimitMinutes());
        test.setTotalMarks(request.getTotalMarks());
        test.setPassingMarks(request.getPassingMarks());
        test.setNegativeMarking(Boolean.TRUE.equals(request.getNegativeMarking()));
        test.setNegativeMarkPercentage(request.getNegativeMarkPercentage());
        test.setMaxAttempts(request.getMaxAttempts());
        test.setIsActive(Boolean.TRUE.equals(request.getIsActive()));
        test.setIsPublished(Boolean.TRUE.equals(request.getIsPublished()));
        if (request.getStartDate() != null) {
            test.setStartDate(request.getStartDate().atStartOfDay());
        } else {
            test.setStartDate(null);
        }
        if (request.getEndDate() != null) {
            // set to end of day 23:59:59 for an inclusive end
            test.setEndDate(request.getEndDate().atTime(23, 59, 59));
        } else {
            test.setEndDate(null);
        }
        test.setTestType(request.getTestType());
        test.setAllowReview(Boolean.TRUE.equals(request.getAllowReview()));
        test.setShowCorrectAnswers(Boolean.TRUE.equals(request.getShowCorrectAnswers()));
        test.setShuffleQuestions(Boolean.TRUE.equals(request.getShuffleQuestions()));
        test.setShuffleOptions(Boolean.TRUE.equals(request.getShuffleOptions()));
        test.setAllowSkip(Boolean.TRUE.equals(request.getAllowSkip()));
        test.setTimePerQuestion(request.getTimePerQuestion());
    }

    private TestResponseDTO toResponse(Test test) {
        TestResponseDTO dto = new TestResponseDTO();
        dto.setId(test.getId());
        dto.setTestName(test.getTestName());
        dto.setDescription(test.getDescription());
        dto.setInstructions(test.getInstructions());
        dto.setTimeLimitMinutes(test.getTimeLimitMinutes());
        dto.setTotalMarks(test.getTotalMarks() != null ? test.getTotalMarks().doubleValue() : null);
        dto.setPassingMarks(test.getPassingMarks() != null ? test.getPassingMarks().doubleValue() : null);
        dto.setNegativeMarking(test.getNegativeMarking());
        dto.setNegativeMarkPercentage(test.getNegativeMarkPercentage());
        dto.setMaxAttempts(test.getMaxAttempts());
        dto.setIsActive(test.getIsActive());
        dto.setIsPublished(test.getIsPublished());
        dto.setStartDate(test.getStartDate() != null ? test.getStartDate().toLocalDate() : null);
        dto.setEndDate(test.getEndDate() != null ? test.getEndDate().toLocalDate() : null);
        dto.setTestType(test.getTestType());
        dto.setAllowReview(test.getAllowReview());
        dto.setShowCorrectAnswers(test.getShowCorrectAnswers());
        dto.setShuffleQuestions(test.getShuffleQuestions());
        dto.setShuffleOptions(test.getShuffleOptions());
        dto.setAllowSkip(test.getAllowSkip());
        dto.setTimePerQuestion(test.getTimePerQuestion());
        if (test.getMasterExam() != null) {
            dto.setMasterExamId(test.getMasterExam().getId());
            dto.setMasterExamName(test.getMasterExam().getExamName());
        }

        List<TestQuestion> tqs = testQuestionRepository.findByTestIdOrderByQuestionOrderAsc(test.getId());
        dto.setQuestions(tqs.stream().map(this::toSummary).collect(Collectors.toList()));
        return dto;
    }

    private TestQuestionSummaryDTO toSummary(TestQuestion tq) {
        TestQuestionSummaryDTO dto = new TestQuestionSummaryDTO();
        dto.setId(tq.getId());
        dto.setQuestionId(tq.getQuestion().getId());
        dto.setQuestionText(tq.getQuestion().getQuestionText());
        dto.setQuestionOrder(tq.getQuestionOrder());
        dto.setMarks(tq.getMarks());
        dto.setNegativeMarks(tq.getNegativeMarks());
        return dto;
    }
}


