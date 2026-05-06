# Specification: CSRF Protection

**Status:** Draft  
**Version:** 1.0  
**Last Updated:** 2026-05-05  
**Owner:** Security Team  
**Related Change:** [storefront-csrf-protection](../../changes/storefront-csrf-protection/)

## Overview

This specification defines the mandatory requirements for Cross-Site Request Forgery (CSRF) protection across the Posters Demo Store application. All state-changing HTTP operations MUST be protected against CSRF attacks using Spring Security's synchronizer token pattern.

## Scope

### In Scope
- All storefront POST/PUT/DELETE/PATCH endpoints
- All backoffice POST/PUT/DELETE/PATCH endpoints
- Thymeleaf form submissions
- HTMX AJAX requests
- Session-based authentication flows

### Out of Scope
- Stateless JSON APIs under `/api/v2/**` (WebMCP endpoints)
- GET requests (safe methods, no state changes)
- OPTIONS/HEAD requests

## Requirements

### REQ-CSRF-001: Spring Security Configuration
**Priority:** CRITICAL  
**Status:** Mandatory

The storefront security filter chain MUST enable CSRF protection using Spring Security's built-in mechanisms.

**Implementation:**
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
        );
    return http.build();
}
```

**Rationale:**
- `CookieCsrfTokenRepository.withHttpOnlyFalse()` allows JavaScript access for HTMX
- Cookie-based storage is more scalable than session-based
- `/api/v2/**` exclusion for stateless APIs

**Verification:**
- [ ] CSRF filter is active in security chain
- [ ] Cookie named `XSRF-TOKEN` is set on first request
- [ ] POST requests without token return 403 Forbidden
- [ ] `/api/v2/**` endpoints accept requests without CSRF tokens

---

### REQ-CSRF-002: Thymeleaf Form Integration
**Priority:** CRITICAL  
**Status:** Mandatory

ALL HTML forms with `method="post"` MUST use Thymeleaf's `th:action` attribute to enable automatic CSRF token injection.

**Implementation:**
```html
<!-- ✅ CORRECT -->
<form th:action="@{'/' + ${urlLocale} + '/login'}" method="post">
    <!-- Thymeleaf auto-injects: <input type="hidden" name="_csrf" value="token"/> -->
    <input type="email" name="email" required/>
    <button type="submit">Login</button>
</form>

<!-- ❌ INCORRECT -->
<form action="/en/login" method="post">
    <!-- No CSRF token - request will be blocked -->
    <input type="email" name="email" required/>
    <button type="submit">Login</button>
</form>
```

**Rationale:**
- Thymeleaf automatically injects CSRF tokens when using `th:action`
- Eliminates manual token management
- Reduces developer error
- Consistent across all forms

**Verification:**
- [ ] All forms use `th:action` (no hardcoded `action` attributes)
- [ ] Rendered HTML contains hidden `_csrf` input field
- [ ] Form submission succeeds with valid token
- [ ] Form submission fails without token (403)

**Affected Templates:**
- `customer/login.html`
- `customer/register.html`
- `customer/accountOverview.html`
- `checkout/shippingAddress.html`
- `checkout/billingAddress.html`
- `checkout/payment.html`
- `checkout/placeOrder.html`
- `cart/cart.html`
- All backoffice forms

---

### REQ-CSRF-003: HTMX Integration
**Priority:** CRITICAL  
**Status:** Mandatory

ALL HTMX requests that modify state MUST include CSRF tokens in request headers.

**Implementation:**
```html
<!-- Layout template (default.html, checkoutLayout.html) -->
<head>
    <!-- CSRF meta tags for JavaScript access -->
    <meta name="_csrf" th:content="${_csrf.token}"/>
    <meta name="_csrf_header" th:content="${_csrf.headerName}"/>
    
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
</head>
```

**Rationale:**
- HTMX makes AJAX requests that need CSRF protection
- Meta tag approach is HTMX's recommended pattern
- Single configuration point in layout template
- Automatic for all HTMX requests

**Verification:**
- [ ] Meta tags present in all layout templates
- [ ] HTMX requests include `X-CSRF-TOKEN` header
- [ ] Cart operations work (add, update, remove)
- [ ] HTMX POST/PUT/DELETE succeed with token
- [ ] HTMX POST/PUT/DELETE fail without token (403)

**Affected Operations:**
- Add to cart
- Update cart quantity
- Remove from cart
- Any other HTMX-based state changes

---

### REQ-CSRF-004: Error Handling
**Priority:** HIGH  
**Status:** Mandatory

CSRF validation failures MUST provide user-friendly error messages, especially for session timeouts.

**Implementation:**
```java
// SecurityConfig.java
.exceptionHandling(ex -> ex
    .accessDeniedHandler((request, response, accessDeniedException) -> {
        if (accessDeniedException instanceof MissingCsrfTokenException ||
            accessDeniedException instanceof InvalidCsrfTokenException) {
            response.sendRedirect(request.getContextPath() + "/error?reason=session-expired");
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    })
)

// CustomErrorController.java
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

**Rationale:**
- Session timeouts are common during checkout
- Generic 403 errors confuse users
- Clear messaging improves user experience
- Provides actionable recovery steps

**Verification:**
- [ ] Session timeout shows friendly error message
- [ ] Error page includes "refresh" action
- [ ] Invalid token shows appropriate error
- [ ] Missing token shows appropriate error

---

### REQ-CSRF-005: API Endpoint Exclusions
**Priority:** MEDIUM  
**Status:** Mandatory

Stateless JSON APIs under `/api/v2/**` MUST be excluded from CSRF protection.

**Implementation:**
```java
.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    .ignoringRequestMatchers("/api/v2/**")  // WebMCP stateless APIs
)
```

**Rationale:**
- WebMCP APIs are stateless (no sessions)
- Use different authentication (API keys)
- CSRF protection not applicable to stateless APIs
- Documented in WebMCP specification

**Verification:**
- [ ] `/api/v2/checkout` accepts POST without CSRF token
- [ ] `/api/v2/cart` accepts POST without CSRF token
- [ ] Other `/api/v2/**` endpoints work without CSRF
- [ ] Storefront endpoints still require CSRF

**Excluded Endpoints:**
- `/api/v2/checkout` - WebMCP checkout API
- `/api/v2/cart` - WebMCP cart API
- `/api/v2/search` - WebMCP search API
- All other `/api/v2/**` endpoints

---

### REQ-CSRF-006: Token Security
**Priority:** HIGH  
**Status:** Mandatory

CSRF tokens MUST meet security requirements for entropy, lifetime, and storage.

**Requirements:**
1. **Entropy:** Minimum 128-bit cryptographically secure random tokens
2. **Lifetime:** Tokens tied to HTTP session (default: 30 minutes)
3. **Storage:** Cookie-based with appropriate security flags
4. **Rotation:** New token on session creation, invalidated on logout

**Cookie Configuration:**
```java
CookieCsrfTokenRepository.withHttpOnlyFalse()
// Results in:
// - Name: XSRF-TOKEN
// - HttpOnly: false (required for JavaScript access)
// - Secure: true (in production with HTTPS)
// - SameSite: Lax (default, prevents most CSRF)
// - Path: /
```

**Rationale:**
- 128-bit entropy prevents brute force attacks
- Session-based lifetime balances security and usability
- Cookie storage is scalable and standard
- SameSite=Lax provides defense in depth

**Verification:**
- [ ] Token is cryptographically random (UUID-based)
- [ ] Token expires with session
- [ ] New token generated on login
- [ ] Token invalidated on logout
- [ ] Cookie has correct security flags

---

### REQ-CSRF-007: Testing Requirements
**Priority:** HIGH  
**Status:** Mandatory

ALL new state-changing endpoints MUST include CSRF test coverage.

**Required Tests:**
1. **Positive Test:** Request with valid token → Success
2. **Negative Test:** Request without token → 403 Forbidden
3. **Negative Test:** Request with invalid token → 403 Forbidden
4. **Negative Test:** Request with expired token → 403 Forbidden

**Example:**
```java
@Test
void loginWithValidCsrfToken_succeeds() {
    // Arrange: Get CSRF token
    String token = getCsrfToken();
    
    // Act: POST with token
    mockMvc.perform(post("/en/login")
            .param("email", "test@example.com")
            .param("password", "password")
            .param("_csrf", token))
        .andExpect(status().is3xxRedirection());
}

@Test
void loginWithoutCsrfToken_returns403() {
    // Act: POST without token
    mockMvc.perform(post("/en/login")
            .param("email", "test@example.com")
            .param("password", "password"))
        .andExpect(status().isForbidden());
}
```

**Verification:**
- [ ] Unit tests for CSRF configuration
- [ ] Integration tests for each endpoint
- [ ] Manual test cases documented
- [ ] Test coverage >80% for CSRF code

---

## Implementation Patterns

### Pattern 1: Standard Form Submission
```html
<form th:action="@{'/' + ${urlLocale} + '/login'}" method="post">
    <input type="email" name="email" required/>
    <input type="password" name="password" required/>
    <button type="submit">Login</button>
</form>
```

### Pattern 2: HTMX Cart Update
```html
<input type="number" 
       th:value="${item.quantity}" 
       hx-post="@{'/' + ${urlLocale} + '/cart/update/' + ${item.id}}"
       hx-trigger="change"
       hx-target="#cart-content"/>
```

### Pattern 3: JavaScript Fetch Request
```javascript
async function submitForm(url, data) {
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    
    const response = await fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [header]: token
        },
        body: JSON.stringify(data)
    });
    
    return response.json();
}
```

### Pattern 4: Excluding Specific Endpoint
```java
// Only if absolutely necessary and documented
.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    .ignoringRequestMatchers(
        "/api/v2/**",           // WebMCP APIs
        "/webhook/payment"      // External webhook (if needed)
    )
)
```

## Security Considerations

### Defense in Depth
CSRF protection is part of a layered security approach:
1. **Primary:** CSRF tokens (synchronizer token pattern)
2. **Secondary:** SameSite cookies (prevents cross-site requests)
3. **Tertiary:** Origin/Referer validation (Spring Security default)
4. **Future:** Content Security Policy headers

### Attack Scenarios Prevented
- ✅ Cross-site form submission
- ✅ Malicious JavaScript on third-party site
- ✅ Email-based CSRF attacks
- ✅ XSS-based CSRF (with proper XSS prevention)

### Known Limitations
- ⚠️ Does not prevent XSS attacks (separate concern)
- ⚠️ Requires JavaScript for HTMX (graceful degradation needed)
- ⚠️ Session-based (not suitable for stateless APIs)

## Troubleshooting

### Problem: Form submission returns 403
**Cause:** Missing or invalid CSRF token  
**Solution:**
1. Verify form uses `th:action` (not hardcoded `action`)
2. Check browser DevTools → Network → Request payload for `_csrf` parameter
3. Verify session is active (not expired)
4. Check server logs for CSRF validation errors

### Problem: HTMX requests fail with 403
**Cause:** CSRF token not included in headers  
**Solution:**
1. Verify meta tags present in layout template
2. Check `htmx:configRequest` event listener is configured
3. Inspect Network tab → Headers for `X-CSRF-TOKEN`
4. Verify token value matches cookie value

### Problem: Session timeout during checkout
**Cause:** User took too long, session expired  
**Solution:**
1. Verify error handling redirects to friendly error page
2. Consider increasing session timeout for checkout flow
3. Implement session warning before expiration
4. Add "save progress" functionality

### Problem: API endpoint blocked by CSRF
**Cause:** Endpoint not in exclusion list  
**Solution:**
1. Add endpoint to `.ignoringRequestMatchers()` if truly stateless
2. Document security justification
3. Verify endpoint doesn't rely on session state
4. Consider alternative authentication (API keys)

## Compliance

### OWASP Top 10
- ✅ A01:2021 - Broken Access Control (CSRF is a form of access control bypass)
- ✅ A04:2021 - Insecure Design (CSRF protection is secure design)

### PCI DSS
- ✅ Requirement 6.5.9 - Protection against CSRF attacks
- ✅ Requirement 6.5.10 - Broken authentication and session management

### GDPR
- ✅ Article 32 - Security of processing (CSRF protection is a security measure)

## References

- [Spring Security CSRF Documentation](https://docs.spring.io/spring-security/reference/features/exploits/csrf.html)
- [OWASP CSRF Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html)
- [Thymeleaf Security Integration](https://www.thymeleaf.org/doc/articles/springsecurity.html)
- [HTMX Security](https://htmx.org/docs/#security)
- [Project Security Documentation](../../../specifications/security/SECURITY_AND_PCI.md)
- [Implementation Change](../../changes/storefront-csrf-protection/)

## Changelog

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2026-05-05 | Security Team | Initial specification |

## Approval

| Role | Name | Date | Signature |
|------|------|------|-----------|
| Security Lead | TBD | TBD | TBD |
| Tech Lead | TBD | TBD | TBD |
| QA Lead | TBD | TBD | TBD |