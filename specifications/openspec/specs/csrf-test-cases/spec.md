# Test Specification: CSRF Protection

**Related Spec:** [CSRF Protection](../../../../specs/csrf-protection/spec.md)  
**Test Level:** Integration & Manual  
**Priority:** HIGH  
**Status:** Draft

## Overview

This document defines the comprehensive test cases for CSRF (Cross-Site Request Forgery) protection across the Posters Demo Store. Tests cover both positive scenarios (valid tokens) and negative scenarios (missing/invalid tokens), as well as edge cases like session timeouts and HTMX interactions.

## Test Strategy

### Test Levels
1. **Unit Tests** - Security configuration and token generation
2. **Integration Tests** - End-to-end flows with CSRF validation
3. **Manual Tests** - User-facing scenarios and attack simulations

### Test Coverage Goals
- 100% of POST/PUT/DELETE/PATCH endpoints
- All Thymeleaf forms
- All HTMX operations
- Error handling scenarios
- Session timeout scenarios

## Unit Test Cases

### UT-CSRF-001: Security Configuration Enables CSRF
**Objective:** Verify CSRF protection is enabled in storefront filter chain

**Test Steps:**
1. Load Spring Security configuration
2. Inspect storefront filter chain
3. Verify CsrfFilter is present
4. Verify CookieCsrfTokenRepository is configured

**Expected Result:**
- CSRF filter active in chain
- Cookie repository configured with `HttpOnly=false`
- `/api/v2/**` endpoints excluded

**Automation:** JUnit test in `SecurityConfigTest.java`

---

### UT-CSRF-002: Token Generation
**Objective:** Verify CSRF tokens are generated correctly

**Test Steps:**
1. Create new HTTP session
2. Request CSRF token
3. Verify token properties

**Expected Result:**
- Token is non-null
- Token is UUID format (128-bit)
- Token is cryptographically random
- Token stored in session

**Automation:** JUnit test in `CsrfTokenTest.java`

---

### UT-CSRF-003: Token Validation - Valid Token
**Objective:** Verify valid tokens are accepted

**Test Steps:**
1. Generate CSRF token
2. Submit POST request with token
3. Verify request succeeds

**Expected Result:**
- Request returns 200/302 (not 403)
- Controller method executes
- Response is successful

**Automation:** JUnit test in `CsrfValidationTest.java`

---

### UT-CSRF-004: Token Validation - Missing Token
**Objective:** Verify requests without tokens are rejected

**Test Steps:**
1. Submit POST request without CSRF token
2. Verify request is blocked

**Expected Result:**
- Request returns 403 Forbidden
- Controller method does NOT execute
- Error logged

**Automation:** JUnit test in `CsrfValidationTest.java`

---

### UT-CSRF-005: Token Validation - Invalid Token
**Objective:** Verify invalid tokens are rejected

**Test Steps:**
1. Submit POST request with fake/invalid token
2. Verify request is blocked

**Expected Result:**
- Request returns 403 Forbidden
- Controller method does NOT execute
- Error logged

**Automation:** JUnit test in `CsrfValidationTest.java`

---

### UT-CSRF-006: API Endpoints Excluded
**Objective:** Verify `/api/v2/**` endpoints bypass CSRF

**Test Steps:**
1. Submit POST to `/api/v2/checkout` without token
2. Verify request is NOT blocked by CSRF

**Expected Result:**
- Request proceeds to controller
- No CSRF validation performed
- Response based on business logic (not 403)

**Automation:** JUnit test in `ApiCsrfExclusionTest.java`

---

## Integration Test Cases

### IT-CSRF-001: Customer Login Flow
**Objective:** Verify CSRF protection on login form

**Preconditions:**
- Valid customer account exists
- Application running

**Test Steps:**
1. Navigate to `/en/login`
2. Inspect page source for CSRF token
3. Fill in email and password
4. Submit form
5. Verify successful login

**Expected Result:**
- Hidden `_csrf` input present in form
- Token value is non-empty
- Login succeeds with redirect to homepage
- Session established

**Negative Test:**
1. Submit login form with token removed
2. Verify 403 Forbidden response

**Automation:** Integration test in `CsrfLoginTest.java`

---

### IT-CSRF-002: Customer Registration Flow
**Objective:** Verify CSRF protection on registration form

**Preconditions:**
- Application running
- Email address not already registered

**Test Steps:**
1. Navigate to `/en/register`
2. Inspect page source for CSRF token
3. Fill in registration form
4. Submit form
5. Verify account created

**Expected Result:**
- Hidden `_csrf` input present
- Registration succeeds
- User logged in automatically
- Welcome message displayed

