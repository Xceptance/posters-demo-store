# CSRF Protection Developer Guide

## Overview

The Posters Demo Store uses Spring Security's Synchronizer Token Pattern to protect against Cross-Site Request Forgery (CSRF) attacks. This means that every state-changing request (POST, PUT, DELETE, PATCH) must include a valid CSRF token, otherwise it will be rejected with a 403 Forbidden status (or redirected to a session expired error page).

## How It Works

1. **Token Generation:** Spring Security generates a unique token for the user's session.
2. **Token Delivery:** The token is injected into the HTML response via Thymeleaf model attributes and layout meta tags.
3. **Token Submission:** The client includes the token in subsequent state-changing requests (via hidden form field or HTTP header).
4. **Token Validation:** The `CsrfFilter` intercepts the request, extracts the token, and verifies it against the expected session token.

## Adding CSRF to New Forms

### Thymeleaf Forms (Standard POST)

When creating a new form that submits via a standard HTTP POST, you MUST use the `th:action` attribute instead of the plain `action` attribute. Thymeleaf automatically detects `th:action` and injects a hidden `_csrf` input field into the form.

**✅ Correct Implementation:**
```html
<form th:action="@{/en-US/myEndpoint}" method="post">
    <!-- Form fields -->
    <button type="submit">Submit</button>
</form>
```

**❌ Incorrect Implementation:**
```html
<!-- Fails because Thymeleaf won't inject the CSRF token -->
<form action="/en-US/myEndpoint" method="post">
    <!-- Form fields -->
</form>
```

### HTMX / AJAX Requests

For HTMX or JavaScript-driven requests, the CSRF token must be sent as an HTTP header (`X-CSRF-TOKEN`). 

**HTMX is configured automatically:**
If you include the default layout (`layout:decorate="~{layout/default}"`), an event listener automatically adds the CSRF token to all HTMX requests. You don't need to do anything manually for HTMX requests within the main application.

**Custom JavaScript (fetch/XHR):**
If you are writing custom JavaScript to make a POST request, you must extract the token from the meta tags and include it in the headers:

```javascript
const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

fetch('/en-US/myEndpoint', {
    method: 'POST',
    headers: {
        [header]: token,
        'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
});
```

## Excluding Endpoints

Stateless API endpoints (like `/api/v2/**`) do not use session-based authentication and therefore are excluded from CSRF validation.

To add a new exclusion, modify the `storefrontFilterChain` in `SecurityConfig.java`:

```java
.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    .ignoringRequestMatchers("/api/v2/**", "/my/new/stateless/api/**")
)
```

> **Warning:** Never exclude state-changing storefront endpoints that rely on session cookies (e.g., checkout, cart, account management).

## Troubleshooting

### Error: 403 Forbidden on Form Submit
- **Cause:** Missing or invalid CSRF token.
- **Solution:** Verify the form uses `th:action` and the hidden `_csrf` field is present in the rendered HTML.

### Error: "session-expired" Redirect
- **Cause:** The user's session expired, meaning the CSRF token tied to that session is no longer valid.
- **Solution:** This is expected behavior for security. The user must refresh the page to get a new token.

### Error: HTMX Request Fails
- **Cause:** The layout meta tags are missing, or the `htmx:configRequest` listener didn't fire.
- **Solution:** Ensure the template extends a layout that includes the CSRF meta tags (`layout/default` or `layout/checkoutLayout`).
