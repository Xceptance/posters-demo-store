# Security & PCI Guidelines

> [!IMPORTANT]
> This document captures security decisions, PCI compliance constraints, and guidelines that apply across all areas of the application (storefront and backoffice). All teams and modules must adhere to these guidelines.

## Identity & Authentication

### SEC-001: Email Address Immutability

**Status:** Active
**Applies to:** Storefront, Backoffice
**Related:** Customer Backoffice (`customer-backoffice` change), PCI DSS Requirements 7 & 8

Customer email addresses serve as the primary authentication identifier (username). They are **strictly immutable** in all administrative interfaces.

**Rationale:**
- Email is the login credential. Changing it is equivalent to changing the account identity.
- Allowing admin email changes creates a **social engineering attack vector**: an attacker calls support and requests an email change, achieving instant account takeover.
- **Audit trail integrity**: Past audit log entries, order histories, and correspondence reference the original email. Changing it silently breaks the identity chain.
- **PCI DSS alignment**: Requirements 7 (restrict access) and 8 (identify and authenticate) mandate strong controls around identity management. Freely mutable authentication identifiers weaken that posture.

**If email change is ever required:**
- It must be implemented as a **separate, highly-privileged operation** (System Admin only).
- It must require a **confirmation flow** (e.g., dual approval or re-authentication).
- It must generate a **dedicated audit event** (`CUSTOMER_EMAIL_CHANGED`) that captures both the old and new email.
- It must be tracked as a distinct feature, not added to general profile editing.

---

## Payment Card Handling

### SEC-002: Credit Card Masking & Storage

**Status:** Active — Known Gap (see below)
**Applies to:** Storefront, Backoffice
**Related:** `BUS-EPIC-4: PCI Compliance Guidelines`

Credit card numbers must **never** be stored in plain text. The database must only contain masked card numbers (e.g., `**** **** **** 1234`). After initial input and masking, the full number must not be recoverable from stored data.

**Known Gap (Current Phase):**
When adding a credit card via a form submission, the full card number is transmitted over HTTPS and briefly exists in server memory before being masked and persisted. This is acceptable for this phase but does **not** meet full PCI DSS compliance.

**Future Requirements (`BUS-EPIC-4`):**
- Tokenization: Replace full card numbers with tokens before they reach the application server.
- Evaluate whether a third-party payment processor should handle all card data directly.
- Establish secure coding guidelines for any code that handles raw card data (memory clearing, no logging, etc.).

**CVV Handling:**
- CVV is **never** collected in the backoffice.
- CVV in the storefront is used for transaction validation only and must **never** be stored or logged.

---

## General Security Principles

### SEC-003: CSRF Protection

**Status:** Active
**Applies to:** All form submissions (Storefront, Backoffice)

All state-changing requests (POST, PUT, DELETE) must include a valid CSRF token. This is enforced globally by Spring Security.

### SEC-004: Destructive Action Confirmation

**Status:** Active
**Applies to:** Backoffice

All deletion or destructive actions in the backoffice must require explicit user confirmation via a Bootstrap popover dialog. JavaScript `alert()` or `confirm()` must **never** be used.

### SEC-005: Audit Logging for Sensitive Operations

**Status:** Active
**Applies to:** Backoffice

All customer data access (view, edit, delete) and all identity-related changes must be recorded in the audit log with the acting admin user, timestamp, and action type.

---

## Implementation Security Gaps

### SEC-006: H2 Console Exposure

**Status:** Active Gap — Requires Remediation
**Applies to:** All Environments
**Related:** `implementation/src/main/resources/application.yml`

**Current State:**
The H2 database console is enabled globally in [`application.yml`](implementation/src/main/resources/application.yml:33):
```yaml
spring:
  h2:
    console:
      enabled: true
      path: /h2-console
```

**Risk:**
- Exposes direct database access via web interface at `/h2-console`
- No authentication required beyond knowing the JDBC URL
- Allows arbitrary SQL execution, data exfiltration, and schema manipulation
- **Critical security vulnerability in production environments**

**Required Actions:**
1. **Immediate**: Disable H2 console in production profiles
2. **Development**: Restrict to `dev` profile only via `spring.profiles.active=dev`
3. **Alternative**: If needed for debugging, protect behind authentication and IP whitelist