**Negative Test:**
1. Submit registration with token removed
2. Verify 403 Forbidden response

**Automation:** Integration test in `CsrfRegistrationTest.java`

---

### IT-CSRF-003: Checkout Flow - Shipping Address
**Objective:** Verify CSRF protection on shipping address form

**Preconditions:**
- Items in cart
- User on shipping address page

**Test Steps:**
1. Navigate to `/en/checkout/shippingAddress`
2. Inspect form for CSRF token
3. Fill in shipping address
4. Submit form
5. Verify redirect to billing address

**Expected Result:**
- CSRF token present
- Address saved to session
- Redirect to next checkout step

**Negative Test:**
1. Submit form without token
2. Verify 403 Forbidden

**Automation:** Integration test in `CsrfCheckoutTest.java`

---

### IT-CSRF-004: Checkout Flow - Billing Address
**Objective:** Verify CSRF protection on billing address form

**Test Steps:**
1. Navigate to `/en/checkout/billingAddress`
2. Fill in billing address
3. Submit form
4. Verify redirect to payment page

**Expected Result:**
- CSRF token present
- Address saved
- Proceed to payment

**Negative Test:**
1. Submit without token → 403

**Automation:** Integration test in `CsrfCheckoutTest.java`

---

### IT-CSRF-005: Checkout Flow - Payment
**Objective:** Verify CSRF protection on payment form

**Test Steps:**
1. Navigate to `/en/checkout/payment`
2. Fill in credit card details
3. Submit form
4. Verify redirect to order review

**Expected Result:**
- CSRF token present
- Payment info saved (masked)
- Proceed to order review

**Negative Test:**
1. Submit without token → 403

**Automation:** Integration test in `CsrfCheckoutTest.java`

---

### IT-CSRF-006: Checkout Flow - Place Order
**Objective:** Verify CSRF protection on final order submission

**Test Steps:**
1. Navigate to `/en/checkout/placeOrder`
2. Review order details
3. Click "Place Order"
4. Verify order created

**Expected Result:**
- CSRF token present
- Order created in database
- Redirect to confirmation page
- Order number displayed

**Negative Test:**
1. Submit without token → 403
2. Order NOT created

**Automation:** Integration test in `CsrfCheckoutTest.java`

---

### IT-CSRF-007: Complete Checkout Flow
**Objective:** Verify CSRF protection throughout entire checkout

**Test Steps:**
1. Add item to cart
2. Proceed to checkout
3. Complete all checkout steps
4. Place order
5. Verify order confirmation

**Expected Result:**
- All forms have CSRF tokens
- All submissions succeed
- Order created successfully
- No 403 errors

**Automation:** Integration test in `CsrfEndToEndTest.java`

---

### IT-CSRF-008: Cart Operations - Add Item
**Objective:** Verify CSRF protection on add to cart

**Test Steps:**
1. Navigate to product page
2. Click "Add to Cart"
3. Verify item added

**Expected Result:**
- CSRF token included in request
- Item added to cart
- Cart count updated
- Success message displayed

**Negative Test:**
1. POST to add-to-cart without token → 403

**Automation:** Integration test in `CsrfCartTest.java`

---

### IT-CSRF-009: Cart Operations - Update Quantity (HTMX)
**Objective:** Verify CSRF protection on HTMX cart updates

**Test Steps:**
1. Navigate to cart page
2. Change item quantity
3. Verify HTMX request includes CSRF header
4. Verify cart updated

**Expected Result:**
- `X-CSRF-TOKEN` header present in request
- Quantity updated
- Subtotal recalculated
- No page reload

**Negative Test:**
1. HTMX request without header → 403

**Automation:** Integration test in `CsrfCartTest.java`

---

### IT-CSRF-010: Cart Operations - Remove Item (HTMX)
**Objective:** Verify CSRF protection on HTMX item removal

**Test Steps:**
1. Navigate to cart page
2. Click "Remove" on item
3. Verify HTMX DELETE includes CSRF header
4. Verify item removed

**Expected Result:**
- CSRF header present
- Item removed from cart
- Cart total updated
- UI updated via HTMX

**Negative Test:**
1. DELETE without header → 403

**Automation:** Integration test in `CsrfCartTest.java`

---

### IT-CSRF-011: Account Update
**Objective:** Verify CSRF protection on account update form

**Test Steps:**
1. Login as customer
2. Navigate to account page
3. Update account details
4. Submit form
5. Verify changes saved

**Expected Result:**
- CSRF token present
- Account updated
- Success message displayed

**Negative Test:**
1. Submit without token → 403

**Automation:** Integration test in `CsrfAccountTest.java`

---

