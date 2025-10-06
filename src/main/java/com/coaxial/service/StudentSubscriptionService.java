package com.coaxial.service;

import com.coaxial.dto.RazorpayOrderDTO;
import com.coaxial.dto.SubscriptionRequestDTO;
import com.coaxial.dto.SubscriptionResponseDTO;
import com.coaxial.entity.ClassEntity;
import com.coaxial.entity.Course;
import com.coaxial.entity.Exam;
import com.coaxial.entity.StudentSubscription;
import com.coaxial.entity.User;
import com.coaxial.enums.PaymentStatus;
import com.coaxial.enums.SubscriptionLevel;
import com.coaxial.repository.ClassRepository;
import com.coaxial.repository.CourseRepository;
import com.coaxial.repository.ExamRepository;
import com.coaxial.repository.StudentSubscriptionRepository;
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
public class StudentSubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(StudentSubscriptionService.class);

    @Autowired
    private StudentSubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private RazorpayPaymentService razorpayService;

    /**
     * Create a new subscription and initialize payment
     */
    public RazorpayOrderDTO createSubscription(SubscriptionRequestDTO requestDTO, Long studentId) throws Exception {
        // Validate student exists
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        // Validate entity exists and get entity name
        String entityName = validateAndGetEntityName(requestDTO.getSubscriptionLevel(), requestDTO.getEntityId());

        // Check if student already has active subscription for this entity
        if (hasActiveSubscription(studentId, requestDTO.getSubscriptionLevel(), requestDTO.getEntityId())) {
            throw new IllegalArgumentException("Student already has an active subscription for this " + 
                    requestDTO.getSubscriptionLevel().getDisplayName().toLowerCase());
        }

        // Create subscription
        StudentSubscription subscription = new StudentSubscription();
        subscription.setStudent(student);
        subscription.setSubscriptionLevel(requestDTO.getSubscriptionLevel());
        subscription.setEntityId(requestDTO.getEntityId());
        subscription.setEntityName(entityName);
        subscription.setAmount(requestDTO.getAmount());
        subscription.setDurationDays(requestDTO.getDurationDays());
        subscription.setNotes(requestDTO.getNotes());
        subscription.setStartDate(LocalDateTime.now());
        subscription.setPaymentStatus(PaymentStatus.PENDING);
        subscription.setIsActive(false); // Will be activated after successful payment

        // Set end date if duration is specified
        if (requestDTO.getDurationDays() != null) {
            subscription.setEndDate(subscription.getStartDate().plusDays(requestDTO.getDurationDays()));
        }

        // Save subscription
        subscription = subscriptionRepository.save(subscription);
        logger.info("Subscription created with ID: {} for student: {} and entity: {}", 
                subscription.getId(), studentId, requestDTO.getEntityId());

        // Create Razorpay order
        try {
            return razorpayService.createOrder(subscription);
        } catch (Exception e) {
            logger.error("Failed to create Razorpay order for subscription: {}", subscription.getId(), e);
            // Update subscription status to failed
            subscription.setPaymentStatus(PaymentStatus.FAILED);
            subscriptionRepository.save(subscription);
            throw new Exception("Failed to create payment order: " + e.getMessage(), e);
        }
    }

    /**
     * Verify payment and activate subscription
     */
    public boolean verifyPaymentAndActivate(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        return razorpayService.verifyPayment(razorpayOrderId, razorpayPaymentId, razorpaySignature);
    }

    /**
     * Get all subscriptions for a student
     */
    public List<SubscriptionResponseDTO> getStudentSubscriptions(Long studentId) {
        List<StudentSubscription> subscriptions = subscriptionRepository.findByStudentOrderByCreatedAtDesc(
                userRepository.findById(studentId)
                        .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId))
        );

        return subscriptions.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get active subscriptions for a student
     */
    public List<SubscriptionResponseDTO> getActiveStudentSubscriptions(Long studentId) {
        List<StudentSubscription> subscriptions = subscriptionRepository.findActiveSubscriptionsByStudentId(studentId);

        return subscriptions.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get subscription by ID
     */
    public Optional<SubscriptionResponseDTO> getSubscriptionById(Long subscriptionId) {
        return subscriptionRepository.findById(subscriptionId)
                .map(this::convertToResponseDTO);
    }

    /**
     * Cancel subscription
     */
    public boolean cancelSubscription(Long subscriptionId, Long studentId) {
        Optional<StudentSubscription> subscriptionOpt = subscriptionRepository.findById(subscriptionId);
        
        if (subscriptionOpt.isPresent()) {
            StudentSubscription subscription = subscriptionOpt.get();
            
            // Verify ownership
            if (!subscription.getStudent().getId().equals(studentId)) {
                throw new IllegalArgumentException("Student can only cancel their own subscriptions");
            }

            // Cancel subscription
            subscription.setIsActive(false);
            subscription.setPaymentStatus(PaymentStatus.CANCELLED);
            subscriptionRepository.save(subscription);

            // Cancel Razorpay order if exists
            if (subscription.getRazorpayOrderId() != null) {
                razorpayService.cancelOrder(subscription.getRazorpayOrderId());
            }

            logger.info("Subscription cancelled: {} by student: {}", subscriptionId, studentId);
            return true;
        }
        
        return false;
    }

    /**
     * Check if student has access to specific entity
     */
    public boolean hasStudentAccess(Long studentId, SubscriptionLevel level, Long entityId) {
        return subscriptionRepository.hasStudentAccessToEntity(studentId, level, entityId, LocalDateTime.now());
    }

    /**
     * Get accessible entity IDs for a student by subscription level
     */
    public List<Long> getAccessibleEntityIds(Long studentId, SubscriptionLevel level) {
        List<StudentSubscription> subscriptions = subscriptionRepository.findActiveSubscriptionsByLevelAndEntity(
                level, null, LocalDateTime.now()); // null entityId to get all

        return subscriptions.stream()
                .filter(sub -> sub.getStudent().getId().equals(studentId))
                .map(StudentSubscription::getEntityId)
                .collect(Collectors.toList());
    }

    /**
     * Get subscription statistics for admin
     */
    public List<Object[]> getSubscriptionStatistics() {
        return subscriptionRepository.findSubscriptionStatsByLevel();
    }

    /**
     * Get expired subscriptions
     */
    public List<SubscriptionResponseDTO> getExpiredSubscriptions() {
        List<StudentSubscription> expiredSubscriptions = subscriptionRepository.findExpiredSubscriptions(LocalDateTime.now());
        
        return expiredSubscriptions.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Auto-deactivate expired subscriptions (can be called by scheduler)
     */
    @Transactional
    public int deactivateExpiredSubscriptions() {
        List<StudentSubscription> expiredSubscriptions = subscriptionRepository.findExpiredSubscriptions(LocalDateTime.now());
        
        int deactivatedCount = 0;
        for (StudentSubscription subscription : expiredSubscriptions) {
            subscription.setIsActive(false);
            subscriptionRepository.save(subscription);
            deactivatedCount++;
        }
        
        if (deactivatedCount > 0) {
            logger.info("Deactivated {} expired subscriptions", deactivatedCount);
        }
        
        return deactivatedCount;
    }

    /**
     * Validate entity exists and return entity name
     */
    private String validateAndGetEntityName(SubscriptionLevel level, Long entityId) {
        switch (level) {
            case CLASS:
                ClassEntity classEntity = classRepository.findById(entityId)
                        .orElseThrow(() -> new IllegalArgumentException("Class not found with ID: " + entityId));
                return classEntity.getName();

            case EXAM:
                Exam exam = examRepository.findById(entityId)
                        .orElseThrow(() -> new IllegalArgumentException("Exam not found with ID: " + entityId));
                return exam.getName();

            case COURSE:
                Course course = courseRepository.findById(entityId)
                        .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + entityId));
                return course.getName();

            default:
                throw new IllegalArgumentException("Invalid subscription level: " + level);
        }
    }

    /**
     * Check if student has active subscription for entity
     */
    private boolean hasActiveSubscription(Long studentId, SubscriptionLevel level, Long entityId) {
        return subscriptionRepository.findActiveSubscriptionForEntity(studentId, level, entityId, LocalDateTime.now())
                .isPresent();
    }

    /**
     * Convert entity to response DTO
     */
    private SubscriptionResponseDTO convertToResponseDTO(StudentSubscription subscription) {
        SubscriptionResponseDTO dto = new SubscriptionResponseDTO();
        dto.setId(subscription.getId());
        dto.setStudentId(subscription.getStudent().getId());
        dto.setStudentName(subscription.getStudent().getFullName());
        dto.setStudentEmail(subscription.getStudent().getEmail());
        dto.setSubscriptionLevel(subscription.getSubscriptionLevel());
        dto.setEntityId(subscription.getEntityId());
        dto.setEntityName(subscription.getEntityName());
        dto.setAmount(subscription.getAmount());
        dto.setCurrency(subscription.getCurrency());
        dto.setIsActive(subscription.getIsActive());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setDurationDays(subscription.getDurationDays());
        dto.setRazorpayOrderId(subscription.getRazorpayOrderId());
        dto.setRazorpayPaymentId(subscription.getRazorpayPaymentId());
        dto.setPaymentStatus(subscription.getPaymentStatus());
        dto.setPaymentDate(subscription.getPaymentDate());
        dto.setRazorpayReceipt(subscription.getRazorpayReceipt());
        dto.setNotes(subscription.getNotes());
        dto.setCreatedAt(subscription.getCreatedAt());
        dto.setUpdatedAt(subscription.getUpdatedAt());
        dto.setRemainingDays(subscription.getRemainingDays());
        dto.setIsExpired(subscription.isExpired());

        return dto;
    }
}
