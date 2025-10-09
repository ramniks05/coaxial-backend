# API Changes Summary - Payment Flow Update

## 🚨 **BREAKING CHANGES**

### Endpoint: POST /api/student/subscriptions/verify-payment

**What Changed**: Removed `subscriptionId` from request body

#### Before (OLD - Don't use)
```json
Request: {
  "razorpay_order_id": "order_xxx",
  "razorpay_payment_id": "pay_xxx",
  "razorpay_signature": "sig_xxx",
  "subscriptionId": 123  // ❌ REMOVED
}
```

#### After (NEW - Use this)
```json
Request: {
  "razorpay_order_id": "order_xxx",
  "razorpay_payment_id": "pay_xxx",
  "razorpay_signature": "sig_xxx"
  // NO subscriptionId field
}
```

**Response** (unchanged):
```json
{
  "success": true,
  "message": "Payment verified successfully",
  "subscription": {
    "id": 123,  // Get subscription ID from here
    "status": "ACTIVE",
    ...
  }
}
```

**Why Changed**: 
- Subscriptions are now created ONLY after payment verification
- Prevents duplicate subscriptions on payment failures
- subscriptionId doesn't exist when verification is called

---

## 🟡 **BEHAVIORAL CHANGES (No API contract change)**

### 1. POST /api/student/subscriptions (Create Subscription)

**Behavior Changed**:
- ❌ **Before**: Subscription created in database immediately
- ✅ **After**: Only Payment record created, subscription created after payment verification

**What This Means**:
- No subscriptionId available until payment is verified
- If payment fails, NO subscription record exists
- NO duplicate subscriptions on payment failure ✅

**Frontend Impact**:
```javascript
// Before: Subscription already exists
const { subscriptionId } = await createSubscription(data);
await verifyPayment(razorpayResponse, subscriptionId);

// After: Subscription doesn't exist yet
const { order } = await createSubscription(data);
const result = await verifyPayment(razorpayResponse);
const subscriptionId = result.subscription.id;  // Get from verify response
```

---

### 2. POST /api/student/subscriptions/{id}/renew (Renew Subscription)

**Behavior Changed**:
- ❌ **Before**: New subscription created immediately when renew called
- ✅ **After**: Only Payment record created, new subscription created after payment verification

**What This Means**:
- Original subscription remains active until new payment succeeds
- If renewal payment fails, original subscription intact
- NO duplicate subscription records ✅

**Frontend Impact**:
```javascript
// Before
const { newSubscriptionId } = await renewSubscription(123);  // ❌ No ID yet

// After  
const { order } = await renewSubscription(123);
// New subscription created after payment verification
const result = await verifyPayment(razorpayResponse);
const newSubscriptionId = result.subscription.id;  // Get from verify response
```

---

## ✅ **ENHANCED RESPONSES (New fields added)**

### All Subscription Endpoints

**New Fields Added**:
- `courseTypeName` - Course type name (e.g., "Academic")
- `courseName` - Course name (e.g., "CBSE")
- `status` - Subscription status enum
- `expiryDate` - Calculated expiry date
- `planType` - Plan type (MONTHLY/QUARTERLY/YEARLY)
- `monthlyPrice` - Monthly plan price
- `quarterlyPrice` - Quarterly plan price
- `yearlyPrice` - Yearly plan price

**Razorpay Fields** - Now use snake_case in JSON:
- `razorpay_payment_id` (was razorpayPaymentId)
- `razorpay_order_id` (was razorpayOrderId)
- `razorpay_receipt` (was razorpayReceipt)

**Example Response**:
```json
{
  "id": 1,
  "entityName": "Class 10",
  "courseTypeName": "Academic",      // ← NEW
  "courseName": "CBSE",              // ← NEW
  "status": "ACTIVE",                // ← NEW
  "planType": "MONTHLY",             // ← NEW
  "monthlyPrice": 1500.00,           // ← NEW
  "quarterlyPrice": 4000.00,         // ← NEW
  "yearlyPrice": 15000.00,           // ← NEW
  "expiryDate": "2025-02-01",        // ← NEW (was only endDate)
  "remainingDays": 23,
  "razorpay_payment_id": "pay_xxx",  // ← snake_case
  "razorpay_order_id": "order_xxx",  // ← snake_case
  ...
}
```

