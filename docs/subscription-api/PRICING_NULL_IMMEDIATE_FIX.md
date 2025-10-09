# IMMEDIATE FIX - Null Pricing Issue

## 🚨 Problem

Pricing is null for COURSE subscriptions because **PricingConfiguration record doesn't exist** in database.

---

## 🔍 Step 1: Check Database (Run These SQL Queries)

### Query 1: Check what entity_type values exist
```sql
SELECT DISTINCT entity_type FROM pricing_configurations;
```

**Expected Output**:
```
CLASS
EXAM
COURSE (or might be "Professional" or "PROFESSIONAL")
```

---

### Query 2: Check if pricing exists for COURSE entityId=3
```sql
SELECT * FROM pricing_configurations 
WHERE entity_id = 3;
```

**If NO rows returned** → Pricing doesn't exist (need to create it)

**If rows returned** → Check the `entity_type` value

---

### Query 3: Check ALL pricing configurations
```sql
SELECT id, entity_type, entity_id, entity_name, 
       monthly_price, quarterly_price, yearly_price, is_active
FROM pricing_configurations
ORDER BY entity_type, entity_id;
```

**Look for**:
- Is there any pricing for COURSE entities?
- What naming is used for entity_type?

---

## ✅ Step 2: Add Missing Pricing

### Option A: Using SQL (Quick Fix)

```sql
-- Add pricing for COURSE entity_id = 3
INSERT INTO pricing_configurations 
(entity_type, entity_id, entity_name, 
 monthly_price, quarterly_price, yearly_price, 
 is_active, created_at, updated_at)
VALUES 
('COURSE', 3, 'Software Development', 
 1000.00, 2700.00, 10000.00, 
 TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Verify it was created
SELECT * FROM pricing_configurations WHERE entity_id = 3;
```

---

### Option B: Using Admin API (Recommended)

**Check if you have this endpoint**:
```
POST /api/admin/pricing
```

**Request**:
```json
{
  "entityType": "COURSE",
  "entityId": 3,
  "entityName": "Software Development",
  "monthlyPrice": 1000.00,
  "quarterlyPrice": 2700.00,
  "yearlyPrice": 10000.00,
  "isActive": true
}
```

---

## 🔍 Step 3: If Entity Type Naming is Different

### If database uses "PROFESSIONAL" instead of "COURSE"

**Query to check**:
```sql
SELECT * FROM pricing_configurations 
WHERE entity_type LIKE '%COURSE%' 
   OR entity_type LIKE '%PROFESSIONAL%';
```

**If you find pricing with different entity_type**, update the mapping:

**File**: `src/main/java/com/coaxial/service/StudentSubscriptionService.java`

**Update `getPricingConfiguration()` method**:

```java
private PricingConfiguration getPricingConfiguration(SubscriptionLevel level, Long entityId) {
    String entityType = level.name(); // CLASS, EXAM, COURSE
    
    // Try exact match first
    Optional<PricingConfiguration> pricingOpt = pricingConfigurationRepository
            .findByEntityTypeAndEntityId(entityType, entityId);
    
    if (pricingOpt.isPresent()) {
        return pricingOpt.get();
    }
    
    // Try alternative names for COURSE
    if ("COURSE".equals(entityType)) {
        // Try "PROFESSIONAL"
        pricingOpt = pricingConfigurationRepository
                .findByEntityTypeAndEntityId("PROFESSIONAL", entityId);
        
        if (pricingOpt.isPresent()) {
            return pricingOpt.get();
        }
        
        // Try "Professional"  
        pricingOpt = pricingConfigurationRepository
                .findByEntityTypeAndEntityId("Professional", entityId);
    }
    
    logger.warn("Pricing configuration not found for entityType={}, entityId={}", entityType, entityId);
    return pricingOpt.orElse(null);
}
```

---

## 🧪 Step 4: Test After Fix

### After adding pricing configuration:

**Restart application** (if you updated code)

