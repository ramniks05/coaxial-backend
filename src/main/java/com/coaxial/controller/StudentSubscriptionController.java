package com.coaxial.controller;

import com.coaxial.dto.PaymentCallbackDTO;
import com.coaxial.dto.RazorpayOrderDTO;
import com.coaxial.dto.SubscriptionRequestDTO;
import com.coaxial.dto.SubscriptionResponseDTO;
import com.coaxial.service.RazorpayPaymentService;
import com.coaxial.service.StudentSubscriptionService;
import com.coaxial.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/student/subscriptions")
@PreAuthorize("hasRole('STUDENT')")
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
    @PostMapping
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
     */
    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@Valid @RequestBody PaymentCallbackDTO callbackDTO, 
                                          Authentication authentication) {
        try {
            boolean isValid = subscriptionService.verifyPaymentAndActivate(
                    callbackDTO.getRazorpay_order_id(),
                    callbackDTO.getRazorpay_payment_id(),
                    callbackDTO.getRazorpay_signature()
            );

            if (isValid) {
                // Get updated subscription details
                Optional<SubscriptionResponseDTO> subscription = subscriptionService.getSubscriptionById(callbackDTO.getSubscriptionId());
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Payment verified successfully");
                response.put("subscription", subscription.orElse(null));
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Payment verification failed"
                ));
            }
            
        } catch (Exception e) {
            logger.error("Error verifying payment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Payment verification failed: " + e.getMessage()));
        }
    }

    /**
     * Get all subscriptions for current student
     */
    @GetMapping
    public ResponseEntity<List<SubscriptionResponseDTO>> getMySubscriptions(Authentication authentication) {
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
    @PostMapping("/{id}/cancel")
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
     */
    @GetMapping("/check-access")
    public ResponseEntity<?> checkAccess(@RequestParam String subscriptionLevel,
                                        @RequestParam Long entityId,
                                        Authentication authentication) {
        try {
            Long studentId = getCurrentStudentId(authentication);
            
            com.coaxial.enums.SubscriptionLevel level = com.coaxial.enums.SubscriptionLevel.valueOf(subscriptionLevel.toUpperCase());
            boolean hasAccess = subscriptionService.hasStudentAccess(studentId, level, entityId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("hasAccess", hasAccess);
            response.put("studentId", studentId);
            response.put("subscriptionLevel", level);
            response.put("entityId", entityId);
            
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
