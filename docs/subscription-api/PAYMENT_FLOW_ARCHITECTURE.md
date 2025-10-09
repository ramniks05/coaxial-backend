# Payment Flow Architecture - Preventing Duplicate Subscriptions

## Problem Solved

**Before**: When a subscription renewal payment failed, a duplicate subscription record was created in the database with FAILED status. Each retry created another duplicate.

**After**: Payment tracking is separated from subscription management. Subscriptions are ONLY created after successful payment verification. Failed payments leave NO subscription records.

---

## Architecture Overview

### Separation of Concerns

```
┌─────────────────────────────────────────────────────────────┐
│                    OLD FLOW (PROBLEMATIC)                    │
├─────────────────────────────────────────────────────────────┤
│  1. User clicks "Renew"                                      │
│  2. ❌ CREATE Subscription (PENDING)                         │
│  3. Create Razorpay order                                   │
│  4. User pays → Payment fails                               │
│  5. ❌ Subscription stays in DB with FAILED status          │
│  6. User retries → ❌ Another duplicate subscription created │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    NEW FLOW (FIXED)                          │
├─────────────────────────────────────────────────────────────┤
│  1. User clicks "Renew"                                      │
│  2. ✅ CREATE Payment record (PENDING)                       │
│  3. Create Razorpay order                                   │
│  4. User pays → Payment fails                               │
│  5. ✅ Update Payment to FAILED (NO subscription)           │
│  6. User retries → ✅ Same Payment record, new order        │
│  7. Payment succeeds → ✅ NOW create Subscription (ACTIVE)  │
└─────────────────────────────────────────────────────────────┘
```

---

## Database Schema

### New `payments` Table

```sql
CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_reference VARCHAR(100) UNIQUE,
    payment_type VARCHAR(20),  -- SUBSCRIPTION, RENEWAL, UPGRADE
    
    -- Links
    student_id BIGINT NOT NULL,
    subscription_id BIGINT,  -- NULL for new, populated for renewal
    
    -- What to create after payment
    subscription_level VARCHAR(20),
    entity_id BIGINT,
    entity_name VARCHAR(255),
    plan_type VARCHAR(20),
    duration_days INT,
    amount DECIMAL(10,2),
    
    -- Razorpay tracking
    razorpay_order_id VARCHAR(100) UNIQUE,
    razorpay_payment_id VARCHAR(100),
    razorpay_signature VARCHAR(255),
    
    -- Status
    payment_status VARCHAR(20),  -- PENDING, PAID, FAILED, CANCELLED
    payment_date TIMESTAMP,
    failure_reason TEXT,
    retry_count INT DEFAULT 0,
    
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Existing `student_subscriptions` Table

**No Changes Required** - Payment fields kept for backward compatibility (denormalized).
- Payment entity is source of truth
- Subscription payment fields populated after successful payment

---

## Flow Details

### 1. New Subscription Flow

```
POST /api/student/subscriptions
{
  "subscriptionLevel": "CLASS",
  "entityId": 6,
  "amount": 1500.00,
  "planType": "MONTHLY",
  "durationDays": 30
}

Step 1: CREATE Payment record
├─ payment_type: SUBSCRIPTION
├─ payment_status: PENDING
├─ subscription_id: NULL
└─ Store: level, entityId, amount, planType, duration

Step 2: CREATE Razorpay order
├─ Link to Payment.id
└─ Return order details to frontend

Step 3: User completes payment on Razorpay

Step 4: POST /verify-payment
├─ Verify signature
├─ Update Payment status → PAID
└─ ✅ CREATE StudentSubscription (ACTIVE)

If payment fails:
├─ Update Payment status → FAILED
├─ Store failure reason
└─ ❌ NO subscription created
```

### 2. Renewal Flow

```
POST /api/student/subscriptions/{id}/renew

