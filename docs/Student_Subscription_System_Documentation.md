# Student Subscription System Documentation

## Overview
This document describes the complete student subscription system with Razorpay payment integration for the Coaxial Learning Management System.

## Architecture

### Core Components

1. **StudentSubscription Entity** - Main subscription data model
2. **StudentSubscriptionRepository** - Data access layer
3. **StudentSubscriptionService** - Business logic layer
4. **StudentSubscriptionController** - REST API endpoints
5. **RazorpayPaymentService** - Payment gateway integration
6. **StudentDashboardService** - Content access control
7. **StudentDashboardController** - Student dashboard APIs

### Database Schema

#### StudentSubscription Table
```sql
CREATE TABLE student_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES users(id),
    subscription_level VARCHAR(20) NOT NULL, -- CLASS, EXAM, COURSE
    entity_id BIGINT NOT NULL,
    entity_name VARCHAR(255),
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    is_active BOOLEAN NOT NULL DEFAULT true,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    duration_days INTEGER,
    
    -- Razorpay fields
    razorpay_order_id VARCHAR(255),
    razorpay_payment_id VARCHAR(255),
    razorpay_signature VARCHAR(255),
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_date TIMESTAMP,
    razorpay_receipt VARCHAR(255),
    
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Subscription Flow

### 1. Subscription Creation
1. Student selects subscription level (Class/Exam/Course)
2. System validates entity exists and gets entity name
3. Creates subscription record with PENDING status
4. Generates Razorpay order
5. Returns payment details to frontend

### 2. Payment Processing
1. Student completes payment on Razorpay
2. Razorpay sends callback to `/api/student/subscriptions/verify-payment`
3. System verifies payment signature
4. Updates subscription status to PAID
5. Activates subscription (sets isActive = true)

### 3. Content Access
1. Student accesses dashboard content
2. System checks active subscriptions
3. Filters content based on subscription level and entity access
4. Returns only accessible subjects, tests, and questions

## API Endpoints

### Student Subscription Management

#### Create Subscription
```http
POST /api/student/subscriptions
Content-Type: application/json
Authorization: Bearer <token>

{
    "subscriptionLevel": "CLASS",
    "entityId": 6,
    "amount": 999.00,
    "durationDays": 365,
    "notes": "Annual subscription for Grade 1"
}
```

**Response:**
```json
{
    "order": {
        "id": "order_1234567890",
        "amount": 99900,
        "currency": "INR",
        "receipt": "sub_1_1703123456",
        "status": "created"
    },
    "keyId": "rzp_test_1234567890",
    "subscriptionLevel": "CLASS",
    "entityId": 6,
    "amount": 999.00
}
```

#### Verify Payment
```http
POST /api/student/subscriptions/verify-payment
Content-Type: application/json

{
    "razorpay_order_id": "order_1234567890",
    "razorpay_payment_id": "pay_1234567890",
    "razorpay_signature": "signature_hash",
    "subscriptionId": 1
}
```

#### Get My Subscriptions
```http
GET /api/student/subscriptions
Authorization: Bearer <token>
```

#### Get Active Subscriptions
```http
GET /api/student/subscriptions/active
Authorization: Bearer <token>
```

#### Cancel Subscription
```http
POST /api/student/subscriptions/{id}/cancel
Authorization: Bearer <token>
```

#### Check Content Access
```http
GET /api/student/subscriptions/check-access?subscriptionLevel=CLASS&entityId=6
Authorization: Bearer <token>
```

### Student Dashboard

#### Dashboard Summary
```http
GET /api/student/dashboard/summary
Authorization: Bearer <token>
```

**Response:**
```json
{
    "totalSubjects": 25,
    "totalTests": 10,
    "activeSubscriptions": 3,
    "classSubjectsCount": 15,
    "examSubjectsCount": 5,
    "courseSubjectsCount": 5,
    "recentSubjects": [...],
    "recentTests": [...]
}
```

#### Accessible Subjects
```http
GET /api/student/dashboard/subjects
Authorization: Bearer <token>
```

#### Accessible Tests
```http
GET /api/student/dashboard/tests
Authorization: Bearer <token>
```

#### Check Test Access
```http
GET /api/student/dashboard/test-access/{testId}
Authorization: Bearer <token>
```

#### Student Profile
```http
GET /api/student/dashboard/profile
Authorization: Bearer <token>
```

## Configuration

### Razorpay Setup
Add to `application.properties`:
```properties
# Razorpay Configuration
razorpay.key.id=rzp_test_your_key_id
razorpay.key.secret=your_secret_key
```

### Maven Dependencies
The following dependencies are added to `pom.xml`:
```xml
<!-- Razorpay SDK for payment integration -->
<dependency>
    <groupId>com.razorpay</groupId>
    <artifactId>razorpay-java</artifactId>
    <version>1.4.0</version>
