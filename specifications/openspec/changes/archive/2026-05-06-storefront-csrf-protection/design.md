# Design: CSRF Protection for Storefront

## Context

The Posters Demo Store uses Spring Security with two isolated filter chains:
1. **Backoffice** (`/backoffice/**`) - Already has CSRF protection enabled with form-based authentication
2. **Storefront** (`/**`) - Currently has CSRF **disabled** at [`SecurityConfig.java:75`](../../../implementation/src/main/java/com/xceptance/posters/config/SecurityConfig.java:75)

The storefront uses:
- **Thymeleaf** for server-side HTML rendering
- **HTMX** for dynamic cart interactions (add/update/remove items)
- **Session-based** state management (no JWT/tokens)
- **10 POST endpoints** requiring CSRF protection

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    Browser (User Agent)                      │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  CSRF Token Storage                                     │ │
│  │  • Meta tag: <meta name="_csrf" content="token">       │ │
│  │  • Hidden input: <input type="hidden" name="_csrf">    │ │
│  │  • HTMX header: X-CSRF-TOKEN                           │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                            ↓ ↑
                    HTTP POST with token
                            ↓ ↑
┌─────────────────────────────────────────────────────────────┐
│              Spring Security Filter Chain                    │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  CsrfFilter (Order 2 - Storefront)                     │ │
│  │  • Validates token on POST/PUT/DELETE/PATCH            │ │
│  │  • Compares request token with session token           │ │
│  │  • Returns 403 Forbidden if mismatch                   │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                            ↓ ↑
                    Valid request proceeds
                            ↓ ↑