**Test endpoint**:
```bash
curl -X GET "http://localhost:8080/api/student/subscriptions/my-subscriptions" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Check response for COURSE subscription**:
```json
{
  "subscriptionLevel": "COURSE",
  "entityId": 3,
  "monthlyPrice": 1000.00,      // ✅ Should have value now
  "quarterlyPrice": 2700.00,    // ✅ Should have value now
  "yearlyPrice": 10000.00,      // ✅ Should have value now
  "discountPercentage": 10.00,  // ✅ Calculated
  "savingsAmount": 300.00       // ✅ Calculated
}
```

---

## 📊 Expected Database State

### pricing_configurations Table

**Minimum required rows**:

```
| id | entity_type | entity_id | entity_name          | monthly_price | quarterly_price | yearly_price |
|----|-------------|-----------|----------------------|---------------|-----------------|--------------|
| 1  | CLASS       | 1         | Grade 1              | 1000.00       | 700.00          | 500.00       |
| 2  | EXAM        | 1         | SSC GD               | 500.00        | 500.00          | 500.00       |
| 3  | COURSE      | 3         | Software Development | 1000.00       | 2700.00         | 10000.00     |
```

**If your entity_type uses different naming**:
```
| entity_type    | Notes                                   |
|----------------|-----------------------------------------|
| CLASS          | For academic classes                    |
| EXAM           | For competitive exams                   |
| COURSE         | For professional courses (SubscriptionLevel.COURSE) |
| PROFESSIONAL   | Alternative naming for courses          |
```

---

## 🎯 Quick Diagnosis Commands

### Check 1: Count Pricing Configurations
```sql
SELECT entity_type, COUNT(*) as count
FROM pricing_configurations
GROUP BY entity_type;
```

### Check 2: Find Pricing for Your Entities
```sql
-- For your active subscriptions
SELECT s.id, s.subscription_level, s.entity_id, s.entity_name,
       p.id as pricing_id, p.monthly_price, p.quarterly_price, p.yearly_price
FROM student_subscriptions s
LEFT JOIN pricing_configurations p 
  ON p.entity_type = s.subscription_level 
  AND p.entity_id = s.entity_id
WHERE s.student_id = 2  -- Your student ID
ORDER BY s.id;
```

**This will show**:
- Which subscriptions have pricing (pricing_id is not null)
- Which subscriptions are missing pricing (pricing_id is null)

---

## ✅ Solution Checklist

- [ ] Run query to check entity_type values in pricing_configurations
- [ ] Run query to check if pricing exists for entity_id = 3
- [ ] If missing, add pricing configuration using SQL or Admin API
- [ ] If exists with different entity_type, update code mapping
- [ ] Restart application (if code changed)
- [ ] Test /my-subscriptions endpoint
- [ ] Verify pricing fields are populated
- [ ] Verify discount percentage calculated

---

## 🚀 After Fix - Expected Behavior

**All subscriptions will have**:
- ✅ monthlyPrice (not null)
- ✅ quarterlyPrice (not null)
- ✅ yearlyPrice (not null)
- ✅ discountPercentage (calculated)
- ✅ savingsAmount (calculated)

**Frontend can then**:
- Show discount badges
- Display savings information
- Compare plan pricing
- Encourage upgrades to yearly plans

---

## 📝 Most Likely Fix

**Run this SQL query** (adjust prices as needed):

```sql
INSERT INTO pricing_configurations 
(entity_type, entity_id, entity_name, monthly_price, quarterly_price, yearly_price, is_active, created_at, updated_at)
VALUES 
('COURSE', 3, 'Software Development', 1000.00, 2700.00, 10000.00, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (entity_type, entity_id) DO UPDATE 
SET monthly_price = 1000.00,
    quarterly_price = 2700.00,
    yearly_price = 10000.00,
    updated_at = CURRENT_TIMESTAMP;
```

Then test the endpoint again - pricing should appear!

