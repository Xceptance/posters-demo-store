# Implementation Tasks: CSRF Protection for Storefront

## Task 1: Update Security Configuration
**Priority:** Critical  
**Estimated Time:** 1 hour  
**Assignee:** Backend Developer

### Description
Enable CSRF protection in the storefront security filter chain by removing the `csrf.disable()` call and configuring proper CSRF token handling.

### Acceptance Criteria
- [x] Remove `.csrf(csrf -> csrf.disable())` from [`SecurityConfig.java:75`](../../../implementation/src/main/java/com/xceptance/posters/config/SecurityConfig.java:75)
- [x] Add CSRF configuration with `CookieCsrfTokenRepository.withHttpOnlyFalse()`
- [x] Exclude `/api/v2/**` endpoints from CSRF validation using `.ignoringRequestMatchers()`
- [x] Add custom `AccessDeniedHandler` for CSRF-specific error messages
- [x] Verify backoffice filter chain remains unchanged
- [ ] Unit tests pass for `SecurityConfig`

### Implementation Details
```java
@Bean
@Order(2)
public SecurityFilterChain storefrontFilterChain(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/**")
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .ignoringRequestMatchers("/api/v2/**")
        )
        .exceptionHandling(ex -> ex
            .accessDeniedHandler((request, response, accessDeniedException) -> {
                if (accessDeniedException instanceof MissingCsrfTokenException ||
                    accessDeniedException instanceof InvalidCsrfTokenException) {
                    response.sendRedirect(request.getContextPath() + "/error?reason=session-expired");
                } else {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }
            })
        );
    return http.build();
}
```

### Files to Modify
- `implementation/src/main/java/com/xceptance/posters/config/SecurityConfig.java`

### Testing
- Run existing security tests
- Verify backoffice login still works
- Verify storefront loads without errors

---

## Task 2: Add CSRF Meta Tags to Layout Templates
**Priority:** Critical  
**Estimated Time:** 30 minutes  
**Assignee:** Frontend Developer

### Description
Add CSRF token meta tags to the main layout templates so they're available to all pages and JavaScript code.

### Acceptance Criteria
- [x] Add `<meta name="_csrf">` tag to `layout/default.html`
- [x] Add `<meta name="_csrf_header">` tag to `layout/default.html`
- [x] Add same meta tags to `layout/checkoutLayout.html`
- [ ] Verify meta tags render with actual token values
- [ ] Verify `_csrf` model attribute is automatically provided by Spring Security

### Implementation Details
```html
<head>
    <!-- Existing head content -->
    
    <!-- CSRF Token for JavaScript -->
    <meta name="_csrf" th:content="${_csrf.token}"/>
    <meta name="_csrf_header" th:content="${_csrf.headerName}"/>
</head>
```

### Files to Modify
- `implementation/src/main/resources/templates/layout/default.html`
- `implementation/src/main/resources/templates/layout/checkoutLayout.html`

### Testing
- Load any storefront page
- Inspect HTML source
- Verify meta tags contain token values

---

## Task 3: Configure HTMX for CSRF Tokens
**Priority:** Critical  
**Estimated Time:** 30 minutes  
**Assignee:** Frontend Developer

### Description
Add JavaScript to automatically include CSRF tokens in all HTMX requests.

### Acceptance Criteria
- [x] Add `htmx:configRequest` event listener to `layout/default.html`
- [x] Event listener reads token from meta tag
- [x] Event listener adds token to request headers
- [ ] Verify HTMX cart operations work correctly
- [ ] Verify HTMX requests include `X-CSRF-TOKEN` header

### Implementation Details
```html
<script>
    // Configure HTMX to include CSRF token in all requests
    document.body.addEventListener('htmx:configRequest', function(evt) {
        const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
        if (token && header) {
            evt.detail.headers[header] = token;
        }
    });
</script>
```

### Files to Modify
- `implementation/src/main/resources/templates/layout/default.html`

### Testing
- Add item to cart
- Update cart quantity
- Remove item from cart
- Verify all operations work
- Check browser network tab for CSRF header

---

## Task 4: Update Customer Forms
**Priority:** High  
**Estimated Time:** 1 hour  
**Assignee:** Frontend Developer

