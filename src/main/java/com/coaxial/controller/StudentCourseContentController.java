package com.coaxial.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coaxial.dto.ChapterResponseDTO;
import com.coaxial.dto.ModuleResponseDTO;
import com.coaxial.entity.ClassSubject;
import com.coaxial.entity.CourseSubject;
import com.coaxial.entity.ExamSubject;
import com.coaxial.entity.Topic;
import com.coaxial.repository.ClassSubjectRepository;
import com.coaxial.repository.CourseSubjectRepository;
import com.coaxial.repository.ExamSubjectRepository;
import com.coaxial.repository.TopicRepository;
import com.coaxial.service.ChapterService;
import com.coaxial.service.ModuleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/student/course-content")
@PreAuthorize("hasRole('STUDENT')")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://127.0.0.1:3000", "http://127.0.0.1:3001"}, allowCredentials = "true")
@Tag(name = "Student Course Content", description = "APIs for students to browse course content (subjects, topics, modules, chapters)")
public class StudentCourseContentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentCourseContentController.class);

    @Autowired
    private ClassSubjectRepository classSubjectRepository;

    @Autowired
    private ExamSubjectRepository examSubjectRepository;

    @Autowired
    private CourseSubjectRepository courseSubjectRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private ChapterService chapterService;

    /**
     * Get all subjects for an entity based on entityId and courseTypeId
     * Returns subjects from the appropriate linkage table (ClassSubject/ExamSubject/CourseSubject)
     */
    @Operation(
        summary = "Get subjects by entity and course type",
        description = "Retrieve all subjects linked to an entity based on entityId and courseTypeId. " +
                      "CourseTypeId: 1=Academic (ClassSubject), 2=Competitive (ExamSubject), 3=Professional (CourseSubject)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved subjects"),
        @ApiResponse(responseCode = "400", description = "Invalid parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/subjects")
    public ResponseEntity<?> getSubjectsByEntity(
            @Parameter(description = "Entity ID (class/exam/course)", example = "1", required = true)
            @RequestParam Long entityId,
            @Parameter(description = "Course Type ID (1=Academic, 2=Competitive, 3=Professional)", example = "1", required = true)
            @RequestParam Long courseTypeId,
            Authentication authentication) {
        try {
            List<Map<String, Object>> subjects = new ArrayList<>();
            String courseTypeName = null;
            
            // Based on courseTypeId, query the appropriate linkage table
            switch (courseTypeId.intValue()) {
                case 1: // Academic - ClassSubject
                    List<ClassSubject> classSubjects = classSubjectRepository.findByClassEntityId(entityId);
                    subjects = classSubjects.stream()
                            .map(cs -> {
                                Map<String, Object> subjectMap = new HashMap<>();
                                subjectMap.put("linkageId", cs.getId());
                                subjectMap.put("subjectId", cs.getSubject().getId());
                                subjectMap.put("subjectName", cs.getSubject().getName());
                                subjectMap.put("subjectDescription", cs.getSubject().getDescription());
                                subjectMap.put("displayOrder", cs.getDisplayOrder());
                                subjectMap.put("isActive", cs.getIsActive());
                                return subjectMap;
                            })
                            .collect(Collectors.toList());
                    courseTypeName = "Academic";
                    break;
                    
                case 2: // Competitive - ExamSubject
                    List<ExamSubject> examSubjects = examSubjectRepository.findByExamId(entityId);
                    subjects = examSubjects.stream()
                            .map(es -> {
                                Map<String, Object> subjectMap = new HashMap<>();
                                subjectMap.put("linkageId", es.getId());
                                subjectMap.put("subjectId", es.getSubject().getId());
                                subjectMap.put("subjectName", es.getSubject().getName());
                                subjectMap.put("subjectDescription", es.getSubject().getDescription());
                                subjectMap.put("displayOrder", es.getDisplayOrder());
                                subjectMap.put("weightage", es.getWeightage());
                                return subjectMap;
                            })
                            .collect(Collectors.toList());
                    courseTypeName = "Competitive";
                    break;
                    
                case 3: // Professional - CourseSubject
                    List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseId(entityId);
                    subjects = courseSubjects.stream()
                            .map(cs -> {
                                Map<String, Object> subjectMap = new HashMap<>();
                                subjectMap.put("linkageId", cs.getId());
                                subjectMap.put("subjectId", cs.getSubject().getId());
                                subjectMap.put("subjectName", cs.getSubject().getName());
                                subjectMap.put("subjectDescription", cs.getSubject().getDescription());
                                subjectMap.put("displayOrder", cs.getDisplayOrder());
                                return subjectMap;
                            })
                            .collect(Collectors.toList());
                    courseTypeName = "Professional";
                    break;
                    
                default:
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Invalid courseTypeId. Must be 1 (Academic), 2 (Competitive), or 3 (Professional)"));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("entityId", entityId);
            response.put("courseTypeId", courseTypeId);
            response.put("courseTypeName", courseTypeName);
            response.put("subjects", subjects);
            response.put("totalCount", subjects.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching subjects for entityId {} and courseTypeId {}", entityId, courseTypeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch subjects: " + e.getMessage()));
        }
    }

    /**
     * Get all topics for a subject based on courseTypeId and linkageId (relationshipId)
     * Returns topics linked to the subject through the appropriate linkage table
     */
    @Operation(
        summary = "Get topics by course type and linkage ID",
        description = "Retrieve all topics for a subject based on courseTypeId and linkageId (relationshipId). " +
                      "CourseTypeId: 1=Academic (ClassSubject), 2=Competitive (ExamSubject), 3=Professional (CourseSubject). " +
                      "LinkageId is the ID from the respective linkage table."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved topics"),
        @ApiResponse(responseCode = "400", description = "Invalid parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/topics")
    public ResponseEntity<?> getTopicsByLinkage(
            @Parameter(description = "Course Type ID (1=Academic, 2=Competitive, 3=Professional)", example = "1", required = true)
            @RequestParam Long courseTypeId,
            @Parameter(description = "Linkage ID (ClassSubject/ExamSubject/CourseSubject ID)", example = "1", required = true)
            @RequestParam Long linkageId,
            Authentication authentication) {
        try {
            // Validate courseTypeId
            if (courseTypeId < 1 || courseTypeId > 3) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid courseTypeId. Must be 1 (Academic), 2 (Competitive), or 3 (Professional)"));
            }
            
            // Fetch subject information based on courseTypeId and linkageId
            Long subjectId = null;
            String subjectName = null;
            
            switch (courseTypeId.intValue()) {
                case 1: // Academic - ClassSubject
                    ClassSubject classSubject = classSubjectRepository.findById(linkageId).orElse(null);
                    if (classSubject != null && classSubject.getSubject() != null) {
                        subjectId = classSubject.getSubject().getId();
                        subjectName = classSubject.getSubject().getName();
                    }
                    break;
                    
                case 2: // Competitive - ExamSubject
                    ExamSubject examSubject = examSubjectRepository.findById(linkageId).orElse(null);
                    if (examSubject != null && examSubject.getSubject() != null) {
                        subjectId = examSubject.getSubject().getId();
                        subjectName = examSubject.getSubject().getName();
                    }
                    break;
                    
                case 3: // Professional - CourseSubject
                    CourseSubject courseSubject = courseSubjectRepository.findById(linkageId).orElse(null);
                    if (courseSubject != null && courseSubject.getSubject() != null) {
                        subjectId = courseSubject.getSubject().getId();
                        subjectName = courseSubject.getSubject().getName();
                    }
                    break;
            }
            
            // Fetch topics by courseTypeId and relationshipId (linkageId)
            List<Topic> topics = topicRepository.findByCourseTypeIdAndRelationshipIdOrderByDisplayOrderAsc(
                    courseTypeId, linkageId);
            
            // Store subjectId and subjectName for use in stream
            final Long finalSubjectId = subjectId;
            final String finalSubjectName = subjectName;
            
            // Map to response format with only required fields
            List<Map<String, Object>> topicsList = topics.stream()
                    .map(topic -> {
                        Map<String, Object> topicMap = new HashMap<>();
                        topicMap.put("topicId", topic.getId());
                        topicMap.put("topicName", topic.getName());
                        topicMap.put("linkageId", topic.getRelationshipId());
                        topicMap.put("subjectId", finalSubjectId);
                        topicMap.put("subjectName", finalSubjectName);
                        return topicMap;
                    })
                    .collect(Collectors.toList());
            
            String courseTypeName;
            switch (courseTypeId.intValue()) {
                case 1: courseTypeName = "Academic"; break;
                case 2: courseTypeName = "Competitive"; break;
                case 3: courseTypeName = "Professional"; break;
                default: courseTypeName = "Unknown";
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("courseTypeId", courseTypeId);
            response.put("courseTypeName", courseTypeName);
            response.put("linkageId", linkageId);
            response.put("subjectId", subjectId);
            response.put("subjectName", subjectName);
            response.put("topics", topicsList);
            response.put("totalCount", topicsList.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching topics for courseTypeId {} and linkageId {}", courseTypeId, linkageId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch topics: " + e.getMessage()));
        }
    }

    /**
     * Get all modules for a topic based on topicId
     * Returns modules with topic information
     */
    @Operation(
        summary = "Get modules by topic ID",
        description = "Retrieve all modules for a specific topic"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved modules"),
        @ApiResponse(responseCode = "404", description = "Topic not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/modules")
    public ResponseEntity<?> getModulesByTopic(
            @Parameter(description = "Topic ID", example = "1", required = true)
            @RequestParam Long topicId,
            Authentication authentication) {
        try {
            // Fetch topic information
            Topic topic = topicRepository.findById(topicId).orElse(null);
            
            if (topic == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Topic not found with ID: " + topicId));
            }
            
            // Fetch modules by topicId using service (returns full DTO with all fields)
            List<ModuleResponseDTO> modules = moduleService.getModulesByTopicId(topicId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("topicId", topicId);
            response.put("topicName", topic.getName());
            response.put("modules", modules);
            response.put("totalCount", modules.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching modules for topicId {}", topicId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch modules: " + e.getMessage()));
        }
    }

    /**
     * Get all chapters for a module based on moduleId
     * Returns chapters with module and topic information
     */
    @Operation(
        summary = "Get chapters by module ID",
        description = "Retrieve all chapters for a specific module"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved chapters"),
        @ApiResponse(responseCode = "404", description = "Module not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/chapters")
    public ResponseEntity<?> getChaptersByModule(
            @Parameter(description = "Module ID", example = "1", required = true)
            @RequestParam Long moduleId,
            Authentication authentication) {
        try {
            // Fetch chapters by moduleId using service (returns full DTO with all fields including videos and documents)
            List<ChapterResponseDTO> chapters = chapterService.getChaptersByModuleId(moduleId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("moduleId", moduleId);
            response.put("chapters", chapters);
            response.put("totalCount", chapters.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching chapters for moduleId {}", moduleId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch chapters: " + e.getMessage()));
        }
    }
}

