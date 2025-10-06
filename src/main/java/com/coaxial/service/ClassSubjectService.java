package com.coaxial.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coaxial.dto.ClassSubjectRequest;
import com.coaxial.dto.ClassSubjectResponse;
import com.coaxial.entity.ClassEntity;
import com.coaxial.entity.ClassSubject;
import com.coaxial.entity.Subject;
import com.coaxial.entity.User;
import com.coaxial.repository.ClassRepository;
import com.coaxial.repository.ClassSubjectRepository;
import com.coaxial.repository.SubjectRepository;

@Service
@Transactional
public class ClassSubjectService {
    
    @Autowired
    private ClassSubjectRepository classSubjectRepository;
    
    @Autowired
    private ClassRepository classRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    // Add subject to class
    public ClassSubjectResponse addSubjectToClass(ClassSubjectRequest request) {
        // Validate class exists
        ClassEntity classEntity = classRepository.findById(request.getClassId())
            .orElseThrow(() -> new IllegalArgumentException("Class not found"));
        
        // Validate subject exists
        Subject subject = subjectRepository.findById(request.getSubjectId())
            .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
        
        // Check if relationship already exists
        if (classSubjectRepository.existsByClassEntityAndSubject(classEntity, subject)) {
            throw new IllegalArgumentException("Subject is already assigned to this class");
        }
        
        ClassSubject classSubject = new ClassSubject();
        classSubject.setClassEntity(classEntity);
        classSubject.setSubject(subject);
        classSubject.setDisplayOrder(request.getDisplayOrder());
        classSubject.setIsActive(request.getIsActive());
        classSubject.setCreatedBy(getCurrentUser());
        
        ClassSubject savedClassSubject = classSubjectRepository.save(classSubject);
        return convertToResponse(savedClassSubject);
    }
    
    // Get all class-subject relationships
    @Transactional(readOnly = true)
    public List<ClassSubjectResponse> getAllClassSubjects() {
        List<ClassSubject> classSubjects = classSubjectRepository.findAll();
        return classSubjects.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    // Get subjects for class
    @Transactional(readOnly = true)
    public List<ClassSubjectResponse> getSubjectsForClass(Long classId) {
        ClassEntity classEntity = classRepository.findById(classId)
            .orElseThrow(() -> new IllegalArgumentException("Class not found"));
        
        List<ClassSubject> classSubjects = classSubjectRepository.findByClassEntityIdAndIsActive(classId, true);
        return classSubjects.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    // Get classes for subject
    @Transactional(readOnly = true)
    public List<ClassSubjectResponse> getClassesForSubject(Long subjectId) {
        List<ClassSubject> classSubjects = classSubjectRepository.findBySubjectIdAndIsActive(subjectId, true);
        return classSubjects.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    // Update class-subject relationship
    public ClassSubjectResponse updateClassSubject(Long id, ClassSubjectRequest request) {
        ClassSubject classSubject = classSubjectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Class-Subject relationship not found"));
        
        // Update fields
        classSubject.setDisplayOrder(request.getDisplayOrder());
        classSubject.setIsActive(request.getIsActive());
        classSubject.setUpdatedBy(getCurrentUser());
        
        ClassSubject updatedClassSubject = classSubjectRepository.save(classSubject);
        return convertToResponse(updatedClassSubject);
    }
    
    // Remove subject from class
    public void removeSubjectFromClass(Long id) {
        if (!classSubjectRepository.existsById(id)) {
            throw new IllegalArgumentException("Class-Subject relationship not found");
        }
        classSubjectRepository.deleteById(id);
    }
    
    // Convert ClassSubject entity to ClassSubjectResponse DTO
    private ClassSubjectResponse convertToResponse(ClassSubject classSubject) {
        ClassSubjectResponse response = new ClassSubjectResponse();
        response.setId(classSubject.getId());
        response.setClassId(classSubject.getClassEntity().getId());
        response.setClassName(classSubject.getClassEntity().getName());
        response.setSubjectId(classSubject.getSubject().getId());
        response.setSubjectName(classSubject.getSubject().getName());
        response.setSubjectType(classSubject.getSubject().getCourseType() != null ? classSubject.getSubject().getCourseType().getName() : null);
        response.setDisplayOrder(classSubject.getDisplayOrder());
        response.setIsActive(classSubject.getIsActive());
        response.setCreatedAt(classSubject.getCreatedAt());
        response.setUpdatedAt(classSubject.getUpdatedAt());
        
        if (classSubject.getCreatedBy() != null) {
            response.setCreatedByName(classSubject.getCreatedBy().getFullName());
        }
        if (classSubject.getUpdatedBy() != null) {
            response.setUpdatedByName(classSubject.getUpdatedBy().getFullName());
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

