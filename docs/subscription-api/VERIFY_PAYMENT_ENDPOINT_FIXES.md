# Verify-Payment Endpoint Troubleshooting Guide

## 🚨 Identified Issues & Fixes

### Issue 1: Missing CORS Annotation

**Problem**: `StudentSubscriptionController` doesn't have `@CrossOrigin` annotation like other controllers.

**Symptom**: Browser CORS errors, preflight OPTIONS requests failing

**Fix**: Add CORS annotation to controller

```java
@RestController
@RequestMapping("/api/student/subscriptions")
@PreAuthorize("hasRole('STUDENT')")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://127.0.0.1:3000", "http://127.0.0.1:3001"}, allowCredentials = "true")
public class StudentSubscriptionController {
    // ... 
}
```

---

### Issue 2: Circular Dependency Risk

**Problem**: `StudentSubscriptionService` injects `PaymentService`, which could cause initialization issues.

**Symptom**: Bean creation failure, application doesn't start

**Status**: Currently OK (PaymentService doesn't inject StudentSubscriptionService), but needs monitoring

---

### Issue 3: Exception During Payment Verification

**Potential Problems**:

1. **Payment not found**: If payment record doesn't exist for razorpay_order_id
2. **Subscription creation fails**: If entity validation fails
3. **Database transaction issues**: If save fails

**Fix**: Add better error handling and logging

---

### Issue 4: Authentication Token Issues

**Problem**: If JWT token is invalid/expired, returns 401 JSON (not redirect), but frontend might not handle it

**Check**:
1. Token being sent in Authorization header?
2. Token format: `Bearer <token>`
3. Token not expired?

---

## Backend Fixes Needed

### Fix 1: Add CORS Annotation

**File**: `src/main/java/com/coaxial/controller/StudentSubscriptionController.java`

**Add this annotation**:
```java
@RestController
@RequestMapping("/api/student/subscriptions")
@PreAuthorize("hasRole('STUDENT')")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://127.0.0.1:3000", "http://127.0.0.1:3001"}, allowCredentials = "true")
public class StudentSubscriptionController {
```

### Fix 2: Enhanced Error Handling in verify-payment

**Add detailed logging**:
```java
@PostMapping("/verify-payment")
public ResponseEntity<?> verifyPayment(@Valid @RequestBody PaymentCallbackDTO callbackDTO, 
                                      Authentication authentication) {
    logger.info("Verify payment called with order ID: {}", callbackDTO.getRazorpay_order_id());
    
    try {
        boolean isValid = subscriptionService.verifyPaymentAndActivate(
                callbackDTO.getRazorpay_order_id(),
                callbackDTO.getRazorpay_payment_id(),
                callbackDTO.getRazorpay_signature()
        );
        
        logger.info("Payment verification result: {}", isValid);

        if (isValid) {
            // Get newly created subscription
            Optional<SubscriptionResponseDTO> subscription = subscriptionService.getSubscriptionByRazorpayOrderId(
                    callbackDTO.getRazorpay_order_id()
            );
            
            if (subscription.isPresent()) {
                logger.info("Subscription found and returning: {}", subscription.get().getId());
            } else {
                logger.warn("Payment verified but subscription not found for order: {}", callbackDTO.getRazorpay_order_id());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment verified successfully");
            response.put("subscription", subscription.orElse(null));
            
            return ResponseEntity.ok(response);
        } else {
            logger.warn("Payment verification failed for order: {}", callbackDTO.getRazorpay_order_id());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Payment verification failed"
            ));
        }
        
    } catch (IllegalArgumentException e) {
        logger.error("Validation error in verify payment: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
        ));
    } catch (Exception e) {
        logger.error("Error verifying payment for order: {}", callbackDTO.getRazorpay_order_id(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", "Payment verification failed: " + e.getMessage()));
    }
}
```

### Fix 3: Add Content-Type Header Explicitly

**Ensure JSON response**:
```java
@PostMapping(value = "/verify-payment", produces = "application/json")
public ResponseEntity<?> verifyPayment(...) {
    // ...
}
```

---

## Frontend Debugging Checklist

### 1. Check Request Headers

```javascript
// Verify these headers are sent:
{
  'Content-Type': 'application/json',
  'Authorization': 'Bearer <your-jwt-token>',
  'Accept': 'application/json'
}
```

### 2. Check Request Body

```javascript
// Should be:
{
  "razorpay_order_id": "order_xxx",
  "razorpay_payment_id": "pay_xxx",
  "razorpay_signature": "sig_xxx"
}

// NOT:
{
  razorpay_order_id,  // Missing quotes
  subscriptionId: 123  // Should be removed
}
```

### 3. Check Response in Network Tab

**Look for**:
- Status code: Should be 200, 400, or 500 (not 302)
- Response type: Should be JSON (not HTML)
- CORS errors in console

### 4. Add Debugging

```javascript
const verifyPayment = async (razorpayResponse) => {
  console.log('Verifying payment with:', {
    razorpay_order_id: razorpayResponse.razorpay_order_id,
    razorpay_payment_id: razorpayResponse.razorpay_payment_id,
    razorpay_signature: razorpayResponse.razorpay_signature
  });
  
  try {
    const response = await axios.post(
      '/api/student/subscriptions/verify-payment',
      {
        razorpay_order_id: razorpayResponse.razorpay_order_id,
        razorpay_payment_id: razorpayResponse.razorpay_payment_id,
        razorpay_signature: razorpayResponse.razorpay_signature
      },
      {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${getToken()}`,  // Make sure token exists
          'Accept': 'application/json'
        }
      }
    );
    
    console.log('Verify payment response:', response.data);
    return response.data;
    
  } catch (error) {
    console.error('Verify payment error:', error);
    console.error('Error response:', error.response);
    console.error('Error status:', error.response?.status);
    console.error('Error data:', error.response?.data);
    throw error;
  }
};
```

---

## Common Issues & Solutions

### Issue: "No response coming"

**Possible Causes**:

1. **CORS Preflight Failing**
   - Solution: Add `@CrossOrigin` to controller
   - Check browser console for CORS errors

2. **Authentication Failed**
   - Solution: Verify JWT token is valid and sent correctly
   - Check Authorization header format: `Bearer <token>`

3. **Request Not Reaching Controller**
   - Solution: Check URL is correct: `/api/student/subscriptions/verify-payment`
   - Verify method is POST

4. **Exception Thrown Before Response**
   - Solution: Check backend logs
   - Add try-catch in controller

5. **Content-Type Mismatch**
   - Solution: Set `Content-Type: application/json` in request
   - Add `produces = "application/json"` to @PostMapping

---

### Issue: Getting 302 Redirect

**Cause**: Spring Security redirecting to login page

**Solutions**:

1. **Check Authentication**:
   ```javascript
   // Make sure token is sent
   headers: {
     'Authorization': `Bearer ${localStorage.getItem('token')}`
   }
   ```

2. **Check Token Validity**:
   ```javascript
   // Token might be expired
   // Check token expiration before making request
   ```

3. **Check Endpoint Security**:
   ```java
   // verify-payment should be under /api/student/** 
   // which requires STUDENT role
   ```

---

### Issue: Getting HTML Instead of JSON

**Cause**: Error page being returned

**Solutions**:

1. **Check ExceptionHandling**:
   - Verify `JwtAuthenticationEntryPoint` returns JSON
   - Check `GlobalExceptionHandler`

2. **Add Produces Annotation**:
   ```java
   @PostMapping(value = "/verify-payment", produces = MediaType.APPLICATION_JSON_VALUE)
   ```

3. **Check Accept Header**:
   ```javascript
   headers: {
     'Accept': 'application/json'
   }
   ```

---

## Testing Commands

### Test 1: Check Endpoint Exists

```bash
# Should return 401 (Unauthorized) not 404
curl -X POST http://localhost:8080/api/student/subscriptions/verify-payment \
  -H "Content-Type: application/json" \
  -d '{}'

