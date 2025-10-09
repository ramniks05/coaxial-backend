# Frontend Quick Start - Student Subscription API

## 🚨 BREAKING CHANGE - ACTION REQUIRED

### Critical Fix: Remove `subscriptionId` from Payment Verification

**Your verify-payment call MUST be updated immediately:**

#### ❌ OLD CODE (Will break):
```javascript
await api.post('/subscriptions/verify-payment', {
  razorpay_order_id: response.razorpay_order_id,
  razorpay_payment_id: response.razorpay_payment_id,
  razorpay_signature: response.razorpay_signature,
  subscriptionId: 123  // ❌ REMOVE THIS LINE!
});
```

#### ✅ NEW CODE (Required):
```javascript
await api.post('/subscriptions/verify-payment', {
  razorpay_order_id: response.razorpay_order_id,
  razorpay_payment_id: response.razorpay_payment_id,
  razorpay_signature: response.razorpay_signature
  // NO subscriptionId - backend creates it after verification
});

// Get subscription ID from response:
const result = await verifyPayment(razorpayResponse);
const subscriptionId = result.subscription.id;  // ← Here!
```

---

## Quick Integration Steps

### Step 1: Update Payment Verification (5 minutes)

Find this code in your project:
```javascript
// Search for: "verify-payment"
// Remove: subscriptionId field from request
```

**Before**:
```javascript
{
  razorpay_order_id,
  razorpay_payment_id,
  razorpay_signature,
  subscriptionId  // ← Delete this
}
```

**After**:
```javascript
{
  razorpay_order_id,
  razorpay_payment_id,
  razorpay_signature
}
```

---

### Step 2: Use New Response Fields (10 minutes)

**New fields available in subscription responses:**

```javascript
subscription.courseTypeName     // "Academic" | "Competitive" | "Professional"
subscription.courseName         // "CBSE" | "JEE Preparation"
subscription.status            // "ACTIVE" | "EXPIRED" | "CANCELLED" | "PENDING"
subscription.planType          // "MONTHLY" | "QUARTERLY" | "YEARLY"
subscription.expiryDate        // "2025-02-01T00:00:00"
subscription.remainingDays     // 23

// Pricing for all plans
subscription.monthlyPrice      // 1500.00
subscription.quarterlyPrice    // 4000.00
subscription.yearlyPrice       // 15000.00

// Razorpay fields (now snake_case)
subscription.razorpay_payment_id
subscription.razorpay_order_id
subscription.razorpay_receipt
```

---

### Step 3: Update TypeScript Interfaces (5 minutes)

```typescript
// Update your Subscription interface
interface Subscription {
  // ... existing fields ...
  
  // ADD THESE:
  courseTypeName: string;
  courseName: string;
  status: 'ACTIVE' | 'EXPIRED' | 'CANCELLED' | 'PENDING';
  planType: 'MONTHLY' | 'QUARTERLY' | 'YEARLY';
  expiryDate: string;
  monthlyPrice: number;
  quarterlyPrice: number;
  yearlyPrice: number;
  
  // UPDATE THESE (snake_case):
  razorpay_payment_id: string;  // was: razorpayPaymentId
  razorpay_order_id: string;    // was: razorpayOrderId
  razorpay_receipt: string;     // was: razorpayReceipt
}

// Update PaymentCallback
interface PaymentCallback {
  razorpay_order_id: string;
  razorpay_payment_id: string;
  razorpay_signature: string;
  // REMOVE: subscriptionId
}
```

---

## Complete Working Example

