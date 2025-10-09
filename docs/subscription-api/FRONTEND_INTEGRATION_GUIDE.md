# Frontend Integration Guide - Student Subscription API

## Overview

This guide covers how to integrate with the updated Student Subscription Management API that includes:
- ✅ Separate Payment tracking (prevents duplicate subscriptions)
- ✅ Subscription created ONLY after payment success
- ✅ Complete pricing information in responses
- ✅ Snake_case for Razorpay fields

---

## 🚨 **CRITICAL BREAKING CHANGES**

### 1. Payment Verification Flow Changed

**OLD (Don't use)**:
```javascript
// ❌ This no longer works!
const verifyPayment = async (razorpayResponse) => {
  await api.post('/subscriptions/verify-payment', {
    razorpay_order_id: razorpayResponse.razorpay_order_id,
    razorpay_payment_id: razorpayResponse.razorpay_payment_id,
    razorpay_signature: razorpayResponse.razorpay_signature,
    subscriptionId: 123  // ❌ REMOVE THIS - doesn't exist yet!
  });
};
```

**NEW (Correct)**:
```javascript
// ✅ Correct - subscriptionId NOT needed
const verifyPayment = async (razorpayResponse) => {
  const response = await api.post('/subscriptions/verify-payment', {
    razorpay_order_id: razorpayResponse.razorpay_order_id,
    razorpay_payment_id: razorpayResponse.razorpay_payment_id,
    razorpay_signature: razorpayResponse.razorpay_signature
    // NO subscriptionId - backend creates it after verification
  });
  
  // Subscription is in the response
  const subscription = response.data.subscription;
  console.log('Subscription created:', subscription.id);
};
```

---

## API Base URL

```
BASE_URL: /api/student/subscriptions
```

All endpoints require authentication with `ROLE_STUDENT`.

---

## API Endpoints

### 1. Create New Subscription

**Endpoint**: `POST /api/student/subscriptions`

**Request**:
```javascript
const createSubscription = async (subscriptionData) => {
  const response = await api.post('/api/student/subscriptions', {
    subscriptionLevel: "CLASS",      // "CLASS" | "EXAM" | "COURSE"
    entityId: 6,
    amount: 1500.00,
    planType: "MONTHLY",             // "MONTHLY" | "QUARTERLY" | "YEARLY"
    durationDays: 30,
    notes: "Optional notes"
  });
  
  return response.data;
};
```

**Response**:
```json
{
  "order": {
    "id": "order_xxx",
    "entity": "order",
    "amount": 150000,
    "currency": "INR",
    "receipt": "pay_1_xxx",
    "status": "created"
  },
  "keyId": "rzp_test_xxx",
  "subscriptionLevel": "CLASS",
  "entityId": 6,
  "amount": 1500.00
}
```

**Important Notes**:
- ⚠️ **NO subscriptionId in response** - subscription NOT created yet
- ✅ Payment record created in backend
- ✅ Subscription will be created ONLY after payment verification
- Save `order.id` to verify payment later

---

### 2. Initialize Razorpay Payment

**Frontend Code**:
```javascript
const initiatePayment = async (subscriptionData) => {
  try {
    // Step 1: Create subscription (creates payment record)
    const response = await createSubscription(subscriptionData);
    
    const { order, keyId } = response;
    
    // Step 2: Initialize Razorpay
    const options = {
      key: keyId,
      amount: order.amount,
      currency: order.currency,
      name: "Coaxial Learning",
      description: `Subscription to ${subscriptionData.entityName}`,
      order_id: order.id,
      handler: function (razorpayResponse) {
        // Step 3: Verify payment
        verifyPayment(razorpayResponse);
      },
      prefill: {
        name: user.name,
        email: user.email,
        contact: user.phone
      },
      theme: {
        color: "#3399cc"
      }
    };
    
    const razorpay = new window.Razorpay(options);
    razorpay.open();
    
  } catch (error) {
    console.error('Error initiating payment:', error);
    alert('Failed to create subscription: ' + error.message);
  }
};
```

---

### 3. Verify Payment (UPDATED)

**Endpoint**: `POST /api/student/subscriptions/verify-payment`

**Request** (UPDATED - NO subscriptionId):
```javascript
const verifyPayment = async (razorpayResponse) => {
  try {
    const response = await api.post('/api/student/subscriptions/verify-payment', {
      razorpay_order_id: razorpayResponse.razorpay_order_id,
      razorpay_payment_id: razorpayResponse.razorpay_payment_id,
      razorpay_signature: razorpayResponse.razorpay_signature
      // ⚠️ NO subscriptionId - backend will create it!
    });
    
    if (response.data.success) {
      const subscription = response.data.subscription;
      console.log('✅ Subscription created:', subscription);
      
      // Navigate to success page
      router.push(`/subscriptions/${subscription.id}`);
      
      // Or show success message
      toast.success('Subscription activated successfully!');
    }
    
  } catch (error) {
    console.error('Payment verification failed:', error);
    alert('Payment verification failed. Please contact support.');
  }
};
```

**Response**:
```json
{
  "success": true,
  "message": "Payment verified successfully",
  "subscription": {
    "id": 123,
    "studentId": 456,
    "subscriptionLevel": "CLASS",
    "entityId": 6,
    "entityName": "Class 10",
    "courseTypeName": "Academic",
    "courseName": "CBSE",
    "status": "ACTIVE",
    "amount": 1500.00,
    "planType": "MONTHLY",
    "monthlyPrice": 1500.00,
    "quarterlyPrice": 4000.00,
    "yearlyPrice": 15000.00,
    "expiryDate": "2025-02-01T00:00:00",
    "remainingDays": 30,
    "razorpay_payment_id": "pay_xxx",
    "razorpay_order_id": "order_xxx",
    "paymentStatus": "PAID",
    "isExpired": false
  }
}
```

---

### 4. Get My Subscriptions

**Endpoint**: `GET /api/student/subscriptions/my-subscriptions`

**Request**:
```javascript
const getMySubscriptions = async (status = null) => {
  const params = {};
  if (status) {
    params.status = status; // "ACTIVE" | "EXPIRED" | "CANCELLED" | "PENDING"
  }
  
  const response = await api.get('/api/student/subscriptions/my-subscriptions', { params });
  return response.data;
};

// Examples
const allSubscriptions = await getMySubscriptions();
const activeOnly = await getMySubscriptions('ACTIVE');
const expiredOnly = await getMySubscriptions('EXPIRED');
```

**Response**:
```json
[
  {
    "id": 1,
    "subscriptionLevel": "CLASS",
    "entityName": "Class 10",
    "courseTypeName": "Academic",
    "courseName": "CBSE",
    "status": "ACTIVE",
    "amount": 1500.00,
    "planType": "MONTHLY",
    "monthlyPrice": 1500.00,
    "quarterlyPrice": 4000.00,
    "yearlyPrice": 15000.00,
    "expiryDate": "2025-02-01T00:00:00",
    "remainingDays": 23,
    "razorpay_payment_id": "pay_xxx",
    "razorpay_order_id": "order_xxx",
    "isExpired": false
  }
]
```

---

### 5. Get Single Subscription

**Endpoint**: `GET /api/student/subscriptions/{id}`

```javascript
const getSubscription = async (subscriptionId) => {
  const response = await api.get(`/api/student/subscriptions/${subscriptionId}`);
  return response.data;
};
```

---

### 6. Cancel Subscription

**Endpoint**: `PUT /api/student/subscriptions/{id}/cancel`

```javascript
const cancelSubscription = async (subscriptionId) => {
  const response = await api.put(`/api/student/subscriptions/${subscriptionId}/cancel`);
  return response.data;
};

// Usage
await cancelSubscription(123);
// Response: { "message": "Subscription cancelled successfully" }
```

---

### 7. Check Content Access

**Endpoint**: `GET /api/student/subscriptions/check-access`

```javascript
const checkAccess = async (entityType, entityId) => {
  const response = await api.get('/api/student/subscriptions/check-access', {
    params: {
      entityType: entityType,  // "CLASS" | "EXAM" | "COURSE"
      entityId: entityId
    }
  });
  
  return response.data;
};

// Usage
const access = await checkAccess('CLASS', 6);

if (access.hasAccess) {
  console.log('User has access!');
  console.log('Subscription details:', access.subscription);
  console.log('Days remaining:', access.subscription.remainingDays);
} else {
  console.log('No access - show paywall');
}
```

**Response with Access**:
```json
{
  "hasAccess": true,
  "subscription": {
    "id": 1,
    "status": "ACTIVE",
    "expiryDate": "2025-02-01T00:00:00",
    "remainingDays": 23,
    "planType": "MONTHLY",
    ...full subscription object...
  }
}
```

**Response without Access**:
```json
{
  "hasAccess": false,
  "subscription": null,
  "message": "No active subscription found for this content"
}
```

---

### 8. Get Expiring Soon

**Endpoint**: `GET /api/student/subscriptions/expiring-soon`

```javascript
const getExpiringSoon = async (days = 7) => {
  const response = await api.get('/api/student/subscriptions/expiring-soon', {
    params: { days }
  });
  return response.data;
};

// Usage
const expiring = await getExpiringSoon(7);  // Next 7 days
const expiring30 = await getExpiringSoon(30);  // Next 30 days
```

**Response**: Array of subscriptions expiring within specified days

---

### 9. Renew Subscription

**Endpoint**: `POST /api/student/subscriptions/{id}/renew`

```javascript
const renewSubscription = async (subscriptionId) => {
  try {
    // Step 1: Create renewal payment
    const response = await api.post(`/api/student/subscriptions/${subscriptionId}/renew`);
    
    const { order, keyId, originalSubscriptionId } = response.data;
    
    // Step 2: Initialize Razorpay payment
    const options = {
      key: keyId,
      amount: order.amount,
      currency: order.currency,
      name: "Coaxial Learning",
      description: "Subscription Renewal",
      order_id: order.id,
      handler: function (razorpayResponse) {
        // Step 3: Verify payment (creates new subscription)
        verifyPayment(razorpayResponse);
      },
      prefill: {
        name: user.name,
        email: user.email
      }
    };
    
    const razorpay = new window.Razorpay(options);
    razorpay.open();
    
  } catch (error) {
    console.error('Renewal failed:', error);
  }
};
```

**Response**:
```json
{
  "order": {
    "id": "order_yyy",
    "amount": 150000,
    "currency": "INR"
  },
  "keyId": "rzp_test_xxx",
  "message": "Renewal order created successfully",
  "originalSubscriptionId": 123
}
```

**Important**: 
- ⚠️ New subscription NOT created yet
- ✅ Only payment record created
- ✅ If payment fails, original subscription remains intact
- ✅ NO duplicate subscriptions!

---

## Complete Integration Examples

### Example 1: New Subscription Flow

```javascript
import React, { useState } from 'react';
import axios from 'axios';

const SubscriptionPurchase = ({ entityType, entityId, entityName }) => {
  const [loading, setLoading] = useState(false);
  const [selectedPlan, setSelectedPlan] = useState('MONTHLY');
  
  const pricing = {
    MONTHLY: 1500,
    QUARTERLY: 4000,
    YEARLY: 15000
  };
  
  const durationDays = {
    MONTHLY: 30,
    QUARTERLY: 90,
    YEARLY: 365
  };
  
  const handleSubscribe = async () => {
    setLoading(true);
    
    try {
      // Step 1: Create subscription (creates payment record)
      const response = await axios.post('/api/student/subscriptions', {
        subscriptionLevel: entityType,
        entityId: entityId,
        amount: pricing[selectedPlan],
        planType: selectedPlan,
        durationDays: durationDays[selectedPlan],
        notes: `Subscription to ${entityName}`
      });
      
      const { order, keyId } = response.data;
      
      // Step 2: Open Razorpay payment
      const options = {
        key: keyId,
        amount: order.amount,
        currency: order.currency,
        name: "Coaxial Learning",
        description: `Subscribe to ${entityName}`,
        order_id: order.id,
        handler: async function (razorpayResponse) {
          // Step 3: Verify payment - subscription created here!
          await verifyPayment(razorpayResponse);
        },
        modal: {
          ondismiss: function() {
            setLoading(false);
            // Payment cancelled - no subscription created
          }
        }
      };
      
      const razorpay = new window.Razorpay(options);
      razorpay.open();
      
    } catch (error) {
      setLoading(false);
      alert('Failed to create subscription: ' + error.message);
    }
  };
  
  const verifyPayment = async (razorpayResponse) => {
    try {
      const response = await axios.post('/api/student/subscriptions/verify-payment', {
        razorpay_order_id: razorpayResponse.razorpay_order_id,
        razorpay_payment_id: razorpayResponse.razorpay_payment_id,
        razorpay_signature: razorpayResponse.razorpay_signature
        // NO subscriptionId needed!
      });
      
      if (response.data.success) {
        const subscription = response.data.subscription;
        alert(`Subscription activated! Expires on ${subscription.expiryDate}`);
        // Redirect to subscription page
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
        <option value="MONTHLY">Monthly - ₹{pricing.MONTHLY}</option>
        <option value="QUARTERLY">Quarterly - ₹{pricing.QUARTERLY}</option>
        <option value="YEARLY">Yearly - ₹{pricing.YEARLY}</option>
      </select>
      
      <button onClick={handleSubscribe} disabled={loading}>
        {loading ? 'Processing...' : `Subscribe for ₹${pricing[selectedPlan]}`}
      </button>
    </div>
  );
};

export default SubscriptionPurchase;
```

---

### Example 2: Subscription List with Pricing

```javascript
import React, { useEffect, useState } from 'react';
import axios from 'axios';

const MySubscriptions = () => {
  const [subscriptions, setSubscriptions] = useState([]);
  const [filter, setFilter] = useState(null);
  
  useEffect(() => {
    loadSubscriptions();
  }, [filter]);
  
  const loadSubscriptions = async () => {
    const params = filter ? { status: filter } : {};
    const response = await axios.get('/api/student/subscriptions/my-subscriptions', { params });
    setSubscriptions(response.data);
  };
  
  const renderSubscription = (sub) => (
    <div key={sub.id} className="subscription-card">
      <h3>{sub.entityName}</h3>
      <p>{sub.courseTypeName} - {sub.courseName}</p>
      
      <div className="current-plan">
        <span className="badge">{sub.status}</span>
        <span className="plan-type">{sub.planType} Plan</span>
        <span className="price">₹{sub.amount}</span>
      </div>
      
      {sub.status === 'ACTIVE' && (
        <div className="expiry-info">
          <p>Expires: {new Date(sub.expiryDate).toLocaleDateString()}</p>
          <p className={sub.remainingDays <= 7 ? 'warning' : ''}>
            {sub.remainingDays} days remaining
          </p>
        </div>
      )}
      
      {/* Show other plan options using pricing from response */}
      {sub.status === 'ACTIVE' && sub.planType !== 'YEARLY' && (
        <div className="upgrade-options">
          <h4>Upgrade and Save</h4>
          {sub.planType === 'MONTHLY' && (
            <>
              <p>Quarterly: ₹{sub.quarterlyPrice} 
                 (Save ₹{sub.monthlyPrice * 3 - sub.quarterlyPrice})
              </p>
              <p>Yearly: ₹{sub.yearlyPrice} 
                 (Save ₹{sub.monthlyPrice * 12 - sub.yearlyPrice})
              </p>
            </>
          )}
        </div>
      )}
      
      {(sub.status === 'EXPIRED' || sub.remainingDays <= 7) && (
        <button onClick={() => handleRenew(sub.id)}>
          Renew Subscription
        </button>
      )}
    </div>
  );
  
  return (
    <div>
      <h2>My Subscriptions</h2>
      
      <div className="filters">
        <button onClick={() => setFilter(null)}>All</button>
        <button onClick={() => setFilter('ACTIVE')}>Active</button>
        <button onClick={() => setFilter('EXPIRED')}>Expired</button>
        <button onClick={() => setFilter('CANCELLED')}>Cancelled</button>
      </div>
      
      <div className="subscription-list">
        {subscriptions.map(renderSubscription)}
      </div>
    </div>
  );
};
```

---

### Example 3: Renewal Flow

```javascript
const handleRenew = async (subscriptionId) => {
  try {
    setRenewing(true);
    
    // Step 1: Create renewal payment
    const response = await axios.post(`/api/student/subscriptions/${subscriptionId}/renew`);
    
    const { order, keyId, originalSubscriptionId } = response.data;
    
    console.log('Renewing subscription:', originalSubscriptionId);
    // ⚠️ NO new subscription created yet - only payment record!
    
    // Step 2: Open Razorpay
    const options = {
      key: keyId,
      amount: order.amount,
      currency: order.currency,
      name: "Coaxial Learning",
      description: "Subscription Renewal",
      order_id: order.id,
      handler: async function (razorpayResponse) {
        // Step 3: Verify payment - NEW subscription created here!
        await verifyRenewal(razorpayResponse);
      },
      modal: {
        ondismiss: function() {
          setRenewing(false);
          // Payment cancelled - original subscription still active!
        }
      }
    };
    
    const razorpay = new window.Razorpay(options);
    razorpay.open();
    
  } catch (error) {
    setRenewing(false);
    alert('Renewal failed: ' + error.message);
  }
};

const verifyRenewal = async (razorpayResponse) => {
  try {
    const response = await axios.post('/api/student/subscriptions/verify-payment', {
      razorpay_order_id: razorpayResponse.razorpay_order_id,
      razorpay_payment_id: razorpayResponse.razorpay_payment_id,
      razorpay_signature: razorpayResponse.razorpay_signature
      // NO subscriptionId!
    });
    
    if (response.data.success) {
      const newSubscription = response.data.subscription;
      alert('Subscription renewed successfully!');
      console.log('New subscription ID:', newSubscription.id);
      
      // Refresh subscription list
      loadSubscriptions();
    }
    
  } catch (error) {
    alert('Payment verification failed: ' + error.message);
  } finally {
    setRenewing(false);
  }
};
```

---

### Example 4: Check Access Before Showing Content

```javascript
const ProtectedContent = ({ entityType, entityId, children }) => {
  const [access, setAccess] = useState(null);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    checkContentAccess();
  }, [entityType, entityId]);
  
  const checkContentAccess = async () => {
    try {
      const response = await axios.get('/api/student/subscriptions/check-access', {
        params: { entityType, entityId }
      });
      
      setAccess(response.data);
      setLoading(false);
    } catch (error) {
      console.error('Access check failed:', error);
      setLoading(false);
    }
  };
  
  if (loading) return <div>Loading...</div>;
  
  if (!access.hasAccess) {
    return (
      <div className="paywall">
        <h2>Subscribe to Access This Content</h2>
        <p>{access.message}</p>
        <button onClick={() => navigate('/subscribe')}>
          View Subscription Plans
        </button>
      </div>
    );
  }
  
  return (
    <div>
      <div className="subscription-banner">
        ✅ Access granted via {access.subscription.planType} subscription
        ({access.subscription.remainingDays} days remaining)
      </div>
      {children}
    </div>
  );
};
```

---

### Example 5: Expiring Soon Notifications

```javascript
const ExpiringNotifications = () => {
  const [expiring, setExpiring] = useState([]);
  
  useEffect(() => {
    loadExpiringSoon();
  }, []);
  
  const loadExpiringSoon = async () => {
    const response = await axios.get('/api/student/subscriptions/expiring-soon', {
      params: { days: 7 }
    });
    setExpiring(response.data);
  };
  
  if (expiring.length === 0) return null;
  
  return (
    <div className="notifications">
      <h3>⚠️ Subscriptions Expiring Soon</h3>
      {expiring.map(sub => (
        <div key={sub.id} className="notification-item">
          <p>{sub.entityName} - Expires in {sub.remainingDays} days</p>
          <button onClick={() => handleRenew(sub.id)}>
            Renew Now
          </button>
        </div>
      ))}
    </div>
  );
};
```

---

## TypeScript Interfaces

```typescript
// Enums
export type SubscriptionLevel = 'CLASS' | 'EXAM' | 'COURSE';
export type SubscriptionStatus = 'PENDING' | 'ACTIVE' | 'EXPIRED' | 'CANCELLED';
export type PlanType = 'MONTHLY' | 'QUARTERLY' | 'YEARLY';
export type PaymentStatus = 'PENDING' | 'PAID' | 'FAILED' | 'CANCELLED';

// Subscription Response
export interface Subscription {
  id: number;
  studentId: number;
  studentName: string;
  studentEmail: string;
  subscriptionLevel: SubscriptionLevel;
  entityId: number;
  entityName: string;
  courseTypeName: string;
  courseName: string;
  status: SubscriptionStatus;
  amount: number;
  currency: string;
  isActive: boolean;
  
  // Dates
  startDate: string;
  endDate: string;
  expiryDate: string;
  
  // Plan info
  durationDays: number;
  planType: PlanType;
  remainingDays: number;
  isExpired: boolean;
  
  // Pricing (available plans)
  monthlyPrice: number;
  quarterlyPrice: number;
  yearlyPrice: number;
  
  // Payment info (snake_case!)
  razorpay_order_id: string;
  razorpay_payment_id: string;
  razorpay_receipt: string;
  paymentStatus: PaymentStatus;
  paymentDate: string;
  
  // Metadata
  notes: string;
  createdAt: string;
  updatedAt: string;
}

// Subscription Request
export interface SubscriptionRequest {
  subscriptionLevel: SubscriptionLevel;
  entityId: number;
  amount: number;
  planType: PlanType;
  durationDays: number;
  notes?: string;
}

// Payment Callback (UPDATED - NO subscriptionId)
export interface PaymentCallback {
  razorpay_order_id: string;
  razorpay_payment_id: string;
  razorpay_signature: string;
  // ⚠️ NO subscriptionId property!
}

// Razorpay Order
export interface RazorpayOrder {
  id: string;
  entity: string;
  amount: number;
  currency: string;
  receipt: string;
  status: string;
}

// API Responses
export interface CreateSubscriptionResponse {
  order: RazorpayOrder;
  keyId: string;
  subscriptionLevel: SubscriptionLevel;
  entityId: number;
  amount: number;
}

export interface VerifyPaymentResponse {
  success: boolean;
  message: string;
  subscription: Subscription | null;
}

export interface CheckAccessResponse {
  hasAccess: boolean;
  subscription: Subscription | null;
  message?: string;
}
```

---

## API Service (TypeScript)

```typescript
import axios from 'axios';

const API_BASE = '/api/student/subscriptions';

export const subscriptionAPI = {
  // Create new subscription
  create: async (data: SubscriptionRequest): Promise<CreateSubscriptionResponse> => {
    const response = await axios.post(API_BASE, data);
    return response.data;
  },
  
  // Verify payment (UPDATED - no subscriptionId needed)
  verifyPayment: async (callback: PaymentCallback): Promise<VerifyPaymentResponse> => {
    const response = await axios.post(`${API_BASE}/verify-payment`, {
      razorpay_order_id: callback.razorpay_order_id,
      razorpay_payment_id: callback.razorpay_payment_id,
      razorpay_signature: callback.razorpay_signature
    });
    return response.data;
  },
  
  // Get my subscriptions
  getMySubscriptions: async (status?: SubscriptionStatus): Promise<Subscription[]> => {
    const params = status ? { status } : {};
    const response = await axios.get(`${API_BASE}/my-subscriptions`, { params });
    return response.data;
  },
  
  // Get single subscription
  getById: async (id: number): Promise<Subscription> => {
    const response = await axios.get(`${API_BASE}/${id}`);
    return response.data;
  },
  
  // Cancel subscription
  cancel: async (id: number): Promise<{ message: string }> => {
    const response = await axios.put(`${API_BASE}/${id}/cancel`);
    return response.data;
  },
  
  // Check access
  checkAccess: async (entityType: SubscriptionLevel, entityId: number): Promise<CheckAccessResponse> => {
    const response = await axios.get(`${API_BASE}/check-access`, {
      params: { entityType, entityId }
    });
    return response.data;
  },
  
  // Get expiring soon
  getExpiringSoon: async (days: number = 7): Promise<Subscription[]> => {
    const response = await axios.get(`${API_BASE}/expiring-soon`, {
      params: { days }
    });
    return response.data;
  },
  
  // Renew subscription
  renew: async (id: number): Promise<CreateSubscriptionResponse> => {
    const response = await axios.post(`${API_BASE}/${id}/renew`);
    return response.data;
  }
};
```

---

## Common Patterns

### Pattern 1: Complete Purchase Flow

```javascript
const completePurchaseFlow = async (subscriptionData) => {
  try {
    // 1. Create subscription (payment record created)
    const { order, keyId } = await subscriptionAPI.create(subscriptionData);
    
    // 2. Show Razorpay payment UI
    await openRazorpay(order, keyId);
    
    // 3. After payment - subscription is created in verify callback
    
  } catch (error) {
    handleError(error);
  }
};

const openRazorpay = (order, keyId) => {
  return new Promise((resolve, reject) => {
    const options = {
      key: keyId,
      amount: order.amount,
      order_id: order.id,
      handler: async (response) => {
        try {
          const result = await subscriptionAPI.verifyPayment(response);
          if (result.success) {
            resolve(result.subscription);
          } else {
            reject(new Error('Payment verification failed'));
          }
        } catch (error) {
          reject(error);
        }
      },
      modal: {
        ondismiss: () => reject(new Error('Payment cancelled'))
      }
    };
    
    const razorpay = new window.Razorpay(options);
    razorpay.open();
  });
};
```

---

### Pattern 2: Display Pricing Options

```javascript
const PricingDisplay = ({ subscription }) => {
  const { monthlyPrice, quarterlyPrice, yearlyPrice, planType } = subscription;
  
  const plans = [
    {
      type: 'MONTHLY',
      price: monthlyPrice,
      duration: '1 month',
      isCurrent: planType === 'MONTHLY'
    },
    {
      type: 'QUARTERLY',
      price: quarterlyPrice,
      duration: '3 months',
      savings: monthlyPrice * 3 - quarterlyPrice,
      isCurrent: planType === 'QUARTERLY'
    },
    {
      type: 'YEARLY',
      price: yearlyPrice,
      duration: '12 months',
      savings: monthlyPrice * 12 - yearlyPrice,
      isCurrent: planType === 'YEARLY'
    }
  ];
  
  return (
    <div className="pricing-table">
      {plans.map(plan => (
        <div key={plan.type} className={plan.isCurrent ? 'current-plan' : ''}>
          <h3>{plan.type}</h3>
          <p className="price">₹{plan.price}</p>
          <p className="duration">{plan.duration}</p>
          {plan.savings > 0 && (
            <p className="savings">Save ₹{plan.savings}</p>
          )}
          {plan.isCurrent ? (
            <span className="badge">Current Plan</span>
          ) : (
            <button onClick={() => handleUpgrade(plan.type)}>
              Upgrade
            </button>
          )}
        </div>
      ))}
    </div>
  );
};
```

---

### Pattern 3: Access Control HOC

```javascript
import React, { useEffect, useState } from 'react';
import { subscriptionAPI } from './api';

export const withSubscriptionAccess = (Component, entityType, entityId) => {
  return (props) => {
    const [access, setAccess] = useState(null);
    const [loading, setLoading] = useState(true);
    
    useEffect(() => {
      checkAccess();
    }, []);
    
    const checkAccess = async () => {
      try {
        const result = await subscriptionAPI.checkAccess(entityType, entityId);
        setAccess(result);
      } catch (error) {
        console.error('Access check failed:', error);
        setAccess({ hasAccess: false });
      } finally {
        setLoading(false);
      }
    };
    
    if (loading) return <LoadingSpinner />;
    
    if (!access.hasAccess) {
      return <SubscriptionRequired entityType={entityType} entityId={entityId} />;
    }
    
    return <Component {...props} subscription={access.subscription} />;
  };
};

// Usage
const ProtectedLesson = withSubscriptionAccess(LessonComponent, 'CLASS', 6);
```

---

## Error Handling

### Handle API Errors

```javascript
const handleAPIError = (error) => {
  if (error.response) {
    // Server responded with error
    const { status, data } = error.response;
    
    switch (status) {
      case 400:
        alert(data.error || 'Invalid request');
        break;
      case 403:
        alert('Access denied - not your subscription');
        break;
      case 404:
        alert('Subscription not found');
        break;
      case 503:
        alert('Payment service unavailable');
        break;
      default:
        alert('An error occurred: ' + (data.error || 'Please try again'));
    }
  } else if (error.request) {
    // Request made but no response
    alert('Network error - please check your connection');
  } else {
    // Other errors
    alert('Error: ' + error.message);
  }
};

// Usage
try {
  await subscriptionAPI.create(data);
} catch (error) {
  handleAPIError(error);
}
```

---

## State Management (Redux Example)

```javascript
// subscriptionSlice.js
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { subscriptionAPI } from './api';

export const fetchMySubscriptions = createAsyncThunk(
  'subscriptions/fetchMy',
  async (status = null) => {
    return await subscriptionAPI.getMySubscriptions(status);
  }
);

export const checkAccess = createAsyncThunk(
  'subscriptions/checkAccess',
  async ({ entityType, entityId }) => {
    return await subscriptionAPI.checkAccess(entityType, entityId);
  }
);

const subscriptionSlice = createSlice({
  name: 'subscriptions',
  initialState: {
    list: [],
    current: null,
    access: {},
    loading: false,
    error: null
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchMySubscriptions.fulfilled, (state, action) => {
        state.list = action.payload;
        state.loading = false;
      })
      .addCase(checkAccess.fulfilled, (state, action) => {
        const { entityType, entityId } = action.meta.arg;
        state.access[`${entityType}-${entityId}`] = action.payload;
      });
  }
});

export default subscriptionSlice.reducer;
```

---

## Important Notes

### 1. ⚠️ **Payment Verification Changes**

**OLD** (Don't use):
```javascript
{
  razorpay_order_id: "order_xxx",
  razorpay_payment_id: "pay_xxx",
  razorpay_signature: "sig_xxx",
  subscriptionId: 123  // ❌ REMOVE THIS
}
```

**NEW** (Correct):
```javascript
{
  razorpay_order_id: "order_xxx",
  razorpay_payment_id: "pay_xxx",
  razorpay_signature: "sig_xxx"
  // NO subscriptionId - backend creates subscription after verification
}
```

### 2. **Subscription Creation Timeline**

- ❌ **Before**: Subscription created when user clicks "Subscribe"
- ✅ **After**: Subscription created when payment is verified

**What this means**:
- Don't try to get subscriptionId immediately after POST /subscriptions
- Get subscriptionId from verify-payment response
- Handle payment failures gracefully (no duplicate subscriptions!)

### 3. **Field Naming Convention**

**Razorpay fields use snake_case** in JSON:
```javascript
subscription.razorpay_payment_id  // ✅ Correct
subscription.razorpayPaymentId    // ❌ Wrong
```

### 4. **Pricing in Every Response**

All subscription responses now include pricing:
```javascript
subscription.monthlyPrice    // ₹1500
subscription.quarterlyPrice  // ₹4000
subscription.yearlyPrice     // ₹15000
```

Use these to show upgrade options without additional API calls!

---

## Testing Checklist

### Create Subscription
- [ ] Can select plan type (Monthly/Quarterly/Yearly)
- [ ] Razorpay modal opens correctly
- [ ] Payment success creates subscription
- [ ] Payment failure shows error (NO duplicate subscription)
- [ ] Can retry after failure

### Renewal
- [ ] Can renew expiring subscription
- [ ] Payment success creates new subscription
- [ ] Payment failure keeps original subscription intact
- [ ] NO duplicate subscriptions on failure

### Subscription List
- [ ] Shows all subscriptions with correct status
- [ ] Filter by status works
- [ ] Displays pricing for plan comparisons
- [ ] Shows days remaining correctly

### Access Control
- [ ] Check-access returns correct response
- [ ] Protected content blocks unauthorized access
- [ ] Subscription details shown when access granted

---

## Migration from Old Code

### Step 1: Update Payment Verification

**Find all instances of**:
```javascript
subscriptionId: someValue
```

**In verify-payment calls and REMOVE them**.

### Step 2: Update Razorpay Handler

**OLD**:
```javascript
handler: function(response) {
  verifyPayment(response, subscriptionId);  // ❌
}
```

**NEW**:
```javascript
handler: function(response) {
  verifyPayment(response);  // ✅ No subscriptionId needed
}
```

### Step 3: Get Subscription from Response

**After verification**:
```javascript
const result = await verifyPayment(razorpayResponse);
const subscriptionId = result.subscription.id;  // Get from response
```

---

## Support & Troubleshooting

### Common Issues

**Q: Payment succeeds but subscription not created?**
A: Check verify-payment endpoint was called. Check backend logs.

**Q: Getting 400 error on verify-payment?**
A: Make sure you're NOT sending subscriptionId in the request.

**Q: Seeing duplicate subscriptions?**
A: This should NO LONGER happen with new payment flow. Contact backend team if it does.

**Q: How to handle payment failures?**
A: Show error message to user. They can retry. Original subscription (if renewal) remains intact.

---

## Complete Example App

```javascript
// App.js
import React, { useState, useEffect } from 'react';
import { subscriptionAPI } from './services/api';

function SubscriptionManager() {
  const [subscriptions, setSubscriptions] = useState([]);
  const [loading, setLoading] = useState(false);
  
  useEffect(() => {
    loadSubscriptions();
  }, []);
  
  const loadSubscriptions = async () => {
    try {
      const data = await subscriptionAPI.getMySubscriptions();
      setSubscriptions(data);
    } catch (error) {
      console.error('Failed to load subscriptions:', error);
    }
  };
  
  const handleSubscribe = async (entityType, entityId, planType) => {
    setLoading(true);
    
    try {
      const response = await subscriptionAPI.create({
        subscriptionLevel: entityType,
        entityId: entityId,
        amount: getPriceForPlan(planType),
        planType: planType,
        durationDays: getDurationForPlan(planType)
      });
      
      await openRazorpayPayment(response);
      
    } catch (error) {
      alert('Failed to create subscription: ' + error.message);
      setLoading(false);
    }
  };
  
  const openRazorpayPayment = ({ order, keyId }) => {
    return new Promise((resolve, reject) => {
      const options = {
        key: keyId,
        amount: order.amount,
        currency: order.currency,
        order_id: order.id,
        handler: async (razorpayResponse) => {
          try {
            const result = await subscriptionAPI.verifyPayment({
              razorpay_order_id: razorpayResponse.razorpay_order_id,
              razorpay_payment_id: razorpayResponse.razorpay_payment_id,
              razorpay_signature: razorpayResponse.razorpay_signature
            });
            
            if (result.success) {
              alert('Subscription activated!');
              await loadSubscriptions();
              resolve(result.subscription);
            } else {
              reject(new Error('Payment verification failed'));
            }
            
          } catch (error) {
            reject(error);
          } finally {
            setLoading(false);
          }
        },
        modal: {
          ondismiss: () => {
            setLoading(false);
            reject(new Error('Payment cancelled'));
          }
        }
      };
      
      const razorpay = new window.Razorpay(options);
      razorpay.open();
    });
  };
  
  return (
    <div className="subscription-manager">
      <h1>My Subscriptions</h1>
      
      {subscriptions.map(sub => (
        <SubscriptionCard 
          key={sub.id} 
          subscription={sub}
          onRenew={() => handleRenew(sub.id)}
        />
      ))}
    </div>
  );
}

const getPriceForPlan = (planType) => {
  const pricing = { MONTHLY: 1500, QUARTERLY: 4000, YEARLY: 15000 };
  return pricing[planType];
};

const getDurationForPlan = (planType) => {
  const durations = { MONTHLY: 30, QUARTERLY: 90, YEARLY: 365 };
  return durations[planType];
};
```

---

## Summary of Changes

### ✅ What Changed

1. **Payment Verification** - NO subscriptionId in request
2. **Subscription Creation** - Happens AFTER payment verification
3. **Field Names** - Razorpay fields use snake_case
4. **Response Data** - Includes pricing information

### ⚠️ Action Required

1. Remove `subscriptionId` from PaymentCallbackDTO/verify-payment requests
2. Get subscription from verify-payment response instead
3. Update TypeScript interfaces
4. Test payment failure scenarios (should NOT create duplicates)

### ✅ What Stayed Same

1. All other endpoints work normally
2. Subscription response structure (just added fields)
3. Razorpay integration flow
4. Authentication requirements

---

## Ready to Integrate! 🚀

The backend is now production-ready with:
- ✅ No duplicate subscriptions on payment failure
- ✅ Clean payment tracking
- ✅ Better error handling
- ✅ Complete pricing information in responses

