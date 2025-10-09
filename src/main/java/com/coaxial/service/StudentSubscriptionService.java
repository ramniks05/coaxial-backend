package com.coaxial.service;

import com.coaxial.dto.RazorpayOrderDTO;
import com.coaxial.dto.SubscriptionRequestDTO;
import com.coaxial.dto.SubscriptionResponseDTO;
import com.coaxial.entity.ClassEntity;
import com.coaxial.entity.Course;
import com.coaxial.entity.Exam;
import com.coaxial.entity.PricingConfiguration;
import com.coaxial.entity.StudentSubscription;
import com.coaxial.enums.PaymentStatus;
import com.coaxial.enums.SubscriptionLevel;
import com.coaxial.enums.SubscriptionStatus;
import com.coaxial.repository.ClassRepository;
import com.coaxial.repository.CourseRepository;
import com.coaxial.repository.ExamRepository;
import com.coaxial.repository.PricingConfigurationRepository;
import com.coaxial.repository.StudentSubscriptionRepository;
import com.coaxial.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private PricingConfigurationRepository pricingConfigurationRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RazorpayPaymentService razorpayService;

    /**
     * Create a new subscription payment (subscription will be created after payment success)
     * This prevents duplicate subscriptions on payment failure
     */
    public RazorpayOrderDTO createSubscription(SubscriptionRequestDTO requestDTO, Long studentId) throws Exception {
        // Validate entity exists and get entity name
        String entityName = validateAndGetEntityName(requestDTO.getSubscriptionLevel(), requestDTO.getEntityId());

        // Check if student already has active subscription for this entity
        if (hasActiveSubscription(studentId, requestDTO.getSubscriptionLevel(), requestDTO.getEntityId())) {
            throw new IllegalArgumentException("Student already has an active subscription for this " + 
                    requestDTO.getSubscriptionLevel().getDisplayName().toLowerCase());
        }

        // Create Payment record (NOT subscription yet)
        com.coaxial.entity.Payment payment = paymentService.createPaymentForSubscription(requestDTO, studentId, entityName);
        logger.info("Payment record created with ID: {} for student: {} and entity: {}", 
                payment.getId(), studentId, requestDTO.getEntityId());

        // Create Razorpay order
        try {
            return razorpayService.createOrderForPayment(payment);
        } catch (Exception e) {
            logger.error("Failed to create Razorpay order for payment: {}", payment.getId(), e);
            // Mark payment as failed (NO subscription created)
            paymentService.markPaymentFailed(payment.getRazorpayOrderId(), e.getMessage());
            throw new Exception("Failed to create payment order: " + e.getMessage(), e);
        }
    }

    /**
     * Verify payment and create/activate subscription
     * Subscription is ONLY created after successful payment verification
     */
    @Transactional
    public boolean verifyPaymentAndActivate(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        try {
            logger.info("Starting payment verification for order: {}", razorpayOrderId);
            
            // Verify payment signature
            boolean isValid = razorpayService.verifyPaymentSignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);
            
            if (!isValid) {
                logger.warn("Payment signature verification failed for order: {}", razorpayOrderId);
                return false;
            }
            
            logger.info("Payment signature verified successfully for order: {}", razorpayOrderId);
            
            // Get payment record
            Optional<com.coaxial.entity.Payment> paymentOpt = paymentService.getPaymentByRazorpayOrderId(razorpayOrderId);
            if (!paymentOpt.isPresent()) {
                logger.error("Payment record not found for order ID: {}", razorpayOrderId);
                return false;
            }
            
            com.coaxial.entity.Payment payment = paymentOpt.get();
            logger.info("Payment record found: Payment ID {}", payment.getId());
            
            // Mark payment as successful
            paymentService.markPaymentSuccess(razorpayOrderId, razorpayPaymentId, razorpaySignature);
            logger.info("Payment marked as successful: Payment ID {}", payment.getId());
            
            // NOW create the subscription (only on successful payment)
            StudentSubscription subscription = createSubscriptionFromPayment(payment);
            logger.info("Subscription created successfully: ID {}", subscription.getId());
            
            logger.info("Payment verified and subscription created: Payment ID {}, Subscription ID {}", 
                        payment.getId(), subscription.getId());
            
            return true;
            
        } catch (Exception e) {
            logger.error("Exception in verifyPaymentAndActivate for order {}: {}", razorpayOrderId, e.getMessage(), e);
            throw new RuntimeException("Failed to verify payment and create subscription: " + e.getMessage(), e);
        }
    }
    
    /**
     * Create subscription from successful payment
     * Validates that no active subscription exists for same entity before creating
     */
    private StudentSubscription createSubscriptionFromPayment(com.coaxial.entity.Payment payment) {
        // Validate: Check if active subscription already exists for this entity
        Optional<StudentSubscription> existingActiveOpt = subscriptionRepository.findActiveSubscriptionForEntity(
            payment.getStudent().getId(),
            payment.getSubscriptionLevel(),
            payment.getEntityId(),
            LocalDateTime.now()
        );
        
        if (existingActiveOpt.isPresent()) {
            StudentSubscription existingActive = existingActiveOpt.get();
            logger.error("Attempted to create duplicate active subscription. " +
                        "Student ID: {}, Level: {}, Entity ID: {}. " +
                        "Existing active subscription ID: {}", 
                        payment.getStudent().getId(), payment.getSubscriptionLevel(), 
                        payment.getEntityId(), existingActive.getId());
            
            throw new IllegalStateException(
                "You already have an active subscription for " + payment.getEntityName() + 
                " (Subscription ID: " + existingActive.getId() + ", expires " + existingActive.getEndDate() + "). " +
                "Cannot create duplicate active subscription. Please cancel the existing one first."
            );
        }
        
        // Create new subscription
        StudentSubscription subscription = new StudentSubscription();
        subscription.setStudent(payment.getStudent());
        subscription.setSubscriptionLevel(payment.getSubscriptionLevel());
        subscription.setEntityId(payment.getEntityId());
        subscription.setEntityName(payment.getEntityName());
        subscription.setAmount(payment.getAmount());
        subscription.setCurrency(payment.getCurrency());
        subscription.setPlanType(payment.getPlanType());
        subscription.setDurationDays(payment.getDurationDays());
        subscription.setStartDate(LocalDateTime.now());
        subscription.setPaymentStatus(PaymentStatus.PAID);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setIsActive(true);
        subscription.setPaymentDate(payment.getPaymentDate());
        subscription.setRazorpayOrderId(payment.getRazorpayOrderId());
        subscription.setRazorpayPaymentId(payment.getRazorpayPaymentId());
        subscription.setRazorpaySignature(payment.getRazorpaySignature());
        subscription.setRazorpayReceipt(payment.getRazorpayReceipt());
        
        // Set end date if duration is specified
        if (payment.getDurationDays() != null) {
            subscription.setEndDate(subscription.getStartDate().plusDays(payment.getDurationDays()));
        }
        
        subscription = subscriptionRepository.save(subscription);
        logger.info("Subscription created from payment: Subscription ID {}, Payment ID {}", 
                    subscription.getId(), payment.getId());
        
        // Link payment to created subscription
        paymentService.linkPaymentToSubscription(payment.getId(), subscription);
        
        return subscription;
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
     * Get subscription by Razorpay order ID
     * Used after payment verification to get newly created subscription
     */
    public Optional<SubscriptionResponseDTO> getSubscriptionByRazorpayOrderId(String razorpayOrderId) {
        return subscriptionRepository.findByRazorpayOrderId(razorpayOrderId)
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
            subscription.setStatus(SubscriptionStatus.CANCELLED);
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
     * Get active subscription details for check-access endpoint
     * Returns the subscription DTO if student has access, null otherwise
     */
    public Optional<SubscriptionResponseDTO> getActiveSubscriptionForAccess(Long studentId, SubscriptionLevel level, Long entityId) {
        Optional<StudentSubscription> subscriptionOpt = subscriptionRepository.findActiveSubscriptionForEntity(
                studentId, level, entityId, LocalDateTime.now());
        
        return subscriptionOpt.map(this::convertToResponseDTO);
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
     * Get subscriptions for a student with optional status filter
     * By default shows only latest subscription per entity (prevents duplicate cancelled subscriptions)
     * Use includeAll=true to see complete history
     * 
     * For student view: Always shows latest per entity (even when filtering by status)
     * For admin view: Use includeAll=true to see all records
     */
    public List<SubscriptionResponseDTO> getMySubscriptions(Long studentId, SubscriptionStatus status, Boolean includeAll) {
        List<StudentSubscription> subscriptions;
        
        if (includeAll != null && includeAll) {
            // Admin/Complete history view: Show ALL subscriptions
            if (status != null) {
                subscriptions = subscriptionRepository.findByStudentIdAndStatus(studentId, status);
            } else {
                subscriptions = subscriptionRepository.findByStudentId(studentId);
            }
        } else if (status != null) {
            // Filter by status AND show only latest per entity
            // e.g., ?status=CANCELLED shows latest cancelled per entity, not all 3 cancelled Class 1
            subscriptions = subscriptionRepository.findLatestSubscriptionsPerEntityByStatus(studentId, status);
        } else {
            // Default: Show only latest subscription per entity (any status)
            // This prevents showing 3 cancelled "Class 1" subscriptions
            subscriptions = subscriptionRepository.findLatestSubscriptionsPerEntity(studentId);
        }
        
        return subscriptions.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get subscriptions expiring within specified days
     */
    public List<SubscriptionResponseDTO> getSubscriptionsExpiringSoon(Long studentId, int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(days);
        
        List<StudentSubscription> subscriptions = subscriptionRepository.findSubscriptionsExpiringSoon(now, futureDate);
        
        // Filter by student if studentId is provided
        if (studentId != null) {
            subscriptions = subscriptions.stream()
                    .filter(sub -> sub.getStudent().getId().equals(studentId))
                    .collect(Collectors.toList());
        }
        
        return subscriptions.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Renew an existing subscription
     * Creates Payment record first, subscription created only after successful payment
     */
    public RazorpayOrderDTO renewSubscription(Long subscriptionId, Long studentId) throws Exception {
        // Get existing subscription
        StudentSubscription existingSubscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found with ID: " + subscriptionId));
        
        // Verify ownership
        if (!existingSubscription.getStudent().getId().equals(studentId)) {
            throw new IllegalArgumentException("Student can only renew their own subscriptions");
        }
        
        // Check if this subscription is already active (no need to renew)
        if (existingSubscription.isActiveAndNotExpired()) {
            throw new IllegalArgumentException(
                "Subscription is already active and will expire on " + 
                existingSubscription.getEndDate() + ". No need to renew yet."
            );
        }
        
        // Check if ANOTHER active subscription exists for the same entity
        Optional<StudentSubscription> activeSubscriptionOpt = subscriptionRepository.findActiveSubscriptionForEntity(
            studentId, 
            existingSubscription.getSubscriptionLevel(), 
            existingSubscription.getEntityId(), 
            LocalDateTime.now()
        );
        
        if (activeSubscriptionOpt.isPresent() && !activeSubscriptionOpt.get().getId().equals(subscriptionId)) {
            StudentSubscription activeSubscription = activeSubscriptionOpt.get();
            throw new IllegalArgumentException(
                "You already have an active subscription for " + existingSubscription.getEntityName() + 
                " (Subscription ID: " + activeSubscription.getId() + "). " +
                "Please manage or cancel the existing subscription before renewing this one."
            );
        }
        
        // Validate entity still exists
        validateAndGetEntityName(existingSubscription.getSubscriptionLevel(), existingSubscription.getEntityId());
        
        // Create Payment record for renewal (NOT subscription yet)
        com.coaxial.entity.Payment payment = paymentService.createPaymentForRenewal(existingSubscription, studentId);
        logger.info("Renewal payment created with ID: {} for original subscription: {}", 
                payment.getId(), subscriptionId);
        
        // Create Razorpay order
        try {
            return razorpayService.createOrderForPayment(payment);
        } catch (Exception e) {
            logger.error("Failed to create Razorpay order for renewal payment: {}", payment.getId(), e);
            // Mark payment as failed (NO new subscription created - original subscription intact)
            paymentService.markPaymentFailed(payment.getRazorpayOrderId(), e.getMessage());
            throw new Exception("Failed to create payment order for renewal: " + e.getMessage(), e);
        }
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
            subscription.setStatus(SubscriptionStatus.EXPIRED);
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
        
        // Fetch and set courseTypeName and courseName
        try {
            String[] courseDetails = getCourseDetails(subscription.getSubscriptionLevel(), subscription.getEntityId());
            dto.setCourseTypeName(courseDetails[0]);
            dto.setCourseName(courseDetails[1]);
        } catch (Exception e) {
            logger.warn("Failed to fetch course details for subscription {}: {}", subscription.getId(), e.getMessage());
            dto.setCourseTypeName(null);
            dto.setCourseName(null);
        }
        
        dto.setAmount(subscription.getAmount());
        dto.setCurrency(subscription.getCurrency());
        dto.setIsActive(subscription.getIsActive());
        
        // Fetch and set pricing information
        try {
            PricingConfiguration pricing = getPricingConfiguration(subscription.getSubscriptionLevel(), subscription.getEntityId());
            if (pricing != null) {
                dto.setMonthlyPrice(pricing.getMonthlyPrice());
                dto.setQuarterlyPrice(pricing.getQuarterlyPrice());
                dto.setYearlyPrice(pricing.getYearlyPrice());
                
                // Calculate discount percentage and savings
                calculateAndSetDiscount(dto, subscription, pricing);
            } else {
                // Set null prices if not found - graceful degradation
                dto.setMonthlyPrice(null);
                dto.setQuarterlyPrice(null);
                dto.setYearlyPrice(null);
                dto.setDiscountPercentage(null);
                dto.setSavingsAmount(null);
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch pricing for subscription {}: {}", subscription.getId(), e.getMessage());
            // Set null prices if not found - graceful degradation
            dto.setMonthlyPrice(null);
            dto.setQuarterlyPrice(null);
            dto.setYearlyPrice(null);
            dto.setDiscountPercentage(null);
            dto.setSavingsAmount(null);
        }
        
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setExpiryDate(subscription.getExpiryDate());
        dto.setDurationDays(subscription.getDurationDays());
        dto.setPlanType(subscription.getPlanType());
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
        
        // Compute and set status
        SubscriptionStatus computedStatus = subscription.getStatus() != null 
            ? subscription.getStatus() 
            : subscription.computeStatus();
        dto.setStatus(computedStatus);

        return dto;
    }

    /**
     * Get course details (courseTypeName and courseName) based on subscription level and entity ID
     * Returns [courseTypeName, courseName]
     * Handles lazy loading gracefully to prevent LazyInitializationException
     */
    private String[] getCourseDetails(SubscriptionLevel level, Long entityId) {
        String[] details = new String[2];
        details[0] = null;  // courseTypeName
        details[1] = null;  // courseName
        
        try {
            switch (level) {
                case CLASS:
                    ClassEntity classEntity = classRepository.findById(entityId).orElse(null);
                    if (classEntity != null) {
                        try {
                            Course course = classEntity.getCourse();
                            if (course != null) {
                                details[1] = course.getName();
                                try {
                                    if (course.getCourseType() != null) {
                                        details[0] = course.getCourseType().getName();
                                    }
                                } catch (Exception e) {
                                    logger.debug("Could not fetch courseType for class {}: {}", entityId, e.getMessage());
                                }
                            }
                        } catch (Exception e) {
                            logger.debug("Could not fetch course for class {}: {}", entityId, e.getMessage());
                        }
                    }
                    break;
                    
                case EXAM:
                    Exam exam = examRepository.findById(entityId).orElse(null);
                    if (exam != null) {
                        try {
                            Course course = exam.getCourse();
                            if (course != null) {
                                details[1] = course.getName();
                                try {
                                    if (course.getCourseType() != null) {
                                        details[0] = course.getCourseType().getName();
                                    }
                                } catch (Exception e) {
                                    logger.debug("Could not fetch courseType for exam {}: {}", entityId, e.getMessage());
                                }
                            }
                        } catch (Exception e) {
                            logger.debug("Could not fetch course for exam {}: {}", entityId, e.getMessage());
                        }
                    }
                    break;
                    
                case COURSE:
                    Course course = courseRepository.findById(entityId).orElse(null);
                    if (course != null) {
                        details[1] = course.getName();
                        try {
                            if (course.getCourseType() != null) {
                                details[0] = course.getCourseType().getName();
                            }
                        } catch (Exception e) {
                            logger.debug("Could not fetch courseType for course {}: {}", entityId, e.getMessage());
                        }
                    }
                    break;
                    
                default:
                    logger.warn("Invalid subscription level: {}", level);
            }
        } catch (Exception e) {
            logger.error("Error fetching course details for level {} and entityId {}: {}", level, entityId, e.getMessage(), e);
        }
        
        return details;
    }

    /**
     * Get pricing configuration for a specific entity and subscription level
     * Handles different entity type naming conventions
     */
    private PricingConfiguration getPricingConfiguration(SubscriptionLevel level, Long entityId) {
        String entityType = level.name(); // CLASS, EXAM, COURSE
        
        // Try to find by exact match first
        Optional<PricingConfiguration> pricingOpt = pricingConfigurationRepository
                .findByEntityTypeAndEntityId(entityType, entityId);
        
        if (pricingOpt.isPresent()) {
            return pricingOpt.get();
        }
        
        // If not found, try alternative naming (for backward compatibility)
        // Some configurations might use full names like "Professional Course" instead of "COURSE"
        logger.debug("Pricing not found for entityType={}, entityId={}, trying alternatives", entityType, entityId);
        
        return null;
    }

    /**
     * Calculate discount percentage and savings amount for subscription
     */
    private void calculateAndSetDiscount(SubscriptionResponseDTO dto, StudentSubscription subscription, PricingConfiguration pricing) {
        if (subscription.getPlanType() == null || pricing == null || pricing.getMonthlyPrice() == null) {
            dto.setDiscountPercentage(BigDecimal.ZERO);
            dto.setSavingsAmount(BigDecimal.ZERO);
            return;
        }
        
        BigDecimal monthlyPrice = pricing.getMonthlyPrice();
        BigDecimal paidAmount = subscription.getAmount();
        
        try {
            switch (subscription.getPlanType()) {
                case QUARTERLY:
                    // Monthly equivalent = monthlyPrice * 3
                    BigDecimal quarterlyMonthlyEquivalent = monthlyPrice.multiply(BigDecimal.valueOf(3));
                    if (quarterlyMonthlyEquivalent.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal quarterlySavings = quarterlyMonthlyEquivalent.subtract(paidAmount);
                        BigDecimal quarterlyDiscountPct = quarterlySavings
                                .divide(quarterlyMonthlyEquivalent, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100));
                        dto.setDiscountPercentage(quarterlyDiscountPct);
                        dto.setSavingsAmount(quarterlySavings);
                    } else {
                        dto.setDiscountPercentage(BigDecimal.ZERO);
                        dto.setSavingsAmount(BigDecimal.ZERO);
                    }
                    break;
                    
                case YEARLY:
                    // Monthly equivalent = monthlyPrice * 12
                    BigDecimal yearlyMonthlyEquivalent = monthlyPrice.multiply(BigDecimal.valueOf(12));
                    if (yearlyMonthlyEquivalent.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal yearlySavings = yearlyMonthlyEquivalent.subtract(paidAmount);
                        BigDecimal yearlyDiscountPct = yearlySavings
                                .divide(yearlyMonthlyEquivalent, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100));
                        dto.setDiscountPercentage(yearlyDiscountPct);
                        dto.setSavingsAmount(yearlySavings);
                    } else {
                        dto.setDiscountPercentage(BigDecimal.ZERO);
                        dto.setSavingsAmount(BigDecimal.ZERO);
                    }
                    break;
                    
                case MONTHLY:
                default:
                    // No discount for monthly plan (base price)
                    dto.setDiscountPercentage(BigDecimal.ZERO);
                    dto.setSavingsAmount(BigDecimal.ZERO);
                    break;
            }
        } catch (Exception e) {
            logger.warn("Error calculating discount for subscription {}: {}", subscription.getId(), e.getMessage());
            dto.setDiscountPercentage(BigDecimal.ZERO);
            dto.setSavingsAmount(BigDecimal.ZERO);
        }
    }
}
