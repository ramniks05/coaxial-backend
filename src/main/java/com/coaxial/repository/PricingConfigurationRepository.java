package com.coaxial.repository;

import com.coaxial.entity.PricingConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PricingConfiguration entity
 * Handles database operations for admin-defined pricing configurations
 */
@Repository
public interface PricingConfigurationRepository extends JpaRepository<PricingConfiguration, Long> {
    
    /**
     * Find pricing configuration by entity type and entity ID
     */
    Optional<PricingConfiguration> findByEntityTypeAndEntityId(String entityType, Long entityId);
    
    /**
     * Find all pricing configurations by entity type
     */
    List<PricingConfiguration> findByEntityType(String entityType);
    
    /**
     * Find all active pricing configurations by entity type
     */
    List<PricingConfiguration> findByEntityTypeAndIsActiveTrue(String entityType);
    
    /**
     * Find all pricing configurations for a specific course
     */
    @Query("SELECT pc FROM PricingConfiguration pc WHERE pc.entityType = 'COURSE' AND pc.entityId = :courseId")
    List<PricingConfiguration> findByCourseId(@Param("courseId") Long courseId);
    
    /**
     * Find all pricing configurations for classes in a specific course
     */
    @Query("SELECT pc FROM PricingConfiguration pc WHERE pc.entityType = 'CLASS' AND pc.entityId IN " +
           "(SELECT c.id FROM ClassEntity c WHERE c.course.id = :courseId)")
    List<PricingConfiguration> findClassPricingByCourseId(@Param("courseId") Long courseId);
    
    /**
     * Find all pricing configurations for exams in a specific course
     */
    @Query("SELECT pc FROM PricingConfiguration pc WHERE pc.entityType = 'EXAM' AND pc.entityId IN " +
           "(SELECT e.id FROM Exam e WHERE e.course.id = :courseId)")
    List<PricingConfiguration> findExamPricingByCourseId(@Param("courseId") Long courseId);
    
    /**
     * Check if pricing configuration exists for entity
     */
    boolean existsByEntityTypeAndEntityId(String entityType, Long entityId);
    
    /**
     * Find all pricing configurations with pagination
     */
    @Query("SELECT pc FROM PricingConfiguration pc WHERE " +
           "(:entityType IS NULL OR pc.entityType = :entityType) AND " +
           "(:isActive IS NULL OR pc.isActive = :isActive) " +
           "ORDER BY pc.entityType, pc.entityName")
    List<PricingConfiguration> findPricingConfigurationsWithFilters(
            @Param("entityType") String entityType,
            @Param("isActive") Boolean isActive);
    
    /**
     * Find pricing configurations by entity name (for search functionality)
     */
    @Query("SELECT pc FROM PricingConfiguration pc WHERE pc.entityName LIKE %:entityName% " +
           "ORDER BY pc.entityType, pc.entityName")
    List<PricingConfiguration> findByEntityNameContaining(@Param("entityName") String entityName);
    
    /**
     * Count pricing configurations by entity type
     */
    long countByEntityType(String entityType);
    
    /**
     * Count active pricing configurations by entity type
     */
    long countByEntityTypeAndIsActiveTrue(String entityType);
    
    /**
     * Find pricing configurations created after a specific date
     */
    @Query("SELECT pc FROM PricingConfiguration pc WHERE pc.createdAt >= :fromDate " +
           "ORDER BY pc.createdAt DESC")
    List<PricingConfiguration> findByCreatedAtAfter(@Param("fromDate") java.time.LocalDateTime fromDate);
    
    /**
     * Find pricing configurations updated by a specific user
     */
    @Query("SELECT pc FROM PricingConfiguration pc WHERE pc.updatedBy.id = :userId " +
           "ORDER BY pc.updatedAt DESC")
    List<PricingConfiguration> findByUpdatedByUserId(@Param("userId") Long userId);
    
    /**
     * Find pricing configurations within a price range
     */
    @Query("SELECT pc FROM PricingConfiguration pc WHERE " +
           "pc.monthlyPrice BETWEEN :minPrice AND :maxPrice " +
           "ORDER BY pc.monthlyPrice")
    List<PricingConfiguration> findByMonthlyPriceBetween(
            @Param("minPrice") java.math.BigDecimal minPrice,
            @Param("maxPrice") java.math.BigDecimal maxPrice);
    
    /**
     * Get pricing configuration summary by entity type
     */
    @Query("SELECT pc.entityType, COUNT(pc), AVG(pc.monthlyPrice), AVG(pc.quarterlyPrice), AVG(pc.yearlyPrice) " +
           "FROM PricingConfiguration pc WHERE pc.isActive = true " +
           "GROUP BY pc.entityType")
    List<Object[]> getPricingConfigurationSummary();
    
    /**
     * Find all pricing configurations by course type and entity type
     */
    List<PricingConfiguration> findByCourseTypeIdAndEntityType(Long courseTypeId, String entityType);
    
    /**
     * Find all active pricing configurations by course type and entity type
     */
    List<PricingConfiguration> findByCourseTypeIdAndEntityTypeAndIsActiveTrue(Long courseTypeId, String entityType);
}
