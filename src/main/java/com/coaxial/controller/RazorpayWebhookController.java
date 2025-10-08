package com.coaxial.controller;

import com.coaxial.dto.RazorpayWebhookDTO;
import com.coaxial.service.RazorpayPaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling Razorpay webhook notifications
 * This endpoint is public (no JWT authentication required)
 */
@RestController
@RequestMapping("/api/webhooks")
public class RazorpayWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(RazorpayWebhookController.class);

    @Autowired
    private RazorpayPaymentService razorpayService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Handle Razorpay webhook events
     * 
     * Razorpay sends webhook notifications for various payment events:
     * - payment.authorized - Payment authorized but not captured
     * - payment.captured - Payment captured successfully
     * - payment.failed - Payment failed
     * - order.paid - Order fully paid
     * 
     * Reference: https://razorpay.com/docs/webhooks/
     */
    @PostMapping("/razorpay")
    public ResponseEntity<?> handleWebhook(@RequestBody String payload,
                                          @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {
        
        logger.info("Received Razorpay webhook notification");
        
        try {
            // Verify webhook signature
            if (signature == null || signature.isEmpty()) {
                logger.warn("Webhook received without signature");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Missing signature"));
            }

            boolean isValid = razorpayService.verifyWebhookSignature(payload, signature);
            if (!isValid) {
                logger.warn("Invalid webhook signature");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid signature"));
            }

            // Parse webhook payload
            RazorpayWebhookDTO webhookDTO = objectMapper.readValue(payload, RazorpayWebhookDTO.class);
            String event = webhookDTO.getEvent();
            
            logger.info("Processing webhook event: {}", event);

            // Handle different webhook events
            switch (event) {
                case "payment.captured":
                    return handlePaymentCaptured(webhookDTO);
                    
                case "payment.authorized":
                    return handlePaymentAuthorized(webhookDTO);
                    
                case "payment.failed":
                    return handlePaymentFailed(webhookDTO);
                    
                case "order.paid":
                    return handleOrderPaid(webhookDTO);
                    
                default:
                    logger.info("Unhandled webhook event: {}", event);
                    return ResponseEntity.ok(Map.of("status", "event_received", "message", "Event not processed"));
            }

        } catch (Exception e) {
            logger.error("Error processing webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Webhook processing failed"));
        }
    }

    /**
     * Handle payment captured event
     */
    private ResponseEntity<?> handlePaymentCaptured(RazorpayWebhookDTO webhookDTO) {
        try {
            RazorpayWebhookDTO.PaymentEntity payment = webhookDTO.getPayload().getPayment();
            
            if (payment != null && payment.getOrder_id() != null) {
                String orderId = payment.getOrder_id();
                String paymentId = payment.getId();
                
                logger.info("Payment captured - Order ID: {}, Payment ID: {}", orderId, paymentId);
                
                boolean success = razorpayService.handlePaymentSuccess(orderId, paymentId);
                
                if (success) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("message", "Payment captured and subscription activated");
                    response.put("order_id", orderId);
                    response.put("payment_id", paymentId);
                    
                    return ResponseEntity.ok(response);
                } else {
                    logger.error("Failed to process payment success for order: {}", orderId);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "Failed to activate subscription"));
                }
            } else {
                logger.warn("Payment captured event missing required data");
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid payment data"));
            }
        } catch (Exception e) {
            logger.error("Error handling payment captured event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process payment captured event"));
        }
    }

    /**
     * Handle payment authorized event (auto-capture is enabled, so this is informational)
     */
    private ResponseEntity<?> handlePaymentAuthorized(RazorpayWebhookDTO webhookDTO) {
        try {
            RazorpayWebhookDTO.PaymentEntity payment = webhookDTO.getPayload().getPayment();
            
            if (payment != null) {
                logger.info("Payment authorized - Payment ID: {}, Order ID: {}", 
                        payment.getId(), payment.getOrder_id());
                
                // For auto-capture mode, we wait for payment.captured event
                // This is just for logging/monitoring
                return ResponseEntity.ok(Map.of(
                        "status", "acknowledged",
                        "message", "Payment authorized, waiting for capture"
                ));
            }
            
            return ResponseEntity.ok(Map.of("status", "acknowledged"));
        } catch (Exception e) {
            logger.error("Error handling payment authorized event", e);
            return ResponseEntity.ok(Map.of("status", "error", "message", "Failed to process event"));
        }
    }

    /**
     * Handle payment failed event
     */
    private ResponseEntity<?> handlePaymentFailed(RazorpayWebhookDTO webhookDTO) {
        try {
            RazorpayWebhookDTO.PaymentEntity payment = webhookDTO.getPayload().getPayment();
            
            if (payment != null && payment.getOrder_id() != null) {
                String orderId = payment.getOrder_id();
                String errorReason = payment.getError_description() != null ? 
                        payment.getError_description() : "Unknown error";
                
                logger.warn("Payment failed - Order ID: {}, Reason: {}", orderId, errorReason);
                
                boolean success = razorpayService.handlePaymentFailed(orderId, errorReason);
                
                if (success) {
                    return ResponseEntity.ok(Map.of(
                            "status", "success",
                            "message", "Payment failure recorded",
                            "order_id", orderId
                    ));
                } else {
                    logger.error("Failed to process payment failure for order: {}", orderId);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "Failed to record payment failure"));
                }
            } else {
                logger.warn("Payment failed event missing required data");
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid payment data"));
            }
        } catch (Exception e) {
            logger.error("Error handling payment failed event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process payment failed event"));
        }
    }

    /**
     * Handle order paid event
     */
    private ResponseEntity<?> handleOrderPaid(RazorpayWebhookDTO webhookDTO) {
        try {
            RazorpayWebhookDTO.OrderEntity order = webhookDTO.getPayload().getOrder();
            
            if (order != null) {
                String orderId = order.getId();
                
                logger.info("Order paid - Order ID: {}", orderId);
                
                // Order paid event confirms the order is fully paid
                // The subscription should already be activated via payment.captured event
                // This is mainly for confirmation and logging
                
                return ResponseEntity.ok(Map.of(
                        "status", "acknowledged",
                        "message", "Order paid event received",
                        "order_id", orderId
                ));
            }
            
            return ResponseEntity.ok(Map.of("status", "acknowledged"));
        } catch (Exception e) {
            logger.error("Error handling order paid event", e);
            return ResponseEntity.ok(Map.of("status", "error", "message", "Failed to process event"));
        }
    }

    /**
     * Health check endpoint for webhook
     */
    @GetMapping("/razorpay/health")
    public ResponseEntity<?> webhookHealth() {
        return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "service", "Razorpay Webhook Handler",
                "timestamp", System.currentTimeMillis()
        ));
    }
}

