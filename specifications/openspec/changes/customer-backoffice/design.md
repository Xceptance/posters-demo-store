## Context

The backoffice currently has a fully functional Security module (Users, Roles, Audit Log) but every other module (Customers, Catalog, Orders) is a placeholder. We are building the Customers module as the first concrete business module under the `BUS-EPIC-1: Backoffice` epic. The data model already exists (`Customer`, `CustomerProfile`, `CustomerAddress`, `CatalogCreditCard`), and navigation stubs are wired. We need to implement search, pagination, CRUD, detail views, and a dashboard. This module will establish the reference implementation and UI patterns for all future backoffice data-management modules.

## Goals / Non-Goals

**Goals:**
- Provide a robust, TDD-driven implementation of the Customer Backoffice module.
- Introduce an external, auto-incrementing customer number.
- Establish clean architectural separation between storefront and backoffice logic.
- Implement Lucene-backed search and standard pagination for customer listing.
- Provide secure profile, address, and credit card management (masked storage).
- Implement role-based access control with a new "Customer Admin" role.
- Implement comprehensive audit logging for customer-related actions.

**Non-Goals:**
- PCI compliance (tokenization, encrypted transport) for credit cards (deferred to a future change).
- Password management or resetting from the backoffice (storefront password change is `BUS-FEAT-9`; admin-triggered resets invalidated as unnecessary — customers self-service).
- Email address changes from the backoffice (security decision — see Decisions and `specifications/security/SECURITY_AND_PCI.md`).
- Advanced search-ahead/autocomplete in the backoffice.

## Decisions

- **Customer Identifier:** Use an auto-incrementing `customer_number` sequence while retaining the existing UUID as the internal primary key. 
  - *Rationale*: Provides a human-readable identifier without breaking existing UUID relationships.
- **Architectural Separation:** Do not reuse storefront controllers, services, or business logic. Create dedicated `CustomerService` and `CustomersModuleController` for the backoffice.
  - *Rationale*: Ensures clean boundaries, prevents cross-contamination, and allows independent evolution.
- **Audit Logging:** Pre-register all customer-related action types in the `AuditLogEntry.Action` enum.
  - *Rationale*: Fixes the existing crash and prepares for comprehensive logging of view, edit, and delete events.
- **Credit Card Masking:** Store only masked credit card numbers. 
  - *Rationale*: Minimizes security risk while providing enough information for identification.
- **Search Implementation:** Use Lucene-style free-text search for the customer list. The new `customer_number` will be indexed as a searchable and sortable field.
  - *Rationale*: Provides powerful search capabilities and consistency with product search patterns. Allows quick lookup and sorting by the primary external identifier.
- **Search Indexing Lifecycle:** Lucene indexing must be strictly asynchronous. Synchronous indexing is prohibited. We will use a size-limited queue to notify the background indexer.
  - **Bounded Queue & Fast Path:** As long as the queue is not full, it passes the customer identifiers directly to the indexer, avoiding extra database queries.
  - **Overflow & Slow Path:** If the queue is full (overflow), it drops the individual data payloads and acts purely as a "nudge" signal. The background indexer, on its next run, will query the database to identify all changes since its last run and process them in bulk.
  - *Rationale*: Prevents blocking web requests during data modifications and avoids memory overflow under heavy load. The dual-path approach optimizes for both typical load (fast path, no DB queries) and spike load (slow path, bulk DB query). It also decouples the data layer from the search engine.
- **Concurrent Edit Protection:** Implement optimistic locking (`@Version`) for `CustomerProfile`. Use inline HTMX form validation to gracefully handle `OptimisticLockingFailureException` by preserving user input and rendering a localized Bootstrap alert.
  - *Rationale*: Prevents silent data overwrites between backoffice admins and storefront users while providing a modern, seamless UX without full page reloads.
- **Email Immutability:** Customer email addresses are strictly immutable in the backoffice. No admin role may change a customer's email. If this capability is ever needed, it must be a separate, highly-privileged operation (System Admin only) with its own confirmation flow and a dedicated audit event (`CUSTOMER_EMAIL_CHANGED`). See `specifications/security/SECURITY_AND_PCI.md`.
  - *Rationale*: Email is the authentication identifier. Allowing admin changes creates a social engineering attack vector (account takeover via support call), corrupts audit trail integrity, and weakens PCI DSS identity management posture (Requirements 7 & 8).
- **Order Count Source:** Order counts per customer (for the list view and dashboard) are retrieved via a dedicated backoffice `OrderService`. This service queries the orders repository directly and is not shared with or delegated to any storefront service.
  - *Rationale*: Maintains clean architectural separation. The backoffice needs its own read path for aggregate data that storefront business logic should not expose.
- **Initial Index Trigger:** After XML import of seed data on startup, the async indexer must be triggered immediately to ensure all imported customers are searchable from the first request. This is the only acceptable synchronous-adjacent startup hook — it fires once and hands off to the async queue mechanism for all subsequent changes.
  - *Rationale*: Without this, a freshly seeded environment would return empty search results until the background indexer runs its first scheduled cycle.
- **UI Framework:** Reuse the existing Bootstrap 5 design system and HTMX for dynamic interactions. All deletion actions require a Bootstrap popover confirmation dialog.
  - *Rationale*: Maintains consistency and leverages the existing tech stack effectively.

## Risks / Trade-offs

- **Risk: PCI Compliance Gap:** Full credit card numbers are submitted via the form and briefly exist in transit and server memory before masking.
  - **Mitigation:** Explicitly document this known gap as acceptable for this phase. Full PCI compliance is out of scope and will be addressed in a future dedicated security enhancement.
- **Risk: Schema Breakage:** Adding `customer_number`, renaming entities (`CatalogCustomer` to `Customer`, `CatalogAddress` to `CustomerAddress`), and adding address names will break the existing schema.
  - **Mitigation:** This is expected and acceptable. The database schema will be recreated. We will seed 10 test customers on startup for development to alleviate data loss friction.

## Migration Plan

- The application uses JPA auto-generation. The schema changes (`customer_number`, entity renames) will require a database drop and recreate.
- The startup sequence will be updated to seed 10 test customers.

## Open Questions

- None at this time.