**Implementation:**
```yaml
spring:
  h2:
    console:
      enabled: false  # Default for production
---
spring:
  config:
    activate:
      on-profile: dev
  h2:
    console:
      enabled: true  # Only in dev profile
```

---

### SEC-007: CSRF Protection for Storefront

**Status:** Active Gap — Requires Remediation
**Applies to:** Storefront
**Related:** `implementation/src/main/java/com/xceptance/posters/config/SecurityConfig.java`

**Current State:**
CSRF protection is globally disabled for the storefront in [`SecurityConfig`](implementation/src/main/java/com/xceptance/posters/config/SecurityConfig.java:75):
```java
.csrf(csrf -> csrf.disable());
```

**Risk:**
- Exposes all storefront forms to Cross-Site Request Forgery attacks
- Attackers can trick authenticated users into submitting malicious requests
- Particularly dangerous for: cart operations, checkout, account updates, order placement

**Required Actions:**
1. **Enable CSRF protection** for storefront filter chain
2. **Add CSRF tokens** to all Thymeleaf forms using `th:action` (auto-includes token)
3. **Update AJAX calls** to include CSRF token in headers
4. **Test thoroughly** to ensure no form breakage

**Implementation:**
```java
@Bean
@Order(2)
public SecurityFilterChain storefrontFilterChain(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/**")
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()
        )
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        );
    return http.build();
}
```

---

### SEC-008: API Rate Limiting

**Status:** Recommended Enhancement
**Applies to:** All API Endpoints
**Related:** `/api/v2/*` endpoints

**Current State:**
No rate limiting is implemented on API endpoints, including:
- `/api/v2/checkout` (checkout processing)
- `/api/v2/catalog` (catalog queries)
- Other JSON APIs for AI agents (WebMCP)

**Risk:**
- Denial of Service (DoS) attacks via request flooding
- Brute force attacks on payment processing
- Resource exhaustion from automated scrapers
- Abuse of AI agent endpoints

**Recommended Actions:**
1. **Implement rate limiting** using Spring framework or external solution (e.g., Bucket4j, Resilience4j)
2. **Apply tiered limits**:
   - Checkout: 10 requests/minute per IP
   - Catalog: 100 requests/minute per IP
   - Search: 50 requests/minute per IP
3. **Return HTTP 429** (Too Many Requests) with `Retry-After` header
4. **Log rate limit violations** for security monitoring

**Example Implementation (Bucket4j):**
```java
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) {
        String ip = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ip, k ->
            Bucket.builder()
                .addLimit(Bandwidth.simple(100, Duration.ofMinutes(1)))
                .build()
        );
        
        if (bucket.tryConsume(1)) {
            return true;
        }
        
        response.setStatus(429);
        return false;
    }
}
```

---

### SEC-009: Database Migration Strategy

**Status:** Recommended Enhancement
**Applies to:** Database Schema Management
**Related:** `implementation/src/main/resources/application.yml`

**Current State:**
Using Hibernate's `ddl-auto: update` for schema management:
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

**Risk:**
- **Production hazard**: Automatic schema changes can cause data loss
- **No rollback capability**: Failed migrations leave database in inconsistent state
- **No audit trail**: Schema changes are not tracked or versioned
- **Race conditions**: Multiple instances can conflict during startup
- **Unpredictable behavior**: Hibernate may drop columns it doesn't recognize

**Recommended Actions:**
1. **Adopt Flyway or Liquibase** for versioned database migrations
2. **Change to `ddl-auto: validate`** in production (fails if schema doesn't match)
3. **Use `ddl-auto: none`** and rely entirely on migration tool
4. **Create baseline migration** from current schema
5. **Implement CI/CD pipeline** for migration testing

**Example Flyway Setup:**
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # Production
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
```

**Migration File Structure:**
```
src/main/resources/db/migration/
├── V1__initial_schema.sql
├── V2__add_audit_log.sql
└── V3__add_customer_indexes.sql
```

**Benefits:**
- Version-controlled schema changes
- Repeatable deployments
- Rollback capability
- Team collaboration on schema evolution
- Production safety
