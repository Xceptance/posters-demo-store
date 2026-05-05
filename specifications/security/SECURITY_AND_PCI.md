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
