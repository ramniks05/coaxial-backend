package com.coaxial.service;

import java.time.LocalDateTime;
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

import com.coaxial.dto.ModuleRequestDTO;
import com.coaxial.dto.ModuleResponseDTO;
import com.coaxial.entity.ClassSubject;
import com.coaxial.entity.CourseSubject;
import com.coaxial.entity.ExamSubject;
import com.coaxial.entity.Module;
import com.coaxial.entity.Subject;
import com.coaxial.entity.Topic;
import com.coaxial.entity.User;
import com.coaxial.repository.ClassRepository;
import com.coaxial.repository.ClassSubjectRepository;
import com.coaxial.repository.CourseRepository;
import com.coaxial.repository.CourseSubjectRepository;
import com.coaxial.repository.CourseTypeRepository;
import com.coaxial.repository.ExamRepository;
import com.coaxial.repository.ExamSubjectRepository;
import com.coaxial.repository.ModuleRepository;
import com.coaxial.repository.TopicRepository;

@Service
@Transactional
public class ModuleService {
    
    @Autowired
    private ModuleRepository moduleRepository;
    
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
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CourseTypeRepository courseTypeRepository;
    
    public List<ModuleResponseDTO> getAllModules() {
        return moduleRepository.findAll().stream()
                .map(this::createModuleResponseDTOWithSubjectInfo)
                .collect(Collectors.toList());
    }
    
    public List<ModuleResponseDTO> getModulesByTopicId(Long topicId) {
        return moduleRepository.findByTopicIdAndIsActiveTrueOrderByDisplayOrderAsc(topicId).stream()
                .map(this::createModuleResponseDTOWithSubjectInfo)
                .collect(Collectors.toList());
    }
    
    public List<ModuleResponseDTO> getModulesByTopicIds(List<Long> topicIds) {
        return moduleRepository.findByTopicIdInAndIsActiveTrueOrderByDisplayOrderAsc(topicIds).stream()
                .map(this::createModuleResponseDTOWithSubjectInfo)
                .collect(Collectors.toList());
    }
    
    public List<ModuleResponseDTO> getModulesWithFilters(Boolean isActive, String name, Long topicId, Long subjectId, Long courseTypeId, LocalDateTime createdAfter) {
        List<Module> modules;
        
        if (topicId != null) {
            modules = moduleRepository.findByTopicIdAndIsActiveOrderByDisplayOrderAsc(topicId, isActive);
        } else if (subjectId != null) {
            // Note: Direct subject filtering is no longer supported due to Topic entity structure change
            // Subject information is resolved through courseTypeId and relationshipId
            // For now, return all modules and filter by subject in the service layer if needed
            modules = moduleRepository.findAll();
        } else if (courseTypeId != null) {
            // Note: CourseType filtering not directly supported due to entity relationship structure
            modules = moduleRepository.findAll();
        } else if (name != null && !name.trim().isEmpty()) {
            modules = moduleRepository.findByNameContainingIgnoreCaseAndIsActiveTrueOrderByDisplayOrderAsc(name);
        } else if (createdAfter != null) {
            modules = moduleRepository.findByCreatedAtAfterAndIsActiveTrueOrderByDisplayOrderAsc(createdAfter);
        } else {
            modules = moduleRepository.findAll();
        }
        
        return modules.stream()
                .map(this::createModuleResponseDTOWithSubjectInfo)
                .collect(Collectors.toList());
    }
    
    public Page<ModuleResponseDTO> getModulesPaginated(Pageable pageable) {
        return moduleRepository.findAll(pageable)
                .map(this::createModuleResponseDTOWithSubjectInfo);
    }
    
    public Page<ModuleResponseDTO> getModulesWithFilters(Long topicId, Boolean isActive, String search, Pageable pageable) {
        List<Module> allModules = getAllModulesList(topicId, isActive, search);
        
        // Manual pagination since we're using simple JPA methods
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allModules.size());
        List<Module> pageContent = allModules.subList(start, end);
        
