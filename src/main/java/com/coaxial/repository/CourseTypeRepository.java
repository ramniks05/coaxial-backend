package com.coaxial.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.CourseType;
import com.coaxial.entity.StructureType;

@Repository
public interface CourseTypeRepository extends JpaRepository<CourseType, Long> {
    
    // Find by name (case insensitive)
    Optional<CourseType> findByNameIgnoreCase(String name);
    
    // Find active course types
    List<CourseType> findByIsActiveTrue();
    List<CourseType> findByIsActiveTrueOrderByDisplayOrderAsc();
    
    // Find by structure type
    List<CourseType> findByStructureType(StructureType structureType);
    List<CourseType> findByStructureTypeAndIsActiveTrue(StructureType structureType);
    
    // Find all ordered by display order
    List<CourseType> findAllByOrderByDisplayOrderAsc();
    
    // Check existence
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    
    // Paginated methods
    Page<CourseType> findByIsActive(Boolean isActive, Pageable pageable);
    Page<CourseType> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description, Pageable pageable);
    Page<CourseType> findByIsActiveAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(Boolean isActive, String name, String description, Pageable pageable);
}