### IT-CSRF-012: Session Timeout During Checkout
**Objective:** Verify graceful handling of session timeout

**Test Steps:**
1. Start checkout flow
2. Wait for session to expire (or force expiration)
3. Submit form
4. Verify error handling

**Expected Result:**
- Redirect to error page (not generic 403)
- Error message: "Session Expired"
- "Refresh page" button displayed
- User can recover by refreshing

**Automation:** Integration test in `CsrfSessionTimeoutTest.java`

---

### IT-CSRF-013: Token Reuse Across Sessions
**Objective:** Verify tokens cannot be reused across sessions

**Test Steps:**
1. Login as User A, get CSRF token
2. Logout
3. Login as User B
4. Try to use User A's token
5. Verify request blocked

**Expected Result:**
- Request returns 403
- Token from different session rejected
- Security event logged

**Automation:** Integration test in `CsrfSecurityTest.java`

---

### IT-CSRF-014: Backoffice CSRF Still Enabled
**Objective:** Verify backoffice CSRF protection unchanged

**Test Steps:**
1. Navigate to `/backoffice/login`
2. Verify CSRF token present
3. Login with valid credentials
4. Verify CSRF on admin operations

**Expected Result:**
- Backoffice has CSRF protection
- All admin forms have tokens
- CSRF validation works

**Automation:** Integration test in `BackofficeCsrfTest.java`

---

## Manual Test Cases

### Manual Test Cases Overview
The following test cases should be executed manually following the format in [`test-management/tests/TEMPLATE.md`](../../../../test-management/tests/TEMPLATE.md).

### TC_SEC_001: CSRF Protection on Login
**Test Case ID:** TC_SEC_001  
**Priority:** HIGH  
**Test Type:** Security, Functional  
**Execution Type:** Manual

**Objective:** Verify login form is protected against CSRF attacks

**Preconditions:**
- Application running
- Valid test account exists

**Test Steps:**
1. Open browser, navigate to `/en/login`
2. Open browser DevTools → Elements
3. Inspect login form HTML
4. Verify hidden input with name="_csrf" exists
5. Note the token value
6. Fill in valid credentials
7. Submit form
8. Verify successful login

**Expected Results:**
- Step 4: Hidden CSRF input field present
- Step 5: Token is non-empty UUID
- Step 8: Login succeeds, redirect to homepage

**Negative Test:**
1. Open DevTools → Console
2. Execute: `document.querySelector('input[name="_csrf"]').remove()`
3. Submit login form
4. Expected: 403 Forbidden or session expired error

**Test Data:**
- Email: `test@example.com`
- Password: `Test123!`

**Location:** `test-management/tests/security/TC_SEC_001.md`

---

### TC_SEC_002: CSRF Protection on Registration
**Test Case ID:** TC_SEC_002  
**Priority:** HIGH  
**Test Type:** Security, Functional

**Objective:** Verify registration form is protected against CSRF

**Test Steps:**
1. Navigate to `/en/register`
2. Inspect form for CSRF token
3. Fill in registration details
4. Submit form
5. Verify account created

**Expected Results:**
- CSRF token present
- Registration succeeds
- User logged in

**Negative Test:**
- Remove token, submit → 403

**Location:** `test-management/tests/security/TC_SEC_002.md`

---

### TC_SEC_003: CSRF Protection on Complete Checkout
**Test Case ID:** TC_SEC_003  
**Priority:** CRITICAL  
**Test Type:** Security, E2E

**Objective:** Verify entire checkout flow has CSRF protection

**Test Steps:**
1. Add item to cart
2. Proceed to checkout
3. Complete shipping address (verify token)
4. Complete billing address (verify token)
5. Complete payment (verify token)
6. Place order (verify token)
7. Verify order confirmation

**Expected Results:**
- All forms have CSRF tokens
- All submissions succeed
- Order created

**Location:** `test-management/tests/security/TC_SEC_003.md`

---

### TC_SEC_004: CSRF Protection on Cart Operations
**Test Case ID:** TC_SEC_004  
**Priority:** HIGH  
**Test Type:** Security, Functional

**Objective:** Verify cart operations include CSRF protection

**Test Steps:**
1. Add item to cart
2. Open cart page
3. Update quantity (HTMX)
4. Open DevTools → Network
5. Inspect HTMX request headers
6. Verify `X-CSRF-TOKEN` header present
7. Remove item (HTMX)
8. Verify header present

**Expected Results:**
- All HTMX requests include CSRF header
- Operations succeed

**Location:** `test-management/tests/security/TC_SEC_004.md`

---