</dependency>
<!-- HTTP Client for external API calls -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

## Security Features

1. **Authentication Required** - All endpoints require valid JWT token
2. **Student Role Only** - Only students can access subscription endpoints
3. **Ownership Validation** - Students can only manage their own subscriptions
4. **Payment Signature Verification** - Razorpay signatures are verified
5. **Access Control** - Content access is controlled by active subscriptions

## Error Handling

### Common Error Responses
```json
{
    "error": "Payment service is not configured"
}
```

```json
{
    "error": "Student already has an active subscription for this class"
}
```

```json
{
    "success": false,
    "error": "Payment verification failed"
}
```

## Frontend Integration

### Razorpay Integration
1. Include Razorpay script in your React application
2. Use the `keyId` from payment config endpoint
3. Create payment options with order details
4. Handle payment success/failure callbacks
5. Call verify payment endpoint after successful payment

### Example Frontend Flow
```javascript
// 1. Create subscription
const response = await fetch('/api/student/subscriptions', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(subscriptionData)
});

const { order, keyId } = await response.json();

// 2. Open Razorpay checkout
const options = {
    key: keyId,
    amount: order.amount,
    currency: order.currency,
    name: 'Coaxial LMS',
    description: 'Subscription Payment',
    order_id: order.id,
    handler: function (response) {
        // 3. Verify payment
        verifyPayment(response);
    }
};

const rzp = new Razorpay(options);
rzp.open();

// 4. Verify payment
async function verifyPayment(paymentResponse) {
    await fetch('/api/student/subscriptions/verify-payment', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
            razorpay_order_id: paymentResponse.razorpay_order_id,
            razorpay_payment_id: paymentResponse.razorpay_payment_id,
            razorpay_signature: paymentResponse.razorpay_signature,
            subscriptionId: subscriptionId
        })
    });
}
```

## Future Enhancements

1. **Automatic Renewal** - Implement recurring subscriptions
2. **Subscription Plans** - Predefined subscription packages
3. **Discounts & Coupons** - Promotional pricing
4. **Refund Management** - Handle subscription cancellations
5. **Analytics** - Subscription metrics and reporting
6. **Email Notifications** - Payment confirmations and reminders
7. **Mobile App Support** - Push notifications for subscription updates

## Testing

### Test Scenarios
1. Create subscription with valid data
2. Create subscription with invalid entity
3. Verify valid payment signature
4. Verify invalid payment signature
5. Check content access with active subscription
6. Check content access without subscription
7. Cancel active subscription
8. Handle expired subscriptions

### Test Data
```json
{
    "subscriptionLevel": "CLASS",
    "entityId": 6,
    "amount": 999.00,
    "durationDays": 365
}
```

## Monitoring & Logging

The system includes comprehensive logging for:
- Subscription creation and updates
- Payment processing
- Access control decisions
- Error conditions
- Performance metrics

Log levels are configured in `application.properties`:
```properties
logging.level.com.coaxial=DEBUG
```

## Support

For issues or questions regarding the subscription system:
1. Check application logs for error details
2. Verify Razorpay configuration
3. Ensure database connectivity
4. Validate JWT token authentication
