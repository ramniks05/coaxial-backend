package com.coaxial.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coaxial.dto.ExamRequest;
import com.coaxial.dto.ExamResponse;
import com.coaxial.entity.Course;
import com.coaxial.entity.CourseType;
import com.coaxial.entity.Exam;
import com.coaxial.entity.User;
import com.coaxial.repository.CourseRepository;
import com.coaxial.repository.ExamRepository;

@Service
@Transactional
public class ExamService {
    
    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    // Create new exam
    public ExamResponse createExam(ExamRequest examRequest) {
        // Validate course exists
        Course course = courseRepository.findById(examRequest.getCourseId())
            .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        
        // Check if exam name already exists within the course
        if (examRepository.existsByNameAndCourse(examRequest.getName(), course)) {
            throw new IllegalArgumentException("Exam name already exists in this course");
        }
        
        Exam exam = new Exam();
        exam.setName(examRequest.getName());
        exam.setDescription(examRequest.getDescription());
        exam.setCourse(course);
        exam.setDisplayOrder(examRequest.getDisplayOrder());
        exam.setIsActive(examRequest.getIsActive());
        exam.setCreatedBy(getCurrentUser());
        
        Exam savedExam = examRepository.save(exam);
        return convertToResponse(savedExam);
    }
    
    // Helper method to get exams list using simplified JPA methods
    private List<Exam> getAllExamsList(Long courseTypeId, Long courseId, Boolean isActive, String search) {
        // Handle courseTypeId filtering first
        if (courseTypeId != null) {
            CourseType courseType = courseRepository.findById(courseTypeId)
                .map(course -> course.getCourseType())
                .orElse(null);
            if (courseType == null) {
                return List.of(); // Return empty list if courseType not found
            }
            
            // Get all exams for courses of this type
            List<Exam> examsByType = examRepository.findByCourseCourseType(courseType);
            
            // Apply additional filters
            if (courseId != null) {
                examsByType = examsByType.stream()
                    .filter(e -> e.getCourse().getId().equals(courseId))
                    .collect(Collectors.toList());
            }
            if (isActive != null) {
                examsByType = examsByType.stream()
                    .filter(e -> e.getIsActive().equals(isActive))
                    .collect(Collectors.toList());
            }
            if (search != null && !search.isEmpty()) {
                examsByType = examsByType.stream()
                    .filter(e -> e.getName().toLowerCase().contains(search.toLowerCase()) ||
                               (e.getDescription() != null && e.getDescription().toLowerCase().contains(search.toLowerCase())))
                    .collect(Collectors.toList());
            }
            return examsByType;
        }
        
        // Original filtering logic for when courseTypeId is not provided
        if (courseId != null && isActive != null && search != null && !search.isEmpty()) {
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
            // First get exams by course and active status, then filter by search
            List<Exam> exams = examRepository.findByCourseAndIsActive(course, isActive);
            return exams.stream()
                    .filter(exam -> exam.getName().toLowerCase().contains(search.toLowerCase()) ||
                                   exam.getDescription().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        } else if (courseId != null && isActive != null) {
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
            return examRepository.findByCourseAndIsActive(course, isActive);
        } else if (courseId != null) {
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
            return examRepository.findByCourse(course);
        } else if (isActive != null) {
            return examRepository.findByIsActive(isActive);
        } else if (search != null && !search.isEmpty()) {
            return examRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search);
        } else {
            return examRepository.findAllWithCourseAndCourseType();
        }
    }
    
    // Get all exams with pagination and filters
    @Transactional(readOnly = true)
    public Page<ExamResponse> getExams(Long courseTypeId, Long courseId, Boolean isActive, String search, Pageable pageable) {
        List<Exam> allExams = getAllExamsList(courseTypeId, courseId, isActive, search);
        
        // Manual pagination since we're using simple JPA methods
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allExams.size());
        List<Exam> pageContent = allExams.subList(start, end);
        
