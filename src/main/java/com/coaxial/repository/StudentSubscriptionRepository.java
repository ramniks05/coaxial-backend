package com.coaxial.repository;

import com.coaxial.entity.StudentSubscription;
import com.coaxial.entity.User;
import com.coaxial.enums.PaymentStatus;
import com.coaxial.enums.SubscriptionLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSubscriptionRepository extends JpaRepository<StudentSubscription, Long> {

    // Find active subscriptions for a student
    List<StudentSubscription> findByStudentAndIsActiveTrueOrderByCreatedAtDesc(User student);

    // Find all subscriptions for a student
    List<StudentSubscription> findByStudentOrderByCreatedAtDesc(User student);

    // Find active subscriptions by student ID
    @Query("SELECT s FROM StudentSubscription s WHERE s.student.id = :studentId AND s.isActive = true ORDER BY s.createdAt DESC")
    List<StudentSubscription> findActiveSubscriptionsByStudentId(@Param("studentId") Long studentId);

    // Find active subscriptions for specific entity
    @Query("SELECT s FROM StudentSubscription s WHERE s.student.id = :studentId AND s.subscriptionLevel = :level AND s.entityId = :entityId AND s.isActive = true AND (s.endDate IS NULL OR s.endDate > :now)")
    Optional<StudentSubscription> findActiveSubscriptionForEntity(@Param("studentId") Long studentId, 
                                                                  @Param("level") SubscriptionLevel level, 
                                                                  @Param("entityId") Long entityId, 
                                                                  @Param("now") LocalDateTime now);

    // Find subscriptions by payment status
    List<StudentSubscription> findByPaymentStatusOrderByCreatedAtDesc(PaymentStatus paymentStatus);

    // Find subscriptions by Razorpay order ID
    Optional<StudentSubscription> findByRazorpayOrderId(String razorpayOrderId);

    // Find subscriptions by Razorpay payment ID
    Optional<StudentSubscription> findByRazorpayPaymentId(String razorpayPaymentId);

    // Find expired subscriptions
    @Query("SELECT s FROM StudentSubscription s WHERE s.isActive = true AND s.endDate IS NOT NULL AND s.endDate < :now")
    List<StudentSubscription> findExpiredSubscriptions(@Param("now") LocalDateTime now);

    // Find subscriptions expiring soon (within specified days)
    @Query("SELECT s FROM StudentSubscription s WHERE s.isActive = true AND s.endDate IS NOT NULL AND s.endDate BETWEEN :now AND :futureDate")
    List<StudentSubscription> findSubscriptionsExpiringSoon(@Param("now") LocalDateTime now, 
                                                           @Param("futureDate") LocalDateTime futureDate);

    // Count active subscriptions for a student
    @Query("SELECT COUNT(s) FROM StudentSubscription s WHERE s.student.id = :studentId AND s.isActive = true AND (s.endDate IS NULL OR s.endDate > :now)")
    Long countActiveSubscriptionsByStudentId(@Param("studentId") Long studentId, @Param("now") LocalDateTime now);

    // Find subscriptions by subscription level and entity ID
    @Query("SELECT s FROM StudentSubscription s WHERE s.subscriptionLevel = :level AND s.entityId = :entityId AND s.isActive = true AND (s.endDate IS NULL OR s.endDate > :now)")
    List<StudentSubscription> findActiveSubscriptionsByLevelAndEntity(@Param("level") SubscriptionLevel level, 
                                                                     @Param("entityId") Long entityId, 
                                                                     @Param("now") LocalDateTime now);

    // Find subscriptions created between dates
    @Query("SELECT s FROM StudentSubscription s WHERE s.createdAt BETWEEN :startDate AND :endDate ORDER BY s.createdAt DESC")
    List<StudentSubscription> findSubscriptionsByDateRange(@Param("startDate") LocalDateTime startDate, 
                                                          @Param("endDate") LocalDateTime endDate);

    // Find total revenue by date range
    @Query("SELECT SUM(s.amount) FROM StudentSubscription s WHERE s.paymentStatus = 'PAID' AND s.createdAt BETWEEN :startDate AND :endDate")
    Optional<Double> calculateRevenueByDateRange(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);

    // Find subscription statistics
    @Query("SELECT s.subscriptionLevel, COUNT(s) FROM StudentSubscription s WHERE s.paymentStatus = 'PAID' GROUP BY s.subscriptionLevel")
    List<Object[]> findSubscriptionStatsByLevel();

    // Check if student has access to specific entity
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM StudentSubscription s WHERE s.student.id = :studentId AND s.subscriptionLevel = :level AND s.entityId = :entityId AND s.isActive = true AND s.paymentStatus = 'PAID' AND (s.endDate IS NULL OR s.endDate > :now)")
    Boolean hasStudentAccessToEntity(@Param("studentId") Long studentId, 
                                   @Param("level") SubscriptionLevel level, 
                                   @Param("entityId") Long entityId, 
                                   @Param("now") LocalDateTime now);
}
