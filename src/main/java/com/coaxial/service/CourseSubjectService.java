package com.coaxial.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coaxial.dto.CourseSubjectRequestDTO;
import com.coaxial.dto.CourseSubjectResponseDTO;
import com.coaxial.entity.Course;
import com.coaxial.entity.CourseSubject;
import com.coaxial.entity.Subject;
import com.coaxial.repository.CourseRepository;
import com.coaxial.repository.CourseSubjectRepository;
import com.coaxial.repository.SubjectRepository;

@Service
@Transactional
public class CourseSubjectService {
    
    @Autowired
    private CourseSubjectRepository courseSubjectRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    // Add subjects to course
    public List<CourseSubjectResponseDTO> addSubjectsToCourse(CourseSubjectRequestDTO request) {
        // Validate course exists
        Course course = courseRepository.findById(request.getCourseId())
            .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        
        // Validate all subjects exist
        List<Subject> subjects = subjectRepository.findAllById(request.getSubjectIds());
        if (subjects.size() != request.getSubjectIds().size()) {
            throw new IllegalArgumentException("One or more subjects not found");
        }
        
        // Check for existing relationships and create new ones
        List<CourseSubject> courseSubjects = subjects.stream()
            .map(subject -> {
                // Check if relationship already exists
                if (courseSubjectRepository.existsByCourseAndSubject(course, subject)) {
                    throw new IllegalArgumentException(
                        String.format("Subject '%s' is already assigned to course '%s'", 
                        subject.getName(), course.getName()));
                }
                
                CourseSubject courseSubject = new CourseSubject();
                courseSubject.setCourse(course);
                courseSubject.setSubject(subject);
                courseSubject.setIsCompulsory(request.getIsCompulsory());
                courseSubject.setDisplayOrder(request.getDisplayOrder());
                
                return courseSubject;
            })
            .collect(Collectors.toList());
        
        List<CourseSubject> savedCourseSubjects = courseSubjectRepository.saveAll(courseSubjects);
        return savedCourseSubjects.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    // Get all course-subject relationships
    @Transactional(readOnly = true)
    public List<CourseSubjectResponseDTO> getAllCourseSubjects() {
        List<CourseSubject> courseSubjects = courseSubjectRepository.findAll();
        return courseSubjects.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    // Get subjects for course
    @Transactional(readOnly = true)
    public List<CourseSubjectResponseDTO> getSubjectsForCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        
        List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseOrderByDisplayOrderAsc(course);
        return courseSubjects.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    // Get courses for subject
    @Transactional(readOnly = true)
    public List<CourseSubjectResponseDTO> getCoursesForSubject(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
        
        List<CourseSubject> courseSubjects = courseSubjectRepository.findBySubjectOrderByDisplayOrderAsc(subject);
        return courseSubjects.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    // Update course-subject relationship
    public CourseSubjectResponseDTO updateCourseSubject(Long id, CourseSubjectRequestDTO request) {
        CourseSubject courseSubject = courseSubjectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Course-Subject relationship not found"));
        
        // Update fields
        courseSubject.setIsCompulsory(request.getIsCompulsory());
        courseSubject.setDisplayOrder(request.getDisplayOrder());
        
        CourseSubject updatedCourseSubject = courseSubjectRepository.save(courseSubject);
        return convertToResponse(updatedCourseSubject);
    }
    
    // Remove subject from course
    public void removeSubjectFromCourse(Long id) {
        if (!courseSubjectRepository.existsById(id)) {
            throw new IllegalArgumentException("Course-Subject relationship not found");
        }
        courseSubjectRepository.deleteById(id);
    }
    
    // Bulk remove subjects from course
    public void removeSubjectsFromCourse(Long courseId, List<Long> subjectIds) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        
        List<Subject> subjects = subjectRepository.findAllById(subjectIds);
        List<CourseSubject> courseSubjects = courseSubjectRepository.findByCourseAndSubjectIn(course, subjects);
        
        courseSubjectRepository.deleteAll(courseSubjects);
    }
    
    // Convert CourseSubject entity to CourseSubjectResponseDTO
    private CourseSubjectResponseDTO convertToResponse(CourseSubject courseSubject) {
        CourseSubjectResponseDTO response = new CourseSubjectResponseDTO();
        response.setId(courseSubject.getId());
        response.setCourseId(courseSubject.getCourse().getId());
        response.setCourseName(courseSubject.getCourse().getName());
        response.setSubjectId(courseSubject.getSubject().getId());
        response.setSubjectName(courseSubject.getSubject().getName());
        response.setSubjectType(courseSubject.getSubject().getCourseType() != null ? courseSubject.getSubject().getCourseType().getName() : "Unknown");
        response.setIsCompulsory(courseSubject.getIsCompulsory());
        response.setDisplayOrder(courseSubject.getDisplayOrder());
        response.setCreatedAt(courseSubject.getCreatedAt());
        return response;
    }
}