        Page<Exam> exams = new PageImpl<>(pageContent, pageable, allExams.size());
        return exams.map(this::convertToResponse);
    }
    
    // Get all exams without pagination
    @Transactional(readOnly = true)
    public List<ExamResponse> getAllExams(Long courseTypeId, Long courseId, Boolean isActive, String search) {
        List<Exam> exams = getAllExamsList(courseTypeId, courseId, isActive, search);
        return exams.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    // Get exam by ID
    @Transactional(readOnly = true)
    public ExamResponse getExamById(Long id) {
        Exam exam = examRepository.findByIdWithCourseAndCourseType(id);
        if (exam == null) {
            throw new IllegalArgumentException("Exam not found with id: " + id);
        }
        return convertToResponse(exam);
    }
    
    // Update exam
    public ExamResponse updateExam(Long id, ExamRequest examRequest) {
        Exam exam = examRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Exam not found with id: " + id));
        
        // Validate course exists
        Course course = courseRepository.findById(examRequest.getCourseId())
            .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        
        // Check if exam name is being changed and if it already exists
        if (!exam.getName().equals(examRequest.getName()) && 
            examRepository.existsByNameAndCourseAndIdNot(examRequest.getName(), course, id)) {
            throw new IllegalArgumentException("Exam name already exists in this course");
        }
        
        // Update fields
        exam.setName(examRequest.getName());
        exam.setDescription(examRequest.getDescription());
        exam.setCourse(course);
        exam.setDisplayOrder(examRequest.getDisplayOrder());
        exam.setIsActive(examRequest.getIsActive());
        exam.setUpdatedBy(getCurrentUser());
        
        Exam updatedExam = examRepository.save(exam);
        return convertToResponse(updatedExam);
    }
    
    // Delete exam
    public void deleteExam(Long id) {
        if (!examRepository.existsById(id)) {
            throw new IllegalArgumentException("Exam not found with id: " + id);
        }
        examRepository.deleteById(id);
    }
    
    // Get exams by course
    @Transactional(readOnly = true)
    public List<ExamResponse> getExamsByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        
        List<Exam> exams = examRepository.findByCourseOrderByDisplayOrderAsc(course);
        return exams.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    // Paginated method for standardized endpoints
    @Transactional(readOnly = true)
    public Page<ExamResponse> getExamsByCourse(Long courseTypeId, Long courseId, Boolean active, String search, Pageable pageable) {
        return getExams(courseTypeId, courseId, active, search, pageable);
    }
    
    // Convert Exam entity to ExamResponse DTO
    private ExamResponse convertToResponse(Exam exam) {
        ExamResponse response = new ExamResponse();
        response.setId(exam.getId());
        response.setName(exam.getName());
        response.setDescription(exam.getDescription());
        response.setDisplayOrder(exam.getDisplayOrder());
        response.setIsActive(exam.getIsActive());
        
        response.setCreatedAt(exam.getCreatedAt());
        response.setUpdatedAt(exam.getUpdatedAt());
        
        // Safely access course to avoid lazy loading issues
        try {
            Course course = exam.getCourse();
            if (course != null) {
                response.setCourseId(course.getId());
                response.setCourseName(course.getName());
                
                // Safely access courseType to get name
                try {
                    if (course.getCourseType() != null) {
                        String courseTypeName = course.getCourseType().getName();
                        response.setCourseTypeName(courseTypeName);
                    } else {
                        response.setCourseTypeName("UNKNOWN");
                    }
                } catch (Exception e) {
                    response.setCourseTypeName("ERROR");
                }
            }
        } catch (Exception e) {
            // If there's any issue accessing the course, just leave them null
        }
        
        // Safely access user references to avoid lazy loading issues
        try {
            User createdBy = exam.getCreatedBy();
            if (createdBy != null) {
                response.setCreatedByName(createdBy.getFullName());
            }
            
            User updatedBy = exam.getUpdatedBy();
            if (updatedBy != null) {
                response.setUpdatedByName(updatedBy.getFullName());
            }
        } catch (Exception e) {
            // If there's any issue accessing user references, just leave them null
        }
        
        return response;
    }
    
    // Get current authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
}
