package com.coaxial.service;

import com.coaxial.dto.RazorpayOrderDTO;
import com.coaxial.entity.StudentSubscription;
import com.coaxial.enums.PaymentStatus;
import com.coaxial.repository.StudentSubscriptionRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class RazorpayPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(RazorpayPaymentService.class);

    @Value("${razorpay.key.id:}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret:}")
    private String razorpayKeySecret;

    @Autowired
    private StudentSubscriptionRepository subscriptionRepository;

    private RazorpayClient razorpayClient;

    public void initRazorpayClient() throws RazorpayException {
        if (razorpayClient == null && !razorpayKeyId.isEmpty() && !razorpayKeySecret.isEmpty()) {
            this.razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        }
    }

    /**
     * Create a Razorpay order for subscription payment
     */
    public RazorpayOrderDTO createOrder(StudentSubscription subscription) throws RazorpayException {
        try {
            initRazorpayClient();
            
            if (razorpayClient == null) {
                throw new RazorpayException("Razorpay client not initialized. Please check configuration.");
            }

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", subscription.getAmount().multiply(java.math.BigDecimal.valueOf(100)).longValue()); // Convert to paise
            orderRequest.put("currency", subscription.getCurrency());
            orderRequest.put("receipt", "sub_" + subscription.getId() + "_" + System.currentTimeMillis());
            orderRequest.put("notes", new JSONObject()
                .put("subscription_id", subscription.getId().toString())
                .put("student_id", subscription.getStudent().getId().toString())
                .put("entity_name", subscription.getEntityName())
                .put("subscription_level", subscription.getSubscriptionLevel().name())
            );

            Order order = razorpayClient.orders.create(orderRequest);
            
            // Update subscription with order ID
            subscription.setRazorpayOrderId(order.get("id"));
            subscription.setRazorpayReceipt(order.get("receipt"));
            subscriptionRepository.save(subscription);

            // Convert to DTO
            RazorpayOrderDTO orderDTO = new RazorpayOrderDTO();
            orderDTO.setId(order.get("id").toString());
            orderDTO.setEntity(order.get("entity").toString());
            orderDTO.setAmount(Long.valueOf(order.get("amount").toString()));
            orderDTO.setAmount_paid(order.get("amount_paid").toString());
            orderDTO.setAmount_due(order.get("amount_due").toString());
            orderDTO.setCurrency(order.get("currency").toString());
            orderDTO.setReceipt(order.get("receipt").toString());
            orderDTO.setStatus(order.get("status").toString());
            orderDTO.setAttempts(Long.valueOf(order.get("attempts").toString()));
            orderDTO.setCreated_at(Long.valueOf(order.get("created_at").toString()));
            
            if (order.has("notes")) {
                orderDTO.setNotes(order.get("notes").toString());
            }

            logger.info("Razorpay order created successfully: {} for subscription: {}", order.get("id"), subscription.getId());
            return orderDTO;

        } catch (RazorpayException e) {
            logger.error("Error creating Razorpay order for subscription: {}", subscription.getId(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error creating Razorpay order for subscription: {}", subscription.getId(), e);
            throw new RazorpayException("Failed to create order: " + e.getMessage());
        }
    }

    /**
     * Verify payment signature and update subscription
     */
    public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        try {
            String generatedSignature = generateSignature(razorpayOrderId + "|" + razorpayPaymentId);
            boolean isValid = generatedSignature.equals(razorpaySignature);
            
            if (isValid) {
                // Update subscription with payment details
                Optional<StudentSubscription> subscriptionOpt = subscriptionRepository.findByRazorpayOrderId(razorpayOrderId);
                if (subscriptionOpt.isPresent()) {
                    StudentSubscription subscription = subscriptionOpt.get();
                    subscription.setRazorpayPaymentId(razorpayPaymentId);
                    subscription.setRazorpaySignature(razorpaySignature);
                    subscription.setPaymentStatus(PaymentStatus.PAID);
                    subscription.setPaymentDate(LocalDateTime.now());
                    subscription.setIsActive(true);
                    subscriptionRepository.save(subscription);
                    
                    logger.info("Payment verified and subscription activated for order: {}", razorpayOrderId);
                    return true;
                } else {
                    logger.error("Subscription not found for order ID: {}", razorpayOrderId);
                    return false;
                }
            } else {
                logger.warn("Payment signature verification failed for order: {}", razorpayOrderId);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error verifying payment for order: {}", razorpayOrderId, e);
            return false;
        }
    }

    /**
     * Generate Razorpay signature
     */
    private String generateSignature(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            logger.error("Error generating Razorpay signature", e);
            throw new RuntimeException("Failed to generate signature", e);
        }
    }

    /**
     * Get order details from Razorpay
     */
    public RazorpayOrderDTO getOrder(String orderId) throws RazorpayException {
        try {
            initRazorpayClient();
            
            if (razorpayClient == null) {
                throw new RazorpayException("Razorpay client not initialized");
            }

            Order order = razorpayClient.orders.fetch(orderId);
            
            RazorpayOrderDTO orderDTO = new RazorpayOrderDTO();
            orderDTO.setId(order.get("id").toString());
            orderDTO.setEntity(order.get("entity").toString());
            orderDTO.setAmount(Long.valueOf(order.get("amount").toString()));
            orderDTO.setAmount_paid(order.get("amount_paid").toString());
            orderDTO.setAmount_due(order.get("amount_due").toString());
            orderDTO.setCurrency(order.get("currency").toString());
            orderDTO.setReceipt(order.get("receipt").toString());
            orderDTO.setStatus(order.get("status").toString());
            orderDTO.setAttempts(Long.valueOf(order.get("attempts").toString()));
            orderDTO.setCreated_at(Long.valueOf(order.get("created_at").toString()));
            
            if (order.has("notes")) {
                orderDTO.setNotes(order.get("notes").toString());
            }

            return orderDTO;
        } catch (RazorpayException e) {
            logger.error("Error fetching order: {}", orderId, e);
            throw e;
        }
    }

    /**
     * Cancel order and update subscription
     */
    public boolean cancelOrder(String orderId) {
        try {
            Optional<StudentSubscription> subscriptionOpt = subscriptionRepository.findByRazorpayOrderId(orderId);
            if (subscriptionOpt.isPresent()) {
                StudentSubscription subscription = subscriptionOpt.get();
                subscription.setPaymentStatus(PaymentStatus.CANCELLED);
                subscription.setIsActive(false);
                subscriptionRepository.save(subscription);
                
                logger.info("Order cancelled for subscription: {}", subscription.getId());
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Error cancelling order: {}", orderId, e);
            return false;
        }
    }

    /**
     * Check if Razorpay is configured
     */
    public boolean isRazorpayConfigured() {
        return !razorpayKeyId.isEmpty() && !razorpayKeySecret.isEmpty();
    }

    /**
     * Get Razorpay key ID for frontend
     */
    public String getRazorpayKeyId() {
        return razorpayKeyId;
    }
}
