package com.coaxial.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coaxial.dto.ClassSubjectResponse;
import com.coaxial.dto.ClassSubjectSummaryDTO;
import com.coaxial.dto.CourseSubjectResponseDTO;
import com.coaxial.dto.CourseSubjectSummaryDTO;
import com.coaxial.dto.ExamSubjectResponseDTO;
import com.coaxial.dto.ExamSubjectSummaryDTO;
import com.coaxial.dto.SubjectRequestDTO;
import com.coaxial.dto.SubjectResponseDTO;
import com.coaxial.entity.ClassSubject;
import com.coaxial.entity.CourseSubject;
import com.coaxial.entity.CourseType;
import com.coaxial.entity.ExamSubject;
import com.coaxial.entity.Subject;
import com.coaxial.repository.ClassRepository;
import com.coaxial.repository.CourseRepository;
import com.coaxial.repository.CourseTypeRepository;
import com.coaxial.repository.ExamRepository;
import com.coaxial.repository.SubjectRepository;

@Service
@Transactional
public class SubjectService {
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private ClassSubjectService classSubjectService;
    
    @Autowired
    private ExamSubjectService examSubjectService;
    
    @Autowired
    private CourseSubjectService courseSubjectService;
    
    @Autowired
    private ClassRepository classRepository;
    
    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CourseTypeRepository courseTypeRepository;
    
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }
    
    public List<Subject> getSubjectsWithFilters(Boolean active, Long courseTypeId, Long courseId) {
        // Enhanced filtering with course relationships
        if (courseTypeId != null) {
            // Filter by course type through relationships
            if (active != null) {
                return subjectRepository.findByCourseSubjectsCourseCourseTypeIdAndIsActiveOrderByDisplayOrderAsc(courseTypeId, active);
            } else {
                return subjectRepository.findByCourseSubjectsCourseCourseTypeIdOrderByDisplayOrderAsc(courseTypeId);
            }
        } else if (courseId != null) {
            // Filter by specific course
            if (active != null) {
                return subjectRepository.findByCourseSubjectsCourseIdAndIsActiveOrderByDisplayOrderAsc(courseId, active);
            } else {
                return subjectRepository.findByCourseSubjectsCourseIdOrderByDisplayOrderAsc(courseId);
            }
        } else {
            // Basic active/inactive filtering
            if (active != null && active) {
                return subjectRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
            } else if (active != null && !active) {
                return subjectRepository.findByIsActiveFalseOrderByDisplayOrderAsc();
            } else {
                return subjectRepository.findAllByOrderByDisplayOrderAsc();
            }
        }
    }
    
    public List<Subject> getSubjectsByCourseType(CourseType courseType) {
        return subjectRepository.findByCourseTypeAndIsActiveTrueOrderByDisplayOrderAsc(courseType);
    }
    
    
    public Optional<Subject> getSubjectById(Long id) {
        return subjectRepository.findById(id);
    }
    
    public Subject createSubject(SubjectRequestDTO subjectRequestDTO) {
        Subject subject = new Subject();
        subject.setName(subjectRequestDTO.getName());
        subject.setDescription(subjectRequestDTO.getDescription());
        subject.setCourseType(subjectRequestDTO.getCourseType());
        subject.setDisplayOrder(subjectRequestDTO.getDisplayOrder());
        subject.setIsActive(subjectRequestDTO.getIsActive());
        
        return subjectRepository.save(subject);
    }
    
    public Subject updateSubject(Long id, SubjectRequestDTO subjectRequestDTO) {
        Subject existingSubject = subjectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Subject not found with ID: " + id));
        
        existingSubject.setName(subjectRequestDTO.getName());
        existingSubject.setDescription(subjectRequestDTO.getDescription());
        existingSubject.setCourseType(subjectRequestDTO.getCourseType());
        existingSubject.setDisplayOrder(subjectRequestDTO.getDisplayOrder());
        existingSubject.setIsActive(subjectRequestDTO.getIsActive());
        
        return subjectRepository.save(existingSubject);
    }
    
    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new IllegalArgumentException("Subject not found with ID: " + id);
        }
        subjectRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return subjectRepository.existsById(id);
    }
    
    // Enhanced search functionality
    public List<Subject> searchSubjectsByName(String name) {
        return subjectRepository.findByNameContainingIgnoreCaseAndIsActiveTrueOrderByDisplayOrderAsc(name);
    }
    
    public List<Subject> searchSubjectsByDescription(String description) {
        return subjectRepository.findByDescriptionContainingIgnoreCaseAndIsActiveTrueOrderByDisplayOrderAsc(description);
    }
    
    public List<Subject> searchSubjects(String searchTerm) {
        return subjectRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsActiveTrueOrderByDisplayOrderAsc(searchTerm, searchTerm);
    }
    
    // Get subjects with relationship data
    public List<Subject> getSubjectsWithRelationships() {
        return subjectRepository.findAllByOrderByDisplayOrderAsc();
    }
    
    public Optional<Subject> getSubjectWithRelationships(Long id) {
        return subjectRepository.findById(id);
    }
    
    // DTO-based methods to avoid recursive loops
    public List<SubjectResponseDTO> getAllSubjectsAsDTO() {
        List<Subject> subjects = subjectRepository.findAllByOrderByDisplayOrderAsc();
        return subjects.stream()
            .map(this::convertToResponseDTO)
            .filter(dto -> dto != null) // Filter out any null DTOs
            .collect(java.util.stream.Collectors.toList());
    }
    
    public List<SubjectResponseDTO> getSubjectsWithCombinedFiltersAsDTO(Boolean active, Long courseTypeId, Long courseId, Long classId, Long examId) {
        List<Subject> subjects = getSubjectsWithCombinedFilters(active, courseTypeId, courseId, classId, examId);
        return subjects.stream()
            .map(this::convertToResponseDTO)
            .filter(dto -> dto != null) // Filter out any null DTOs
            .collect(java.util.stream.Collectors.toList());
    }
    
    public Optional<SubjectResponseDTO> getSubjectByIdAsDTO(Long id) {
        Optional<Subject> subject = subjectRepository.findById(id);
        return subject.map(this::convertToResponseDTO);
    }
    
    public List<SubjectResponseDTO> searchSubjectsAsDTO(String searchTerm) {
        List<Subject> subjects = searchSubjects(searchTerm);
        return subjects.stream()
            .map(this::convertToResponseDTO)
            .filter(dto -> dto != null) // Filter out any null DTOs
            .collect(java.util.stream.Collectors.toList());
    }
    
    // Convert Subject entity to SubjectResponseDTO to avoid recursive loops
    public SubjectResponseDTO convertToResponseDTO(Subject subject) {
        try {
            if (subject == null) {
                return null;
            }
            
            SubjectResponseDTO dto = new SubjectResponseDTO();
            dto.setId(subject.getId());
            dto.setName(sanitizeString(subject.getName()));
            dto.setDescription(sanitizeString(subject.getDescription()));
            dto.setCourseType(subject.getCourseType());
            dto.setDisplayOrder(subject.getDisplayOrder());
            dto.setIsActive(subject.getIsActive());
            dto.setCreatedAt(subject.getCreatedAt());
            dto.setUpdatedAt(subject.getUpdatedAt());
            dto.setCreatedBy(sanitizeString(subject.getCreatedBy()));
            dto.setUpdatedBy(sanitizeString(subject.getUpdatedBy()));
            
            // Convert relationships to summary DTOs to avoid loops
            if (subject.getClassSubjects() != null) {
                dto.setClassSubjects(subject.getClassSubjects().stream()
                    .map(this::convertToClassSubjectSummaryDTO)
                    .filter(cs -> cs != null) // Filter out null conversions
                    .collect(java.util.stream.Collectors.toList()));
            }
            
            if (subject.getExamSubjects() != null) {
                dto.setExamSubjects(subject.getExamSubjects().stream()
                    .map(this::convertToExamSubjectSummaryDTO)
                    .filter(es -> es != null) // Filter out null conversions
                    .collect(java.util.stream.Collectors.toList()));
            }
            
            if (subject.getCourseSubjects() != null) {
                dto.setCourseSubjects(subject.getCourseSubjects().stream()
                    .map(this::convertToCourseSubjectSummaryDTO)
                    .filter(cs -> cs != null) // Filter out null conversions
                    .collect(java.util.stream.Collectors.toList()));
            }
            
            return dto;
        } catch (Exception e) {
            // Log the error and return a minimal DTO to prevent complete failure
            System.err.println("Error converting subject to DTO: " + e.getMessage());
            if (subject != null) {
                SubjectResponseDTO fallbackDto = new SubjectResponseDTO();
                fallbackDto.setId(subject.getId());
                fallbackDto.setName("Error loading subject");
                fallbackDto.setDescription("Unable to load subject details");
                return fallbackDto;
            }
            return null;
        }
    }
    
    private ClassSubjectSummaryDTO convertToClassSubjectSummaryDTO(ClassSubject classSubject) {
        ClassSubjectSummaryDTO dto = new ClassSubjectSummaryDTO();
        dto.setId(classSubject.getId());
        dto.setClassId(classSubject.getClassEntity() != null ? classSubject.getClassEntity().getId() : null);
        dto.setClassName(sanitizeString(classSubject.getClassEntity() != null ? classSubject.getClassEntity().getName() : null));
        dto.setSubjectId(classSubject.getSubject() != null ? classSubject.getSubject().getId() : null);
        dto.setSubjectName(sanitizeString(classSubject.getSubject() != null ? classSubject.getSubject().getName() : null));
        dto.setIsCompulsory(classSubject.getIsCompulsory());
        dto.setDisplayOrder(classSubject.getDisplayOrder());
        dto.setIsActive(classSubject.getIsActive());
        dto.setCreatedAt(classSubject.getCreatedAt());
        return dto;
    }
    
    private ExamSubjectSummaryDTO convertToExamSubjectSummaryDTO(ExamSubject examSubject) {
        ExamSubjectSummaryDTO dto = new ExamSubjectSummaryDTO();
        dto.setId(examSubject.getId());
        dto.setExamId(examSubject.getExam() != null ? examSubject.getExam().getId() : null);
        dto.setExamName(sanitizeString(examSubject.getExam() != null ? examSubject.getExam().getName() : null));
        dto.setSubjectId(examSubject.getSubject() != null ? examSubject.getSubject().getId() : null);
        dto.setSubjectName(sanitizeString(examSubject.getSubject() != null ? examSubject.getSubject().getName() : null));
        dto.setWeightage(examSubject.getWeightage());
        dto.setDisplayOrder(examSubject.getDisplayOrder());
        dto.setCreatedAt(examSubject.getCreatedAt());
        return dto;
    }
    
    private CourseSubjectSummaryDTO convertToCourseSubjectSummaryDTO(CourseSubject courseSubject) {
        CourseSubjectSummaryDTO dto = new CourseSubjectSummaryDTO();
        dto.setId(courseSubject.getId());
        dto.setCourseId(courseSubject.getCourse() != null ? courseSubject.getCourse().getId() : null);
        dto.setCourseName(sanitizeString(courseSubject.getCourse() != null ? courseSubject.getCourse().getName() : null));
        dto.setSubjectId(courseSubject.getSubject() != null ? courseSubject.getSubject().getId() : null);
        dto.setSubjectName(sanitizeString(courseSubject.getSubject() != null ? courseSubject.getSubject().getName() : null));
        dto.setIsCompulsory(courseSubject.getIsCompulsory());
        dto.setDisplayOrder(courseSubject.getDisplayOrder());
        dto.setCreatedAt(courseSubject.getCreatedAt());
        return dto;
    }
    
    // String sanitization method to remove control characters
    private String sanitizeString(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove control characters (ASCII 0-31 except tab, newline, carriage return)
        // Also remove DEL character (127) and other problematic characters
        String sanitized = input.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "")
                               .replaceAll("[\\uFEFF]", "") // Remove BOM
                               .replaceAll("[\\u200B-\\u200D\\uFEFF]", "") // Remove zero-width characters
                               .trim();
        
        // If the string becomes empty after sanitization, return null
        return sanitized.isEmpty() ? null : sanitized;
    }
    
    // Comprehensive combined filtering method
    public List<Subject> getSubjectsWithCombinedFilters(Boolean active, Long courseTypeId, Long courseId, Long classId, Long examId) {
        // Priority-based filtering logic
        
        // 1. Most specific: Filter by class ID (Academic subjects linked to specific class)
        if (classId != null) {
            if (active != null) {
                return subjectRepository.findByClassSubjectsClassEntityIdAndIsActiveOrderByDisplayOrderAsc(classId, active);
            } else {
                return subjectRepository.findByClassSubjectsClassEntityIdOrderByDisplayOrderAsc(classId);
            }
        }
        
        // 2. Specific: Filter by exam ID (Competitive subjects linked to specific exam)
        if (examId != null) {
            if (active != null) {
                return subjectRepository.findByExamSubjectsExamIdAndIsActiveOrderByDisplayOrderAsc(examId, active);
            } else {
                return subjectRepository.findByExamSubjectsExamIdOrderByDisplayOrderAsc(examId);
            }
        }
        
        // 3. Course-specific: Filter by course ID (Professional subjects linked to specific course)
        if (courseId != null) {
            if (active != null) {
                return subjectRepository.findByCourseSubjectsCourseIdAndIsActiveOrderByDisplayOrderAsc(courseId, active);
            } else {
                return subjectRepository.findByCourseSubjectsCourseIdOrderByDisplayOrderAsc(courseId);
            }
        }
        
        // 4. Course type: Filter by course type (returns appropriate subject type)
        if (courseTypeId != null) {
            return getSubjectsByCourseType(courseTypeId, active);
        }
        
        // 5. Default: Basic active/inactive filtering
        if (active != null && active) {
            return subjectRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
        } else if (active != null && !active) {
            return subjectRepository.findByIsActiveFalseOrderByDisplayOrderAsc();
        } else {
            return subjectRepository.findAllByOrderByDisplayOrderAsc();
        }
    }
    
    // Helper method to get subjects by course type
    private List<Subject> getSubjectsByCourseType(Long courseTypeId, Boolean active) {
        // Note: This method needs to be updated to work with CourseType
        // For now, return all subjects until CourseType is properly set up
        if (active != null && active) {
            return subjectRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
        } else if (active != null && !active) {
            return subjectRepository.findByIsActiveFalseOrderByDisplayOrderAsc();
        } else {
            return subjectRepository.findAllByOrderByDisplayOrderAsc();
        }
    }
    
    // Fix existing subjects with incorrect CourseType
    @Transactional
    public Map<String, Object> fixSubjectTypes() {
        Map<String, Object> result = new HashMap<>();
        
        // Get all subjects
        List<Subject> allSubjects = subjectRepository.findAll();
        
        int fixedCount = 0;
        int nullTypeCount = 0;
        int incorrectTypeCount = 0;
        
        for (Subject subject : allSubjects) {
            // Determine correct CourseType based on relationships
            CourseType correctType = determineCourseTypeFromRelationships(subject);
            
            if (subject.getCourseType() == null) {
                nullTypeCount++;
                if (correctType != null) {
                    subject.setCourseType(correctType);
                    subjectRepository.save(subject);
                    fixedCount++;
                }
            } else if (subject.getCourseType() != correctType) {
                incorrectTypeCount++;
                if (correctType != null) {
                    subject.setCourseType(correctType);
                    subjectRepository.save(subject);
                    fixedCount++;
                }
            }
        }
        
        result.put("totalSubjects", allSubjects.size());
        result.put("subjectsWithNullType", nullTypeCount);
        result.put("subjectsWithIncorrectType", incorrectTypeCount);
        result.put("fixedCount", fixedCount);
        result.put("remainingIssues", (nullTypeCount + incorrectTypeCount) - fixedCount);
        
        return result;
    }
    
    // Helper method to determine CourseType from relationships
    private CourseType determineCourseTypeFromRelationships(Subject subject) {
        // Note: This method needs to be updated to work with CourseType
        // For now, return null until CourseType is properly set up
        // The actual CourseType should be determined based on the subject's existing courseType field
        return subject.getCourseType();
    }
    
    // Initialize predefined subjects
    @Transactional
    public void initializePredefinedSubjects() {
        // Note: This method needs to be updated to work with CourseType
        // The predefined subjects should now be created through the database migration
        // This method is kept for backward compatibility but does nothing
        System.out.println("initializePredefinedSubjects: This method is deprecated. Use database migration instead.");
    }
    
    // Paginated method for master subjects by course type
    @Transactional(readOnly = true)
    public Page<SubjectResponseDTO> getMasterSubjectsByCourseType(Long courseTypeId, Boolean active, String search, Pageable pageable) {
        List<Subject> subjects;
        
        if (search != null && !search.trim().isEmpty()) {
            subjects = searchSubjects(search.trim());
            subjects = subjects.stream()
                .filter(s -> s.getCourseType() != null && courseTypeId.equals(s.getCourseType().getId()))
                .collect(Collectors.toList());
            if (active != null) {
                boolean act = active.booleanValue();
                subjects = subjects.stream().filter(s -> Boolean.TRUE.equals(s.getIsActive()) == act).collect(Collectors.toList());
            }
        } else {
            if (active != null && active.booleanValue()) {
                subjects = subjectRepository.findByCourseTypeIdAndIsActiveTrueOrderByDisplayOrderAsc(courseTypeId);
            } else if (active != null) {
                subjects = subjectRepository.findByCourseTypeIdOrderByDisplayOrderAsc(courseTypeId)
                    .stream().filter(s -> Boolean.FALSE.equals(s.getIsActive())).collect(Collectors.toList());
            } else {
                subjects = subjectRepository.findByCourseTypeIdOrderByDisplayOrderAsc(courseTypeId);
            }
        }
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), subjects.size());
        List<Subject> pageContent = start < subjects.size() ? subjects.subList(start, end) : List.of();
        
        List<SubjectResponseDTO> dtos = pageContent.stream()
            .map(this::convertToResponseDTO)
            .filter(dto -> dto != null)
            .collect(Collectors.toList());
        
        return new PageImpl<>(dtos, pageable, subjects.size());
    }
    
    // Paginated method for subject linkages filter
    @Transactional(readOnly = true)
    public Page<Map<String, Object>> getSubjectLinkagesFilter(Long courseTypeId, Long courseId, Long classId, 
            Long examId, Boolean active, String search, Pageable pageable) {
        
        List<Map<String, Object>> allLinkages = new java.util.ArrayList<>();
        
        // Get ClassSubject linkages (Academic - courseTypeId = 1)
        if (courseTypeId == null || courseTypeId == 1) {
            List<ClassSubjectResponse> classSubjects = classSubjectService.getAllClassSubjects();
            
            // Apply filters
            if (classId != null) {
                classSubjects = classSubjects.stream()
                    .filter(cs -> classId.equals(cs.getClassId()))
                    .collect(Collectors.toList());
            }
            if (active != null) {
                classSubjects = classSubjects.stream()
                    .filter(cs -> active.equals(cs.getIsActive()))
                    .collect(Collectors.toList());
            }
            if (search != null && !search.trim().isEmpty()) {
                String searchLower = search.toLowerCase();
                classSubjects = classSubjects.stream()
                    .filter(cs -> 
                        cs.getClassName().toLowerCase().contains(searchLower) ||
                        cs.getSubjectName().toLowerCase().contains(searchLower) ||
                        (cs.getSubjectType() != null && cs.getSubjectType().toLowerCase().contains(searchLower))
                    )
                    .collect(Collectors.toList());
            }
            
            // Convert to Map format with course and courseType information
            for (ClassSubjectResponse cs : classSubjects) {
                Map<String, Object> linkage = new java.util.HashMap<>();
                linkage.put("linkageType", "ClassSubject");
                linkage.put("linkageId", cs.getId());
                linkage.put("classId", cs.getClassId());
                linkage.put("className", cs.getClassName());
                linkage.put("subjectId", cs.getSubjectId());
                linkage.put("subjectName", cs.getSubjectName());
                linkage.put("subjectType", cs.getSubjectType());
                linkage.put("displayOrder", cs.getDisplayOrder());
                linkage.put("isActive", cs.getIsActive());
                linkage.put("createdAt", cs.getCreatedAt());
                linkage.put("name", cs.getSubjectName()); // For sorting
                
                // Add course and courseType information
                try {
                    Optional<com.coaxial.entity.ClassEntity> classEntity = classRepository.findById(cs.getClassId());
                    if (classEntity.isPresent() && classEntity.get().getCourse() != null) {
                        com.coaxial.entity.Course course = classEntity.get().getCourse();
                        linkage.put("courseId", course.getId());
                        linkage.put("courseName", course.getName());
                        
                        if (course.getCourseType() != null) {
                            CourseType courseType = course.getCourseType();
                            linkage.put("courseTypeId", courseType.getId());
                            linkage.put("courseTypeName", courseType.getName());
                            linkage.put("structureType", courseType.getStructureType() != null ? courseType.getStructureType().name() : null);
                        }
                    }
                } catch (Exception e) {
                    // Log error but don't fail the entire operation
                    System.err.println("Error fetching course info for classId " + cs.getClassId() + ": " + e.getMessage());
                }
                
                allLinkages.add(linkage);
            }
        }
        
        // Get ExamSubject linkages (Competitive - courseTypeId = 2)
        if (courseTypeId == null || courseTypeId == 2) {
            List<ExamSubjectResponseDTO> examSubjects = examSubjectService.getAllExamSubjects();
            
            // Apply filters
            if (examId != null) {
                examSubjects = examSubjects.stream()
                    .filter(es -> examId.equals(es.getExamId()))
                    .collect(Collectors.toList());
            }
            if (search != null && !search.trim().isEmpty()) {
                String searchLower = search.toLowerCase();
                examSubjects = examSubjects.stream()
                    .filter(es -> 
                        es.getExamName().toLowerCase().contains(searchLower) ||
                        es.getSubjectName().toLowerCase().contains(searchLower) ||
                        (es.getSubjectType() != null && es.getSubjectType().toLowerCase().contains(searchLower))
                    )
                    .collect(Collectors.toList());
            }
            
            // Convert to Map format with course and courseType information
            for (ExamSubjectResponseDTO es : examSubjects) {
                Map<String, Object> linkage = new java.util.HashMap<>();
                linkage.put("linkageType", "ExamSubject");
                linkage.put("linkageId", es.getId());
                linkage.put("examId", es.getExamId());
                linkage.put("examName", es.getExamName());
                linkage.put("subjectId", es.getSubjectId());
                linkage.put("subjectName", es.getSubjectName());
                linkage.put("subjectType", es.getSubjectType());
                linkage.put("weightage", es.getWeightage());
                linkage.put("displayOrder", es.getDisplayOrder());
                linkage.put("createdAt", es.getCreatedAt());
                linkage.put("name", es.getSubjectName()); // For sorting
                
                // Add course and courseType information
                try {
                    Optional<com.coaxial.entity.Exam> exam = examRepository.findById(es.getExamId());
                    if (exam.isPresent() && exam.get().getCourse() != null) {
                        com.coaxial.entity.Course course = exam.get().getCourse();
                        linkage.put("courseId", course.getId());
                        linkage.put("courseName", course.getName());
                        
                        if (course.getCourseType() != null) {
                            CourseType courseType = course.getCourseType();
                            linkage.put("courseTypeId", courseType.getId());
                            linkage.put("courseTypeName", courseType.getName());
                            linkage.put("structureType", courseType.getStructureType() != null ? courseType.getStructureType().name() : null);
                        }
                    }
                } catch (Exception e) {
                    // Log error but don't fail the entire operation
                    System.err.println("Error fetching course info for examId " + es.getExamId() + ": " + e.getMessage());
                }
                
                allLinkages.add(linkage);
            }
        }
        
        // Get CourseSubject linkages (Professional - courseTypeId = 3)
        if (courseTypeId == null || courseTypeId == 3) {
            List<CourseSubjectResponseDTO> courseSubjects = courseSubjectService.getAllCourseSubjects();
            
            // Apply filters
            if (courseId != null) {
                courseSubjects = courseSubjects.stream()
                    .filter(cs -> courseId.equals(cs.getCourseId()))
                    .collect(Collectors.toList());
            }
            if (search != null && !search.trim().isEmpty()) {
                String searchLower = search.toLowerCase();
                courseSubjects = courseSubjects.stream()
                    .filter(cs -> 
                        cs.getCourseName().toLowerCase().contains(searchLower) ||
                        cs.getSubjectName().toLowerCase().contains(searchLower) ||
                        (cs.getSubjectType() != null && cs.getSubjectType().toLowerCase().contains(searchLower))
                    )
                    .collect(Collectors.toList());
            }
            
            // Convert to Map format with course and courseType information
            for (CourseSubjectResponseDTO cs : courseSubjects) {
                Map<String, Object> linkage = new java.util.HashMap<>();
                linkage.put("linkageType", "CourseSubject");
                linkage.put("linkageId", cs.getId());
                linkage.put("courseId", cs.getCourseId());
                linkage.put("courseName", cs.getCourseName());
                linkage.put("subjectId", cs.getSubjectId());
                linkage.put("subjectName", cs.getSubjectName());
                linkage.put("subjectType", cs.getSubjectType());
                linkage.put("isCompulsory", cs.getIsCompulsory());
                linkage.put("displayOrder", cs.getDisplayOrder());
                linkage.put("createdAt", cs.getCreatedAt());
                linkage.put("name", cs.getSubjectName()); // For sorting
                
                // Add course and courseType information
                try {
                    Optional<com.coaxial.entity.Course> course = courseRepository.findById(cs.getCourseId());
                    if (course.isPresent() && course.get().getCourseType() != null) {
                        CourseType courseType = course.get().getCourseType();
                        linkage.put("courseTypeId", courseType.getId());
                        linkage.put("courseTypeName", courseType.getName());
                        linkage.put("structureType", courseType.getStructureType() != null ? courseType.getStructureType().name() : null);
                    }
                } catch (Exception e) {
                    // Log error but don't fail the entire operation
                    System.err.println("Error fetching courseType info for courseId " + cs.getCourseId() + ": " + e.getMessage());
                }
                
                allLinkages.add(linkage);
            }
        }
        
        // Apply sorting
        String sortBy = pageable.getSort().isSorted() ? 
            pageable.getSort().get().findFirst().map(Sort.Order::getProperty).orElse("name") : "name";
        boolean ascending = pageable.getSort().isSorted() ? 
            pageable.getSort().get().findFirst().map(order -> order.getDirection().isAscending()).orElse(true) : true;
        
        allLinkages.sort((a, b) -> {
            Object valA = a.get(sortBy);
            Object valB = b.get(sortBy);
            
            if (valA == null && valB == null) return 0;
            if (valA == null) return ascending ? -1 : 1;
            if (valB == null) return ascending ? 1 : -1;
            
            int comparison = valA.toString().compareTo(valB.toString());
            return ascending ? comparison : -comparison;
        });
        
        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allLinkages.size());
        List<Map<String, Object>> pageContent = start < allLinkages.size() ? 
            allLinkages.subList(start, end) : List.of();
        
        return new PageImpl<>(pageContent, pageable, allLinkages.size());
    }
}