# Student Subscription Management API - Implementation Summary

## Overview
Implemented comprehensive Student Subscription Management API endpoints with full subscription lifecycle support, status tracking, and Razorpay payment integration.

## API Endpoints

### Base URL: `/api/student/subscriptions`

### 1. GET /my-subscriptions
**Description**: List all student subscriptions with optional status filtering

**Query Parameters**:
- `status` (optional): Filter by subscription status (ACTIVE, EXPIRED, CANCELLED, PENDING)

**Response**: Array of subscription objects with full details including:
- Basic subscription info (id, studentId, entityId, entityName)
- Course details (courseTypeName, courseName)
- Status information (status, isActive, isExpired)
- Date information (startDate, endDate, expiryDate, daysRemaining)
- Payment details (razorpay_payment_id, amount, paymentStatus)

**Example Request**:
```
GET /api/student/subscriptions/my-subscriptions?status=ACTIVE
```

**Example Response**:
```json
[
  {
    "id": 1,
    "studentId": 123,
    "studentName": "John Doe",
    "studentEmail": "john@example.com",
    "subscriptionLevel": "CLASS",
    "entityId": 6,
    "entityName": "Class 10",
    "courseTypeName": "Academic",
    "courseName": "CBSE",
    "status": "ACTIVE",
    "amount": 1500.00,
    "currency": "INR",
    "isActive": true,
    "monthlyPrice": 1500.00,
    "quarterlyPrice": 4000.00,
    "yearlyPrice": 15000.00,
    "startDate": "2025-01-01T00:00:00",
    "endDate": "2025-02-01T00:00:00",
    "expiryDate": "2025-02-01T00:00:00",
    "durationDays": 31,
    "planType": "MONTHLY",
    "remainingDays": 23,
    "razorpay_order_id": "order_xxx",
    "razorpay_payment_id": "pay_xxx",
    "paymentStatus": "PAID",
    "paymentDate": "2025-01-01T10:30:00",
    "razorpay_receipt": "sub_1_xxx",
    "isExpired": false,
    "createdAt": "2025-01-01T10:00:00",
    "updatedAt": "2025-01-01T10:30:00"
  }
]
```

---

### 2. GET /{id}
**Description**: Get single subscription details by ID

**Path Parameters**:
- `id`: Subscription ID

**Response**: Single subscription object with full details

**Security**: Verifies that the subscription belongs to the authenticated student

**Example Request**:
```
GET /api/student/subscriptions/1
```

---

### 3. PUT /{id}/cancel
**Description**: Cancel an active subscription

**Path Parameters**:
- `id`: Subscription ID

**Response**:
```json
{
  "message": "Subscription cancelled successfully"
}
```

**Security**: 
- Verifies ownership (student can only cancel their own subscriptions)
- Also cancels associated Razorpay order if present

**Example Request**:
```
PUT /api/student/subscriptions/1/cancel
```

---

### 4. GET /check-access
**Description**: Check if student has access to specific content and return full subscription details

**Query Parameters**:
- `entityType`: Type of entity (CLASS, EXAM, or COURSE)
- `entityId`: ID of the entity

