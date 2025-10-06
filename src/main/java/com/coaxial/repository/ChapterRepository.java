package com.coaxial.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.Chapter;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    
    // Find by module
    List<Chapter> findByModuleIdAndIsActiveTrueOrderByDisplayOrderAsc(Long moduleId);
    List<Chapter> findByModuleIdOrderByDisplayOrderAsc(Long moduleId);
    List<Chapter> findByModuleIdAndIsActiveOrderByDisplayOrderAsc(Long moduleId, Boolean isActive);
    List<Chapter> findByModuleIdInAndIsActiveTrueOrderByDisplayOrderAsc(List<Long> moduleIds);
    
    // Find by name
    List<Chapter> findByNameContainingIgnoreCaseAndIsActiveTrueOrderByDisplayOrderAsc(String name);
    
    // Note: Subject type filtering removed due to Topic entity structure change
    // Subject information is now resolved through courseTypeId and relationshipId
    
    // Find by creation date
    List<Chapter> findByCreatedAtAfterAndIsActiveTrueOrderByDisplayOrderAsc(LocalDateTime createdAfter);
    
    // Find by active status
    List<Chapter> findByIsActiveOrderByDisplayOrderAsc(Boolean isActive);
    
    // Search by name or description
    List<Chapter> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
    
    // Check existence
    Optional<Chapter> findByNameAndModuleId(String name, Long moduleId);
    boolean existsByNameAndModuleId(String name, Long moduleId);
    boolean existsByNameAndModuleIdAndIdNot(String name, Long moduleId, Long id);
}