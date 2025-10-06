package com.coaxial.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coaxial.dto.TopicRequestDTO;
import com.coaxial.dto.TopicResponseDTO;
import com.coaxial.entity.ClassEntity;
import com.coaxial.entity.ClassSubject;
import com.coaxial.entity.Course;
import com.coaxial.entity.CourseSubject;
import com.coaxial.entity.Exam;
import com.coaxial.entity.ExamSubject;
import com.coaxial.entity.Subject;
import com.coaxial.entity.Topic;
import com.coaxial.entity.User;
import com.coaxial.repository.ClassRepository;
import com.coaxial.repository.ClassSubjectRepository;
import com.coaxial.repository.CourseSubjectRepository;
import com.coaxial.repository.ExamRepository;
import com.coaxial.repository.ExamSubjectRepository;
import com.coaxial.repository.TopicRepository;

@Service
@Transactional
public class TopicService {
    
    @Autowired
    private TopicRepository topicRepository;
    
    @Autowired
    private ClassSubjectRepository classSubjectRepository;
    
    @Autowired
    private ExamSubjectRepository examSubjectRepository;
    
    @Autowired
    private CourseSubjectRepository courseSubjectRepository;
    
    @Autowired
    private ClassRepository classRepository;
    
    @Autowired
    private ExamRepository examRepository;
    
    // Basic CRUD operations
    public List<Topic> getAllTopics() {
        return topicRepository.findAllByOrderByDisplayOrderAsc();
    }
    
    public List<Topic> getActiveTopics() {
        return topicRepository.findByIsActiveOrderByDisplayOrderAsc(true);
    }
    
    public Optional<Topic> getTopicById(Long id) {
        return topicRepository.findById(id);
    }
    
    public Topic createTopic(TopicRequestDTO topicRequestDTO) {
        // Validate the relationship exists based on course type
        validateRelationship(topicRequestDTO.getCourseTypeId(), topicRequestDTO.getRelationshipId());
        
        // Check for duplicate topic name within the same relationship
        if (topicRepository.existsByNameAndCourseTypeIdAndRelationshipId(
            topicRequestDTO.getName(), topicRequestDTO.getCourseTypeId(), topicRequestDTO.getRelationshipId())) {
            throw new IllegalArgumentException("Topic with name '" + topicRequestDTO.getName() + 
                "' already exists for this relationship");
        }
        
        Topic topic = new Topic();
        topic.setName(topicRequestDTO.getName());
        topic.setDescription(topicRequestDTO.getDescription());
        topic.setCourseTypeId(topicRequestDTO.getCourseTypeId());
        topic.setRelationshipId(topicRequestDTO.getRelationshipId());
        topic.setDisplayOrder(topicRequestDTO.getDisplayOrder());
        topic.setIsActive(topicRequestDTO.getIsActive());
        
        // Set created by user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            topic.setCreatedBy((User) authentication.getPrincipal());
        }
        
