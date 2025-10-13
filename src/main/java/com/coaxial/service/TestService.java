package com.coaxial.service;

import java.time.LocalDateTime;
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
import com.coaxial.entity.Chapter;
import com.coaxial.entity.ClassEntity;
import com.coaxial.entity.ClassSubject;
import com.coaxial.entity.Course;
import com.coaxial.entity.CourseSubject;
import com.coaxial.entity.CourseType;
import com.coaxial.entity.Exam;
import com.coaxial.entity.ExamSubject;
import com.coaxial.entity.MasterExam;
import com.coaxial.entity.Module;
import com.coaxial.entity.Question;
import com.coaxial.entity.Test;
import com.coaxial.entity.TestQuestion;
import com.coaxial.entity.Topic;
import com.coaxial.enums.SubscriptionLevel;
import com.coaxial.enums.TestCreationMode;
import com.coaxial.enums.TestLevel;
import com.coaxial.exception.ValidationException;
import com.coaxial.repository.ChapterRepository;
import com.coaxial.repository.ClassRepository;
import com.coaxial.repository.ClassSubjectRepository;
import com.coaxial.repository.CourseRepository;
import com.coaxial.repository.CourseSubjectRepository;
import com.coaxial.repository.CourseTypeRepository;
import com.coaxial.repository.ExamRepository;
import com.coaxial.repository.ExamSubjectRepository;
import com.coaxial.repository.MasterExamRepository;
import com.coaxial.repository.ModuleRepository;
import com.coaxial.repository.QuestionRepository;
import com.coaxial.repository.StudentSubscriptionRepository;
import com.coaxial.repository.TestQuestionRepository;
import com.coaxial.repository.TestRepository;
import com.coaxial.repository.TopicRepository;

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
    
    // Content hierarchy repositories for dual-mode test system
    @Autowired
    private CourseTypeRepository courseTypeRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private ClassRepository classRepository;
    
    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private ClassSubjectRepository classSubjectRepository;
    
    @Autowired
    private ExamSubjectRepository examSubjectRepository;
    
    @Autowired
    private CourseSubjectRepository courseSubjectRepository;
    
    @Autowired
    private TopicRepository topicRepository;
    
    @Autowired
    private ModuleRepository moduleRepository;
    
    @Autowired
    private ChapterRepository chapterRepository;

    @Transactional
    public TestResponseDTO createTest(TestRequestDTO request) {
        // Validate request based on test creation mode
        validateTestRequest(request);
        
        Test test = new Test();
        applyRequestToEntity(request, test);

        // Set master exam if provided
        if (request.getMasterExamId() != null) {
            MasterExam masterExam = masterExamRepository.findById(request.getMasterExamId())
                .orElseThrow(() -> new IllegalArgumentException("MasterExam not found: " + request.getMasterExamId()));
            test.setMasterExam(masterExam);
        }
        
        // Set content hierarchy relationships for CONTENT_BASED mode
        if (request.getTestCreationMode() == TestCreationMode.CONTENT_BASED) {
            setContentHierarchyRelationships(request, test);
        }

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
     * Get tests with filters for dual-mode system
     */
    @Transactional(readOnly = true)
    public List<TestResponseDTO> getTestsWithFilters(
            TestCreationMode testCreationMode, TestLevel testLevel, Long masterExamId,
            Long courseTypeId, Long courseId, Long classId, Long examId,
            Long subjectLinkageId, Long topicId, Long moduleId, Long chapterId,
            Boolean isPublished) {
        
        List<Test> tests = testRepository.findAll();
        
        // Apply filters
        if (testCreationMode != null) {
            tests = tests.stream()
                .filter(t -> testCreationMode.equals(t.getTestCreationMode()))
                .collect(Collectors.toList());
        }
        
        if (testLevel != null) {
            tests = tests.stream()
                .filter(t -> testLevel.equals(t.getTestLevel()))
                .collect(Collectors.toList());
        }
        
        if (masterExamId != null) {
            tests = tests.stream()
                .filter(t -> t.getMasterExam() != null && masterExamId.equals(t.getMasterExam().getId()))
                .collect(Collectors.toList());
        }
        
        if (courseTypeId != null) {
            tests = tests.stream()
                .filter(t -> t.getCourseType() != null && courseTypeId.equals(t.getCourseType().getId()))
                .collect(Collectors.toList());
        }
        
        if (courseId != null) {
            tests = tests.stream()
                .filter(t -> t.getCourse() != null && courseId.equals(t.getCourse().getId()))
                .collect(Collectors.toList());
        }
        
        if (classId != null) {
            tests = tests.stream()
                .filter(t -> t.getClassEntity() != null && classId.equals(t.getClassEntity().getId()))
                .collect(Collectors.toList());
        }
        
        if (examId != null) {
            tests = tests.stream()
                .filter(t -> t.getExam() != null && examId.equals(t.getExam().getId()))
                .collect(Collectors.toList());
        }
        
        if (subjectLinkageId != null) {
            tests = tests.stream()
                .filter(t -> subjectLinkageId.equals(t.getSubjectLinkageId()))
                .collect(Collectors.toList());
        }
        
        if (topicId != null) {
            tests = tests.stream()
                .filter(t -> t.getTopic() != null && topicId.equals(t.getTopic().getId()))
                .collect(Collectors.toList());
        }
        
        if (moduleId != null) {
            tests = tests.stream()
                .filter(t -> t.getModule() != null && moduleId.equals(t.getModule().getId()))
                .collect(Collectors.toList());
        }
        
        if (chapterId != null) {
            tests = tests.stream()
                .filter(t -> t.getChapter() != null && chapterId.equals(t.getChapter().getId()))
                .collect(Collectors.toList());
        }
        
        if (isPublished != null) {
            tests = tests.stream()
                .filter(t -> isPublished.equals(t.getIsPublished()))
                .collect(Collectors.toList());
        }
        
        return tests.stream()
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
     * Get tests accessible to a specific student with optional filters for course/class/exam
     */
    @Transactional(readOnly = true)
    public List<TestResponseDTO> getAccessibleTests(Long studentId, Long courseId, Long classId, Long examId) {
        return testRepository.findByIsActiveTrue().stream()
                .filter(test -> hasStudentAccessToTest(studentId, test))
                .filter(test -> {
                    // Apply course filter if provided
                    if (courseId != null && test.getCourse() != null) {
                        return courseId.equals(test.getCourse().getId());
                    }
                    // Apply class filter if provided
                    if (classId != null && test.getClassEntity() != null) {
                        return classId.equals(test.getClassEntity().getId());
                    }
                    // Apply exam filter if provided
                    if (examId != null && test.getExam() != null) {
                        return examId.equals(test.getExam().getId());
                    }
                    // If no filters provided, return all accessible tests
                    return courseId == null && classId == null && examId == null;
                })
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
     * Check if student has access to a specific test entity based on their active subscriptions.
     * Tests created at any level (course, class, exam, subject, topic, module, chapter) are accessible
     * if the student has a subscription for the parent class/exam/course.
     */
    private boolean hasStudentAccessToTest(Long studentId, Test test) {
        // Check if test is active and published
        if (!test.getIsActive() || !test.getIsPublished()) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // Check CONTENT_BASED tests (linked to course hierarchy)
        if (test.getTestCreationMode() == TestCreationMode.CONTENT_BASED) {
            // Check Course-level subscription (highest priority - grants access to all tests in course)
            if (test.getCourse() != null) {
                if (subscriptionRepository.hasStudentAccessToEntity(
                    studentId, SubscriptionLevel.COURSE, test.getCourse().getId(), now)) {
                    return true;
                }
            }
            
            // Check Class-level subscription (for academic courses)
            if (test.getClassEntity() != null) {
                if (subscriptionRepository.hasStudentAccessToEntity(
                    studentId, SubscriptionLevel.CLASS, test.getClassEntity().getId(), now)) {
                    return true;
                }
            }
            
            // Check Exam-level subscription (for competitive courses)
            if (test.getExam() != null) {
                if (subscriptionRepository.hasStudentAccessToEntity(
                    studentId, SubscriptionLevel.EXAM, test.getExam().getId(), now)) {
                    return true;
                }
            }
        }
        
        // Check EXAM_BASED tests (linked to MasterExam - general exam practice)
        if (test.getTestCreationMode() == TestCreationMode.EXAM_BASED) {
            if (test.getMasterExam() != null) {
                // For master exams, check if student has EXAM subscription
                if (subscriptionRepository.hasStudentAccessToEntity(
                    studentId, SubscriptionLevel.EXAM, test.getMasterExam().getId(), now)) {
                    return true;
                }
            }
        }
        
        // No valid subscription found - deny access
        return false;
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
        // Validate request based on test creation mode
        validateTestRequest(request);
        
        Test test = getById(id);
        applyRequestToEntity(request, test);

        // Set master exam if provided
        if (request.getMasterExamId() != null) {
            MasterExam masterExam = masterExamRepository.findById(request.getMasterExamId())
                .orElseThrow(() -> new IllegalArgumentException("MasterExam not found: " + request.getMasterExamId()));
            test.setMasterExam(masterExam);
        } else {
            test.setMasterExam(null); // Clear if not provided
        }
        
        // Set content hierarchy relationships for CONTENT_BASED mode
        if (request.getTestCreationMode() == TestCreationMode.CONTENT_BASED) {
            setContentHierarchyRelationships(request, test);
        } else {
            // Clear content hierarchy for EXAM_BASED mode
            test.setCourseType(null);
            test.setCourse(null);
            test.setClassEntity(null);
            test.setExam(null);
            test.setTopic(null);
            test.setModule(null);
            test.setChapter(null);
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
        
        // Dual-mode system fields
        test.setTestCreationMode(request.getTestCreationMode() != null ? request.getTestCreationMode() : TestCreationMode.EXAM_BASED);
        test.setTestLevel(request.getTestLevel());
        test.setSubjectLinkageId(request.getSubjectLinkageId());
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
        
        // Dual-mode system fields
        dto.setTestCreationMode(test.getTestCreationMode());
        dto.setTestLevel(test.getTestLevel());
        
        // Content hierarchy with resolved names
        if (test.getCourseType() != null) {
            dto.setCourseTypeId(test.getCourseType().getId());
            dto.setCourseTypeName(test.getCourseType().getName());
        }
        
        if (test.getCourse() != null) {
            dto.setCourseId(test.getCourse().getId());
            dto.setCourseName(test.getCourse().getName());
        }
        
        if (test.getClassEntity() != null) {
            dto.setClassId(test.getClassEntity().getId());
            dto.setClassName(test.getClassEntity().getName());
        }
        
        if (test.getExam() != null) {
            dto.setExamId(test.getExam().getId());
            dto.setExamName(test.getExam().getName());
        }
        
        // Resolve subject name from linkage
        dto.setSubjectLinkageId(test.getSubjectLinkageId());
        if (test.getSubjectLinkageId() != null && test.getCourseType() != null) {
            dto.setSubjectName(resolveSubjectName(test.getSubjectLinkageId(), test.getCourseType().getId()));
        }
        
        if (test.getTopic() != null) {
            dto.setTopicId(test.getTopic().getId());
            dto.setTopicName(test.getTopic().getName());
        }
        
        if (test.getModule() != null) {
            dto.setModuleId(test.getModule().getId());
            dto.setModuleName(test.getModule().getName());
        }
        
        if (test.getChapter() != null) {
            dto.setChapterId(test.getChapter().getId());
            dto.setChapterName(test.getChapter().getName());
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
    
    // ========== DUAL-MODE TEST SYSTEM VALIDATION AND HELPER METHODS ==========
    
    /**
     * Validate test request based on creation mode
     */
    private void validateTestRequest(TestRequestDTO request) {
        if (request.getTestCreationMode() == null) {
            request.setTestCreationMode(TestCreationMode.EXAM_BASED); // Default
        }
        
        if (request.getTestCreationMode() == TestCreationMode.EXAM_BASED) {
            validateExamBasedTest(request);
        } else if (request.getTestCreationMode() == TestCreationMode.CONTENT_BASED) {
            validateContentBasedTest(request);
        }
    }
    
    /**
     * Validate EXAM_BASED test rules
     */
    private void validateExamBasedTest(TestRequestDTO request) {
        if (request.getMasterExamId() == null) {
            throw new ValidationException("masterExamId is required for EXAM_BASED tests");
        }
        
        // Content linkage fields must be null for EXAM_BASED mode
        if (request.getCourseTypeId() != null || request.getTestLevel() != null || 
            request.getClassId() != null || request.getExamId() != null ||
            request.getSubjectLinkageId() != null || request.getTopicId() != null ||
            request.getModuleId() != null || request.getChapterId() != null) {
            throw new ValidationException("Content linkage fields must be null for EXAM_BASED tests");
        }
    }
    
    /**
     * Validate CONTENT_BASED test rules
     */
    private void validateContentBasedTest(TestRequestDTO request) {
        if (request.getCourseTypeId() == null) {
            throw new ValidationException("courseTypeId is required for CONTENT_BASED tests");
        }
        if (request.getCourseId() == null) {
            throw new ValidationException("courseId is required for CONTENT_BASED tests");
        }
        if (request.getTestLevel() == null) {
            throw new ValidationException("testLevel is required for CONTENT_BASED tests");
        }
        
        // Validate based on test level
        switch (request.getTestLevel()) {
            case CLASS_EXAM:
                validateClassExamLevel(request);
                break;
            case SUBJECT:
                validateSubjectLevel(request);
                break;
            case MODULE:
                validateModuleLevel(request);
                break;
            case CHAPTER:
                validateChapterLevel(request);
                break;
        }
    }
    
    /**
     * Validate CLASS_EXAM level requirements
     */
    private void validateClassExamLevel(TestRequestDTO request) {
        if (request.getClassId() == null && request.getExamId() == null) {
            throw new ValidationException("Either classId or examId is required for CLASS_EXAM level");
        }
        
        // Lower level fields must be null
        if (request.getSubjectLinkageId() != null || request.getTopicId() != null || 
            request.getModuleId() != null || request.getChapterId() != null) {
            throw new ValidationException("Subject, topic, module, and chapter fields must be null for CLASS_EXAM level");
        }
    }
    
    /**
     * Validate SUBJECT level requirements
     */
    private void validateSubjectLevel(TestRequestDTO request) {
        if (request.getClassId() == null && request.getExamId() == null) {
            throw new ValidationException("Either classId or examId is required for SUBJECT level");
        }
        if (request.getSubjectLinkageId() == null) {
            throw new ValidationException("subjectLinkageId is required for SUBJECT level");
        }
        
        // Lower level fields must be null
        if (request.getTopicId() != null || request.getModuleId() != null || request.getChapterId() != null) {
            throw new ValidationException("Topic, module, and chapter fields must be null for SUBJECT level");
        }
    }
    
    /**
     * Validate MODULE level requirements
     */
    private void validateModuleLevel(TestRequestDTO request) {
        if (request.getClassId() == null && request.getExamId() == null) {
            throw new ValidationException("Either classId or examId is required for MODULE level");
        }
        if (request.getSubjectLinkageId() == null) {
            throw new ValidationException("subjectLinkageId is required for MODULE level");
        }
        if (request.getTopicId() == null) {
            throw new ValidationException("topicId is required for MODULE level");
        }
        if (request.getModuleId() == null) {
            throw new ValidationException("moduleId is required for MODULE level");
        }
        
        // Lower level fields must be null
        if (request.getChapterId() != null) {
            throw new ValidationException("Chapter field must be null for MODULE level");
        }
    }
    
    /**
     * Validate CHAPTER level requirements
     */
    private void validateChapterLevel(TestRequestDTO request) {
        if (request.getClassId() == null && request.getExamId() == null) {
            throw new ValidationException("Either classId or examId is required for CHAPTER level");
        }
        if (request.getSubjectLinkageId() == null) {
            throw new ValidationException("subjectLinkageId is required for CHAPTER level");
        }
        if (request.getTopicId() == null) {
            throw new ValidationException("topicId is required for CHAPTER level");
        }
        if (request.getModuleId() == null) {
            throw new ValidationException("moduleId is required for CHAPTER level");
        }
        if (request.getChapterId() == null) {
            throw new ValidationException("chapterId is required for CHAPTER level");
        }
    }
    
    /**
     * Set content hierarchy relationships on Test entity
     */
    private void setContentHierarchyRelationships(TestRequestDTO request, Test test) {
        // Set CourseType
        if (request.getCourseTypeId() != null) {
            CourseType courseType = courseTypeRepository.findById(request.getCourseTypeId())
                .orElseThrow(() -> new IllegalArgumentException("CourseType not found: " + request.getCourseTypeId()));
            test.setCourseType(courseType);
        }
        
        // Set Course
        if (request.getCourseId() != null) {
            Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + request.getCourseId()));
            test.setCourse(course);
        }
        
        // Set ClassEntity or Exam
        if (request.getClassId() != null) {
            ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new IllegalArgumentException("Class not found: " + request.getClassId()));
            test.setClassEntity(classEntity);
        }
        
        if (request.getExamId() != null) {
            Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new IllegalArgumentException("Exam not found: " + request.getExamId()));
            test.setExam(exam);
        }
        
        // Set Topic
        if (request.getTopicId() != null) {
            Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new IllegalArgumentException("Topic not found: " + request.getTopicId()));
            test.setTopic(topic);
        }
        
        // Set Module
        if (request.getModuleId() != null) {
            Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new IllegalArgumentException("Module not found: " + request.getModuleId()));
            test.setModule(module);
        }
        
        // Set Chapter
        if (request.getChapterId() != null) {
            Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found: " + request.getChapterId()));
            test.setChapter(chapter);
        }
    }
    
    /**
     * Resolve subject name from linkageId and courseTypeId
     */
    private String resolveSubjectName(Long subjectLinkageId, Long courseTypeId) {
        if (subjectLinkageId == null || courseTypeId == null) {
            return null;
        }
        
        try {
            switch (courseTypeId.intValue()) {
                case 1: // Academic - ClassSubject
                    ClassSubject classSubject = classSubjectRepository.findById(subjectLinkageId).orElse(null);
                    return classSubject != null && classSubject.getSubject() != null ? 
                           classSubject.getSubject().getName() : null;
                           
                case 2: // Competitive - ExamSubject
                    ExamSubject examSubject = examSubjectRepository.findById(subjectLinkageId).orElse(null);
                    return examSubject != null && examSubject.getSubject() != null ? 
                           examSubject.getSubject().getName() : null;
                           
                case 3: // Professional - CourseSubject
                    CourseSubject courseSubject = courseSubjectRepository.findById(subjectLinkageId).orElse(null);
                    return courseSubject != null && courseSubject.getSubject() != null ? 
                           courseSubject.getSubject().getName() : null;
                           
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}


