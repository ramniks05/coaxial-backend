package com.coaxial.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

import com.coaxial.dto.ClassSubjectRequest;
import com.coaxial.dto.ClassSubjectResponse;
import com.coaxial.dto.CourseSubjectRequestDTO;
import com.coaxial.dto.CourseSubjectResponseDTO;
import com.coaxial.dto.ExamSubjectRequestDTO;
import com.coaxial.dto.ExamSubjectResponseDTO;
import com.coaxial.dto.SubjectRequestDTO;
import com.coaxial.dto.SubjectResponseDTO;
import com.coaxial.entity.ClassEntity;
import com.coaxial.entity.Course;
import com.coaxial.entity.CourseType;
import com.coaxial.entity.Exam;
import com.coaxial.entity.StructureType;
import com.coaxial.entity.Subject;
import com.coaxial.repository.ClassRepository;
import com.coaxial.repository.CourseRepository;
import com.coaxial.repository.CourseTypeRepository;
import com.coaxial.repository.ExamRepository;
import com.coaxial.repository.SubjectRepository;
import com.coaxial.service.ClassSubjectService;
import com.coaxial.service.CourseSubjectService;
import com.coaxial.service.ExamSubjectService;
import com.coaxial.service.SubjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/subjects")
@CrossOrigin(origins = "*")
public class SubjectController {
    