        return topicRepository.save(topic);
    }
    
    public Topic updateTopic(Long id, TopicRequestDTO topicRequestDTO) {
        Topic existingTopic = topicRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Topic not found with ID: " + id));
        
        // Validate the relationship exists based on course type
        validateRelationship(topicRequestDTO.getCourseTypeId(), topicRequestDTO.getRelationshipId());
        
        // Check for duplicate topic name within the same relationship (excluding current topic)
        if (topicRepository.existsByNameAndCourseTypeIdAndRelationshipIdAndIdNot(
            topicRequestDTO.getName(), topicRequestDTO.getCourseTypeId(),
            topicRequestDTO.getRelationshipId(), id)) {
            throw new IllegalArgumentException("Topic with name '" + topicRequestDTO.getName() + 
                "' already exists for this relationship");
        }
        
        existingTopic.setName(topicRequestDTO.getName());
        existingTopic.setDescription(topicRequestDTO.getDescription());
        existingTopic.setCourseTypeId(topicRequestDTO.getCourseTypeId());
        existingTopic.setRelationshipId(topicRequestDTO.getRelationshipId());
        existingTopic.setDisplayOrder(topicRequestDTO.getDisplayOrder());
        existingTopic.setIsActive(topicRequestDTO.getIsActive());
        
        // Set updated by user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            existingTopic.setUpdatedBy((User) authentication.getPrincipal());
        }
        
        return topicRepository.save(existingTopic);
    }
    
    public void deleteTopic(Long id) {
        if (!topicRepository.existsById(id)) {
            throw new IllegalArgumentException("Topic not found with ID: " + id);
        }
        topicRepository.deleteById(id);
    }
    
    // Filtering methods
    public List<Topic> getTopicsByCourseType(Long courseTypeId, Boolean active) {
        if (active != null) {
            return topicRepository.findByCourseTypeIdAndIsActiveOrderByDisplayOrderAsc(courseTypeId, active);
        } else {
            return topicRepository.findByCourseTypeIdOrderByDisplayOrderAsc(courseTypeId);
        }
    }
    
    public List<Topic> getTopicsByRelationship(Long courseTypeId, Long relationshipId, Boolean active) {
        if (active != null) {
            return topicRepository.findByCourseTypeIdAndRelationshipIdAndIsActiveOrderByDisplayOrderAsc(
                courseTypeId, relationshipId, active);
        } else {
            return topicRepository.findByCourseTypeIdAndRelationshipIdOrderByDisplayOrderAsc(
                courseTypeId, relationshipId);
        }
    }
    
    // Search functionality
    public List<Topic> searchTopics(String searchTerm) {
        return topicRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, searchTerm);
    }
    
    public List<Topic> searchTopicsByName(String name) {
        return topicRepository.findByNameContainingIgnoreCaseAndIsActiveTrueOrderByDisplayOrderAsc(name);
    }
    
    // DTO conversion methods
    public List<TopicResponseDTO> getAllTopicsAsDTO() {
        List<Topic> topics = topicRepository.findAllByOrderByDisplayOrderAsc();
        return topics.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    public List<TopicResponseDTO> getTopicsByCourseTypeAsDTO(Long courseTypeId, Boolean active) {
        List<Topic> topics = getTopicsByCourseType(courseTypeId, active);
        return topics.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    public List<TopicResponseDTO> getTopicsByRelationshipAsDTO(Long courseTypeId, Long relationshipId, Boolean active) {
        List<Topic> topics = getTopicsByRelationship(courseTypeId, relationshipId, active);
        return topics.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    
    public List<TopicResponseDTO> searchTopicsAsDTO(String searchTerm) {
        List<Topic> topics = searchTopics(searchTerm);
        return topics.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    public Optional<TopicResponseDTO> getTopicByIdAsDTO(Long id) {
        Optional<Topic> topic = topicRepository.findById(id);
        return topic.map(this::convertToResponseDTO);
    }
    
    /**
     * Enhanced combined filter for topics
     * Correctly handles Topic's relationshipId structure:
     * - courseTypeId = 1: relationshipId refers to ClassSubject.id
     * - courseTypeId = 2: relationshipId refers to ExamSubject.id  
     * - courseTypeId = 3: relationshipId refers to CourseSubject.id
     */
    public List<TopicResponseDTO> getTopicsWithCombinedFilter(
            Boolean active, Long courseTypeId, Long courseId, 
            Long classId, Long examId, Long subjectId, String search) {
        
        List<Topic> topics;
        
        // Priority 1: Search functionality (highest priority)
        if (search != null && !search.trim().isEmpty()) {
            topics = searchTopics(search.trim());
            // Apply additional filters to search results
            if (active != null || courseTypeId != null || courseId != null || 
                classId != null || examId != null || subjectId != null) {
                topics = applyAdditionalFilters(topics, active, courseTypeId, courseId, classId, examId, subjectId);
            }
        }
        // Priority 1.5: If courseTypeId is specified, treat subjectId as linkageId for that type
        else if (subjectId != null && courseTypeId != null) {
            topics = getTopicsByLinkageId(courseTypeId, subjectId, active, courseId, classId, examId);
        }
        // Priority 2: Filter by specific subject (most specific)
        else if (subjectId != null) {
            topics = getTopicsBySubjectId(subjectId, active, courseTypeId, courseId, classId, examId);
        }
        // Priority 3: Filter by class (Academic courses)
        else if (classId != null) {
            topics = getTopicsByClassId(classId, active);
        }
        // Priority 4: Filter by exam (Competitive courses)
        else if (examId != null) {
            topics = getTopicsByExamId(examId, active);
        }
        // Priority 5: Filter by course
        else if (courseId != null) {
            topics = getTopicsByCourseId(courseId, active);
        }
        // Priority 6: Filter by course type
        else if (courseTypeId != null) {
            topics = getTopicsByCourseType(courseTypeId, active);
        }
        // Priority 7: Basic active/inactive filtering
        else if (active != null) {
            topics = topicRepository.findByIsActiveOrderByDisplayOrderAsc(active);
        }
        // Default: Get all topics
        else {
            topics = topicRepository.findAllByOrderByDisplayOrderAsc();
        }
        
        return topics.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }

    // Fetch topics by linkage id based on courseTypeId, with optional validation against higher-level filters
    private List<Topic> getTopicsByLinkageId(Long courseTypeId, Long linkageId, Boolean active,
            Long courseId, Long classId, Long examId) {
        List<Topic> topics = new java.util.ArrayList<>();

        if (courseTypeId == 1) {
            // ClassSubject
            Optional<ClassSubject> csOpt = classSubjectRepository.findById(linkageId);
            if (csOpt.isEmpty()) {
                return topics;
            }
            ClassSubject cs = csOpt.get();
            ClassEntity classEntity = cs.getClassEntity();
            // Validate filters
            if (classId != null && !classId.equals(classEntity.getId())) {
                return topics;
            }
            if (courseId != null && !courseId.equals(classEntity.getCourse().getId())) {
                return topics;
            }
            // Fetch topics
            if (active != null) {
                topics.addAll(topicRepository.findByCourseTypeIdAndRelationshipIdAndIsActiveOrderByDisplayOrderAsc(1L, linkageId, active));
            } else {
                topics.addAll(topicRepository.findByCourseTypeIdAndRelationshipIdOrderByDisplayOrderAsc(1L, linkageId));
            }
        } else if (courseTypeId == 2) {
            // ExamSubject
            Optional<ExamSubject> esOpt = examSubjectRepository.findById(linkageId);
            if (esOpt.isEmpty()) {
                return topics;
            }
            ExamSubject es = esOpt.get();
            Exam exam = es.getExam();
            // Validate filters
            if (examId != null && !examId.equals(exam.getId())) {
                return topics;
            }
            if (courseId != null && !courseId.equals(exam.getCourse().getId())) {
                return topics;
            }
            // Fetch topics
            if (active != null) {
                topics.addAll(topicRepository.findByCourseTypeIdAndRelationshipIdAndIsActiveOrderByDisplayOrderAsc(2L, linkageId, active));
            } else {
                topics.addAll(topicRepository.findByCourseTypeIdAndRelationshipIdOrderByDisplayOrderAsc(2L, linkageId));
            }
        } else if (courseTypeId == 3) {
            // CourseSubject
            Optional<CourseSubject> cosOpt = courseSubjectRepository.findById(linkageId);
            if (cosOpt.isEmpty()) {
                return topics;
            }
            CourseSubject cos = cosOpt.get();
            // Validate filters
            if (courseId != null && !courseId.equals(cos.getCourse().getId())) {
                return topics;
            }
            // Fetch topics
            if (active != null) {
                topics.addAll(topicRepository.findByCourseTypeIdAndRelationshipIdAndIsActiveOrderByDisplayOrderAsc(3L, linkageId, active));
            } else {
                topics.addAll(topicRepository.findByCourseTypeIdAndRelationshipIdOrderByDisplayOrderAsc(3L, linkageId));
            }
        }

        return topics;
    }
    
    /**
     * Get topics by subject ID (works across all course types)
     * Finds topics where relationshipId points to ClassSubject/ExamSubject/CourseSubject 
     * that have the specified subjectId, with additional filtering support
     */
    private List<Topic> getTopicsBySubjectId(Long subjectId, Boolean active, Long courseTypeId, 
            Long courseId, Long classId, Long examId) {
        List<Topic> topics = new java.util.ArrayList<>();
        
        System.out.println("DEBUG: getTopicsBySubjectId called with subjectId=" + subjectId + 
                          ", courseTypeId=" + courseTypeId + ", courseId=" + courseId + 
                          ", examId=" + examId + ", active=" + active);
        
        // Academic courses (courseTypeId = 1)
        if (courseTypeId == null || courseTypeId == 1) {
            List<Long> classSubjectIds = classSubjectRepository.findBySubjectId(subjectId)
                .stream().map(cs -> cs.getId()).collect(Collectors.toList());
            
            // Apply additional filters for Academic courses
            if (courseId != null || classId != null) {
                classSubjectIds = classSubjectIds.stream()
                    .filter(csId -> {
                        Optional<ClassSubject> csOpt = classSubjectRepository.findById(csId);
                        if (csOpt.isEmpty()) return false;
                        
                        ClassSubject cs = csOpt.get();
                        ClassEntity classEntity = cs.getClassEntity();
                        
                        // Filter by classId
                        if (classId != null && !classId.equals(classEntity.getId())) {
                            return false;
                        }
                        
                        // Filter by courseId
                        if (courseId != null && !courseId.equals(classEntity.getCourse().getId())) {
                            return false;
                        }
                        
                        return true;
                    })
                    .collect(Collectors.toList());
            }
            
            // Find topics for filtered ClassSubject IDs
            for (Long classSubjectId : classSubjectIds) {
                if (active != null) {
                    topics.addAll(topicRepository.findByCourseTypeIdAndRelationshipIdAndIsActiveOrderByDisplayOrderAsc(
                        1L, classSubjectId, active));
                } else {
                    topics.addAll(topicRepository.findByCourseTypeIdAndRelationshipIdOrderByDisplayOrderAsc(
                        1L, classSubjectId));
                }
            }
        }
        
        // Competitive courses (courseTypeId = 2)
        if (courseTypeId == null || courseTypeId == 2) {
            List<Long> examSubjectIds = examSubjectRepository.findBySubjectId(subjectId)
                .stream().map(es -> es.getId()).collect(Collectors.toList());
            
            System.out.println("DEBUG: Found " + examSubjectIds.size() + " ExamSubject records for subjectId=" + subjectId);
            System.out.println("DEBUG: ExamSubject IDs: " + examSubjectIds);
            
            // Apply additional filters for Competitive courses
            if (courseId != null || examId != null) {
                System.out.println("DEBUG: Applying filters - courseId=" + courseId + ", examId=" + examId);
                examSubjectIds = examSubjectIds.stream()
                    .filter(esId -> {
                        Optional<ExamSubject> esOpt = examSubjectRepository.findById(esId);
                        if (esOpt.isEmpty()) {
                            System.out.println("DEBUG: ExamSubject not found for ID=" + esId);
                            return false;
                        }
                        
                        ExamSubject es = esOpt.get();
                        Exam exam = es.getExam();
                        
                        System.out.println("DEBUG: Checking ExamSubject ID=" + esId + ", Exam ID=" + exam.getId() + ", Course ID=" + exam.getCourse().getId());
                        
                        // Filter by examId
                        if (examId != null && !examId.equals(exam.getId())) {
                            System.out.println("DEBUG: Filtered out by examId - expected=" + examId + ", actual=" + exam.getId());
                            return false;
                        }
                        
                        // Filter by courseId
                        if (courseId != null && !courseId.equals(exam.getCourse().getId())) {
                            System.out.println("DEBUG: Filtered out by courseId - expected=" + courseId + ", actual=" + exam.getCourse().getId());
                            return false;
                        }
                        
                        System.out.println("DEBUG: ExamSubject ID=" + esId + " passed all filters");
                        return true;
                    })
                    .collect(Collectors.toList());
                
                System.out.println("DEBUG: After filtering, " + examSubjectIds.size() + " ExamSubject IDs remain: " + examSubjectIds);
            }
            
            // Find topics for filtered ExamSubject IDs
            for (Long examSubjectId : examSubjectIds) {
                System.out.println("DEBUG: Looking for topics with courseTypeId=2, relationshipId=" + examSubjectId + ", active=" + active);
                if (active != null) {
                    List<Topic> foundTopics = topicRepository.findByCourseTypeIdAndRelationshipIdAndIsActiveOrderByDisplayOrderAsc(
                        2L, examSubjectId, active);
                    System.out.println("DEBUG: Found " + foundTopics.size() + " topics for ExamSubject ID=" + examSubjectId);
                    topics.addAll(foundTopics);
                } else {
                    List<Topic> foundTopics = topicRepository.findByCourseTypeIdAndRelationshipIdOrderByDisplayOrderAsc(
                        2L, examSubjectId);
                    System.out.println("DEBUG: Found " + foundTopics.size() + " topics for ExamSubject ID=" + examSubjectId);
                    topics.addAll(foundTopics);
                }
            }
        }
        
        // Professional courses (courseTypeId = 3)
        if (courseTypeId == null || courseTypeId == 3) {
            List<Long> courseSubjectIds = courseSubjectRepository.findBySubjectId(subjectId)
                .stream().map(cs -> cs.getId()).collect(Collectors.toList());
            
            // Apply additional filters for Professional courses
            if (courseId != null) {
                courseSubjectIds = courseSubjectIds.stream()
                    .filter(csId -> {
                        Optional<CourseSubject> csOpt = courseSubjectRepository.findById(csId);
                        if (csOpt.isEmpty()) return false;
                        
                        CourseSubject cs = csOpt.get();
                        
                        // Filter by courseId
                        if (courseId != null && !courseId.equals(cs.getCourse().getId())) {
                            return false;
                        }
                        
                        return true;
                    })
                    .collect(Collectors.toList());
            }
            
            // Find topics for filtered CourseSubject IDs
            for (Long courseSubjectId : courseSubjectIds) {
                if (active != null) {
                    topics.addAll(topicRepository.findByCourseTypeIdAndRelationshipIdAndIsActiveOrderByDisplayOrderAsc(
                        3L, courseSubjectId, active));
                } else {
                    topics.addAll(topicRepository.findByCourseTypeIdAndRelationshipIdOrderByDisplayOrderAsc(
                        3L, courseSubjectId));
                }
            }
        }
        
        System.out.println("DEBUG: Total topics found: " + topics.size());
        return topics.stream().distinct().collect(Collectors.toList());
    }
    
    /**
     * Get topics by class ID (Academic courses)
     * Finds topics where courseTypeId=1 and relationshipId points to ClassSubject 
     * that belongs to the specified classId
     */
    private List<Topic> getTopicsByClassId(Long classId, Boolean active) {
        List<Topic> topics = new java.util.ArrayList<>();
        
        // Find ClassSubject IDs that belong to this classId
        List<Long> classSubjectIds = classSubjectRepository.findByClassEntityId(classId)
            .stream().map(cs -> cs.getId()).collect(Collectors.toList());
        
        // Find topics where courseTypeId=1 and relationshipId is in the ClassSubject IDs
        for (Long classSubjectId : classSubjectIds) {
            if (active != null) {
                topics.addAll(topicRepository.findByCourseTypeIdAndRelationshipIdAndIsActiveOrderByDisplayOrderAsc(
                    1L, classSubjectId, active));
            } else {
                topics.addAll(topicRepository.findByCourseTypeIdAndRelationshipIdOrderByDisplayOrderAsc(
                    1L, classSubjectId));
            }
        }
        
        return topics;
    }
    
    /**
     * Get topics by exam ID (Competitive courses)
     * Finds topics where courseTypeId=2 and relationshipId points to ExamSubject 
     * that belongs to the specified examId
     */
    private List<Topic> getTopicsByExamId(Long examId, Boolean active) {
        List<Topic> topics = new java.util.ArrayList<>();
        
        // Find ExamSubject IDs that belong to this examId
        List<Long> examSubjectIds = examSubjectRepository.findByExamId(examId)
            .stream().map(es -> es.getId()).collect(Collectors.toList());
        
        // Find topics where courseTypeId=2 and relationshipId is in the ExamSubject IDs
        for (Long examSubjectId : examSubjectIds) {
            if (active != null) {
                topics.addAll(topicRepository.findByCourseTypeIdAndRelationshipIdAndIsActiveOrderByDisplayOrderAsc(
                    2L, examSubjectId, active));
            } else {
                topics.addAll(topicRepository.findByCourseTypeIdAndRelationshipIdOrderByDisplayOrderAsc(
                    2L, examSubjectId));
            }
        }
        
        return topics;
    }
    
    /**
     * Get topics by course ID
     * Finds topics where relationshipId points to ClassSubject/ExamSubject/CourseSubject 
     * that belongs to the specified courseId
     */
    private List<Topic> getTopicsByCourseId(Long courseId, Boolean active) {
        List<Topic> topics = new java.util.ArrayList<>();
        
        // For Professional courses (courseTypeId = 3), find CourseSubject IDs for this course
        List<Long> courseSubjectIds = courseSubjectRepository.findByCourseId(courseId)
            .stream().map(cs -> cs.getId()).collect(Collectors.toList());
        
        for (Long courseSubjectId : courseSubjectIds) {
            if (active != null) {
                topics.addAll(topicRepository.findByCourseTypeIdAndRelationshipIdAndIsActiveOrderByDisplayOrderAsc(
                    3L, courseSubjectId, active));
            } else {
                topics.addAll(topicRepository.findByCourseTypeIdAndRelationshipIdOrderByDisplayOrderAsc(
                    3L, courseSubjectId));
            }
        }
        
        // For Academic courses (courseTypeId = 1), find ClassSubject IDs through classes
        List<Long> classIds = classRepository.findByCourseId(courseId)
            .stream().map(c -> c.getId()).collect(Collectors.toList());
        
        for (Long classId : classIds) {
            topics.addAll(getTopicsByClassId(classId, active));
        }
        
        // For Competitive courses (courseTypeId = 2), find ExamSubject IDs through exams
        List<Long> examIds = examRepository.findByCourseId(courseId)
            .stream().map(e -> e.getId()).collect(Collectors.toList());
        
        for (Long examId : examIds) {
            topics.addAll(getTopicsByExamId(examId, active));
        }
        
        return topics.stream().distinct().collect(Collectors.toList());
    }
    
    /**
     * Apply additional filters to a list of topics
     */
    private List<Topic> applyAdditionalFilters(List<Topic> topics, Boolean active, 
            Long courseTypeId, Long courseId, Long classId, Long examId, Long subjectId) {
        
        return topics.stream()
            .filter(topic -> {
                // Filter by active status
                if (active != null && !active.equals(topic.getIsActive())) {
                    return false;
                }
                
                // Filter by course type
                if (courseTypeId != null && !courseTypeId.equals(topic.getCourseTypeId())) {
                    return false;
                }
                
                // Additional filters can be added here as needed
                return true;
            })
            .collect(Collectors.toList());
    }
    
    // Convert Topic entity to TopicResponseDTO with complete relationship information
    public TopicResponseDTO convertToResponseDTO(Topic topic) {
        if (topic == null) {
            return null;
        }
        
        TopicResponseDTO dto = new TopicResponseDTO();
        
        // Basic topic information
        dto.setId(topic.getId());
        dto.setName(topic.getName());
        dto.setDescription(topic.getDescription());
        dto.setDisplayOrder(topic.getDisplayOrder());
        dto.setIsActive(topic.getIsActive());
        dto.setCreatedAt(topic.getCreatedAt() != null ? topic.getCreatedAt().toString() : null);
        dto.setUpdatedAt(topic.getUpdatedAt() != null ? topic.getUpdatedAt().toString() : null);
        
        // User information
        if (topic.getCreatedBy() != null) {
            dto.setCreatedByName(topic.getCreatedBy().getFullName());
        }
        if (topic.getUpdatedBy() != null) {
            dto.setUpdatedByName(topic.getUpdatedBy().getFullName());
        }
        
        // Course type information
        dto.setCourseTypeId(topic.getCourseTypeId());
        dto.setCourseTypeName(topic.getCourseTypeName());
        
        // Linkage information
        dto.setLinkageId(topic.getRelationshipId());
        dto.setLinkageType(topic.getRelationshipType());
        dto.setRelationshipId(topic.getRelationshipId());
        dto.setRelationshipType(topic.getRelationshipType());
        
        // Get complete relationship information based on courseTypeId
        try {
            if (topic.getCourseTypeId() == 1) {
                // Academic - get ClassSubject
                Optional<ClassSubject> classSubjectOpt = classSubjectRepository.findById(topic.getRelationshipId());
                if (classSubjectOpt.isPresent()) {
                    ClassSubject classSubject = classSubjectOpt.get();
                    ClassEntity classEntity = classSubject.getClassEntity();
                    Course course = classEntity.getCourse();
                    Subject subject = classSubject.getSubject();
                    
                    // Course information
                    dto.setCourseId(course.getId());
                    dto.setCourseName(course.getName());
                    
                    // Class information
                    dto.setClassId(classEntity.getId());
                    dto.setClassName(classEntity.getName());
                    
                    // Master subject information
                    dto.setSubjectId(subject.getId());
                    dto.setSubjectName(subject.getName());
                }
            } else if (topic.getCourseTypeId() == 2) {
                // Competitive - get ExamSubject
                Optional<ExamSubject> examSubjectOpt = examSubjectRepository.findById(topic.getRelationshipId());
                if (examSubjectOpt.isPresent()) {
                    ExamSubject examSubject = examSubjectOpt.get();
                    Exam exam = examSubject.getExam();
                    Course course = exam.getCourse();
                    Subject subject = examSubject.getSubject();
                    
                    // Course information
                    dto.setCourseId(course.getId());
                    dto.setCourseName(course.getName());
                    
                    // Exam information
                    dto.setExamId(exam.getId());
                    dto.setExamName(exam.getName());
                    
                    // Master subject information
                    dto.setSubjectId(subject.getId());
                    dto.setSubjectName(subject.getName());
                }
            } else if (topic.getCourseTypeId() == 3) {
                // Professional - get CourseSubject
                Optional<CourseSubject> courseSubjectOpt = courseSubjectRepository.findById(topic.getRelationshipId());
                if (courseSubjectOpt.isPresent()) {
                    CourseSubject courseSubject = courseSubjectOpt.get();
                    Course course = courseSubject.getCourse();
                    Subject subject = courseSubject.getSubject();
                    
                    // Course information
                    dto.setCourseId(course.getId());
                    dto.setCourseName(course.getName());
                    
                    // Master subject information
                    dto.setSubjectId(subject.getId());
                    dto.setSubjectName(subject.getName());
                }
            }
        } catch (Exception e) {
            // Log error but don't fail the conversion
            System.err.println("Error loading relationship data for topic " + topic.getId() + ": " + e.getMessage());
        }
        
        return dto;
    }
    
    // Validation method
    private void validateRelationship(Long courseTypeId, Long relationshipId) {
        if (courseTypeId == 1) { // Academic
            if (!classSubjectRepository.existsById(relationshipId)) {
                throw new IllegalArgumentException("ClassSubject not found with ID: " + relationshipId);
            }
        } else if (courseTypeId == 2) { // Competitive
            if (!examSubjectRepository.existsById(relationshipId)) {
                throw new IllegalArgumentException("ExamSubject not found with ID: " + relationshipId);
            }
        } else if (courseTypeId == 3) { // Professional
            if (!courseSubjectRepository.existsById(relationshipId)) {
                throw new IllegalArgumentException("CourseSubject not found with ID: " + relationshipId);
            }
        } else {
            throw new IllegalArgumentException("Invalid course type ID: " + courseTypeId);
        }
    }
    
    // Paginated method for standardized endpoints
    @Transactional(readOnly = true)
    public Page<TopicResponseDTO> getTopicsCombinedFilter(Long courseTypeId, Long courseId, Long classId, 
            Long examId, Long subjectId, Boolean active, String search, Pageable pageable) {
        List<TopicResponseDTO> topics = getTopicsWithCombinedFilter(active, courseTypeId, courseId, classId, examId, subjectId, search);
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), topics.size());
        List<TopicResponseDTO> pageContent = start < topics.size() ? topics.subList(start, end) : List.of();
        
        return new PageImpl<>(pageContent, pageable, topics.size());
    }
}