### Description
Update all customer-related forms to use Thymeleaf's `th:action` for automatic CSRF token injection.

### Acceptance Criteria
- [ ] Update login form in `customer/login.html`
- [ ] Update registration form in `customer/register.html`
- [ ] Update account form in `customer/accountOverview.html`
- [ ] Replace hardcoded `action` attributes with `th:action`
- [ ] Verify forms submit successfully
- [ ] Verify CSRF tokens are present in form HTML

### Implementation Pattern
```html
<!-- BEFORE -->
<form action="/en/login" method="post">

<!-- AFTER -->
<form th:action="@{'/' + ${urlLocale} + '/login'}" method="post">
```

### Files to Modify
- `implementation/src/main/resources/templates/customer/login.html` (line 12)
- `implementation/src/main/resources/templates/customer/register.html` (line 12)
- `implementation/src/main/resources/templates/customer/accountOverview.html` (line 40)

### Testing
- Test login with valid credentials
- Test registration with new account
- Test account update
- Verify all forms work correctly

---

## Task 5: Update Checkout Forms
**Priority:** High  
**Estimated Time:** 1 hour  
**Assignee:** Frontend Developer

### Description
Update all checkout-related forms to use Thymeleaf's `th:action` for automatic CSRF token injection.

### Acceptance Criteria
- [ ] Update shipping address form in `checkout/shippingAddress.html`
- [ ] Update billing address form in `checkout/billingAddress.html`
- [ ] Update payment form in `checkout/payment.html`
- [ ] Update place order form in `checkout/placeOrder.html`
- [ ] Replace hardcoded `action` attributes with `th:action`
- [ ] Verify complete checkout flow works end-to-end

### Files to Modify
- `implementation/src/main/resources/templates/checkout/shippingAddress.html` (line 11)
- `implementation/src/main/resources/templates/checkout/billingAddress.html` (line 16)
- `implementation/src/main/resources/templates/checkout/payment.html` (line 22)
- `implementation/src/main/resources/templates/checkout/placeOrder.html` (line 78)

### Testing
- Complete full checkout flow
- Test each step individually
- Verify order placement succeeds
- Check order confirmation page

---

## Task 6: Update Backoffice Forms (Verification Only)
**Priority:** Low  
**Estimated Time:** 30 minutes  
**Assignee:** Backend Developer

### Description
Verify that backoffice forms already use `th:action` and have CSRF protection working correctly.

### Acceptance Criteria
- [ ] Verify backoffice login form uses `th:action`
- [ ] Verify user management forms use `th:action`
- [ ] Verify role management forms use `th:action`
- [ ] Test backoffice login
- [ ] Test user CRUD operations
- [ ] Test role CRUD operations

### Files to Verify
- `implementation/src/main/resources/templates/backoffice/login.html`
- `implementation/src/main/resources/templates/backoffice/admin/users/form.html`
- `implementation/src/main/resources/templates/backoffice/admin/roles/form.html`

### Testing
- Login to backoffice
- Create/edit/delete user
- Create/edit/delete role
- Verify all operations work

---

## Task 7: Enhance Error Handling
**Priority:** Medium  
**Estimated Time:** 1 hour  
**Assignee:** Backend Developer

### Description
Add user-friendly error handling for CSRF validation failures, especially session timeouts.

### Acceptance Criteria
- [ ] Update `CustomErrorController` to handle `session-expired` reason
- [ ] Create error template with helpful message
- [ ] Add "refresh page" button to error page
- [ ] Test session timeout scenario
- [ ] Verify error message is user-friendly

### Implementation Details
```java
@GetMapping("/error")
public String handleError(HttpServletRequest request, Model model) {
    String reason = request.getParameter("reason");
    if ("session-expired".equals(reason)) {
        model.addAttribute("errorTitle", "Session Expired");
        model.addAttribute("errorMessage", "Your session has expired. Please refresh the page and try again.");
        model.addAttribute("showRefresh", true);
    }
    return "error";
}
```

### Files to Modify
- `implementation/src/main/java/com/xceptance/posters/controller/CustomErrorController.java`
- `implementation/src/main/resources/templates/error.html`