### TC_SEC_005: CSRF Token in HTMX Requests
**Test Case ID:** TC_SEC_005  
**Priority:** HIGH  
**Test Type:** Security, Technical

**Objective:** Verify HTMX automatically includes CSRF tokens

**Test Steps:**
1. Navigate to any page with HTMX
2. Open DevTools → Network
3. Trigger HTMX request
4. Inspect request headers
5. Verify `X-CSRF-TOKEN` present
6. Verify token matches meta tag value

**Expected Results:**
- Header present in all HTMX requests
- Token value correct

**Location:** `test-management/tests/security/TC_SEC_005.md`

---

### TC_SEC_006: Session Timeout Handling
**Test Case ID:** TC_SEC_006  
**Priority:** MEDIUM  
**Test Type:** Security, Usability

**Objective:** Verify graceful handling of session timeout

**Test Steps:**
1. Start checkout flow
2. Wait 31 minutes (session timeout)
3. Submit form
4. Verify error message

**Expected Results:**
- User-friendly error displayed
- "Session Expired" message
- "Refresh page" button available
- No generic 403 error

**Location:** `test-management/tests/security/TC_SEC_006.md`

---

### TC_SEC_007: CSRF Attack Simulation
**Test Case ID:** TC_SEC_007  
**Priority:** CRITICAL  
**Test Type:** Security, Penetration

**Objective:** Simulate CSRF attack and verify protection

**Test Steps:**
1. Create malicious HTML page:
```html
<form action="http://localhost:8080/en/login" method="post">
    <input name="email" value="victim@example.com"/>
    <input name="password" value="stolen"/>
</form>
<script>document.forms[0].submit();</script>
```
2. Host on different domain
3. Open in browser where user is logged in
4. Verify attack is blocked

**Expected Results:**
- Request blocked (403)
- User not logged in as victim
- Attack fails

**Location:** `test-management/tests/security/TC_SEC_007.md`

---

### TC_SEC_008: API Endpoints Excluded from CSRF
**Test Case ID:** TC_SEC_008  
**Priority:** MEDIUM  
**Test Type:** Security, API

**Objective:** Verify `/api/v2/**` endpoints bypass CSRF

**Test Steps:**
1. Use curl or Postman
2. POST to `/api/v2/checkout` without CSRF token
3. Include valid JSON payload
4. Verify request succeeds (not blocked by CSRF)

**Expected Results:**
- Request not blocked by CSRF filter
- Response based on business logic
- No 403 error

**Location:** `test-management/tests/security/TC_SEC_008.md`

---

## Test Data

### Valid Test Accounts
```
Customer 1:
- Email: test.customer@example.com
- Password: Test123!
- Name: Test Customer

Customer 2:
- Email: another.customer@example.com
- Password: Test456!
- Name: Another Customer

Admin:
- Username: admin
- Password: admin123
```

### Test Credit Cards
```
Valid Card:
- Number: 4111111111111111
- Name: Test Customer
- Expiry: 12/25
- CVV: 123

Invalid Card:
- Number: 4111111111111112
- (Should fail validation)
```

### Test Products
```
Product 1: "Sunset Poster"
- ID: 1
- Price: $29.99

Product 2: "Mountain Poster"
- ID: 2
- Price: $39.99
```

## Test Execution

### Execution Order
1. Unit tests (automated)
2. Integration tests (automated)
3. Manual tests (following test run plan)

### Test Environment
- **Local:** `http://localhost:8080`
- **Staging:** `https://staging.posters-demo.com`
- **Production:** Not for testing

### Test Tools
- **Unit/Integration:** JUnit 5, Spring Test, MockMvc
- **Manual:** Chrome DevTools, Firefox DevTools
- **Security:** OWASP ZAP, Burp Suite (for attack simulation)

### Success Criteria
- All unit tests pass (100%)
- All integration tests pass (100%)
- All manual tests pass
- No CSRF vulnerabilities found
- Performance impact < 5%

## Test Reporting

### Test Results Location
- Unit/Integration: `implementation/target/surefire-reports/`
- Manual: `test-management/test-runs/YYYY-MM-DD-csrf-protection/`

### Required Documentation
- Test execution report
- Coverage report
- Security audit findings
- Performance test results

## References

- [CSRF Protection Specification](../../../../specs/csrf-protection/spec.md)
- [Test Template](../../../../test-management/tests/TEMPLATE.md)
- [Security Documentation](../../../../specifications/security/SECURITY_AND_PCI.md)
- [OWASP CSRF Testing Guide](https://owasp.org/www-project-web-security-testing-guide/latest/4-Web_Application_Security_Testing/06-Session_Management_Testing/05-Testing_for_Cross_Site_Request_Forgery)