```javascript
// SubscriptionFlow.jsx
import React, { useState } from 'react';
import axios from 'axios';

const SubscriptionFlow = ({ entityType, entityId, entityName }) => {
  const [loading, setLoading] = useState(false);
  const [selectedPlan, setSelectedPlan] = useState('MONTHLY');
  
  const PRICING = {
    MONTHLY: { price: 1500, days: 30 },
    QUARTERLY: { price: 4000, days: 90 },
    YEARLY: { price: 15000, days: 365 }
  };
  
  const handlePurchase = async () => {
    setLoading(true);
    
    try {
      // Step 1: Create subscription
      const response = await axios.post('/api/student/subscriptions', {
        subscriptionLevel: entityType,
        entityId: entityId,
        amount: PRICING[selectedPlan].price,
        planType: selectedPlan,
        durationDays: PRICING[selectedPlan].days
      });
      
      const { order, keyId } = response.data;
      
      // Step 2: Open Razorpay
      const razorpay = new window.Razorpay({
        key: keyId,
        amount: order.amount,
        currency: order.currency,
        order_id: order.id,
        name: "Coaxial Learning",
        description: `Subscribe to ${entityName}`,
        handler: async (razorpayResponse) => {
          // Step 3: Verify payment
          await handlePaymentSuccess(razorpayResponse);
        },
        modal: {
          ondismiss: () => {
            setLoading(false);
          }
        }
      });
      
      razorpay.open();
      
    } catch (error) {
      setLoading(false);
      alert('Error: ' + error.message);
    }
  };
  
  const handlePaymentSuccess = async (razorpayResponse) => {
    try {
      // CRITICAL: NO subscriptionId in request!
      const result = await axios.post('/api/student/subscriptions/verify-payment', {
        razorpay_order_id: razorpayResponse.razorpay_order_id,
        razorpay_payment_id: razorpayResponse.razorpay_payment_id,
        razorpay_signature: razorpayResponse.razorpay_signature
      });
      
      if (result.data.success) {
        const subscription = result.data.subscription;
        alert(`Success! Subscription ID: ${subscription.id}`);
        
        // Redirect or update UI
        window.location.href = `/subscriptions/${subscription.id}`;
      }
      
    } catch (error) {
      alert('Payment verification failed: ' + error.message);
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <div>
      <h2>Subscribe to {entityName}</h2>
      
      <select value={selectedPlan} onChange={(e) => setSelectedPlan(e.target.value)}>
        <option value="MONTHLY">Monthly - ₹{PRICING.MONTHLY.price}</option>
        <option value="QUARTERLY">Quarterly - ₹{PRICING.QUARTERLY.price}</option>
        <option value="YEARLY">Yearly - ₹{PRICING.YEARLY.price}</option>
      </select>
      
      <button onClick={handlePurchase} disabled={loading}>
        {loading ? 'Processing...' : `Pay ₹${PRICING[selectedPlan].price}`}
      </button>
    </div>
  );
};

export default SubscriptionFlow;
```

---

## Testing Your Integration

### Test 1: Successful Payment
```javascript
// 1. Create subscription
// 2. Complete payment on Razorpay
// 3. Verify you receive subscription in response
// 4. Check subscription appears in my-subscriptions list
```

### Test 2: Failed Payment (IMPORTANT!)
```javascript
// 1. Create subscription
// 2. Close Razorpay modal (cancel payment)
// 3. Check my-subscriptions list
// 4. ✅ VERIFY: NO duplicate/pending subscription appears
// 5. Retry purchase - should work normally
```

### Test 3: Renewal
```javascript
// 1. Click renew on existing subscription
// 2. Complete payment
// 3. ✅ VERIFY: New subscription created
// 4. Original subscription still visible in history
```

---

## API Endpoints Quick Reference

```javascript
// Create subscription
POST /api/student/subscriptions
Body: { subscriptionLevel, entityId, amount, planType, durationDays }

// Verify payment (UPDATED - no subscriptionId!)
POST /api/student/subscriptions/verify-payment
Body: { razorpay_order_id, razorpay_payment_id, razorpay_signature }

// My subscriptions
GET /api/student/subscriptions/my-subscriptions?status=ACTIVE

// Get subscription
GET /api/student/subscriptions/{id}

// Cancel
PUT /api/student/subscriptions/{id}/cancel

// Check access
GET /api/student/subscriptions/check-access?entityType=CLASS&entityId=6

// Expiring soon
GET /api/student/subscriptions/expiring-soon?days=7

// Renew
POST /api/student/subscriptions/{id}/renew
```

---

## Need Help?

### Documentation Files
- 📖 **FRONTEND_INTEGRATION_GUIDE.md** - Complete integration guide
- 📖 **API_CHANGES_SUMMARY.md** - Detailed API changes

### Common Questions

**Q: Why remove subscriptionId from verify-payment?**  
A: Subscription is created AFTER payment verification, so ID doesn't exist yet.

**Q: Where do I get subscriptionId now?**  
A: From the verify-payment response: `result.subscription.id`

**Q: Will old code break?**  
A: Yes, if you send subscriptionId in verify-payment. Remove it!

**Q: How to prevent duplicates?**  
A: Backend handles this automatically with new payment flow.

---

## ✅ Ready to Integrate!

**Estimated Integration Time**: 20-30 minutes

1. Update verify-payment call (remove subscriptionId) - 5 min
2. Update TypeScript interfaces - 5 min
3. Test payment flow - 10 min
4. Deploy - 5 min

**Total: ~30 minutes** 🚀

