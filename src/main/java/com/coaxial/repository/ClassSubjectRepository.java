package com.coaxial.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.ClassEntity;
import com.coaxial.entity.ClassSubject;
import com.coaxial.entity.Subject;

@Repository
public interface ClassSubjectRepository extends JpaRepository<ClassSubject, Long> {
    
    // Find by class
    List<ClassSubject> findByClassEntity(ClassEntity classEntity);
    List<ClassSubject> findByClassEntityAndIsActive(ClassEntity classEntity, Boolean isActive);
    List<ClassSubject> findByClassEntityOrderByDisplayOrderAsc(ClassEntity classEntity);
    List<ClassSubject> findByClassEntityId(Long classId);
    List<ClassSubject> findByClassEntityIdAndIsActive(Long classId, Boolean isActive);
    
    // Find by subject
    List<ClassSubject> findBySubject(Subject subject);
    List<ClassSubject> findBySubjectAndIsActive(Subject subject, Boolean isActive);
    List<ClassSubject> findBySubjectId(Long subjectId);
    List<ClassSubject> findBySubjectIdAndIsActive(Long subjectId, Boolean isActive);
    
    // Find by class and subject
    Optional<ClassSubject> findByClassEntityAndSubject(ClassEntity classEntity, Subject subject);
    Optional<ClassSubject> findByClassEntityIdAndSubjectId(Long classId, Long subjectId);
    
    // Count methods
    long countByClassEntityAndIsActive(ClassEntity classEntity, Boolean isActive);
    
    // Check existence
    boolean existsByClassEntityAndSubject(ClassEntity classEntity, Subject subject);
    boolean existsByClassEntityAndSubjectAndIdNot(ClassEntity classEntity, Subject subject, Long id);
}