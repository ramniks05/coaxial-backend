package com.coaxial.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.Module;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    
    // Find by topic
    List<Module> findByTopicIdAndIsActiveTrueOrderByDisplayOrderAsc(Long topicId);
    List<Module> findByTopicIdOrderByDisplayOrderAsc(Long topicId);
    List<Module> findByTopicIdAndIsActiveOrderByDisplayOrderAsc(Long topicId, Boolean isActive);
    List<Module> findByTopicIdInAndIsActiveTrueOrderByDisplayOrderAsc(List<Long> topicIds);
    
    // Find by name
    List<Module> findByNameContainingIgnoreCaseAndIsActiveTrueOrderByDisplayOrderAsc(String name);
    
    // Note: Subject type filtering removed due to Topic entity structure change
    // Subject information is now resolved through courseTypeId and relationshipId
    
    // Find by creation date
    List<Module> findByCreatedAtAfterAndIsActiveTrueOrderByDisplayOrderAsc(LocalDateTime createdAfter);
    
    // Find by active status
    List<Module> findByIsActiveOrderByDisplayOrderAsc(Boolean isActive);
    
    // Search by name or description
    List<Module> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
    
    // Check existence
    Optional<Module> findByNameAndTopicId(String name, Long topicId);
    boolean existsByNameAndTopicId(String name, Long topicId);
    boolean existsByNameAndTopicIdAndIdNot(String name, Long topicId, Long id);
}