### Testing
- Simulate session timeout
- Submit form after timeout
- Verify error message displays
- Test refresh button

---

## Task 8: Update AGENTS.md with CSRF Rules
**Priority:** Critical  
**Estimated Time:** 30 minutes  
**Assignee:** Technical Lead

### Description
Add mandatory CSRF requirements to AGENTS.md so all future AI coding sessions automatically enforce CSRF protection.

### Acceptance Criteria
- [ ] Add new "Security Standards" section to AGENTS.md
- [ ] Document CSRF requirements for all POST/PUT/DELETE/PATCH endpoints
- [ ] Specify Thymeleaf form requirements (must use `th:action`)
- [ ] Specify HTMX requirements (must include CSRF headers)
- [ ] Add examples of correct and incorrect implementations
- [ ] Require CSRF test coverage for new endpoints

### Implementation Details
Add to `AGENTS.md`:

```markdown
## Security Standards

### CSRF Protection (MANDATORY)
All state-changing HTTP operations MUST be protected against Cross-Site Request Forgery (CSRF) attacks.

**Requirements:**
1. **Thymeleaf Forms:** ALL forms with `method="post"` MUST use `th:action` instead of hardcoded `action` attributes
   - ✅ Correct: `<form th:action="@{/path}" method="post">`
   - ❌ Incorrect: `<form action="/path" method="post">`

2. **HTMX Requests:** ALL HTMX requests that modify state MUST include CSRF token headers
   - Automatically handled by `htmx:configRequest` event listener in layout templates
   - Verify meta tags are present: `<meta name="_csrf" th:content="${_csrf.token}"/>`

3. **Controller Endpoints:** ALL POST/PUT/DELETE/PATCH endpoints are automatically protected
   - Exception: Stateless APIs under `/api/v2/**` are excluded
   - If you need to exclude an endpoint, document the security justification

4. **Testing:** ALL new state-changing endpoints MUST include CSRF test coverage
   - Test with valid token → Success
   - Test without token → 403 Forbidden
   - Test with invalid token → 403 Forbidden

**Verification Checklist:**
- [ ] All forms use `th:action` (no hardcoded `action` attributes)
- [ ] HTMX configuration includes CSRF headers
- [ ] New endpoints have CSRF test coverage
- [ ] Security exceptions are documented and justified
```

### Files to Modify
- `AGENTS.md`

### Testing
- Review with security team
- Verify AI agents follow rules in next coding session

---

## Task 9: Create CSRF Specification Document
**Priority:** High  
**Estimated Time:** 1 hour  
**Assignee:** Technical Writer

### Description
Create a formal specification document that serves as the single source of truth for CSRF implementation.

### Acceptance Criteria
- [ ] Create `openspec/specs/csrf-protection/spec.md`
- [ ] Document all CSRF requirements
- [ ] Include code examples
- [ ] Reference from AGENTS.md
- [ ] Link to security documentation

### Files to Create
- `openspec/specs/csrf-protection/spec.md`

### Content Structure
1. Overview and Purpose
2. Technical Requirements
3. Implementation Patterns
4. Testing Requirements
5. Security Considerations
6. Troubleshooting Guide
7. References

---

## Task 10: Write Unit Tests
**Priority:** High  
**Estimated Time:** 2 hours  
**Assignee:** Backend Developer

### Description
Create comprehensive unit tests for CSRF protection configuration and behavior.

### Acceptance Criteria
- [ ] Test CSRF enabled for storefront endpoints
- [ ] Test CSRF disabled for `/api/v2/**` endpoints
- [ ] Test CSRF token generation
- [ ] Test CSRF token validation
- [ ] Test invalid token rejection
- [ ] Test missing token rejection
- [ ] All tests pass with >80% coverage

### Test Cases
```java
@Test
void storefrontPostRequiresCSRF() {
    // POST to /en/login without token → 403
}

@Test
void storefrontPostWithValidTokenSucceeds() {
    // POST to /en/login with valid token → 200/302
}

@Test
void apiEndpointsExcludedFromCSRF() {
    // POST to /api/v2/checkout without token → 200 (if valid request)
}

@Test
void backofficeCSRFStillEnabled() {
    // POST to /backoffice/login without token → 403
}
```

### Files to Create
- `implementation/src/test/java/com/xceptance/posters/config/SecurityConfigCsrfTest.java`
- `implementation/src/test/java/com/xceptance/posters/controller/CsrfIntegrationTest.java`

### Testing
- Run `mvn test`
- Verify all tests pass
- Check coverage report

---

## Task 11: Write Integration Tests
**Priority:** High  
**Estimated Time:** 3 hours  
**Assignee:** QA Engineer

### Description
Create integration tests that verify CSRF protection works correctly across the entire application.

### Acceptance Criteria
- [ ] Test complete checkout flow with CSRF
- [ ] Test cart operations with CSRF
- [ ] Test account operations with CSRF
- [ ] Test HTMX requests with CSRF
- [ ] Test session timeout handling
- [ ] All integration tests pass

### Test Scenarios
1. **Happy Path Checkout**
   - Add to cart → Checkout → Complete order
   - Verify CSRF tokens present at each step

2. **Cart Operations**
   - Add item → Update quantity → Remove item
   - Verify HTMX includes CSRF headers

3. **Account Management**
   - Register → Login → Update account → Logout
   - Verify all forms work with CSRF

4. **Session Timeout**
   - Start checkout → Wait for timeout → Submit form
   - Verify error message displays

5. **CSRF Attack Simulation**
   - Create malicious form → Submit without token
   - Verify request is blocked

### Files to Create
- `implementation/src/test/java/com/xceptance/posters/integration/CsrfCheckoutFlowTest.java`
- `implementation/src/test/java/com/xceptance/posters/integration/CsrfCartOperationsTest.java`
- `implementation/src/test/java/com/xceptance/posters/integration/CsrfAccountTest.java`

### Testing
- Run `mvn verify`
- Verify all integration tests pass

---

## Task 12: Create Manual Test Cases
**Priority:** High  
**Estimated Time:** 2 hours  
**Assignee:** QA Engineer

### Description
Create comprehensive manual test cases following the project's test documentation standards.

### Acceptance Criteria
- [ ] Create test cases in `test-management/tests/security/` directory
- [ ] Follow `test-management/tests/TEMPLATE.md` format
- [ ] Cover all CSRF scenarios
- [ ] Include positive and negative test cases
- [ ] Create overview document
- [ ] Link to ISTQB review document

### Test Cases to Create
1. `TC_SEC_001.md` - CSRF Protection on Login
2. `TC_SEC_002.md` - CSRF Protection on Registration
3. `TC_SEC_003.md` - CSRF Protection on Checkout
4. `TC_SEC_004.md` - CSRF Protection on Cart Operations
5. `TC_SEC_005.md` - CSRF Token in HTMX Requests
6. `TC_SEC_006.md` - Session Timeout Handling
7. `TC_SEC_007.md` - CSRF Attack Simulation
8. `TC_SEC_008.md` - API Endpoints Excluded from CSRF

### Files to Create
- `test-management/tests/security/overview.md`
- `test-management/tests/security/TC_SEC_001.md` through `TC_SEC_008.md`
- `test-management/tests/security/ISTQB_REVIEW.md`

### Testing
- Execute each test case manually
- Document results
- Create test run report

---

## Task 13: Update Documentation
**Priority:** Medium  
**Estimated Time:** 1 hour  
**Assignee:** Technical Writer

### Description
Update project documentation to reflect CSRF protection implementation.

### Acceptance Criteria
- [ ] Update `README.md` with security features
- [ ] Update `SECURITY_AND_PCI.md` to mark SEC-007 as implemented
- [ ] Add developer guide for CSRF in new forms
- [ ] Update API documentation for excluded endpoints
- [ ] Add troubleshooting section

### Files to Modify
- `implementation/README.md`
- `specifications/security/SECURITY_AND_PCI.md`

### Files to Create
- `implementation/doc/CSRF_GUIDE.md` (developer guide)

### Content to Include
- How CSRF protection works
- How to add CSRF to new forms
- How to exclude endpoints if needed
- Common troubleshooting scenarios
- Security best practices

---

## Task 14: Performance Testing
**Priority:** Low  
**Estimated Time:** 2 hours  
**Assignee:** Performance Engineer

### Description
Verify that CSRF protection does not introduce significant performance overhead.

### Acceptance Criteria
- [ ] Measure baseline performance (before CSRF)
- [ ] Measure performance with CSRF enabled
- [ ] Compare response times for key endpoints
- [ ] Verify < 5% performance degradation
- [ ] Document findings

### Metrics to Measure
- Login response time
- Checkout flow completion time
- Cart operation latency
- Page load times
- Memory usage

### Tools
- JMeter or Gatling for load testing
- Browser DevTools for client-side metrics
- JFR (Java Flight Recorder) for server-side profiling

### Deliverable
- Performance test report comparing before/after metrics

---

## Task 15: Security Audit
**Priority:** High  
**Estimated Time:** 2 hours  
**Assignee:** Security Engineer

### Description
Conduct security audit to verify CSRF protection is correctly implemented and effective.

### Acceptance Criteria
- [ ] Verify all POST endpoints require CSRF tokens
- [ ] Verify tokens are cryptographically secure
- [ ] Verify tokens expire with session
- [ ] Test CSRF attack scenarios
- [ ] Verify no bypass vulnerabilities
- [ ] Document findings

### Security Tests
1. **Token Validation**
   - Submit form without token → Blocked
   - Submit form with invalid token → Blocked
   - Submit form with expired token → Blocked
   - Submit form with valid token → Success

2. **Attack Scenarios**
   - Cross-origin form submission → Blocked
   - Token reuse across sessions → Blocked
   - Token prediction attempt → Impossible

3. **Configuration Review**
   - Verify cookie settings (SameSite, Secure)
   - Verify token entropy
   - Verify exclusion list is minimal

### Deliverable
- Security audit report with findings and recommendations

---

## Task 16: Deployment Preparation
**Priority:** Medium  
**Estimated Time:** 1 hour  
**Assignee:** DevOps Engineer

### Description
Prepare deployment checklist and rollback plan for CSRF implementation.

### Acceptance Criteria
- [ ] Create deployment checklist
- [ ] Document rollback procedure
- [ ] Prepare monitoring alerts
- [ ] Update deployment scripts if needed
- [ ] Test deployment in staging environment

### Deployment Checklist
- [ ] All tests passing (unit, integration, manual)
- [ ] Security audit completed
- [ ] Performance testing completed
- [ ] Documentation updated
- [ ] AGENTS.md updated with CSRF rules
- [ ] Staging deployment successful
- [ ] Monitoring configured
- [ ] Rollback plan tested

### Rollback Plan
1. Revert `SecurityConfig.java` changes
2. Restart application
3. Verify storefront works
4. Monitor for errors

### Monitoring
- Track 403 error rates
- Monitor session timeout patterns
- Alert on CSRF validation failures

---

## Summary

**Total Estimated Time:** ~22 hours  
**Critical Path:** Tasks 1-5, 8 (core implementation + AGENTS.md)  
**Dependencies:**
- Task 2 depends on Task 1
- Task 3 depends on Task 2
- Tasks 4-5 depend on Tasks 1-3
- Task 9 depends on Task 8
- Tasks 10-11 depend on Tasks 1-7
- Task 16 depends on all previous tasks

**Recommended Execution Order:**
1. Task 1 (Security Config)
2. Task 2 (Meta Tags)
3. Task 3 (HTMX Config)
4. Tasks 4-5 (Forms) - Can be parallel
5. Task 6 (Backoffice Verification)
6. Task 7 (Error Handling)
7. **Task 8 (Update AGENTS.md) - CRITICAL for future sessions**
8. Task 9 (CSRF Specification)
9. Task 10 (Unit Tests)
10. Task 11 (Integration Tests)
11. Task 12 (Manual Tests)
12. Task 13 (Documentation)
13. Task 14 (Performance Testing)
14. Task 15 (Security Audit)
15. Task 16 (Deployment)

**Key Success Factor:**
Task 8 (Update AGENTS.md) is critical because it ensures all future AI coding sessions will automatically enforce CSRF protection, preventing regression and ensuring consistent security practices.