**Response with Access**:
```json
{
  "hasAccess": true,
  "subscription": {
    "id": 1,
    "studentId": 123,
    "studentName": "John Doe",
    "subscriptionLevel": "CLASS",
    "entityId": 6,
    "entityName": "Class 10",
    "courseTypeName": "Academic",
    "courseName": "CBSE",
    "status": "ACTIVE",
    "amount": 1500.00,
    "monthlyPrice": 1500.00,
    "quarterlyPrice": 4000.00,
    "yearlyPrice": 15000.00,
    "expiryDate": "2025-02-01T00:00:00",
    "planType": "MONTHLY",
    "daysRemaining": 23,
    "razorpay_payment_id": "pay_xxx",
    "razorpay_order_id": "order_xxx",
    "razorpay_receipt": "sub_1_xxx",
    "paymentStatus": "PAID",
    "isExpired": false
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

**Example Request**:
```
GET /api/student/subscriptions/check-access?entityType=CLASS&entityId=6
```

---

### 5. GET /expiring-soon
**Description**: Get subscriptions expiring within specified days

**Query Parameters**:
- `days` (optional, default: 7): Number of days to look ahead

**Response**: Array of subscription objects expiring within the specified timeframe

**Example Request**:
```
GET /api/student/subscriptions/expiring-soon?days=7
```

---

### 6. POST /{id}/renew
**Description**: Renew an existing subscription with new Razorpay order

**Path Parameters**:
- `id`: Original subscription ID to renew

**Response**:
```json
{
  "order": {
    "id": "order_xxx",
    "entity": "order",
    "amount": 150000,
    "currency": "INR",
    "receipt": "sub_123_xxx",
    "status": "created"
  },
  "keyId": "rzp_test_xxx",
  "message": "Renewal order created successfully",
  "originalSubscriptionId": 1
}
```

**Behavior**:
- Creates a new subscription record (maintains history)
- Copies amount, duration, and entity details from original subscription
- Creates new Razorpay payment order
- Links renewal to original subscription via notes

**Example Request**:
```
POST /api/student/subscriptions/1/renew
```

---

## Key Features Implemented

### 1. Subscription Status Management
- **New SubscriptionStatus Enum**: PENDING, ACTIVE, EXPIRED, CANCELLED
- **Automatic Status Computation**: Based on payment status, active flag, and expiry date
- **Status Filtering**: Filter subscriptions by status in my-subscriptions endpoint

### 2. Enhanced Subscription Details
- **courseTypeName**: Name of the course type (e.g., "Academic", "Competitive", "Professional")
- **courseName**: Name of the course (e.g., "CBSE", "JEE Preparation")
- **expiryDate**: Calculated expiry date (startDate + durationDays)
- **daysRemaining**: Number of days until expiration
- **status**: Current subscription status
- **planType**: Type of subscription plan (MONTHLY, QUARTERLY, YEARLY)
  - MONTHLY: 30 days default
  - QUARTERLY: 90 days default
  - YEARLY: 365 days default
- **Pricing Information**: All subscription responses include pricing from PricingConfiguration
  - `monthlyPrice`: Monthly plan price
  - `quarterlyPrice`: Quarterly plan price
  - `yearlyPrice`: Yearly plan price
  - Allows frontend to display available plan options without additional API calls
- **JSON Field Naming**: Razorpay fields use snake_case in JSON responses:
  - `razorpay_payment_id` (instead of razorpayPaymentId)
  - `razorpay_order_id` (instead of razorpayOrderId)
  - `razorpay_receipt` (instead of razorpayReceipt)

### 3. Entity Relationship Loading
- Automatically loads and populates course and course type names based on subscription level:
  - **CLASS**: Loads from ClassEntity → Course → CourseType
  - **EXAM**: Loads from Exam → Course → CourseType
  - **COURSE**: Loads from Course → CourseType

### 4. Subscription Lifecycle
- **Creation**: Initialize with PENDING status, activate on successful payment
- **Activation**: Set to ACTIVE when payment is verified
- **Expiration**: Mark as EXPIRED when current date > expiryDate
- **Cancellation**: Set to CANCELLED on user request or payment failure
- **Renewal**: Create new subscription with fresh payment order

---

## Database Changes

### Migration 1: V2025_01_10__add_subscription_status.sql

**Changes**:
1. Added `status` column to `student_subscriptions` table (VARCHAR(20))
2. Set initial status values for existing records based on current state
3. Created indexes for performance:
   - `idx_student_subscriptions_status`: Single column index on status
   - `idx_student_subscriptions_student_status`: Composite index on (student_id, status)

**Data Migration Logic**:
- CANCELLED: If payment_status is CANCELLED, REFUNDED, or PARTIALLY_REFUNDED
- PENDING: If payment_status is PENDING or FAILED
- EXPIRED: If payment_status is PAID but end_date < current date
- ACTIVE: If payment_status is PAID, is_active is true, and not expired

### Migration 2: V2025_01_11__add_plan_type.sql

**Changes**:
1. Added `plan_type` column to `student_subscriptions` table (VARCHAR(20))
2. Set initial plan_type values based on duration_days:
   - MONTHLY: duration_days <= 31
   - QUARTERLY: duration_days 32-100
   - YEARLY: duration_days > 100
3. Created index: `idx_student_subscriptions_plan_type`

**Plan Type Mapping**:
- MONTHLY: Default 30 days
- QUARTERLY: Default 90 days
- YEARLY: Default 365 days

---

## Files Modified

### New Files Created:
1. `src/main/java/com/coaxial/enums/SubscriptionStatus.java` - Status enum
2. `src/main/java/com/coaxial/enums/PlanType.java` - Plan type enum (MONTHLY, QUARTERLY, YEARLY)
3. `src/main/resources/db/migration/V2025_01_10__add_subscription_status.sql` - Database migration for status
4. `src/main/resources/db/migration/V2025_01_11__add_plan_type.sql` - Database migration for plan_type

### Modified Files:
1. `src/main/java/com/coaxial/entity/StudentSubscription.java`
   - Added `status` field
   - Added `planType` field
   - Added `computeStatus()` method
   - Added `getExpiryDate()` method

2. `src/main/java/com/coaxial/dto/SubscriptionRequestDTO.java`
   - Added `planType` field (required for subscription creation)

3. `src/main/java/com/coaxial/dto/SubscriptionResponseDTO.java`
   - Added `courseTypeName`, `courseName`, `status`, `expiryDate`, `planType` fields
   - Added pricing fields: `monthlyPrice`, `quarterlyPrice`, `yearlyPrice`
   - Added corresponding getters/setters
   - Added `@JsonProperty` annotations for snake_case JSON field mapping (razorpay_payment_id, razorpay_order_id, razorpay_receipt)

4. `src/main/java/com/coaxial/repository/StudentSubscriptionRepository.java`
   - Added `findByStudentIdAndStatus()` query
   - Added `findByStudentId()` query
   - Added `findByStatusOrderByCreatedAtDesc()` method

5. `src/main/java/com/coaxial/service/StudentSubscriptionService.java`
   - Updated `convertToResponseDTO()` to populate new fields including planType and pricing
   - Added `getCourseDetails()` helper method
   - Added `getPricingConfiguration()` helper method to fetch pricing from PricingConfiguration
   - Added `getMySubscriptions()` method with status filter
   - Added `getSubscriptionsExpiringSoon()` method
   - Added `renewSubscription()` method
   - Added `getActiveSubscriptionForAccess()` method for check-access endpoint
   - Updated all subscription creation/update methods to set status and planType

6. `src/main/java/com/coaxial/service/RazorpayPaymentService.java`
   - Updated payment verification to set status to ACTIVE
   - Updated cancellation to set status to CANCELLED
   - Updated failure handling to set status appropriately

7. `src/main/java/com/coaxial/controller/StudentSubscriptionController.java`
   - Added `GET /my-subscriptions` endpoint with status filter
   - Changed `POST /{id}/cancel` to `PUT /{id}/cancel`
   - Updated `GET /check-access` to use `entityType` parameter and return full subscription object
   - Added `GET /expiring-soon` endpoint
   - Added `POST /{id}/renew` endpoint

---

## Security Considerations

1. **Authentication**: All endpoints require student role authentication
2. **Authorization**: Ownership verification ensures students can only access/modify their own subscriptions
3. **Payment Verification**: Razorpay signature verification for payment security
4. **Data Validation**: Input validation on all endpoints

---

## Testing Recommendations

### 1. Test Subscription Creation and Payment Flow
- Create subscription
- Verify payment with Razorpay
- Check status changes to ACTIVE

### 2. Test Status Filtering
- Create subscriptions with different statuses
- Filter by each status type
- Verify correct subscriptions are returned

### 3. Test Expiration
- Create subscription with short duration
- Wait for expiration
- Verify status changes to EXPIRED
- Check expiring-soon endpoint

### 4. Test Cancellation
- Cancel active subscription
- Verify status changes to CANCELLED
- Verify Razorpay order is cancelled

### 5. Test Renewal
- Renew existing subscription
- Verify new subscription is created
- Verify new payment order is generated
- Complete payment and verify activation

### 6. Test Access Check
- Subscribe to an entity
- Check access with check-access endpoint
- Verify access is granted/denied correctly

### 7. Test Course Details Loading
- Create subscriptions for CLASS, EXAM, and COURSE levels
- Verify courseTypeName and courseName are correctly populated
- Test with missing entities

---

## API Response Codes

- **200 OK**: Successful retrieval
- **201 Created**: Subscription/order created successfully
- **400 Bad Request**: Invalid input or business rule violation
- **403 Forbidden**: Attempting to access/modify another student's subscription
- **404 Not Found**: Subscription or entity not found
- **500 Internal Server Error**: Server error
- **503 Service Unavailable**: Payment service not configured

---

## Notes

1. **Backward Compatibility**: Kept original `GET /` endpoint for backward compatibility
2. **Status Synchronization**: Status field is synchronized with paymentStatus and isActive
3. **History Preservation**: Renewal creates new subscription records to maintain history
4. **Performance**: Added database indexes for common query patterns
5. **Error Handling**: Comprehensive error handling with appropriate error messages

---

## Future Enhancements

1. Add subscription pause/resume functionality
2. Add prorated refunds for early cancellations
3. Add subscription upgrade/downgrade
4. Add automatic renewal reminders
5. Add subscription analytics dashboard
6. Add bulk subscription management for admins
7. Add subscription gifting functionality
8. Add coupon/discount code support

---

## Deployment Steps

1. **Database Migration**: Run Flyway migration to add status column and indexes
2. **Build Application**: Compile with updated code
3. **Deploy**: Deploy to target environment
4. **Verify**: Test all endpoints with sample data
5. **Monitor**: Monitor logs for any issues during status computation

---

## Support

For any issues or questions regarding the implementation, please refer to:
- API documentation
- Source code comments
- Database schema documentation
- Razorpay integration documentation

