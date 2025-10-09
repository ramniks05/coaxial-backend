# PlanType Implementation Summary

## Overview
Added `planType` field to subscription system to explicitly track subscription plan duration types (MONTHLY, QUARTERLY, YEARLY).

---

## New Enum: PlanType

**Location**: `src/main/java/com/coaxial/enums/PlanType.java`

**Values**:
- `MONTHLY` - 30 days default
- `QUARTERLY` - 90 days default
- `YEARLY` - 365 days default

**Features**:
- `getDisplayName()` - Returns user-friendly name
- `getDefaultDays()` - Returns default duration in days
- `fromDurationDays(Integer)` - Static method to derive plan type from duration (for backward compatibility)

---

## API Changes

### Request (POST /api/student/subscriptions)

**New Required Field**:
```json
{
  "subscriptionLevel": "CLASS",
  "entityId": 6,
  "amount": 1500.00,
  "planType": "MONTHLY",  // ← NEW: Required field
  "durationDays": 30,
  "notes": "Optional notes"
}
```

### Response (All subscription endpoints)

**New Field in Response**:
```json
{
  "id": 1,
  "subscriptionLevel": "CLASS",
  "entityName": "Class 10",
  "planType": "MONTHLY",  // ← NEW: Returned in all responses
  "durationDays": 30,
  "expiryDate": "2025-02-01T00:00:00",
  ...
}
```

---

## Frontend Integration

The frontend now receives `planType` in all subscription responses:

```typescript
interface Subscription {
  id: number;
  planType: "MONTHLY" | "QUARTERLY" | "YEARLY";  // Use this to display plan
  durationDays: number;
  ...
}
```

**Display Logic**:
```typescript
const displayPlanName = (planType: string) => {
  switch(planType) {
    case 'MONTHLY': return 'Monthly Plan';
    case 'QUARTERLY': return 'Quarterly Plan';
    case 'YEARLY': return 'Yearly Plan';
    default: return 'Unknown Plan';
  }
};
```

---

## Database Changes

### Migration: V2025_01_11__add_plan_type.sql

**Column Added**:
```sql
ALTER TABLE student_subscriptions ADD COLUMN plan_type VARCHAR(20);
```

**Index Created**:
```sql
CREATE INDEX idx_student_subscriptions_plan_type 
ON student_subscriptions(plan_type);
```

**Data Migration**:
- Existing records are automatically assigned plan types based on their `duration_days`:
  - ≤ 31 days → MONTHLY
  - 32-100 days → QUARTERLY
  - > 100 days → YEARLY

---

## Files Modified

### New File:
1. `src/main/java/com/coaxial/enums/PlanType.java`

### Modified Files:
1. `src/main/java/com/coaxial/dto/SubscriptionRequestDTO.java`
   - Added `planType` field (required)
   
2. `src/main/java/com/coaxial/entity/StudentSubscription.java`
   - Added `planType` field with getter/setter
   
3. `src/main/java/com/coaxial/dto/SubscriptionResponseDTO.java`
   - Added `planType` field with getter/setter
   
4. `src/main/java/com/coaxial/service/StudentSubscriptionService.java`
   - Updated `createSubscription()` to set planType
   - Updated `renewSubscription()` to copy planType
   - Updated `convertToResponseDTO()` to include planType

---

## Validation

The `planType` field is **required** when creating a subscription:
- Validation: `@NotNull(message = "Plan type is required")`
- Must be one of: MONTHLY, QUARTERLY, YEARLY
- Frontend should provide this based on user's plan selection

---

## Backward Compatibility

**For existing subscriptions without planType**:
- Database migration automatically assigns plan types based on duration
- API responses will include the derived plan type
- No breaking changes to existing functionality

**Helper Method**:
```java
PlanType.fromDurationDays(durationDays)
```
Can be used to derive plan type from duration if needed.

---

## Testing Checklist

- [x] PlanType enum created with all values
- [x] Database migration adds plan_type column
- [x] Existing data migrated with appropriate plan types
- [x] SubscriptionRequestDTO requires planType
- [x] SubscriptionResponseDTO includes planType
- [x] Service layer sets planType on create and renew
- [ ] Test creating subscription with each plan type
- [ ] Test renewal preserves plan type
- [ ] Verify all API responses include planType
- [ ] Test frontend display of plan types

---

## Example Usage

### Creating a Quarterly Subscription

**Request**:
```bash
POST /api/student/subscriptions
{
  "subscriptionLevel": "CLASS",
  "entityId": 6,
  "amount": 4000.00,
  "planType": "QUARTERLY",
  "durationDays": 90
}
```

**Response**:
```json
{
  "order": {
    "id": "order_xxx",
    "amount": 400000,
    ...
  },
  "keyId": "rzp_test_xxx"
}
```

### Checking Subscription Details

**Request**:
```bash
GET /api/student/subscriptions/my-subscriptions
```

**Response**:
```json
[
  {
    "id": 1,
    "planType": "QUARTERLY",
    "durationDays": 90,
    "expiryDate": "2025-04-01T00:00:00",
    "remainingDays": 85,
    ...
  }
]
```

---

## Notes

1. **Plan Type Selection**: Frontend should provide a UI for users to select MONTHLY, QUARTERLY, or YEARLY plans, then send the appropriate `planType` value in the request.

2. **Pricing Integration**: The `planType` corresponds to the pricing structure in `PricingConfiguration`:
   - `monthlyPrice` → MONTHLY
   - `quarterlyPrice` → QUARTERLY
   - `yearlyPrice` → YEARLY

3. **Custom Durations**: While plan types have default durations, the actual `durationDays` can be customized. The `planType` indicates which pricing tier was used.

4. **Display Purposes**: The `planType` field is primarily for display and categorization. The actual subscription validity is determined by `expiryDate` and `durationDays`.

---

## Deployment Steps

1. **Run Database Migrations**:
   ```bash
   # Both migrations should run in order
   V2025_01_10__add_subscription_status.sql
   V2025_01_11__add_plan_type.sql
   ```

2. **Deploy Backend**: Deploy with updated code

3. **Update Frontend**: Update frontend to:
   - Send `planType` when creating subscriptions
   - Display `planType` in subscription lists
   - Show appropriate plan badges/labels

4. **Verify**: Test all subscription workflows with different plan types

