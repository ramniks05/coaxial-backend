package com.coaxial.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coaxial.dto.PaymentCallbackDTO;
import com.coaxial.dto.RazorpayOrderDTO;
import com.coaxial.dto.SubscriptionRequestDTO;
import com.coaxial.dto.SubscriptionResponseDTO;
import com.coaxial.enums.SubscriptionStatus;
import com.coaxial.service.RazorpayPaymentService;
import com.coaxial.service.StudentSubscriptionService;
import com.coaxial.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/student/subscriptions")
@PreAuthorize("hasRole('STUDENT')")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://127.0.0.1:3000", "http://127.0.0.1:3001"}, allowCredentials = "true")
@Tag(name = "Student Subscriptions", description = "APIs for students to manage their subscriptions")
public class StudentSubscriptionController {

    private static final Logger logger = LoggerFactory.getLogger(StudentSubscriptionController.class);

    @Autowired
    private StudentSubscriptionService subscriptionService;

    @Autowired
    private RazorpayPaymentService razorpayService;

    @Autowired
    private UserService userService;

    /**
     * Create a new subscription and initialize payment
     */
    @PostMapping(produces = "application/json")
    public ResponseEntity<?> createSubscription(@Valid @RequestBody SubscriptionRequestDTO requestDTO, 
                                               Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            
            // Check if Razorpay is configured
            if (!razorpayService.isRazorpayConfigured()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(Map.of("error", "Payment service is not configured"));
            }

            RazorpayOrderDTO orderDTO = subscriptionService.createSubscription(requestDTO, studentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("order", orderDTO);
            response.put("keyId", razorpayService.getRazorpayKeyId());
            response.put("subscriptionLevel", requestDTO.getSubscriptionLevel());
            response.put("entityId", requestDTO.getEntityId());
            response.put("amount", requestDTO.getAmount());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid subscription request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating subscription", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create subscription: " + e.getMessage()));
        }
    }

    /**
     * Verify payment and activate subscription
     * Subscription is created during this verification, not before
     */
    @PostMapping(value = "/verify-payment", produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> verifyPayment(@Valid @RequestBody PaymentCallbackDTO callbackDTO, 
                                          Authentication authentication) {
        logger.info("=== Verify Payment Endpoint Called ===");
        logger.info("Order ID: {}", callbackDTO.getRazorpay_order_id());
        logger.info("Payment ID: {}", callbackDTO.getRazorpay_payment_id());
        logger.info("Signature: {}", callbackDTO.getRazorpay_signature() != null ? "present" : "missing");
        
        try {
            logger.info("Calling subscriptionService.verifyPaymentAndActivate()");
            
            boolean isValid = subscriptionService.verifyPaymentAndActivate(
                    callbackDTO.getRazorpay_order_id(),
                    callbackDTO.getRazorpay_payment_id(),
                    callbackDTO.getRazorpay_signature()
            );

            logger.info("Payment verification completed. Result: {}", isValid);

            if (isValid) {
                logger.info("Fetching subscription by razorpay order ID: {}", callbackDTO.getRazorpay_order_id());
                
                // Get newly created subscription by razorpay order ID
                Optional<SubscriptionResponseDTO> subscriptionOpt = subscriptionService.getSubscriptionByRazorpayOrderId(
                        callbackDTO.getRazorpay_order_id()
                );
                
                if (subscriptionOpt.isPresent()) {
                    SubscriptionResponseDTO subscription = subscriptionOpt.get();
                    logger.info("Subscription found: ID {}, Status {}", subscription.getId(), subscription.getStatus());
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Payment verified successfully");
                    response.put("subscription", subscription);
                    
                    logger.info("Sending success response for subscription ID: {}", subscription.getId());
                    return ResponseEntity.ok(response);
                    
                } else {
                    logger.error("Payment verified but subscription NOT found for order: {}", callbackDTO.getRazorpay_order_id());
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Payment verified but subscription not found");
                    response.put("subscription", null);
                    
                    return ResponseEntity.ok(response);
                }
                
            } else {
                logger.warn("Payment verification returned false for order: {}", callbackDTO.getRazorpay_order_id());
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "Payment verification failed");
                
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (IllegalArgumentException e) {
            logger.error("Validation error in verify payment for order {}: {}", callbackDTO.getRazorpay_order_id(), e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            logger.error("=== EXCEPTION in verify payment ===", e);
            logger.error("Order ID: {}", callbackDTO.getRazorpay_order_id());
            logger.error("Exception type: {}", e.getClass().getName());
            logger.error("Exception message: {}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Payment verification failed: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all subscriptions for current student with optional status filter
     * By default shows only latest subscription per entity to avoid duplicate cancelled subscriptions
     * Use includeAll=true to see complete subscription history
     */
    @Operation(
        summary = "Get student subscriptions with filters",
        description = "Retrieve subscriptions for the authenticated student. By default returns only the latest subscription per entity. Use includeAll=true for complete history."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved subscriptions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/my-subscriptions")
    public ResponseEntity<List<SubscriptionResponseDTO>> getMySubscriptions(
            @Parameter(
                description = "Filter by subscription status. Valid values: PENDING, ACTIVE, EXPIRED, CANCELLED",
                schema = @Schema(implementation = SubscriptionStatus.class),
                example = "ACTIVE"
            )
            @RequestParam(required = false) SubscriptionStatus status,
            @Parameter(
                description = "Include all subscription history (true) or only latest per entity (false)",
                example = "false"
            )
            @RequestParam(required = false, defaultValue = "false") Boolean includeAll,
            Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            List<SubscriptionResponseDTO> subscriptions = subscriptionService.getMySubscriptions(studentId, status, includeAll);
            return ResponseEntity.ok(subscriptions);
        } catch (Exception e) {
            logger.error("Error fetching student subscriptions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all subscriptions for current student (legacy endpoint, kept for backward compatibility)
     */
    @GetMapping
    public ResponseEntity<List<SubscriptionResponseDTO>> getAllSubscriptions(Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            List<SubscriptionResponseDTO> subscriptions = subscriptionService.getStudentSubscriptions(studentId);
            return ResponseEntity.ok(subscriptions);
        } catch (Exception e) {
            logger.error("Error fetching student subscriptions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get active subscriptions for current student
     */
    @Operation(
        summary = "Get all active subscriptions",
        description = "Retrieve all active subscriptions for the authenticated student"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved active subscriptions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/active")
    public ResponseEntity<List<SubscriptionResponseDTO>> getMyActiveSubscriptions(Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            List<SubscriptionResponseDTO> subscriptions = subscriptionService.getActiveStudentSubscriptions(studentId);
            return ResponseEntity.ok(subscriptions);
        } catch (Exception e) {
            logger.error("Error fetching active subscriptions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get subscription by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponseDTO> getSubscriptionById(@PathVariable Long id, 
                                                                      Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            Optional<SubscriptionResponseDTO> subscription = subscriptionService.getSubscriptionById(id);
            
            if (subscription.isPresent()) {
                // Verify ownership
                if (!subscription.get().getStudentId().equals(studentId)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                return ResponseEntity.ok(subscription.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching subscription: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cancel subscription
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelSubscription(@PathVariable Long id, Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            boolean cancelled = subscriptionService.cancelSubscription(id, studentId);
            
            if (cancelled) {
                return ResponseEntity.ok(Map.of("message", "Subscription cancelled successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid cancel request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error cancelling subscription: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to cancel subscription"));
        }
    }

    /**
     * Check access to specific entity
     * Returns full subscription object if access is granted
     */
    @Operation(
        summary = "Check access to content",
        description = "Verify if the student has an active subscription for the specified content"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Access check completed"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/check-access")
    public ResponseEntity<?> checkAccess(
            @Parameter(
                description = "Type of entity/content. Valid values: MASTER_COURSE, MASTER_SUBJECT, MASTER_CHAPTER",
                example = "MASTER_COURSE"
            )
            @RequestParam String entityType,
            @Parameter(
                description = "ID of the entity/content",
                example = "1"
            )
            @RequestParam Long entityId,
            Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            
            com.coaxial.enums.SubscriptionLevel level = com.coaxial.enums.SubscriptionLevel.valueOf(entityType.toUpperCase());
            Optional<SubscriptionResponseDTO> subscriptionOpt = subscriptionService.getActiveSubscriptionForAccess(studentId, level, entityId);
            
            Map<String, Object> response = new HashMap<>();
            
            if (subscriptionOpt.isPresent()) {
                SubscriptionResponseDTO subscription = subscriptionOpt.get();
                response.put("hasAccess", true);
                response.put("subscription", subscription);
            } else {
                response.put("hasAccess", false);
                response.put("subscription", null);
                response.put("message", "No active subscription found for this content");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid access check request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error checking access", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to check access"));
        }
    }

    /**
     * Get subscriptions expiring soon (default 7 days)
     */
    @GetMapping("/expiring-soon")
    public ResponseEntity<List<SubscriptionResponseDTO>> getExpiringSoon(
            @RequestParam(defaultValue = "7") int days,
            Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            List<SubscriptionResponseDTO> subscriptions = subscriptionService.getSubscriptionsExpiringSoon(studentId, days);
            return ResponseEntity.ok(subscriptions);
        } catch (Exception e) {
            logger.error("Error fetching expiring subscriptions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Renew an existing subscription
     */
    @PostMapping(value = "/{id}/renew", produces = "application/json")
    public ResponseEntity<?> renewSubscription(@PathVariable Long id, Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            
            // Check if Razorpay is configured
            if (!razorpayService.isRazorpayConfigured()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(Map.of("error", "Payment service is not configured"));
            }

            RazorpayOrderDTO orderDTO = subscriptionService.renewSubscription(id, studentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("order", orderDTO);
            response.put("keyId", razorpayService.getRazorpayKeyId());
            response.put("message", "Renewal order created successfully");
            response.put("originalSubscriptionId", id);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid renewal request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error renewing subscription: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to renew subscription: " + e.getMessage()));
        }
    }

    /**
     * Get payment configuration for frontend
     */
    @GetMapping("/payment-config")
    public ResponseEntity<?> getPaymentConfig(Authentication authentication) {
        try {
            Map<String, Object> config = new HashMap<>();
            config.put("isConfigured", razorpayService.isRazorpayConfigured());
            config.put("keyId", razorpayService.getRazorpayKeyId());
            
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            logger.error("Error fetching payment config", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch payment configuration"));
        }
    }

    /**
     * Get current student ID from authentication
     */
    private Long getCurrentStudentId(Authentication authentication) {
        String username = authentication.getName();
        return userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"))
                .getId();
    }
}
