package com.coaxial.repository;

import com.coaxial.entity.Payment;
import com.coaxial.entity.User;
import com.coaxial.enums.PaymentStatus;
import com.coaxial.enums.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Find by Razorpay order ID
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    // Find by Razorpay payment ID
    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    // Find by payment reference
    Optional<Payment> findByPaymentReference(String paymentReference);

    // Find all payments by student
    List<Payment> findByStudentOrderByCreatedAtDesc(User student);

    // Find payments by student ID
    @Query("SELECT p FROM Payment p WHERE p.student.id = :studentId ORDER BY p.createdAt DESC")
    List<Payment> findByStudentId(@Param("studentId") Long studentId);

    // Find payments by status
    List<Payment> findByPaymentStatusOrderByCreatedAtDesc(PaymentStatus paymentStatus);

    // Find payments by student and status
    @Query("SELECT p FROM Payment p WHERE p.student.id = :studentId AND p.paymentStatus = :status ORDER BY p.createdAt DESC")
    List<Payment> findByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") PaymentStatus status);

    // Find payments by type
    List<Payment> findByPaymentTypeOrderByCreatedAtDesc(PaymentType paymentType);

    // Find pending payments for a student
    @Query("SELECT p FROM Payment p WHERE p.student.id = :studentId AND p.paymentStatus = 'PENDING' ORDER BY p.createdAt DESC")
    List<Payment> findPendingPaymentsByStudentId(@Param("studentId") Long studentId);

    // Find failed payments that can be retried
    @Query("SELECT p FROM Payment p WHERE p.student.id = :studentId AND p.paymentStatus = 'FAILED' AND p.retryCount < :maxRetries ORDER BY p.createdAt DESC")
    List<Payment> findRetriablePayments(@Param("studentId") Long studentId, @Param("maxRetries") int maxRetries);

    // Find payments created between dates
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate ORDER BY p.createdAt DESC")
    List<Payment> findPaymentsByDateRange(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);

    // Find payments for a specific subscription (for renewal tracking)
    @Query("SELECT p FROM Payment p WHERE p.subscription.id = :subscriptionId ORDER BY p.createdAt DESC")
    List<Payment> findBySubscriptionId(@Param("subscriptionId") Long subscriptionId);

    // Calculate total revenue by date range
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentStatus = 'PAID' AND p.paymentDate BETWEEN :startDate AND :endDate")
    Optional<BigDecimal> calculateRevenueByDateRange(@Param("startDate") LocalDateTime startDate, 
                                                     @Param("endDate") LocalDateTime endDate);

    // Count payments by status
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentStatus = :status")
    Long countByStatus(@Param("status") PaymentStatus status);

    // Find abandoned payments (pending for more than specified hours)
    @Query("SELECT p FROM Payment p WHERE p.paymentStatus = 'PENDING' AND p.createdAt < :cutoffDate ORDER BY p.createdAt DESC")
    List<Payment> findAbandonedPayments(@Param("cutoffDate") LocalDateTime cutoffDate);
}

