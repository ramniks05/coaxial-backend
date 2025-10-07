package com.coaxial.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coaxial.dto.QuestionExamHistoryResponseDTO;
import com.coaxial.dto.QuestionExamSuitabilityResponseDTO;
import com.coaxial.dto.QuestionFilterRequestDTO;
import com.coaxial.dto.QuestionRequestDTO;
import com.coaxial.dto.QuestionResponseDTO;
import com.coaxial.entity.Chapter;
import com.coaxial.entity.ClassSubject;
import com.coaxial.entity.CourseSubject;
import com.coaxial.entity.ExamSubject;
import com.coaxial.entity.MasterExam;
import com.coaxial.entity.MasterYear;
import com.coaxial.entity.Question;
import com.coaxial.entity.QuestionExamHistory;
import com.coaxial.entity.QuestionExamSuitability;
import com.coaxial.entity.QuestionOption;
import com.coaxial.entity.Subject;
import com.coaxial.entity.Topic;
import com.coaxial.entity.User;
import com.coaxial.repository.ChapterRepository;
import com.coaxial.repository.ClassSubjectRepository;
import com.coaxial.repository.CourseSubjectRepository;
import com.coaxial.repository.ExamSubjectRepository;
import com.coaxial.repository.MasterExamRepository;
import com.coaxial.repository.MasterYearRepository;
import com.coaxial.repository.QuestionExamHistoryRepository;
import com.coaxial.repository.QuestionExamSuitabilityRepository;
import com.coaxial.repository.QuestionRepository;

@Service
@Transactional
public class QuestionService {
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private ChapterRepository chapterRepository;
    
    @Autowired
    private QuestionExamHistoryRepository questionExamHistoryRepository;
    
    @Autowired
    private QuestionExamSuitabilityRepository questionExamSuitabilityRepository;
    
    @Autowired
    private MasterExamRepository masterExamRepository;
    
    @Autowired
    private MasterYearRepository masterYearRepository;
    
    @Autowired
    private ClassSubjectRepository classSubjectRepository;
    
    @Autowired
    private ExamSubjectRepository examSubjectRepository;
    
    @Autowired
    private CourseSubjectRepository courseSubjectRepository;
    
