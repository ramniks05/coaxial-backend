package com.coaxial.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coaxial.dto.ExamSubjectRequestDTO;
import com.coaxial.dto.ExamSubjectResponseDTO;
import com.coaxial.entity.Exam;
import com.coaxial.entity.ExamSubject;
import com.coaxial.entity.Subject;
import com.coaxial.repository.ExamRepository;
import com.coaxial.repository.ExamSubjectRepository;
import com.coaxial.repository.SubjectRepository;

@Service
@Transactional
public class ExamSubjectService {
    
    @Autowired
    private ExamSubjectRepository examSubjectRepository;
    
    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    // Add subjects to exam
    public List<ExamSubjectResponseDTO> addSubjectsToExam(ExamSubjectRequestDTO request) {
        // Validate exam exists
        Exam exam = examRepository.findById(request.getExamId())
            .orElseThrow(() -> new IllegalArgumentException("Exam not found"));
        
        // Validate all subjects exist
        List<Subject> subjects = subjectRepository.findAllById(request.getSubjectIds());
        if (subjects.size() != request.getSubjectIds().size()) {
            throw new IllegalArgumentException("One or more subjects not found");
        }
        
        // Check for existing relationships and create new ones
        List<ExamSubject> examSubjects = subjects.stream()
            .map(subject -> {
                // Check if relationship already exists
                if (examSubjectRepository.existsByExamAndSubject(exam, subject)) {
                    throw new IllegalArgumentException(
                        String.format("Subject '%s' is already assigned to exam '%s'", 
                        subject.getName(), exam.getName()));
                }
                
                ExamSubject examSubject = new ExamSubject();
                examSubject.setExam(exam);
                examSubject.setSubject(subject);
                examSubject.setWeightage(request.getWeightage());
                examSubject.setDisplayOrder(request.getDisplayOrder());
                
                return examSubject;
            })
            .collect(Collectors.toList());
        
        List<ExamSubject> savedExamSubjects = examSubjectRepository.saveAll(examSubjects);
        return savedExamSubjects.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    // Get all exam-subject relationships
    @Transactional(readOnly = true)
    public List<ExamSubjectResponseDTO> getAllExamSubjects() {
        List<ExamSubject> examSubjects = examSubjectRepository.findAll();
        return examSubjects.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    // Get subjects for exam
    @Transactional(readOnly = true)
    public List<ExamSubjectResponseDTO> getSubjectsForExam(Long examId) {
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new IllegalArgumentException("Exam not found"));
        
        List<ExamSubject> examSubjects = examSubjectRepository.findByExamOrderByDisplayOrderAsc(exam);
        return examSubjects.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    // Get exams for subject
    @Transactional(readOnly = true)
    public List<ExamSubjectResponseDTO> getExamsForSubject(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
        
        List<ExamSubject> examSubjects = examSubjectRepository.findBySubjectOrderByDisplayOrderAsc(subject);
        return examSubjects.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    // Update exam-subject relationship
    public ExamSubjectResponseDTO updateExamSubject(Long id, ExamSubjectRequestDTO request) {
        ExamSubject examSubject = examSubjectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Exam-Subject relationship not found"));
        
        // Update fields
        examSubject.setWeightage(request.getWeightage());
        examSubject.setDisplayOrder(request.getDisplayOrder());
        
        ExamSubject updatedExamSubject = examSubjectRepository.save(examSubject);
        return convertToResponse(updatedExamSubject);
    }
    
    // Remove subject from exam
    public void removeSubjectFromExam(Long id) {
        if (!examSubjectRepository.existsById(id)) {
            throw new IllegalArgumentException("Exam-Subject relationship not found");
        }
        examSubjectRepository.deleteById(id);
    }
    
    // Bulk remove subjects from exam
    public void removeSubjectsFromExam(Long examId, List<Long> subjectIds) {
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new IllegalArgumentException("Exam not found"));
        
        List<Subject> subjects = subjectRepository.findAllById(subjectIds);
        List<ExamSubject> examSubjects = examSubjectRepository.findByExamAndSubjectIn(exam, subjects);
        
        examSubjectRepository.deleteAll(examSubjects);
    }
    
    // Convert ExamSubject entity to ExamSubjectResponseDTO
    private ExamSubjectResponseDTO convertToResponse(ExamSubject examSubject) {
        ExamSubjectResponseDTO response = new ExamSubjectResponseDTO();
        response.setId(examSubject.getId());
        response.setExamId(examSubject.getExam().getId());
        response.setExamName(examSubject.getExam().getName());
        response.setSubjectId(examSubject.getSubject().getId());
        response.setSubjectName(examSubject.getSubject().getName());
        response.setSubjectType(examSubject.getSubject().getCourseType() != null ? examSubject.getSubject().getCourseType().getName() : "Unknown");
        response.setWeightage(examSubject.getWeightage());
        response.setDisplayOrder(examSubject.getDisplayOrder());
        response.setCreatedAt(examSubject.getCreatedAt());
        return response;
    }
}