# Expected: 401 Unauthorized (needs auth)
# NOT: 404 Not Found
```

### Test 2: Test With Auth Token

```bash
curl -X POST http://localhost:8080/api/student/subscriptions/verify-payment \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "razorpay_order_id": "order_xxx",
    "razorpay_payment_id": "pay_xxx",
    "razorpay_signature": "sig_xxx"
  }'

# Expected: 400 (validation error) or 200 (success)
# Check: Returns JSON, not HTML
```

### Test 3: Check CORS

```bash
# OPTIONS preflight request
curl -X OPTIONS http://localhost:8080/api/student/subscriptions/verify-payment \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type,Authorization" \
  -v

# Should return CORS headers
```

---

## Backend Logs to Check

**Enable debug logging**:
```properties
# application.properties
logging.level.com.coaxial.controller=DEBUG
logging.level.com.coaxial.service=DEBUG
logging.level.org.springframework.security=DEBUG
```

**Look for**:
```
✅ "Verify payment called with order ID: order_xxx"
✅ "Payment verification result: true"
✅ "Subscription found and returning: 123"

Or errors:
❌ "Payment not found for order ID"
❌ "Failed to fetch course details"
❌ "Failed to fetch pricing"
```

---

## Quick Fixes Summary

### Backend
1. ✅ Add `@CrossOrigin` annotation to `StudentSubscriptionController`
2. ✅ Add `produces = "application/json"` to `@PostMapping("/verify-payment")`
3. ✅ Add detailed logging throughout verify-payment flow
4. ✅ Ensure all exceptions return JSON (not HTML)

### Frontend
1. ✅ Send proper Content-Type header
2. ✅ Send Authorization header with Bearer token
3. ✅ Remove subscriptionId from request body
4. ✅ Add error logging to see actual response

---

## Testing Checklist

- [ ] Check backend logs for endpoint access
- [ ] Verify CORS headers in response
- [ ] Confirm request reaches controller (check logs)
- [ ] Verify authentication token is valid
- [ ] Check payment record exists in database
- [ ] Verify subscription is created after verification
- [ ] Check response is JSON (not HTML)
- [ ] Confirm no 302 redirects (use Network tab)

---

## Expected Behavior

### Successful Flow

```
1. Frontend calls verify-payment
   → Backend logs: "Verify payment called with order ID: order_xxx"

2. Payment signature verified
   → Backend logs: "Payment verification result: true"

3. Payment marked as PAID
   → Backend logs: "Payment marked as successful: Payment ID 456"

4. Subscription created from payment
   → Backend logs: "Subscription created from payment: Subscription ID 123"

5. Subscription returned to frontend
   → Response: { success: true, subscription: {...} }
```

### Failed Flow

```
1. Frontend calls verify-payment
   → Backend logs: "Verify payment called..."

2. Payment not found
   → Backend logs: "Payment not found for order ID: order_xxx"
   → Response: { success: false, error: "Payment not found..." }

OR

2. Signature verification fails
   → Backend logs: "Payment verification failed for order: order_xxx"
   → Response: { success: false, error: "Payment verification failed" }
```

---

## Next Steps

1. **Add CORS annotation** to StudentSubscriptionController
2. **Check backend logs** when verify-payment is called
3. **Verify frontend sends** proper headers and request body
4. **Test with curl** to isolate frontend vs backend issues
5. **Check database** - does payment record exist?

**Most Likely Issues**:
1. Missing CORS annotation (80% probability)
2. Authentication token issues (15% probability)
3. Actual code error (5% probability)