        Page<Module> modules = new PageImpl<>(pageContent, pageable, allModules.size());
        return modules.map(this::createModuleResponseDTOWithSubjectInfo);
    }

    public List<ModuleResponseDTO> getModulesWithCombinedFilter(
            Boolean active, Long courseTypeId, Long courseId, Long classId, Long examId,
            Long subjectId, Long topicId, String search) {
        // Resolve topics using TopicService-like rules
        List<Long> topicIds;
        if (topicId != null) {
            topicIds = java.util.List.of(topicId);
        } else {
            // Use TopicRepository to resolve topic IDs by similar combined filters
            List<com.coaxial.entity.Topic> topics;
            if (search != null && !search.trim().isEmpty()) {
                topics = topicRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search.trim(), search.trim());
            } else {
                topics = topicRepository.findAll();
            }
            // Lightweight filter: by courseTypeId and active only (topic-level)
            topics = topics.stream()
                .filter(t -> courseTypeId == null || courseTypeId.equals(t.getCourseTypeId()))
                .filter(t -> active == null || active.equals(t.getIsActive()))
                .collect(Collectors.toList());

            // Optional: refine by linkage level if subjectId/classId/examId/courseId provided
            if (subjectId != null && courseTypeId != null) {
                // Treat subjectId as linkageId for specified courseType
                topics = topics.stream()
                    .filter(t -> t.getCourseTypeId().equals(courseTypeId) && t.getRelationshipId().equals(subjectId))
                    .collect(Collectors.toList());
            }
            topicIds = topics.stream().map(com.coaxial.entity.Topic::getId).collect(Collectors.toList());
        }

        if (topicIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        // Fetch modules for these topics and apply module-level search/active filters
        List<Module> modules = moduleRepository.findByTopicIdInAndIsActiveTrueOrderByDisplayOrderAsc(topicIds);
        if (active != null) {
            modules = modules.stream().filter(m -> active.equals(m.getIsActive())).collect(Collectors.toList());
        }
        if (search != null && !search.trim().isEmpty()) {
            String s = search.trim().toLowerCase();
            modules = modules.stream()
                .filter(m -> (m.getName() != null && m.getName().toLowerCase().contains(s))
                          || (m.getDescription() != null && m.getDescription().toLowerCase().contains(s)))
                .collect(Collectors.toList());
        }

        return modules.stream().map(this::createModuleResponseDTOWithSubjectInfo).collect(Collectors.toList());
    }
    
    // Helper method to get modules list using simplified JPA methods
    private List<Module> getAllModulesList(Long topicId, Boolean isActive, String search) {
        if (topicId != null && isActive != null && search != null && !search.isEmpty()) {
            // First get modules by topic and active status, then filter by search
            List<Module> modules = moduleRepository.findByTopicIdAndIsActiveOrderByDisplayOrderAsc(topicId, isActive);
            return modules.stream()
                    .filter(module -> module.getName().toLowerCase().contains(search.toLowerCase()) ||
                                     module.getDescription().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        } else if (topicId != null && isActive != null) {
            return moduleRepository.findByTopicIdAndIsActiveOrderByDisplayOrderAsc(topicId, isActive);
        } else if (topicId != null) {
            return moduleRepository.findByTopicIdOrderByDisplayOrderAsc(topicId);
        } else if (isActive != null) {
            return moduleRepository.findByIsActiveOrderByDisplayOrderAsc(isActive);
        } else if (search != null && !search.isEmpty()) {
            return moduleRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search);
        } else {
            return moduleRepository.findAll();
        }
    }
    
    public Optional<ModuleResponseDTO> getModuleById(Long id) {
        return moduleRepository.findById(id)
                .map(this::createModuleResponseDTOWithSubjectInfo);
    }
    
    public ModuleResponseDTO createModule(ModuleRequestDTO moduleRequestDTO) {
        // Validate topic exists
        Topic topic = topicRepository.findById(moduleRequestDTO.getTopicId())
                .orElseThrow(() -> (RuntimeException) new IllegalArgumentException("Topic not found with ID: " + moduleRequestDTO.getTopicId()));
        
        // Check for duplicate name within the same topic
        if (moduleRepository.existsByNameAndTopicId(moduleRequestDTO.getName(), moduleRequestDTO.getTopicId())) {
            throw new IllegalArgumentException("Module with name '" + moduleRequestDTO.getName() + "' already exists for this topic");
        }
        
        Module module = new Module();
        module.setName(moduleRequestDTO.getName());
        module.setDescription(moduleRequestDTO.getDescription());
        module.setTopic(topic);
        module.setDisplayOrder(moduleRequestDTO.getDisplayOrder());
        module.setIsActive(moduleRequestDTO.getIsActive());
        module.setCreatedBy(getCurrentUser());
        
        Module savedModule = moduleRepository.save(module);
        return createModuleResponseDTOWithSubjectInfo(savedModule);
    }
    
    public ModuleResponseDTO updateModule(Long id, ModuleRequestDTO moduleRequestDTO) {
        Module existingModule = moduleRepository.findById(id)
                .orElseThrow(() -> (RuntimeException) new IllegalArgumentException("Module not found with ID: " + id));
        
        // Validate topic exists
        Topic topic = topicRepository.findById(moduleRequestDTO.getTopicId())
                .orElseThrow(() -> (RuntimeException) new IllegalArgumentException("Topic not found with ID: " + moduleRequestDTO.getTopicId()));
        
        // Check for duplicate name within the same topic (excluding current module)
        if (moduleRepository.existsByNameAndTopicIdAndIdNot(moduleRequestDTO.getName(), moduleRequestDTO.getTopicId(), id)) {
            throw new IllegalArgumentException("Module with name '" + moduleRequestDTO.getName() + "' already exists for this topic");
        }
        
        existingModule.setName(moduleRequestDTO.getName());
        existingModule.setDescription(moduleRequestDTO.getDescription());
        existingModule.setTopic(topic);
        existingModule.setDisplayOrder(moduleRequestDTO.getDisplayOrder());
        existingModule.setIsActive(moduleRequestDTO.getIsActive());
        existingModule.setUpdatedBy(getCurrentUser());
        
        Module updatedModule = moduleRepository.save(existingModule);
        return createModuleResponseDTOWithSubjectInfo(updatedModule);
    }
    
    public void deleteModule(Long id) {
        if (!moduleRepository.existsById(id)) {
            throw new IllegalArgumentException("Module not found with ID: " + id);
        }
        moduleRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return moduleRepository.existsById(id);
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
     * Creates a ModuleResponseDTO with resolved subject information
     * based on the Topic's courseTypeId and relationshipId
     */
    private ModuleResponseDTO createModuleResponseDTOWithSubjectInfo(Module module) {
        ModuleResponseDTO dto = new ModuleResponseDTO(module);
        
        Topic topic = module.getTopic();
        if (topic != null) {
            Subject subject = resolveSubjectFromTopic(topic);
            if (subject != null) {
                dto.setSubjectId(subject.getId());
                dto.setSubjectName(subject.getName());
                dto.setSubjectType(subject.getCourseType() != null ? subject.getCourseType().getName() : "Unknown");
            }
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
    
    // Paginated method for standardized endpoints with enhanced course/courseType/class/exam info
    @Transactional(readOnly = true)
    public Page<ModuleResponseDTO> getModulesCombinedFilter(Long courseTypeId, Long courseId, Long classId, 
            Long examId, Long subjectId, Long topicId, Boolean active, String search, Pageable pageable) {
        
        // Get modules using the existing combined filter logic
        List<ModuleResponseDTO> allModules = getModulesWithCombinedFilter(
            active, courseTypeId, courseId, classId, examId, subjectId, topicId, search);
        
        // Enhance each module with course/courseType/class/exam information
        List<ModuleResponseDTO> enhancedModules = allModules.stream()
            .map(this::enhanceModuleWithCourseInfo)
            .collect(Collectors.toList());
        
        // Apply sorting
        String sortBy = pageable.getSort().isSorted() ? 
            pageable.getSort().get().findFirst().map(order -> order.getProperty()).orElse("name") : "name";
        boolean ascending = pageable.getSort().isSorted() ? 
            pageable.getSort().get().findFirst().map(order -> order.getDirection().isAscending()).orElse(true) : true;
        
        enhancedModules.sort((a, b) -> {
            Object valA = getFieldValue(a, sortBy);
            Object valB = getFieldValue(b, sortBy);
            
            if (valA == null && valB == null) return 0;
            if (valA == null) return ascending ? -1 : 1;
            if (valB == null) return ascending ? 1 : -1;
            
            int comparison = valA.toString().compareTo(valB.toString());
            return ascending ? comparison : -comparison;
        });
        
        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), enhancedModules.size());
        List<ModuleResponseDTO> pageContent = start < enhancedModules.size() ? 
            enhancedModules.subList(start, end) : java.util.Collections.emptyList();
        
        return new PageImpl<>(pageContent, pageable, enhancedModules.size());
    }
    
    /**
     * Enhances a ModuleResponseDTO with course, courseType, class, and exam information
     */
    private ModuleResponseDTO enhanceModuleWithCourseInfo(ModuleResponseDTO module) {
        try {
            // Get the topic to determine the relationship type
            Topic topic = topicRepository.findById(module.getTopicId()).orElse(null);
            if (topic == null) {
                return module;
            }
            
            Long courseTypeId = topic.getCourseTypeId();
            Long relationshipId = topic.getRelationshipId();
            
            if (courseTypeId == null || relationshipId == null) {
                return module;
            }
            
            // Set courseType information
            module.setCourseTypeId(courseTypeId);
            module.setStructureType(getStructureTypeName(courseTypeId));
            
            // Resolve course and additional information based on courseType
            if (courseTypeId == 1) { // Academic - ClassSubject
                resolveAcademicModuleInfo(module, relationshipId);
            } else if (courseTypeId == 2) { // Competitive - ExamSubject
                resolveCompetitiveModuleInfo(module, relationshipId);
            } else if (courseTypeId == 3) { // Professional - CourseSubject
                resolveProfessionalModuleInfo(module, relationshipId);
            }
            
        } catch (Exception e) {
            System.err.println("Error enhancing module info for moduleId " + module.getId() + ": " + e.getMessage());
        }
        
        return module;
    }
    
    /**
     * Resolves Academic module information (Class -> Course -> CourseType)
     */
    private void resolveAcademicModuleInfo(ModuleResponseDTO module, Long classSubjectId) {
        try {
            ClassSubject classSubject = classSubjectRepository.findById(classSubjectId).orElse(null);
            if (classSubject != null) {
                module.setClassId(classSubject.getClassEntity().getId());
                module.setClassName(classSubject.getClassEntity().getName());
                
                if (classSubject.getClassEntity().getCourse() != null) {
                    com.coaxial.entity.Course course = classSubject.getClassEntity().getCourse();
                    module.setCourseId(course.getId());
                    module.setCourseName(course.getName());
                    
                    if (course.getCourseType() != null) {
                        module.setCourseTypeName(course.getCourseType().getName());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error resolving academic module info: " + e.getMessage());
        }
    }
    
    /**
     * Resolves Competitive module information (Exam -> Course -> CourseType)
     */
    private void resolveCompetitiveModuleInfo(ModuleResponseDTO module, Long examSubjectId) {
        try {
            ExamSubject examSubject = examSubjectRepository.findById(examSubjectId).orElse(null);
            if (examSubject != null) {
                module.setExamId(examSubject.getExam().getId());
                module.setExamName(examSubject.getExam().getName());
                
                if (examSubject.getExam().getCourse() != null) {
                    com.coaxial.entity.Course course = examSubject.getExam().getCourse();
                    module.setCourseId(course.getId());
                    module.setCourseName(course.getName());
                    
                    if (course.getCourseType() != null) {
                        module.setCourseTypeName(course.getCourseType().getName());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error resolving competitive module info: " + e.getMessage());
        }
    }
    
    /**
     * Resolves Professional module information (Course -> CourseType)
     */
    private void resolveProfessionalModuleInfo(ModuleResponseDTO module, Long courseSubjectId) {
        try {
            CourseSubject courseSubject = courseSubjectRepository.findById(courseSubjectId).orElse(null);
            if (courseSubject != null) {
                module.setCourseId(courseSubject.getCourse().getId());
                module.setCourseName(courseSubject.getCourse().getName());
                
                if (courseSubject.getCourse().getCourseType() != null) {
                    module.setCourseTypeName(courseSubject.getCourse().getCourseType().getName());
                }
            }
        } catch (Exception e) {
            System.err.println("Error resolving professional module info: " + e.getMessage());
        }
    }
    
    /**
     * Gets the structure type name for a courseTypeId
     */
    private String getStructureTypeName(Long courseTypeId) {
        if (courseTypeId == 1) return "ACADEMIC";
        if (courseTypeId == 2) return "COMPETITIVE";
        if (courseTypeId == 3) return "PROFESSIONAL";
        return null;
    }
    
    /**
     * Helper method to get field value for sorting
     */
    private Object getFieldValue(ModuleResponseDTO module, String fieldName) {
        switch (fieldName.toLowerCase()) {
            case "name": return module.getName();
            case "coursename": return module.getCourseName();
            case "coursetypename": return module.getCourseTypeName();
            case "classname": return module.getClassName();
            case "examname": return module.getExamName();
            case "topicname": return module.getTopicName();
            case "subjectname": return module.getSubjectName();
            case "displayorder": return module.getDisplayOrder();
            case "createdat": return module.getCreatedAt();
            default: return module.getName();
        }
    }
}
