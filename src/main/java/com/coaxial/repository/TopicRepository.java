package com.coaxial.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.Topic;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    
    // Basic queries
    List<Topic> findAllByOrderByDisplayOrderAsc();
    List<Topic> findByIsActiveOrderByDisplayOrderAsc(Boolean isActive);
    
    // Find topics by course type
    List<Topic> findByCourseTypeIdAndIsActiveOrderByDisplayOrderAsc(Long courseTypeId, Boolean isActive);
    List<Topic> findByCourseTypeIdOrderByDisplayOrderAsc(Long courseTypeId);
    
    // Find topics by course type and relationship ID
    List<Topic> findByCourseTypeIdAndRelationshipIdAndIsActiveOrderByDisplayOrderAsc(
        Long courseTypeId, Long relationshipId, Boolean isActive);
    List<Topic> findByCourseTypeIdAndRelationshipIdOrderByDisplayOrderAsc(
        Long courseTypeId, Long relationshipId);
    
    // Find topics by relationship ID (regardless of course type)
    List<Topic> findByRelationshipIdAndIsActiveOrderByDisplayOrderAsc(Long relationshipId, Boolean isActive);
    List<Topic> findByRelationshipIdOrderByDisplayOrderAsc(Long relationshipId);
    
    // Search functionality
    List<Topic> findByNameContainingIgnoreCaseAndIsActiveTrueOrderByDisplayOrderAsc(String name);
    List<Topic> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
    
    // Creation date queries
    List<Topic> findByCreatedAtAfterAndIsActiveTrueOrderByDisplayOrderAsc(LocalDateTime createdAfter);
    
    // Existence checks for duplicates
    Optional<Topic> findByNameAndCourseTypeIdAndRelationshipId(String name, Long courseTypeId, Long relationshipId);
    boolean existsByNameAndCourseTypeIdAndRelationshipId(String name, Long courseTypeId, Long relationshipId);
    boolean existsByNameAndCourseTypeIdAndRelationshipIdAndIdNot(String name, Long courseTypeId, Long relationshipId, Long id);
}