    public List<QuestionResponseDTO> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(this::createQuestionResponseDTO)
                .collect(Collectors.toList());
    }
    
    public List<QuestionResponseDTO> getQuestionsByChapterId(Long chapterId) {
        return questionRepository.findByChapterIdAndIsActiveTrueOrderByDisplayOrderAsc(chapterId).stream()
                .map(this::createQuestionResponseDTO)
                .collect(Collectors.toList());
    }
    
    public List<QuestionResponseDTO> getQuestionsByModuleId(Long moduleId) {
        return questionRepository.findByModuleIdAndIsActiveTrueOrderByDisplayOrderAsc(moduleId).stream()
                .map(this::createQuestionResponseDTO)
                .collect(Collectors.toList());
    }
    
    public List<QuestionResponseDTO> getQuestionsByTopicId(Long topicId) {
        return questionRepository.findByTopicIdAndIsActiveTrueOrderByDisplayOrderAsc(topicId).stream()
                .map(this::createQuestionResponseDTO)
                .collect(Collectors.toList());
    }
    
    public List<QuestionResponseDTO> getQuestionsBySubjectId(Long subjectId) {
        return questionRepository.findBySubjectIdAndIsActiveTrueOrderByDisplayOrderAsc(subjectId).stream()
                .map(this::createQuestionResponseDTO)
                .collect(Collectors.toList());
    }
    
    public List<QuestionResponseDTO> getQuestionsWithFilters(Boolean isActive, String questionType, 
                                                           String difficultyLevel, Long chapterId, 
                                                           Long moduleId, Long topicId, Long subjectId) {
        List<Question> questions = questionRepository.findAll();
        
        return questions.stream()
                .filter(question -> isActive == null || question.getIsActive().equals(isActive))
                .filter(question -> questionType == null || question.getQuestionType().equals(questionType))
                .filter(question -> difficultyLevel == null || question.getDifficultyLevel().equals(difficultyLevel))
                .filter(question -> chapterId == null || (question.getChapter() != null && question.getChapter().getId().equals(chapterId)))
                .filter(question -> moduleId == null || question.getModuleId() != null && question.getModuleId().equals(moduleId))
                .filter(question -> topicId == null || question.getTopicId() != null && question.getTopicId().equals(topicId))
                .filter(question -> subjectId == null || question.getSubjectId() != null && question.getSubjectId().equals(subjectId))
                .map(this::createQuestionResponseDTO)
                .collect(Collectors.toList());
    }
    
    public Page<QuestionResponseDTO> getQuestionsPaginated(Pageable pageable) {
        Page<Question> questions = questionRepository.findAll(pageable);
        return questions.map(this::createQuestionResponseDTO);
    }
    
    // Enhanced filtering methods
    public Page<QuestionResponseDTO> getQuestionsWithEnhancedFilters(QuestionFilterRequestDTO filter) {
        try {
            System.out.println("Filter request: " + filter.toString());
            
            Pageable pageable = Pageable.ofSize(filter.getSize()).withPage(filter.getPage());
            System.out.println("Pageable created: page=" + pageable.getPageNumber() + ", size=" + pageable.getPageSize());
            
            Page<Question> questions = questionRepository.findQuestionsWithEnhancedFilters(
                filter.getIsActive(),
                filter.getQuestionType(),
                filter.getDifficultyLevel(),
                filter.getMinMarks(),
                filter.getMaxMarks(),
                filter.getCourseTypeId(),
                filter.getRelationshipId(),
                filter.getSubjectId(),
                filter.getTopicId(),
                filter.getModuleId(),
                filter.getChapterId(),
                filter.getExamIds(),
                filter.getSuitabilityLevels(),
                filter.getAppearedYears(),
                pageable
            );
            
            System.out.println("Found " + questions.getTotalElements() + " questions");
            
            return questions.map(this::createQuestionResponseDTO);
        } catch (Exception e) {
            System.err.println("Error in getQuestionsWithEnhancedFilters: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to filter questions: " + e.getMessage(), e);
        }
    }
    
    public List<QuestionResponseDTO> getQuestionsByExamSuitability(List<Long> examIds, List<String> suitabilityLevels) {
        List<Question> questions = questionRepository.findQuestionsByExamSuitability(examIds, suitabilityLevels);
        return questions.stream()
                .map(this::createQuestionResponseDTO)
                .collect(Collectors.toList());
    }
    
    public List<QuestionResponseDTO> getPreviouslyAskedQuestions(List<Long> examIds, List<Integer> appearedYears,
                                                               List<String> examSessions, Integer minMarksInExam,
                                                               Integer maxMarksInExam, List<String> questionNumbers,
                                                               List<String> examDifficulties) {
        List<Question> questions = questionRepository.findPreviouslyAskedQuestions(
            examIds, appearedYears, examSessions, minMarksInExam, 
            maxMarksInExam, questionNumbers, examDifficulties
        );
        return questions.stream()
                .map(this::createQuestionResponseDTO)
                .collect(Collectors.toList());
    }
    
    public List<QuestionResponseDTO> getQuestionsByAcademicLevel(Long courseTypeId, Long relationshipId) {
        return questionRepository.findByCourseTypeIdAndRelationshipIdAndIsActiveTrueOrderByDisplayOrderAsc(
                courseTypeId, relationshipId)
                .stream()
                .map(this::createQuestionResponseDTO)
                .collect(Collectors.toList());
    }
    
    public List<QuestionResponseDTO> getQuestionsByHierarchy(Long courseTypeId, Long relationshipId, 
                                                           Long subjectId, Long topicId, 
                                                           Long moduleId, Long chapterId) {
        List<Question> questions = questionRepository.findQuestionsWithBasicFilters(
            true, // isActive
            null, // questionType
            null, // difficultyLevel
            null, // minMarks
            null, // maxMarks
            courseTypeId,
            relationshipId,
            subjectId,
            topicId,
            moduleId,
            chapterId,
            null, // createdAfter
            null  // createdBefore
        );
        
        return questions.stream()
                .map(this::createQuestionResponseDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<QuestionResponseDTO> getQuestionById(Long id) {
        return questionRepository.findById(id)
                .map(this::createQuestionResponseDTO);
    }
    
    public QuestionResponseDTO createQuestion(QuestionRequestDTO questionRequestDTO) {
        // Validate chapter exists
        Chapter chapter = chapterRepository.findById(questionRequestDTO.getChapterId())
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found with ID: " + questionRequestDTO.getChapterId()));
        
        Question question = new Question();
        question.setQuestionText(questionRequestDTO.getQuestionText());
        question.setQuestionType(questionRequestDTO.getQuestionType());
        question.setDifficultyLevel(questionRequestDTO.getDifficultyLevel());
        question.setMarks(questionRequestDTO.getMarks());
        question.setNegativeMarks(questionRequestDTO.getNegativeMarks());
        question.setTimeLimitSeconds(questionRequestDTO.getTimeLimitSeconds());
        question.setExplanation(questionRequestDTO.getExplanation());
        question.setChapter(chapter);
        
        // Auto-resolve hierarchy from chapter (same pattern as ChapterService)
        question.setModuleId(chapter.getModule().getId());
        if (chapter.getModule().getTopic() != null) {
            question.setTopicId(chapter.getModule().getTopic().getId());
            // Resolve courseTypeId and relationshipId from topic
            question.setCourseTypeId(chapter.getModule().getTopic().getCourseTypeId());
            question.setRelationshipId(chapter.getModule().getTopic().getRelationshipId());
        }
        
        // Set additional fields from DTO if provided (for flexibility)
        if (questionRequestDTO.getCourseTypeId() != null) {
            question.setCourseTypeId(questionRequestDTO.getCourseTypeId());
        }
        if (questionRequestDTO.getRelationshipId() != null) {
            question.setRelationshipId(questionRequestDTO.getRelationshipId());
        }
        if (questionRequestDTO.getTopicId() != null) {
            question.setTopicId(questionRequestDTO.getTopicId());
        }
        if (questionRequestDTO.getModuleId() != null) {
            question.setModuleId(questionRequestDTO.getModuleId());
        }
        if (questionRequestDTO.getSubjectId() != null) {
            question.setSubjectId(questionRequestDTO.getSubjectId());
        }
        
        question.setDisplayOrder(questionRequestDTO.getDisplayOrder());
        question.setIsActive(questionRequestDTO.getIsActive());
        question.setCreatedBy(getCurrentUser());
        
        // Handle question options
        if (questionRequestDTO.getOptions() != null && !questionRequestDTO.getOptions().isEmpty()) {
            List<QuestionOption> options = questionRequestDTO.getOptions().stream()
                    .map(optionDTO -> {
                        QuestionOption option = new QuestionOption();
                        option.setQuestion(question);
                        option.setOptionText(optionDTO.getOptionText());
                        option.setIsCorrect(optionDTO.getIsCorrect());
                        option.setDisplayOrder(optionDTO.getDisplayOrder());
                        return option;
                    })
                    .collect(Collectors.toList());
            question.setOptions(options);
        }
        
        Question savedQuestion = questionRepository.save(question);
        
        // Handle detailed exam histories
        if (questionRequestDTO.getExamHistories() != null && !questionRequestDTO.getExamHistories().isEmpty()) {
            // Pre-validate required fields for clearer errors
            for (var h : questionRequestDTO.getExamHistories()) {
                if (h.getMasterExamId() == null) {
                    throw new IllegalArgumentException("examHistories.masterExamId is required");
                }
                if (h.getAppearedYear() == null) {
                    throw new IllegalArgumentException("examHistories.appearedYear is required");
                }
            }

            // First build with yearId as sent; auto-fill session if blank
            List<QuestionExamHistory> examHistories = questionRequestDTO.getExamHistories().stream()
                    .map(historyDTO -> {
                        MasterExam masterExam = masterExamRepository.findById(historyDTO.getMasterExamId())
                                .orElseThrow(() -> new IllegalArgumentException("Master exam not found with ID: " + historyDTO.getMasterExamId()));
                        
                        QuestionExamHistory examHistory = new QuestionExamHistory();
                        examHistory.setQuestion(savedQuestion);
                        examHistory.setMasterExam(masterExam);
                        // Resolve year reference
                        MasterYear masterYear;
                        if (historyDTO.getAppearedYearId() != null) {
                            masterYear = masterYearRepository.findById(historyDTO.getAppearedYearId())
                                    .orElseThrow(() -> new IllegalArgumentException("Year not found with ID: " + historyDTO.getAppearedYearId()));
                        } else if (historyDTO.getAppearedYear() != null) {
                            // Try to find by value; if not found, create minimal entry
                            Integer yearValue = historyDTO.getAppearedYear() >= 1000 ? historyDTO.getAppearedYear() : (2020 + historyDTO.getAppearedYear());
                            masterYear = masterYearRepository.findByYearValue(yearValue)
                                    .orElseThrow(() -> new IllegalArgumentException("Year not found with value: " + yearValue));
                        } else {
                            throw new IllegalArgumentException("examHistories.appearedYearId or appearedYear is required");
                        }
                        examHistory.setAppearedYear(masterYear);
                        // Session: use provided, else year value
                        String session = historyDTO.getAppearedSession();
                        if (session == null || session.isEmpty()) {
                            session = String.valueOf(masterYear.getYearValue());
                        }
                        examHistory.setAppearedSession(session);
                        examHistory.setMarksInExam(historyDTO.getMarksInExam());
                        examHistory.setQuestionNumberInExam(historyDTO.getQuestionNumberInExam());
                        examHistory.setDifficultyInExam(historyDTO.getDifficultyInExam());
                        examHistory.setNotes(historyDTO.getNotes());
                        examHistory.setCreatedBy(getCurrentUser());
                        
                        return examHistory;
                    })
                    .collect(Collectors.toList());

            try {
                questionExamHistoryRepository.saveAll(examHistories);
            } catch (org.springframework.dao.DataIntegrityViolationException ex) {
                // Retry once by flipping year representation (ID <-> 4-digit) to satisfy DB CHECK
                List<QuestionExamHistory> retryHistories = questionRequestDTO.getExamHistories().stream()
                        .map(historyDTO -> {
                            MasterExam masterExam = masterExamRepository.findById(historyDTO.getMasterExamId())
                                    .orElseThrow(() -> new IllegalArgumentException("Master exam not found with ID: " + historyDTO.getMasterExamId()));
                            
                            QuestionExamHistory examHistory = new QuestionExamHistory();
                            examHistory.setQuestion(savedQuestion);
                            examHistory.setMasterExam(masterExam);
                            Integer in = historyDTO.getAppearedYear();
                            int flippedYearValue = (in != null && in >= 1000) ? (in - 2020) : (2020 + in);
                            // For retry, use the 4-digit year value
                            int yearValue = (in != null && in >= 1000) ? in : (2020 + in);
                            MasterYear flippedYear = masterYearRepository.findByYearValue(yearValue)
                                    .orElseThrow(() -> new IllegalArgumentException("Year not found with value: " + yearValue));
                            examHistory.setAppearedYear(flippedYear);
                            String session = historyDTO.getAppearedSession();
                            if (session == null || session.isEmpty()) {
                                int displayYear = (in != null && in >= 1000) ? in : (in != null ? 2020 + in : 0);
                                session = String.valueOf(displayYear);
                            }
                            examHistory.setAppearedSession(session);
                            examHistory.setMarksInExam(historyDTO.getMarksInExam());
                            examHistory.setQuestionNumberInExam(historyDTO.getQuestionNumberInExam());
                            examHistory.setDifficultyInExam(historyDTO.getDifficultyInExam());
                            examHistory.setNotes(historyDTO.getNotes());
                            examHistory.setCreatedBy(getCurrentUser());
                            return examHistory;
                        })
                        .collect(Collectors.toList());

                questionExamHistoryRepository.saveAll(retryHistories);
            }
        }
        
        // Handle simple exam suitabilities: IDs only, default suitability level
        if (questionRequestDTO.getExamSuitabilities() != null && !questionRequestDTO.getExamSuitabilities().isEmpty()) {
            List<QuestionExamSuitability> examSuitabilities = questionRequestDTO.getExamSuitabilities().stream()
                    .map(examId -> {
                        MasterExam masterExam = masterExamRepository.findById(examId)
                                .orElseThrow(() -> new IllegalArgumentException("Master exam not found with ID: " + examId));
                        
                        QuestionExamSuitability examSuitability = new QuestionExamSuitability();
                        examSuitability.setQuestion(savedQuestion);
                        examSuitability.setMasterExam(masterExam);
                        examSuitability.setSuitabilityLevel("MEDIUM");
                        examSuitability.setCreatedBy(getCurrentUser());
                        
                        return examSuitability;
                    })
                    .collect(Collectors.toList());
            questionExamSuitabilityRepository.saveAll(examSuitabilities);
        }
        
        return createQuestionResponseDTO(savedQuestion);
    }

    // removed unused normalization helper
    
    public QuestionResponseDTO updateQuestion(Long id, QuestionRequestDTO questionRequestDTO) {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + id));
        
        // Validate chapter exists
        Chapter chapter = chapterRepository.findById(questionRequestDTO.getChapterId())
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found with ID: " + questionRequestDTO.getChapterId()));
        
        existingQuestion.setQuestionText(questionRequestDTO.getQuestionText());
        existingQuestion.setQuestionType(questionRequestDTO.getQuestionType());
        existingQuestion.setDifficultyLevel(questionRequestDTO.getDifficultyLevel());
        existingQuestion.setMarks(questionRequestDTO.getMarks());
        existingQuestion.setNegativeMarks(questionRequestDTO.getNegativeMarks());
        existingQuestion.setTimeLimitSeconds(questionRequestDTO.getTimeLimitSeconds());
        existingQuestion.setExplanation(questionRequestDTO.getExplanation());
        existingQuestion.setChapter(chapter);
        
        // Auto-resolve hierarchy from chapter (same pattern as ChapterService)
        existingQuestion.setModuleId(chapter.getModule().getId());
        if (chapter.getModule().getTopic() != null) {
            existingQuestion.setTopicId(chapter.getModule().getTopic().getId());
            // Resolve courseTypeId and relationshipId from topic
            existingQuestion.setCourseTypeId(chapter.getModule().getTopic().getCourseTypeId());
            existingQuestion.setRelationshipId(chapter.getModule().getTopic().getRelationshipId());
        }
        
        // Set additional fields from DTO if provided (for flexibility)
        if (questionRequestDTO.getCourseTypeId() != null) {
            existingQuestion.setCourseTypeId(questionRequestDTO.getCourseTypeId());
        }
        if (questionRequestDTO.getRelationshipId() != null) {
            existingQuestion.setRelationshipId(questionRequestDTO.getRelationshipId());
        }
        if (questionRequestDTO.getTopicId() != null) {
            existingQuestion.setTopicId(questionRequestDTO.getTopicId());
        }
        if (questionRequestDTO.getModuleId() != null) {
            existingQuestion.setModuleId(questionRequestDTO.getModuleId());
        }
        if (questionRequestDTO.getSubjectId() != null) {
            existingQuestion.setSubjectId(questionRequestDTO.getSubjectId());
        }
        
        existingQuestion.setDisplayOrder(questionRequestDTO.getDisplayOrder());
        existingQuestion.setIsActive(questionRequestDTO.getIsActive());
        existingQuestion.setUpdatedBy(getCurrentUser());
        
        // Handle question options - clear existing and add new ones
        existingQuestion.getOptions().clear();
        if (questionRequestDTO.getOptions() != null && !questionRequestDTO.getOptions().isEmpty()) {
            List<QuestionOption> options = questionRequestDTO.getOptions().stream()
                    .map(optionDTO -> {
                        QuestionOption option = new QuestionOption();
                        option.setQuestion(existingQuestion);
                        option.setOptionText(optionDTO.getOptionText());
                        option.setIsCorrect(optionDTO.getIsCorrect());
                        option.setDisplayOrder(optionDTO.getDisplayOrder());
                        return option;
                    })
                    .collect(Collectors.toList());
            existingQuestion.setOptions(options);
        }
        
        Question updatedQuestion = questionRepository.save(existingQuestion);
        return createQuestionResponseDTO(updatedQuestion);
    }
    
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new IllegalArgumentException("Question not found with ID: " + id);
        }
        questionRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return questionRepository.existsById(id);
    }
    
    // Get current authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
    
    /**
     * Creates a QuestionResponseDTO with complete information including
     * subject resolution, exam suitability, and exam histories
     */
    private QuestionResponseDTO createQuestionResponseDTO(Question question) {
        QuestionResponseDTO dto = new QuestionResponseDTO(question);
        
        // Populate additional information that might not be in the basic constructor
        if (question.getChapter() != null) {
            dto.setChapterName(question.getChapter().getName());
            
            // Get module information from chapter
            if (question.getChapter().getModule() != null) {
                dto.setModuleId(question.getChapter().getModule().getId());
                dto.setModuleName(question.getChapter().getModule().getName());
                
                // Get topic information from module
                if (question.getChapter().getModule().getTopic() != null) {
                    Topic topic = question.getChapter().getModule().getTopic();
                    dto.setTopicId(topic.getId());
                    dto.setTopicName(topic.getName());
                    
                    // Resolve subject information
                    Subject subject = resolveSubjectFromTopic(topic);
                    if (subject != null) {
                        dto.setSubjectId(subject.getId());
                        dto.setSubjectName(subject.getName());
                        dto.setSubjectType(subject.getCourseType() != null ? subject.getCourseType().getName() : "Unknown");
                    }
                }
            }
        }
        
        // Populate exam suitability data
        if (question.getExamSuitabilities() != null && !question.getExamSuitabilities().isEmpty()) {
            dto.setExamSuitabilities(question.getExamSuitabilities().stream()
                    .map(QuestionExamSuitabilityResponseDTO::new)
                    .collect(Collectors.toList()));
        }
        
        // Populate exam history data
        if (question.getExamHistories() != null && !question.getExamHistories().isEmpty()) {
            dto.setExamHistories(question.getExamHistories().stream()
                    .map(QuestionExamHistoryResponseDTO::new)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    /**
     * Resolves the Subject entity from a Topic based on its courseTypeId and relationshipId
     * 
     * @param topic The Topic entity
     * @return The resolved Subject entity, or null if not found
     */
    private Subject resolveSubjectFromTopic(Topic topic) {
        Long courseTypeId = topic.getCourseTypeId();
        Long relationshipId = topic.getRelationshipId();
        
        if (courseTypeId == null || relationshipId == null) {
            return null;
        }
        
        try {
            // CourseTypeId 1 = Academic (ClassSubject)
            if (courseTypeId == 1) {
                return classSubjectRepository.findById(relationshipId)
                        .map(ClassSubject::getSubject)
                        .orElse(null);
            }
            // CourseTypeId 2 = Competitive (ExamSubject)
            else if (courseTypeId == 2) {
                return examSubjectRepository.findById(relationshipId)
                        .map(ExamSubject::getSubject)
                        .orElse(null);
            }
            // CourseTypeId 3 = Professional (CourseSubject)
            else if (courseTypeId == 3) {
                return courseSubjectRepository.findById(relationshipId)
                        .map(CourseSubject::getSubject)
                        .orElse(null);
            }
        } catch (Exception e) {
            // Log error if needed, but don't fail the entire operation
            System.err.println("Error resolving subject for topic (courseTypeId=" + topic.getCourseTypeId() + ", relationshipId=" + topic.getRelationshipId() + "): " + e.getMessage());
        }
        
        return null;
    }
}