Step 1: Get existing subscription (#123)

Step 2: CREATE Payment record
├─ payment_type: RENEWAL
├─ payment_status: PENDING  
├─ subscription_id: 123  // Link to original
└─ Copy: level, entityId, amount, planType, duration

Step 3: CREATE Razorpay order
└─ Return order details

Step 4: User completes payment

Step 5: POST /verify-payment
├─ Verify signature
├─ Update Payment status → PAID
└─ ✅ CREATE NEW StudentSubscription (ACTIVE)

If payment fails:
├─ Update Payment status → FAILED
├─ Original subscription (#123) intact
└─ ❌ NO new subscription created
```

### 3. Payment Failure & Retry

```
Attempt 1:
├─ Payment created (ID: 456)
├─ Razorpay order created
├─ Payment fails
├─ Payment.status → FAILED
├─ Payment.retry_count → 1
└─ NO subscription

Attempt 2 (Same Payment record):
├─ Use existing Payment (ID: 456)
├─ Create NEW Razorpay order
├─ Payment succeeds
├─ Payment.status → PAID
└─ ✅ CREATE Subscription

Result: Single Payment record, NO duplicates!
```

---

## Code Implementation

### New Entity: Payment.java

```java
@Entity
@Table(name = "payments")
public class Payment {
    private Long id;
    private String paymentReference;  // PAY-2025-001
    private PaymentType paymentType;  // SUBSCRIPTION, RENEWAL
    
    private User student;
    private StudentSubscription subscription;  // For renewal tracking
    
    // What to create after payment
    private SubscriptionLevel subscriptionLevel;
    private Long entityId;
    private String entityName;
    private PlanType planType;
    private Integer durationDays;
    private BigDecimal amount;
    
    // Razorpay
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
    
    // Status
    private PaymentStatus paymentStatus;  // PENDING, PAID, FAILED
    private String failureReason;
    private Integer retryCount;
}
```

### New Service: PaymentService.java

```java
@Service
public class PaymentService {
    
    // Create payment for new subscription
    public Payment createPaymentForSubscription(SubscriptionRequestDTO dto, Long studentId) {
        Payment payment = new Payment();
        payment.setPaymentType(PaymentType.SUBSCRIPTION);
        payment.setStudent(student);
        // ... set subscription details
        payment.setPaymentStatus(PaymentStatus.PENDING);
        return paymentRepository.save(payment);
    }
    
    // Create payment for renewal
    public Payment createPaymentForRenewal(StudentSubscription existing, Long studentId) {
        Payment payment = new Payment();
        payment.setPaymentType(PaymentType.RENEWAL);
        payment.setSubscription(existing);  // Link to original
        // ... copy subscription details
        return paymentRepository.save(payment);
    }
    
    // Mark payment successful
    public Payment markPaymentSuccess(String orderId, String paymentId, String signature) {
        Payment payment = findByRazorpayOrderId(orderId);
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setRazorpayPaymentId(paymentId);
        payment.setRazorpaySignature(signature);
        return paymentRepository.save(payment);
    }
    
    // Mark payment failed
    public Payment markPaymentFailed(String orderId, String reason) {
        Payment payment = findByRazorpayOrderId(orderId);
        payment.setPaymentStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);
        payment.setRetryCount(payment.getRetryCount() + 1);
        return paymentRepository.save(payment);
    }
}
```

### Updated: StudentSubscriptionService.java

```java
@Service
public class StudentSubscriptionService {
    
    @Autowired
    private PaymentService paymentService;
    
    // ❌ OLD: Created subscription immediately
    // ✅ NEW: Creates payment first
    public RazorpayOrderDTO createSubscription(SubscriptionRequestDTO dto, Long studentId) {
        // Create Payment record (NOT subscription)
        Payment payment = paymentService.createPaymentForSubscription(dto, studentId, entityName);
        
        // Create Razorpay order
        return razorpayService.createOrderForPayment(payment);
    }
    
    // ✅ NEW: Creates subscription ONLY after payment success
    public boolean verifyPaymentAndActivate(String orderId, String paymentId, String signature) {
        // Verify signature
        boolean isValid = razorpayService.verifyPaymentSignature(orderId, paymentId, signature);
        if (!isValid) return false;
        
        // Get payment record
        Payment payment = paymentService.getByRazorpayOrderId(orderId);
        
        // Mark payment successful
        paymentService.markPaymentSuccess(orderId, paymentId, signature);
        
        // NOW create subscription (only on success)
        StudentSubscription subscription = createSubscriptionFromPayment(payment);
        
        return true;
    }
    
    // Create subscription from successful payment
    private StudentSubscription createSubscriptionFromPayment(Payment payment) {
        StudentSubscription sub = new StudentSubscription();
        sub.setStudent(payment.getStudent());
        sub.setSubscriptionLevel(payment.getSubscriptionLevel());
        sub.setEntityId(payment.getEntityId());
        sub.setAmount(payment.getAmount());
        sub.setPlanType(payment.getPlanType());
        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setIsActive(true);
        // ... set other fields from payment
        return subscriptionRepository.save(sub);
    }
}
```

---

## API Changes

### Before

```json
POST /api/student/subscriptions/{id}/renew

Response: {
  "order": { "id": "order_xxx", ... },
  "subscriptionId": 123  // ❌ Already created!
}

// If payment fails → Duplicate subscription #123 in database
```

### After

```json
POST /api/student/subscriptions/{id}/renew

Response: {
  "payment": {
    "id": 456,
    "paymentReference": "PAY-2025-001",
    "paymentType": "RENEWAL",
    "subscriptionId": 123,  // Original being renewed
    "amount": 1500.00,
    "paymentStatus": "PENDING"
  },
  "order": {
    "id": "order_xxx",
    ...
  }
}

// If payment fails → Only Payment #456 marked FAILED
// NO duplicate subscription created ✅
```

---

## Benefits

| Aspect | Before | After |
|--------|--------|-------|
| **Failed Payment** | Creates duplicate subscription | Only Payment record updated |
| **Retry Payment** | Creates another duplicate | Updates same Payment record |
| **Audit Trail** | Mixed with subscriptions | Clear payment history |
| **Data Cleanup** | Hard to find failed attempts | Query payments by status |
| **Database** | Cluttered with failed records | Clean subscription table |
| **Refunds** | Confusing state | Clear payment lifecycle |

---

## Testing Scenarios

### 1. Successful New Subscription
```
✅ Payment created (PENDING)
✅ Razorpay order created  
✅ User pays successfully
✅ Payment updated (PAID)
✅ Subscription created (ACTIVE)
Result: 1 Payment, 1 Subscription
```

### 2. Failed New Subscription
```
✅ Payment created (PENDING)
✅ Razorpay order created
❌ Payment fails
✅ Payment updated (FAILED)
❌ NO subscription created
Result: 1 Payment (FAILED), 0 Subscriptions
```

### 3. Successful Renewal
```
Existing: Subscription #123
✅ Payment created (PENDING, subscription_id=123)
✅ Razorpay order created
✅ User pays successfully
✅ Payment updated (PAID)
✅ New Subscription #124 created (ACTIVE)
Result: Subscription #123 (old), #124 (new), 1 Payment
```

### 4. Failed Renewal - NO DUPLICATES!
```
Existing: Subscription #123
✅ Payment created (PENDING, subscription_id=123)
✅ Razorpay order created
❌ Payment fails
✅ Payment updated (FAILED)
❌ NO new subscription created
✅ Original Subscription #123 still active!
Result: Subscription #123 (intact), 1 Payment (FAILED)
```

### 5. Retry After Failure
```
✅ Payment #456 exists (FAILED, retry_count=1)
✅ Create NEW Razorpay order  
✅ User pays successfully
✅ Payment #456 updated (PAID)
✅ Subscription created
Result: 1 Payment, 1 Subscription (NO duplicates!)
```

---

## Migration Notes

### Running the Migration

```bash
# Migration will create payments table
V2025_01_12__create_payments_table.sql

# No changes to student_subscriptions table
# Backward compatible
```

### Backward Compatibility

- ✅ Existing subscriptions unaffected
- ✅ Payment fields kept in subscription table (denormalized)
- ✅ Old webhook handlers still work
- ✅ Gradual migration possible

### Deployment Steps

1. Deploy database migration
2. Deploy new code
3. Test with Razorpay test mode
4. Monitor payment flow
5. Verify no duplicates created

---

## Monitoring & Cleanup

### Find Abandoned Payments

```sql
-- Payments pending for > 24 hours
SELECT * FROM payments 
WHERE payment_status = 'PENDING' 
AND created_at < NOW() - INTERVAL 24 HOUR;
```

### Automatic Cleanup

```java
@Scheduled(cron = "0 0 2 * * ?")  // 2 AM daily
public void cleanupAbandonedPayments() {
    paymentService.cleanupAbandonedPayments();
}
```

### Payment History for Subscription

```java
List<PaymentResponseDTO> payments = paymentService.getSubscriptionPayments(subscriptionId);
// Shows all payment attempts including failures
```

---

## Summary

**Problem**: Duplicate subscriptions on payment failure  
**Root Cause**: Subscription created before payment success  
**Solution**: Separate Payment entity, create subscription ONLY after payment success  
**Result**: Zero duplicate subscriptions, clean audit trail, better user experience

✅ **No more duplicates on failed payments!**  
✅ **Clean database**  
✅ **Better audit trail**  
✅ **Easier to retry failed payments**  
✅ **Production-ready payment flow**