---

## 🟢 **UNCHANGED ENDPOINTS**

These endpoints work exactly as before:

- ✅ GET /api/student/subscriptions/my-subscriptions
- ✅ GET /api/student/subscriptions/{id}
- ✅ PUT /api/student/subscriptions/{id}/cancel
- ✅ GET /api/student/subscriptions/check-access
- ✅ GET /api/student/subscriptions/expiring-soon
- ✅ GET /api/student/subscriptions/active
- ✅ GET /api/student/subscriptions/payment-config

**Note**: Responses enhanced with new fields, but API contract unchanged.

---

## Migration Checklist for Frontend

### Step 1: Update PaymentCallbackDTO Usage
```javascript
// Find all verify-payment calls
// Remove subscriptionId from request body

// Before
verifyPayment(razorpayResponse, subscriptionId);

// After
verifyPayment(razorpayResponse);
```

### Step 2: Update TypeScript Interfaces
```typescript
// OLD
interface PaymentCallback {
  razorpay_order_id: string;
  razorpay_payment_id: string;
  razorpay_signature: string;
  subscriptionId: number;  // ❌ Remove
}

// NEW
interface PaymentCallback {
  razorpay_order_id: string;
  razorpay_payment_id: string;
  razorpay_signature: string;
  // No subscriptionId
}
```

### Step 3: Update Subscription Interface

```typescript
interface Subscription {
  // ... existing fields ...
  
  // Add new fields
  courseTypeName: string;
  courseName: string;
  status: 'PENDING' | 'ACTIVE' | 'EXPIRED' | 'CANCELLED';
  expiryDate: string;
  planType: 'MONTHLY' | 'QUARTERLY' | 'YEARLY';
  monthlyPrice: number;
  quarterlyPrice: number;
  yearlyPrice: number;
  
  // Update Razorpay field names to snake_case
  razorpay_payment_id: string;  // was razorpayPaymentId
  razorpay_order_id: string;    // was razorpayOrderId
  razorpay_receipt: string;     // was razorpayReceipt
}
```

### Step 4: Update State Management

```javascript
// Don't store subscriptionId before payment
// Get it from verify-payment response

const [pendingPayment, setPendingPayment] = useState(null);

// When creating subscription
const response = await createSubscription(data);
setPendingPayment(response.order.id);  // Store order ID, not subscription ID

// After verification
const result = await verifyPayment(razorpayResponse);
const subscriptionId = result.subscription.id;  // Now you have it
```

### Step 5: Test Thoroughly

- [ ] Create new subscription - success case
- [ ] Create new subscription - failure case (verify NO duplicate)
- [ ] Renew subscription - success case
- [ ] Renew subscription - failure case (verify original intact)
- [ ] Verify all subscription data displays correctly
- [ ] Verify pricing information shows correctly

---

## Quick Reference

| Action | Endpoint | Method | Body | Response Contains |
|--------|----------|--------|------|-------------------|
| Create Subscription | `/subscriptions` | POST | subscriptionLevel, entityId, amount, planType, durationDays | order, keyId |
| Verify Payment | `/subscriptions/verify-payment` | POST | razorpay_order_id, razorpay_payment_id, razorpay_signature | success, subscription |
| My Subscriptions | `/subscriptions/my-subscriptions` | GET | ?status=ACTIVE | Array of subscriptions |
| Get Subscription | `/subscriptions/{id}` | GET | - | Subscription object |
| Cancel | `/subscriptions/{id}/cancel` | PUT | - | message |
| Check Access | `/subscriptions/check-access` | GET | ?entityType=CLASS&entityId=6 | hasAccess, subscription |
| Expiring Soon | `/subscriptions/expiring-soon` | GET | ?days=7 | Array of subscriptions |
| Renew | `/subscriptions/{id}/renew` | POST | - | order, keyId |

---

## Key Takeaways

1. ✅ **Remove subscriptionId from verify-payment requests**
2. ✅ **Get subscription from verify-payment response**
3. ✅ **Subscriptions created ONLY after payment success**
4. ✅ **NO duplicate subscriptions on payment failure**
5. ✅ **Use snake_case for Razorpay fields**
6. ✅ **Pricing included in all subscription responses**

**Ready to integrate!** 🚀

