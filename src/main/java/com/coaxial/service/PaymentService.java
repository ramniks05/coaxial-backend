package com.coaxial.service;

import com.coaxial.dto.PaymentResponseDTO;
import com.coaxial.dto.SubscriptionRequestDTO;
import com.coaxial.entity.Payment;
import com.coaxial.entity.StudentSubscription;
import com.coaxial.entity.User;
import com.coaxial.enums.PaymentStatus;
import com.coaxial.enums.PaymentType;
import com.coaxial.repository.PaymentRepository;
import com.coaxial.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a payment record for new subscription
     */
    public Payment createPaymentForSubscription(SubscriptionRequestDTO requestDTO, Long studentId, String entityName) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        Payment payment = new Payment();
        payment.setStudent(student);
        payment.setPaymentType(PaymentType.SUBSCRIPTION);
        payment.setSubscriptionLevel(requestDTO.getSubscriptionLevel());
        payment.setEntityId(requestDTO.getEntityId());
        payment.setEntityName(entityName);
        payment.setPlanType(requestDTO.getPlanType());
        payment.setDurationDays(requestDTO.getDurationDays());
        payment.setAmount(requestDTO.getAmount());
        payment.setCurrency("INR");
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setNotes(requestDTO.getNotes());
        payment.generatePaymentReference();

        payment = paymentRepository.save(payment);
        logger.info("Payment created for new subscription: Payment ID {}, Student ID {}", payment.getId(), studentId);

        return payment;
    }

    /**
     * Create a payment record for subscription renewal
     */
    public Payment createPaymentForRenewal(StudentSubscription existingSubscription, Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        Payment payment = new Payment();
        payment.setStudent(student);
        payment.setPaymentType(PaymentType.RENEWAL);
        payment.setSubscription(existingSubscription); // Link to existing subscription
        payment.setSubscriptionLevel(existingSubscription.getSubscriptionLevel());
        payment.setEntityId(existingSubscription.getEntityId());
        payment.setEntityName(existingSubscription.getEntityName());
        payment.setPlanType(existingSubscription.getPlanType());
        payment.setDurationDays(existingSubscription.getDurationDays());
        payment.setAmount(existingSubscription.getAmount());
        payment.setCurrency(existingSubscription.getCurrency());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setNotes("Renewal of subscription #" + existingSubscription.getId());
        payment.generatePaymentReference();

        payment = paymentRepository.save(payment);
        logger.info("Payment created for renewal: Payment ID {}, Original Subscription ID {}", 
                    payment.getId(), existingSubscription.getId());

        return payment;
    }

    /**
     * Update payment with Razorpay order details
     */
    public Payment updateWithRazorpayOrder(Long paymentId, String razorpayOrderId, String razorpayReceipt) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + paymentId));

        payment.setRazorpayOrderId(razorpayOrderId);
        payment.setRazorpayReceipt(razorpayReceipt);

        return paymentRepository.save(payment);
    }

    /**
     * Mark payment as successful and link to created subscription
     */
    public Payment markPaymentSuccess(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for order ID: " + razorpayOrderId));

        payment.setRazorpayPaymentId(razorpayPaymentId);
        payment.setRazorpaySignature(razorpaySignature);
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentDate(LocalDateTime.now());

        payment = paymentRepository.save(payment);
        logger.info("Payment marked as successful: Payment ID {}, Razorpay Payment ID {}", 
                    payment.getId(), razorpayPaymentId);

        return payment;
    }

    /**
     * Link payment to created subscription
     */
    public void linkPaymentToSubscription(Long paymentId, StudentSubscription subscription) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + paymentId));
        
        payment.setSubscription(subscription);
        paymentRepository.save(payment);
        
        logger.info("Payment {} linked to subscription {}", paymentId, subscription.getId());
    }

    /**
     * Mark payment as failed
     */
    public Payment markPaymentFailed(String razorpayOrderId, String failureReason) {
        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for order ID: " + razorpayOrderId));

        payment.setPaymentStatus(PaymentStatus.FAILED);
        payment.setFailureReason(failureReason);
        payment.setRetryCount(payment.getRetryCount() + 1);

        payment = paymentRepository.save(payment);
        logger.warn("Payment marked as failed: Payment ID {}, Reason: {}", payment.getId(), failureReason);

        return payment;
    }

    /**
     * Mark payment as cancelled
     */
    public Payment markPaymentCancelled(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with ID: " + paymentId));

        payment.setPaymentStatus(PaymentStatus.CANCELLED);

        payment = paymentRepository.save(payment);
        logger.info("Payment marked as cancelled: Payment ID {}", payment.getId());

        return payment;
    }

    /**
     * Get payment by ID
     */
    public Optional<PaymentResponseDTO> getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .map(this::convertToDTO);
    }

    /**
     * Get payment by Razorpay order ID
     */
    public Optional<Payment> getPaymentByRazorpayOrderId(String razorpayOrderId) {
        return paymentRepository.findByRazorpayOrderId(razorpayOrderId);
    }

    /**
     * Get all payments for a student
     */
    public List<PaymentResponseDTO> getStudentPayments(Long studentId) {
        List<Payment> payments = paymentRepository.findByStudentId(studentId);
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get pending payments for a student
     */
    public List<PaymentResponseDTO> getPendingPayments(Long studentId) {
        List<Payment> payments = paymentRepository.findPendingPaymentsByStudentId(studentId);
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get payments for a specific subscription (renewal history)
     */
    public List<PaymentResponseDTO> getSubscriptionPayments(Long subscriptionId) {
        List<Payment> payments = paymentRepository.findBySubscriptionId(subscriptionId);
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Clean up abandoned payments (pending for more than 24 hours)
     */
    @Transactional
    public int cleanupAbandonedPayments() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusHours(24);
        List<Payment> abandonedPayments = paymentRepository.findAbandonedPayments(cutoffDate);

        int cleanedUp = 0;
        for (Payment payment : abandonedPayments) {
            payment.setPaymentStatus(PaymentStatus.CANCELLED);
            payment.setNotes((payment.getNotes() != null ? payment.getNotes() + "; " : "") + 
                            "Auto-cancelled due to timeout");
            paymentRepository.save(payment);
            cleanedUp++;
        }

        if (cleanedUp > 0) {
            logger.info("Cleaned up {} abandoned payments", cleanedUp);
        }

        return cleanedUp;
    }

    /**
     * Convert Payment entity to DTO
     */
    private PaymentResponseDTO convertToDTO(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setPaymentReference(payment.getPaymentReference());
        dto.setPaymentType(payment.getPaymentType());
        dto.setStudentId(payment.getStudent().getId());
        dto.setStudentName(payment.getStudent().getFullName());
        
        if (payment.getSubscription() != null) {
            dto.setSubscriptionId(payment.getSubscription().getId());
        }
        
        dto.setSubscriptionLevel(payment.getSubscriptionLevel());
        dto.setEntityId(payment.getEntityId());
        dto.setEntityName(payment.getEntityName());
        dto.setPlanType(payment.getPlanType());
        dto.setDurationDays(payment.getDurationDays());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setRazorpayOrderId(payment.getRazorpayOrderId());
        dto.setRazorpayPaymentId(payment.getRazorpayPaymentId());
        dto.setRazorpayReceipt(payment.getRazorpayReceipt());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setFailureReason(payment.getFailureReason());
        dto.setRetryCount(payment.getRetryCount());
        dto.setNotes(payment.getNotes());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());

        return dto;
    }
}

