# Proposal: Implement CSRF Protection for Storefront

## Problem Statement

The Posters Demo Store storefront currently has CSRF (Cross-Site Request Forgery) protection **disabled** in [`SecurityConfig.java:75`](../../../implementation/src/main/java/com/xceptance/posters/config/SecurityConfig.java:75). This creates a critical security vulnerability where malicious websites can trick authenticated users into performing unwanted actions on the storefront, including:

- Adding/removing items from shopping carts
- Modifying account information
- Placing orders with stored payment methods
- Changing shipping addresses
- Completing checkout flows

This vulnerability is documented as **SEC-007** in [`specifications/security/SECURITY_AND_PCI.md`](../../../specifications/security/SECURITY_AND_PCI.md:77) with **HIGH** priority.

## Current State

**Affected Forms (10 POST endpoints identified):**
1. Customer login (`/{locale}/login`)
2. Customer registration (`/{locale}/register`)
3. Account update (`/{locale}/updateAccount`)
4. Shipping address (`/{locale}/checkout/shippingAddress`)
5. Billing address (`/{locale}/checkout/billingAddress`)
6. Payment information (`/{locale}/checkout/payment`)
7. Place order (`/{locale}/checkout/placeOrder`)
8. Cart operations (HTMX-based updates)
9. Search functionality
10. Locale switching

**Technology Stack:**
- Spring Security 6.x with dual filter chains
- Thymeleaf templates for server-side rendering
- HTMX for dynamic cart interactions
- Session-based state management

## Proposed Solution

Enable Spring Security's built-in CSRF protection for the storefront filter chain while maintaining backward compatibility and user experience. The solution involves:

1. **Enable CSRF in Security Configuration**: Remove the `csrf.disable()` call and configure appropriate CSRF token repository
2. **Update All Thymeleaf Forms**: Add `th:action` attributes to automatically inject CSRF tokens
3. **Configure HTMX Integration**: Add CSRF token headers to all HTMX requests
4. **Implement JavaScript Support**: Create utility for AJAX/fetch requests
5. **Add Comprehensive Testing**: Manual and automated test coverage

## Goals

- **Security**: Protect all state-changing operations from CSRF attacks
- **Compliance**: Align with OWASP Top 10 and PCI DSS requirements
- **User Experience**: Zero impact on legitimate user workflows
- **Maintainability**: Use Spring Security's standard mechanisms
- **Testing**: Comprehensive coverage of CSRF scenarios

## Non-Goals

- Implementing token-based authentication (JWT) - storefront remains session-based
- Modifying the backoffice security configuration (already has CSRF enabled)
- Changing the WebMCP JSON API endpoints (these use different authentication)
- Implementing rate limiting or other security enhancements (separate initiatives)

## Success Criteria

1. All 10 identified POST endpoints protected with CSRF tokens
2. HTMX cart operations continue to work seamlessly
3. No user-facing errors or workflow disruptions
4. Comprehensive test suite covering positive and negative scenarios
5. Security audit confirms CSRF protection is effective
6. Documentation updated for developers

## Risks & Mitigation

| Risk | Impact | Mitigation |
|------|--------|------------|
| Breaking existing HTMX functionality | HIGH | Thorough testing of cart operations; HTMX meta tag configuration |
| User session timeout during checkout | MEDIUM | Implement graceful error handling with session refresh |
| Third-party integrations broken | LOW | WebMCP API endpoints excluded from CSRF (stateless) |
| Performance overhead | LOW | CSRF tokens are lightweight; minimal impact expected |

## Timeline Estimate

- **Specification & Design**: 1 day
- **Implementation**: 2-3 days
- **Testing**: 2 days
- **Documentation**: 1 day
- **Total**: ~1 week

## Dependencies

- Spring Security 6.x (already in use)
- Thymeleaf 3.x (already in use)
- HTMX 1.x (already in use)
- No new external dependencies required

## Alternatives Considered

1. **Double Submit Cookie Pattern**: Rejected - Spring Security's synchronizer token pattern is more secure and well-tested
2. **Custom CSRF Implementation**: Rejected - Reinventing the wheel; Spring Security provides battle-tested solution
3. **SameSite Cookie Only**: Rejected - Insufficient protection; doesn't prevent all CSRF attacks
4. **Leaving CSRF Disabled**: Rejected - Unacceptable security risk for production deployment