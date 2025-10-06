package com.coaxial.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coaxial.entity.AuditLog;
import com.coaxial.enums.AuditEventType;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<AuditLog> findByUsernameOrderByCreatedAtDesc(String username);
    
    List<AuditLog> findByEventTypeOrderByCreatedAtDesc(AuditEventType eventType);
    
    List<AuditLog> findByIpAddressOrderByCreatedAtDesc(String ipAddress);
    
    @Query("SELECT a FROM AuditLog a WHERE a.createdAt >= :startDate AND a.createdAt <= :endDate ORDER BY a.createdAt DESC")
    List<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.ipAddress = :ipAddress AND a.eventType = :eventType AND a.createdAt >= :startDate")
    Long countByIpAddressAndEventTypeSince(@Param("ipAddress") String ipAddress, 
                                          @Param("eventType") AuditEventType eventType, 
                                          @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.username = :username AND a.eventType = :eventType AND a.createdAt >= :startDate")
    Long countByUsernameAndEventTypeSince(@Param("username") String username, 
                                         @Param("eventType") AuditEventType eventType, 
                                         @Param("startDate") LocalDateTime startDate);
}
