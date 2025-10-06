package com.coaxial.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coaxial.dto.TopicRequestDTO;
import com.coaxial.dto.TopicResponseDTO;
import com.coaxial.entity.Topic;
import com.coaxial.service.TopicService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/master-data/topics")
@CrossOrigin(origins = "*")
public class TopicController {
    
    @Autowired
    private TopicService topicService;
    
    /**
     * Get all topics with filtering options
     * 
     * @param active Filter by active status (true/false)
     * @param courseTypeId Filter by course type (1=Academic, 2=Competitive, 3=Professional)
     * @param relationshipId Filter by specific relationship ID
     * @param search Search by topic name or description
     * @return List of topics matching the criteria
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<List<TopicResponseDTO>> getAllTopics(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Long courseTypeId,
            @RequestParam(required = false) Long relationshipId,
            @RequestParam(required = false) String search) {
        
        List<TopicResponseDTO> topics;
        
        if (search != null && !search.trim().isEmpty()) {
            // Search functionality
            topics = topicService.searchTopicsAsDTO(search.trim());
        } else if (courseTypeId != null && relationshipId != null) {
            // Filter by course type and relationship
            topics = topicService.getTopicsByRelationshipAsDTO(courseTypeId, relationshipId, active);
        } else if (courseTypeId != null) {
            // Filter by course type only
            topics = topicService.getTopicsByCourseTypeAsDTO(courseTypeId, active);
        } else {
            // Get all topics
            topics = topicService.getAllTopicsAsDTO();
        }
        
        return ResponseEntity.ok(topics);
    }
    
    /**
     * Standardized combined filter endpoint with pagination
     */
    @GetMapping("/combined-filter")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<Page<TopicResponseDTO>> getTopicsCombinedFilter(
            @RequestParam(required = false) Long courseTypeId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TopicResponseDTO> topics = topicService.getTopicsCombinedFilter(
            courseTypeId, courseId, classId, examId, subjectId, active, search, pageable);
        return ResponseEntity.ok(topics);
    }
    
    /**
     * Get topics by course type
     * 
     * @param courseTypeId The course type ID (1=Academic, 2=Competitive, 3=Professional)
     * @param active Filter by active status (optional)
     * @return List of topics for the specified course type
     */
    @GetMapping("/by-course-type/{courseTypeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<List<TopicResponseDTO>> getTopicsByCourseType(
            @PathVariable Long courseTypeId,
            @RequestParam(required = false) Boolean active) {
        
        List<TopicResponseDTO> topics = topicService.getTopicsByCourseTypeAsDTO(courseTypeId, active);
        return ResponseEntity.ok(topics);
    }
    
    /**
     * Get topics by linkage relationship
     * 
     * @param courseTypeId The course type ID (1=Academic, 2=Competitive, 3=Professional)
     * @param relationshipId The relationship ID (classSubjectId, examSubjectId, or courseSubjectId)
     * @param active Filter by active status (optional)
     * @return List of topics for the specified linkage relationship
     */
    @GetMapping("/by-linkage")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<List<TopicResponseDTO>> getTopicsByLinkage(
            @RequestParam Long courseTypeId,
            @RequestParam Long relationshipId,
            @RequestParam(required = false) Boolean active) {
        
        List<TopicResponseDTO> topics = topicService.getTopicsByRelationshipAsDTO(courseTypeId, relationshipId, active);
        return ResponseEntity.ok(topics);
    }
    
    /**
     * Get topics by class-subject linkage (Academic)
     * 
     * @param classSubjectId The ClassSubject ID
     * @param active Filter by active status (optional)
     * @return List of topics for the specified class-subject
     */
    @GetMapping("/by-class-subject/{classSubjectId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<List<TopicResponseDTO>> getTopicsByClassSubject(
            @PathVariable Long classSubjectId,
            @RequestParam(required = false) Boolean active) {
        
        List<TopicResponseDTO> topics = topicService.getTopicsByRelationshipAsDTO(1L, classSubjectId, active);
        return ResponseEntity.ok(topics);
    }
    
    /**
     * Get topics by exam-subject linkage (Competitive)
     * 
     * @param examSubjectId The ExamSubject ID
     * @param active Filter by active status (optional)
     * @return List of topics for the specified exam-subject
     */
    @GetMapping("/by-exam-subject/{examSubjectId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<List<TopicResponseDTO>> getTopicsByExamSubject(
            @PathVariable Long examSubjectId,
            @RequestParam(required = false) Boolean active) {
        
        List<TopicResponseDTO> topics = topicService.getTopicsByRelationshipAsDTO(2L, examSubjectId, active);
        return ResponseEntity.ok(topics);
    }
    
    /**
     * Get topics by course-subject linkage (Professional)
     * 
     * @param courseSubjectId The CourseSubject ID
     * @param active Filter by active status (optional)
     * @return List of topics for the specified course-subject
     */
    @GetMapping("/by-course-subject/{courseSubjectId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<List<TopicResponseDTO>> getTopicsByCourseSubject(
            @PathVariable Long courseSubjectId,
            @RequestParam(required = false) Boolean active) {
        
        List<TopicResponseDTO> topics = topicService.getTopicsByRelationshipAsDTO(3L, courseSubjectId, active);
        return ResponseEntity.ok(topics);
    }
    
    /**
     * Search topics by name or description
     * 
     * @param q Search query
     * @return List of matching topics
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<List<TopicResponseDTO>> searchTopics(@RequestParam String q) {
        List<TopicResponseDTO> topics = topicService.searchTopicsAsDTO(q);
        return ResponseEntity.ok(topics);
    }
    
    
    
    /**
     * Create a new topic
     * 
     * @param topicRequestDTO Topic creation request
     * @return Created topic
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Topic> createTopic(@Valid @RequestBody TopicRequestDTO topicRequestDTO) {
        try {
            Topic createdTopic = topicService.createTopic(topicRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTopic);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update an existing topic
     * 
     * @param id Topic ID
     * @param topicRequestDTO Topic update request
     * @return Updated topic
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Topic> updateTopic(@PathVariable Long id, @Valid @RequestBody TopicRequestDTO topicRequestDTO) {
        try {
            Topic updatedTopic = topicService.updateTopic(id, topicRequestDTO);
            return ResponseEntity.ok(updatedTopic);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Delete a topic
     * 
     * @param id Topic ID
     * @return No content on success
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        try {
            topicService.deleteTopic(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    
    /**
     * Get topic by ID
     * 
     * @param id Topic ID
     * @return Topic details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<TopicResponseDTO> getTopicById(@PathVariable Long id) {
        Optional<TopicResponseDTO> topic = topicService.getTopicByIdAsDTO(id);
        return topic.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
}