    @Autowired
    private SubjectService subjectService;
    
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
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<?> getAllSubjects(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Long courseTypeId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "true") Boolean unique) {
        
        try {
            if (search != null && !search.trim().isEmpty()) {
                // Search functionality - return full subjects
                List<SubjectResponseDTO> subjects = subjectService.searchSubjectsAsDTO(search.trim());
                return ResponseEntity.ok(subjects);
            }
            
            // Handle specific filtering with relationship data - return enriched linkage data
            if (classId != null && courseTypeId != null && courseTypeId == 1) {
                // Academic: Filter by class - return enriched ClassSubject entities
                List<ClassSubjectResponse> classSubjects = classSubjectService.getSubjectsForClass(classId);
                return ResponseEntity.ok(enrichClassSubjects(classSubjects));
                
            } else if (examId != null && courseTypeId != null && courseTypeId == 2) {
                // Competitive: Filter by exam - return enriched ExamSubject entities
                List<ExamSubjectResponseDTO> examSubjects = examSubjectService.getSubjectsForExam(examId);
                return ResponseEntity.ok(enrichExamSubjects(examSubjects));
                
            } else if (courseId != null && courseTypeId != null && courseTypeId == 3) {
                // Professional: Filter by course - return enriched CourseSubject entities
                List<CourseSubjectResponseDTO> courseSubjects = courseSubjectService.getSubjectsForCourse(courseId);
                return ResponseEntity.ok(enrichCourseSubjects(courseSubjects));
                
            } else if (courseId != null && courseTypeId != null && courseTypeId == 1) {
                // Academic: Filter by course -> expand to classes -> gather class subjects
                List<ClassEntity> classes = (active != null)
                    ? classRepository.findByCourseIdAndIsActive(courseId, active)
                    : classRepository.findByCourseId(courseId);

                List<ClassSubjectResponse> aggregated = new java.util.ArrayList<>();
                for (ClassEntity ce : classes) {
                    aggregated.addAll(classSubjectService.getSubjectsForClass(ce.getId()));
                }
                return ResponseEntity.ok(enrichClassSubjects(aggregated));

            } else if (courseId != null && courseTypeId != null && courseTypeId == 2) {
                // Competitive: Filter by course -> expand to exams -> gather exam subjects
                Optional<Course> courseOpt = courseRepository.findById(courseId);
                if (courseOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Course not found: " + courseId));
                }

                Course course = courseOpt.get();
                List<Exam> exams = (active != null)
                    ? examRepository.findByCourseAndIsActive(course, active)
                    : examRepository.findByCourse(course);

                List<ExamSubjectResponseDTO> aggregated = new java.util.ArrayList<>();
                for (Exam exam : exams) {
                    aggregated.addAll(examSubjectService.getSubjectsForExam(exam.getId()));
                }
                return ResponseEntity.ok(enrichExamSubjects(aggregated));
                
            } else {
                // Fallback: Build subject list from linkage tables honoring filters on linkages (not subject.isActive)
                final boolean dedupe = unique == null ? true : unique.booleanValue();
                final java.util.List<Long> collectedSubjectIds = new java.util.ArrayList<>();
                final java.util.Set<Long> uniqueSubjectIds = new java.util.HashSet<>();

                // Optionally derive course type name for filtering linkage DTOs
                final String courseTypeName = (courseTypeId != null)
                    ? courseTypeRepository.findById(courseTypeId).map(com.coaxial.entity.CourseType::getName).orElse(null)
                    : null;

                // class linkages
                java.util.List<ClassSubjectResponse> cls = classSubjectService.getAllClassSubjects();
                if (classId != null) {
                    cls = cls.stream().filter(x -> classId.equals(x.getClassId())).collect(java.util.stream.Collectors.toList());
                }
                if (active != null) {
                    cls = cls.stream().filter(x -> active.equals(x.getIsActive())).collect(java.util.stream.Collectors.toList());
                }
                if (courseTypeName != null) {
                    cls = cls.stream().filter(x -> courseTypeName.equalsIgnoreCase(x.getSubjectType())).collect(java.util.stream.Collectors.toList());
                }
                if (dedupe) {
                    cls.forEach(cs -> uniqueSubjectIds.add(cs.getSubjectId()));
                } else {
                    cls.forEach(cs -> collectedSubjectIds.add(cs.getSubjectId()));
                }

                // exam linkages
                java.util.List<ExamSubjectResponseDTO> exs = examSubjectService.getAllExamSubjects();
                if (examId != null) {
                    exs = exs.stream().filter(x -> examId.equals(x.getExamId())).collect(java.util.stream.Collectors.toList());
                }
                if (courseTypeName != null) {
                    exs = exs.stream().filter(x -> courseTypeName.equalsIgnoreCase(x.getSubjectType())).collect(java.util.stream.Collectors.toList());
                }
                if (dedupe) {
                    exs.forEach(es -> uniqueSubjectIds.add(es.getSubjectId()));
                } else {
                    exs.forEach(es -> collectedSubjectIds.add(es.getSubjectId()));
                }

                // course linkages
                java.util.List<CourseSubjectResponseDTO> cos = courseSubjectService.getAllCourseSubjects();
                if (courseId != null) {
                    cos = cos.stream().filter(x -> courseId.equals(x.getCourseId())).collect(java.util.stream.Collectors.toList());
                }
                if (courseTypeName != null) {
                    cos = cos.stream().filter(x -> courseTypeName.equalsIgnoreCase(x.getSubjectType())).collect(java.util.stream.Collectors.toList());
                }
                if (dedupe) {
                    cos.forEach(cs -> uniqueSubjectIds.add(cs.getSubjectId()));
                } else {
                    cos.forEach(cs -> collectedSubjectIds.add(cs.getSubjectId()));
                }

                java.util.stream.Stream<Long> idStream = dedupe
                    ? uniqueSubjectIds.stream()
                    : collectedSubjectIds.stream();

                java.util.List<SubjectResponseDTO> subjects = idStream
                    .map(id -> subjectRepository.findById(id).map(subjectService::convertToResponseDTO).orElse(null))
                    .filter(java.util.Objects::nonNull)
                    .collect(java.util.stream.Collectors.toList());

                return ResponseEntity.ok(subjects);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Failed to fetch subjects: " + e.getMessage()
            ));
        }
    }

   
    
    @PostMapping("/create-with-auto-link")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createSubjectWithAutoLink(@Valid @RequestBody com.coaxial.dto.SubjectLinkRequest linkRequest) {
        try {
            // 1) Load existing subject
            Subject subject = subjectRepository.findById(linkRequest.getSubjectId())
                .orElseThrow(() -> new IllegalArgumentException("Subject not found with ID: " + linkRequest.getSubjectId()));

            if (subject.getCourseType() == null) {
                throw new IllegalArgumentException("Subject has no course type associated");
            }

            CourseType courseType = subject.getCourseType();

            // Optional validation: ensure front-end sent courseTypeId matches
            if (linkRequest.getCourseTypeId() != null && !linkRequest.getCourseTypeId().equals(courseType.getId())) {
                throw new IllegalArgumentException("courseTypeId does not match subject's course type");
            }

            // 2) Create the appropriate link based on structure
            Map<String, Object> linkResult = new HashMap<>();

            StructureType structureType = courseType.getStructureType();
            if (structureType == StructureType.ACADEMIC) {
                if (linkRequest.getClassId() == null) {
                    throw new IllegalArgumentException("classId is required for Academic course type");
                }

                // Prevent duplicates via service/repository constraints
                ClassSubjectRequest req = new ClassSubjectRequest();
                req.setClassId(linkRequest.getClassId());
                req.setSubjectId(subject.getId());
                req.setDisplayOrder(linkRequest.getDisplayOrder());
                req.setIsActive(linkRequest.getIsActive());

                ClassSubjectResponse res = classSubjectService.addSubjectToClass(req);

                linkResult.put("id", res.getId());
                linkResult.put("classId", res.getClassId());
                linkResult.put("className", res.getClassName());
                linkResult.put("subjectId", res.getSubjectId());
                linkResult.put("subjectName", res.getSubjectName());
                linkResult.put("displayOrder", res.getDisplayOrder());
                linkResult.put("isActive", res.getIsActive());
                linkResult.put("createdAt", res.getCreatedAt());

                        // Verify class exists
                        classRepository.findById(linkRequest.getClassId())
                    .orElseThrow(() -> new IllegalArgumentException("Class not found"));

            } else if (structureType == StructureType.COMPETITIVE) {
                if (linkRequest.getExamId() == null) {
                    throw new IllegalArgumentException("examId is required for Competitive course type");
                }

                ExamSubjectRequestDTO req = new ExamSubjectRequestDTO();
                req.setExamId(linkRequest.getExamId());
                req.setSubjectIds(java.util.List.of(subject.getId()));
                req.setDisplayOrder(linkRequest.getDisplayOrder());
                if (linkRequest.getWeightage() != null) {
                    req.setWeightage(linkRequest.getWeightage());
                } else {
                    req.setWeightage(java.math.BigDecimal.valueOf(100.0));
                }

                java.util.List<ExamSubjectResponseDTO> list = examSubjectService.addSubjectsToExam(req);
                ExamSubjectResponseDTO res = list.get(0);

                linkResult.put("id", res.getId());
                linkResult.put("examId", res.getExamId());
                linkResult.put("examName", res.getExamName());
                linkResult.put("subjectId", res.getSubjectId());
                linkResult.put("subjectName", res.getSubjectName());
                linkResult.put("weightage", res.getWeightage());
                linkResult.put("displayOrder", res.getDisplayOrder());
                linkResult.put("createdAt", res.getCreatedAt());

                        // Verify exam exists
                        examRepository.findById(linkRequest.getExamId())
                    .orElseThrow(() -> new IllegalArgumentException("Exam not found"));

            } else if (structureType == StructureType.PROFESSIONAL) {
                if (linkRequest.getCourseId() == null) {
                    throw new IllegalArgumentException("courseId is required for Professional course type");
                }

                CourseSubjectRequestDTO req = new CourseSubjectRequestDTO();
                req.setCourseId(linkRequest.getCourseId());
                req.setSubjectIds(java.util.List.of(subject.getId()));
                req.setDisplayOrder(linkRequest.getDisplayOrder());
                if (linkRequest.getIsCompulsory() != null) {
                    req.setIsCompulsory(linkRequest.getIsCompulsory());
                } else {
                    req.setIsCompulsory(true);
                }

                java.util.List<CourseSubjectResponseDTO> list = courseSubjectService.addSubjectsToCourse(req);
                CourseSubjectResponseDTO res = list.get(0);

                linkResult.put("id", res.getId());
                linkResult.put("courseId", res.getCourseId());
                linkResult.put("courseName", res.getCourseName());
                linkResult.put("subjectId", res.getSubjectId());
                linkResult.put("subjectName", res.getSubjectName());
                linkResult.put("isCompulsory", res.getIsCompulsory());
                linkResult.put("displayOrder", res.getDisplayOrder());
                linkResult.put("createdAt", res.getCreatedAt());

                        // Verify course exists
                        courseRepository.findById(linkRequest.getCourseId())
                    .orElseThrow(() -> new IllegalArgumentException("Course not found"));

            } else {
                throw new IllegalArgumentException("Unsupported course type structure: " + structureType);
            }

            // 3) Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("subjectId", subject.getId());
            response.put("subjectName", subject.getName());
            response.put("courseTypeId", courseType.getId());
            response.put("courseTypeName", courseType.getName());
            response.put("link", linkResult);
            response.put("message", "Link created successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create subject with link: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // REMOVED master-subject update as per cleanup request
    @PutMapping("/{id:\\d+}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Subject> updateSubject(@PathVariable Long id, @Valid @RequestBody SubjectRequestDTO subjectRequestDTO) {
        try {
            Subject updatedSubject = subjectService.updateSubject(id, subjectRequestDTO);
            return ResponseEntity.ok(updatedSubject);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // REMOVED master-subject delete as per cleanup request
    @DeleteMapping("/{id:\\d+}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        try {
            subjectService.deleteSubject(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Consolidated endpoint to return all linkage subjects (class/exam/course) enriched with course/courseType
    @GetMapping("/subject-linkages/filter")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<Page<Map<String, Object>>> getAllLinkageSubjects(
            @RequestParam(required = false) Long courseTypeId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Map<String, Object>> result = subjectService.getSubjectLinkagesFilter(
                courseTypeId, courseId, classId, examId, active, search, pageable);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // Return empty page with error info
            Page<Map<String, Object>> errorPage = new PageImpl<>(List.of(
                Map.of("error", "Failed to fetch linkage subjects: " + e.getMessage())
            ), PageRequest.of(0, 1), 1);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorPage);
        }
    }

    // Master subjects by course type from Subject entity
    @GetMapping("/master-subjects/by-course-type")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR')")
    public ResponseEntity<?> getMasterSubjects(
            @RequestParam(required = true) Long courseTypeId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        try {
            // Validate courseType exists (optional strictness)
            boolean exists = courseTypeRepository.existsById(courseTypeId);
            if (!exists) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid courseTypeId: " + courseTypeId));
            }

            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<SubjectResponseDTO> result = subjectService.getMasterSubjectsByCourseType(courseTypeId, active, search, pageable);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Failed to fetch master subjects: " + e.getMessage()
            ));
        }
    }
    
    // Helper methods to enrich linkage data with course and courseType info
    private List<Map<String, Object>> enrichClassSubjects(List<ClassSubjectResponse> classSubjects) {
        java.util.Set<Long> classIds = classSubjects.stream().map(ClassSubjectResponse::getClassId).collect(java.util.stream.Collectors.toSet());
        java.util.Map<Long, ClassEntity> classMap = classRepository.findAllById(classIds).stream()
            .collect(java.util.stream.Collectors.toMap(ClassEntity::getId, c -> c));
        
        return classSubjects.stream().map(cs -> {
            Map<String, Object> enriched = new HashMap<>();
            enriched.put("linkageId", cs.getId());
            enriched.put("classId", cs.getClassId());
            enriched.put("className", cs.getClassName());
            enriched.put("subjectId", cs.getSubjectId());
            enriched.put("subjectName", cs.getSubjectName());
            enriched.put("displayOrder", cs.getDisplayOrder());
            enriched.put("isActive", cs.getIsActive());
            enriched.put("createdAt", cs.getCreatedAt());
            
            ClassEntity ce = classMap.get(cs.getClassId());
            if (ce != null && ce.getCourse() != null) {
                Course course = ce.getCourse();
                enriched.put("courseId", course.getId());
                enriched.put("courseName", course.getName());
                if (course.getCourseType() != null) {
                    CourseType ct = course.getCourseType();
                    enriched.put("courseTypeId", ct.getId());
                    enriched.put("courseTypeName", ct.getName());
                    enriched.put("structureType", ct.getStructureType() != null ? ct.getStructureType().name() : null);
                }
            }
            return enriched;
        }).collect(java.util.stream.Collectors.toList());
    }
    
    private List<Map<String, Object>> enrichExamSubjects(List<ExamSubjectResponseDTO> examSubjects) {
        java.util.Set<Long> examIds = examSubjects.stream().map(ExamSubjectResponseDTO::getExamId).collect(java.util.stream.Collectors.toSet());
        java.util.Map<Long, Exam> examMap = examRepository.findAllById(examIds).stream()
            .collect(java.util.stream.Collectors.toMap(Exam::getId, e -> e));
        
        return examSubjects.stream().map(es -> {
            Map<String, Object> enriched = new HashMap<>();
            enriched.put("linkageId", es.getId());
            enriched.put("examId", es.getExamId());
            enriched.put("examName", es.getExamName());
            enriched.put("subjectId", es.getSubjectId());
            enriched.put("subjectName", es.getSubjectName());
            enriched.put("weightage", es.getWeightage());
            enriched.put("displayOrder", es.getDisplayOrder());
            enriched.put("createdAt", es.getCreatedAt());
            
            Exam exam = examMap.get(es.getExamId());
            if (exam != null && exam.getCourse() != null) {
                Course course = exam.getCourse();
                enriched.put("courseId", course.getId());
                enriched.put("courseName", course.getName());
                if (course.getCourseType() != null) {
                    CourseType ct = course.getCourseType();
                    enriched.put("courseTypeId", ct.getId());
                    enriched.put("courseTypeName", ct.getName());
                    enriched.put("structureType", ct.getStructureType() != null ? ct.getStructureType().name() : null);
                }
            }
            return enriched;
        }).collect(java.util.stream.Collectors.toList());
    }
    
    private List<Map<String, Object>> enrichCourseSubjects(List<CourseSubjectResponseDTO> courseSubjects) {
        java.util.Set<Long> courseIds = courseSubjects.stream().map(CourseSubjectResponseDTO::getCourseId).collect(java.util.stream.Collectors.toSet());
        java.util.Map<Long, Course> courseMap = courseRepository.findAllById(courseIds).stream()
            .collect(java.util.stream.Collectors.toMap(Course::getId, c -> c));
        
        return courseSubjects.stream().map(cs -> {
            Map<String, Object> enriched = new HashMap<>();
            enriched.put("linkageId", cs.getId());
            enriched.put("courseId", cs.getCourseId());
            enriched.put("courseName", cs.getCourseName());
            enriched.put("subjectId", cs.getSubjectId());
            enriched.put("subjectName", cs.getSubjectName());
            enriched.put("isCompulsory", cs.getIsCompulsory());
            enriched.put("displayOrder", cs.getDisplayOrder());
            enriched.put("createdAt", cs.getCreatedAt());
            
            Course course = courseMap.get(cs.getCourseId());
            if (course != null && course.getCourseType() != null) {
                CourseType ct = course.getCourseType();
                enriched.put("courseTypeId", ct.getId());
                enriched.put("courseTypeName", ct.getName());
                enriched.put("structureType", ct.getStructureType() != null ? ct.getStructureType().name() : null);
            }
            return enriched;
        }).collect(java.util.stream.Collectors.toList());
    }
    
}