┌─────────────────────────────────────────────────────────────┐
│                   Application Controllers                    │
│  • CustomerController (login, register, account)            │
│  • CheckoutController (shipping, billing, payment, order)   │
│  • CartController (add, update, remove)                     │
└─────────────────────────────────────────────────────────────┘
```

## Technical Decisions

### Decision 1: Use Spring Security's Synchronizer Token Pattern

**Rationale:**
- Industry-standard CSRF protection mechanism
- Built into Spring Security - no custom implementation needed
- Tokens stored server-side in HTTP session
- Automatic token generation and validation
- Well-tested and maintained by Spring team

**Implementation:**
```java
// SecurityConfig.java - Storefront filter chain
@Bean
@Order(2)
public SecurityFilterChain storefrontFilterChain(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/**")
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .ignoringRequestMatchers("/api/v2/**")  // Exclude WebMCP JSON APIs
        );
    return http.build();
}
```

**Key Points:**
- Use `CookieCsrfTokenRepository.withHttpOnlyFalse()` for JavaScript accessibility
- Token stored in cookie named `XSRF-TOKEN` (readable by JavaScript)
- Expected in request header `X-XSRF-TOKEN` or parameter `_csrf`
- Exclude `/api/v2/**` endpoints (stateless WebMCP APIs)

### Decision 2: Thymeleaf Automatic Token Injection

**Rationale:**
- Thymeleaf automatically adds CSRF tokens to forms using `th:action`
- Zero manual token management in templates
- Consistent implementation across all forms
- Reduces developer error

**Implementation Pattern:**
```html
<!-- BEFORE (vulnerable) -->
<form action="/en/login" method="post">
    <!-- No CSRF token -->
</form>

<!-- AFTER (protected) -->
<form th:action="@{'/' + ${urlLocale} + '/login'}" method="post">
    <!-- Thymeleaf auto-injects: <input type="hidden" name="_csrf" value="token"/> -->
</form>
```

**Affected Templates:**
- `customer/login.html`
- `customer/register.html`
- `customer/accountOverview.html`
- `checkout/shippingAddress.html`
- `checkout/billingAddress.html`
- `checkout/payment.html`
- `checkout/placeOrder.html`
- `cart/cart.html` (HTMX forms)

### Decision 3: HTMX Integration via Meta Tag

**Rationale:**
- HTMX makes AJAX requests that need CSRF tokens
- Meta tag approach is HTMX's recommended pattern
- Single configuration point in layout template
- Works for all HTMX requests automatically

**Implementation:**
```html
<!-- layout/default.html -->
<head>
    <meta name="_csrf" th:content="${_csrf.token}"/>
    <meta name="_csrf_header" th:content="${_csrf.headerName}"/>
    
    <script>
        // Configure HTMX to include CSRF token in all requests
        document.body.addEventListener('htmx:configRequest', function(evt) {
            const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
            evt.detail.headers[header] = token;
        });
    </script>
</head>
```

**HTMX Endpoints Affected:**
- Cart quantity updates (`hx-post`, `hx-trigger="change"`)
- Add to cart (`hx-post`)
- Remove from cart (`hx-delete`)
- Mini-cart refresh (`hx-get` - safe, no CSRF needed)

### Decision 4: Graceful Error Handling

**Rationale:**
- Session timeouts during checkout are common
- CSRF token expires with session
- Need user-friendly error messages, not generic 403

**Implementation:**
```java
// SecurityConfig.java
.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    .ignoringRequestMatchers("/api/v2/**")
)
.exceptionHandling(ex -> ex
    .accessDeniedHandler((request, response, accessDeniedException) -> {
        if (accessDeniedException instanceof MissingCsrfTokenException ||
            accessDeniedException instanceof InvalidCsrfTokenException) {
            // Redirect to error page with helpful message
            response.sendRedirect(request.getContextPath() + "/error?reason=session-expired");
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    })
)
```

### Decision 5: Exclude WebMCP JSON APIs

**Rationale:**
- `/api/v2/**` endpoints are stateless JSON APIs for AI agents
- These use different authentication (API keys, not sessions)
- CSRF protection not applicable to stateless APIs
- Documented in WebMCP specification

**Implementation:**
```java
.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    .ignoringRequestMatchers("/api/v2/**")  // Stateless APIs
)
```

## Component Changes

### 1. Security Configuration
**File:** [`implementation/src/main/java/com/xceptance/posters/config/SecurityConfig.java`](../../../implementation/src/main/java/com/xceptance/posters/config/SecurityConfig.java)

**Changes:**
- Remove `.csrf(csrf -> csrf.disable())` at line 75
- Add CSRF configuration with cookie repository
- Add exception handling for CSRF errors
- Add request matcher to exclude `/api/v2/**`

### 2. Layout Templates
**Files:**
- [`implementation/src/main/resources/templates/layout/default.html`](../../../implementation/src/main/resources/templates/layout/default.html)
- [`implementation/src/main/resources/templates/layout/checkoutLayout.html`](../../../implementation/src/main/resources/templates/layout/checkoutLayout.html)

**Changes:**
- Add CSRF meta tags in `<head>`
- Add HTMX configuration script
- Ensure `_csrf` model attribute is available

### 3. Form Templates (10 files)
**Changes Required:**
- Replace hardcoded `action` attributes with `th:action`
- Verify Thymeleaf auto-injection works
- Test form submission

**Pattern:**
```html
<!-- BEFORE -->
<form action="/en/login" method="post">

<!-- AFTER -->
<form th:action="@{'/' + ${urlLocale} + '/login'}" method="post">
```

### 4. Error Handling
**File:** [`implementation/src/main/java/com/xceptance/posters/controller/CustomErrorController.java`](../../../implementation/src/main/java/com/xceptance/posters/controller/CustomErrorController.java)

**Changes:**
- Add handling for `session-expired` error reason
- Display user-friendly message
- Provide "refresh page" action

## Data Flow

### Successful Form Submission
```
1. User loads page → Server generates CSRF token → Stored in session
2. Thymeleaf renders form → Auto-injects token as hidden input
3. User submits form → Browser sends token in POST body
4. CsrfFilter validates → Token matches session → Request proceeds
5. Controller processes → Returns response
```

### HTMX Cart Update
```
1. User changes quantity → HTMX triggers POST request
2. JavaScript reads token from meta tag → Adds X-CSRF-TOKEN header
3. Request sent with header → CsrfFilter validates
4. CartController updates → Returns HTML fragment
5. HTMX swaps content → Cart updated
```

### CSRF Attack Blocked
```
1. Attacker creates malicious page → Embeds form targeting storefront
2. Victim visits attacker's page → Form auto-submits
3. Browser sends request → No valid CSRF token included
4. CsrfFilter rejects → Returns 403 Forbidden
5. Attack fails → User's account safe
```

## Testing Strategy

### Unit Tests
- `SecurityConfigTest`: Verify CSRF enabled for storefront
- `CsrfTokenTest`: Verify token generation and validation

### Integration Tests
- Test each POST endpoint with valid token → Success
- Test each POST endpoint without token → 403 Forbidden
- Test each POST endpoint with invalid token → 403 Forbidden
- Test HTMX requests with token → Success

### Manual Tests
- Complete checkout flow end-to-end
- Test cart operations (add, update, remove)
- Test account operations (login, register, update)
- Test session timeout during checkout
- Test CSRF attack simulation

## Security Considerations

### Token Entropy
- Spring Security generates cryptographically secure random tokens
- 128-bit entropy (UUID-based)
- Sufficient to prevent brute force attacks

### Token Lifetime
- Tied to HTTP session lifetime (default: 30 minutes)
- New token generated on session creation
- Token invalidated on logout

### Cookie Security
- `SameSite=Lax` (default) - prevents most CSRF attacks
- `HttpOnly=false` - required for JavaScript access (HTMX)
- `Secure=true` - enforced in production (HTTPS only)

### Defense in Depth
- CSRF tokens (primary defense)
- SameSite cookies (secondary defense)
- Origin/Referer validation (tertiary defense)
- Content Security Policy (future enhancement)

## Performance Impact

### Token Generation
- **Cost:** ~1ms per session creation
- **Frequency:** Once per user session
- **Impact:** Negligible

### Token Validation
- **Cost:** ~0.1ms per POST request
- **Frequency:** Every state-changing request
- **Impact:** Negligible (< 1% overhead)

### Cookie Overhead
- **Size:** ~50 bytes per request
- **Impact:** Minimal bandwidth increase

## Future Enhancements

1. **Token Rotation:** Rotate tokens on sensitive operations
2. **Double Submit Cookie:** Add as secondary validation
3. **Rate Limiting:** Combine with rate limiting for defense in depth
4. **CSP Headers:** Add Content Security Policy headers
5. **Audit Logging:** Log CSRF validation failures for security monitoring