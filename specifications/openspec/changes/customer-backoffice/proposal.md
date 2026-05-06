## Why

The backoffice currently has a fully functional Security module (Users, Roles, Audit Log) but every other module — including Customers — is still a placeholder. The Customers module is the natural next step because the data model already exists (`Customer`, `CustomerProfile`, `CustomerAddress`, `CatalogCreditCard`), the navigation stubs are wired (`BackofficeModule.CUSTOMERS_*`), and it exercises the same patterns (search, pagination, CRUD, detail views) that will be reused for Catalog and Orders later. Building this module first establishes the reference implementation for all future backoffice data-management modules.

This is the first concrete work under the open backlog epic `BUS-EPIC-1: Backoffice`.

## What Changes

> [!IMPORTANT]
> **Incremental delivery constraint**: The application must remain fully functional after completing each step. No step may leave the application in a broken state. Every step is a shippable increment.

> [!CAUTION]
> **Step gate**: Only the user can confirm and authorize starting the next development step. Never auto-advance to the next step.

> [!NOTE]
> **Technology stack**: The backoffice follows the existing Bootstrap 5 design system and continues to use HTMX for dynamic interactions. All backoffice requests are CSRF-secured. All deletion actions require a Bootstrap popover confirmation dialog — never a JavaScript `alert()` or `confirm()`.

> [!CAUTION]
> **Clean separation**: The backoffice must not reuse storefront controllers, services, or business logic. Shared JPA entities and repositories are acceptable, but all backoffice-specific logic must live in its own service and controller classes.

> [!NOTE]
> **Form validation**: Input validation rules across all backoffice modules will be standardized in a future dedicated initiative. For now, apply only the minimum JPA/DB constraints (not null, max length). Do not implement custom validation frameworks or complex UI validation patterns in this module.

> [!IMPORTANT]
> **Coding standards**: All code must follow `AGENTS.md` — Allman brace style, aggressive `final` modifiers, Apache license headers, AI model attribution on new files, explicit imports (no FQCN inline), JDK 21 features, and thorough code documentation. TDD is mandatory.

> [!IMPORTANT]
> **Testing flow**: Unit tests → implement → manual test cases → manually verify and fix → UI test automation. Test automation is only created after the implementation has been manually confirmed and all issues fixed. Automation freezes the verified state as a regression guard.

> [!NOTE]
> **Implementation log**: Maintain a log file documenting all issues encountered, bugs found, and bugs fixed during implementation.

### Step 0: Role-Based Access Control & Access Verification
- **New "Customer Admin" Role** — Add a built-in role granting access to `customers` module only (not the global backoffice dashboard). This is the minimum-privilege role for customer management.
- **Backoffice Login/Logout Test Baseline** — Manual test cases (`/test-create`) to verify login and logout as the default admin user. Test cases create the necessary users — no seeded test backoffice users. Execute manually, confirm correct behavior, then create UI test automation to freeze the verified state.
- **User Creation & Role-Based Access Testing** — Manual test cases (`/test-create`) to: create backoffice users with each relevant role, log in as each, and verify that the Customers section is visible and accessible (or hidden and blocked) based on the assigned role. Execute manually, confirm correct behavior, then create UI test automation to freeze the verified state.
- **Access Enforcement Verification** — Verify that `ModuleAccessInterceptor` correctly blocks users without `customers` module access from reaching any `/backoffice/customers/**` URL. System Admin, Business Admin, and Customer Admin should have access; Catalog User and Order User should not.
- **Sidebar Visibility** — Users without the `customers` module must not see the Customers entry in the sidebar navigation.
- **Testing**: Unit tests → implement → manual test cases (`/test-create`) → execute and verify (`/test-execute`) → fix issues → UI test automation (freeze verified behavior).

### Step 1: External Customer Number
- **Customer Number** — Add an externally-facing, auto-incrementing customer number to `Customer` (e.g., `0000001`, `0000002`). The existing UUID remains the internal primary key. The customer number is a database sequence, zero-padded for display, and assigned automatically on creation. This is shown in the backoffice and anywhere a human-readable customer identifier is needed.
- **Schema Breaking** — This will modify the `customers` table schema. That is expected and acceptable. Also rename `CatalogCustomer` to `Customer` and `CatalogAddress` to `CustomerAddress`.
- **Test Data** — Seed 10 test customers via XML import on startup for development and testing purposes. After the XML import completes, the async indexer must be triggered to ensure all seeded customers are immediately searchable.
- **Testing (TDD)**: Unit tests → manual test cases → verify and fix → UI test automation (freeze).

### Step 2: Storefront Customer Data Enhancements (Foundation)
- **Last Login Tracking** — The storefront login flow must update `CustomerProfile.lastLogin` on every successful authentication. Currently the field exists but is not populated.
- **Last Password Change Tracking** — Initialize `CustomerProfile.lastPasswordChange` to `createdAt` on customer creation. The storefront does not currently have a password-change flow, so this field will not be updated at runtime yet. A storefront password-change feature is tracked in the business backlog (`BUS-FEAT-9`).
- **Created Timestamp** — `Customer.createdAt` already exists and is populated via `@PrePersist`. Verified, no changes needed.
- **Testing (TDD)**: Unit tests → manual test cases → verify and fix → UI test automation (freeze).

### Step 3: Fix Broken Audit Log
- **Problem** — The database contains audit log rows with action `CUSTOMER_UPDATED` (from a previous discarded branch), but `AuditLogEntry.Action` enum does not define this constant. Loading the audit log page triggers `InvalidDataAccessApiUsageException: No enum constant ... Action.CUSTOMER_UPDATED`.
- **Fix** — Extend the `Action` enum with all customer-related action types needed for this change (e.g., `CUSTOMER_VIEWED`, `CUSTOMER_UPDATED`, `CUSTOMER_ADDRESS_CREATED`, `CUSTOMER_ADDRESS_UPDATED`, `CUSTOMER_ADDRESS_DELETED`, `CUSTOMER_CARD_ADDED`, `CUSTOMER_CARD_DELETED`). This resolves the crash immediately and pre-registers actions for later steps.
- **Verification** — Start the application and confirm the audit log page loads without exceptions.

### Step 4: Customer List with Search
- **Search** — Text-based search supporting both exact/quoted queries (e.g., `"test@varmail.com"`) and Lucene-style free-text search (e.g., `varmail.com`, a first name, or a **Customer No** like `0000001`). No search-ahead/autocomplete — user must press Enter to execute the search. An empty search (no query) returns all customers.
- **Results Table** — Sortable by clicking column headers (including sorting by Customer No). Columns: Customer No (external number), First + Last Name, Email, Last Login, Order Count. Shows current result count and total count. Clicking a customer number or using the three-dot action menu navigates to the customer detail view.
- **Empty State** — When a search returns no results, the table is not rendered. Only a clear message is shown (e.g., "No customers found for your search.").
- **Pagination** — Page size options: 25, 50, 100, 200. Default is 25. This pagination pattern will later be adopted by all other backoffice tables.
- **Service Layer** — Initial `CustomerService` for search, pagination, and order count queries. Order count is retrieved via a dedicated `OrderService` query — not via storefront services.
- **Testing (TDD)**: Unit tests → manual test cases → verify and fix → UI test automation (freeze).

### Step 5: Customer Detail View
- **Detail Page** — Read-only profile summary with sections for addresses and credit cards (masked). Displays customer number, name, email, order count, last login, and created date.
- **Navigation** — Reached by clicking customer number or three-dot menu in the list.
- **Audit Logging** — Viewing a customer detail page is recorded in the audit log (`CUSTOMER_VIEWED`).
- **Testing (TDD)**: Unit tests → manual test cases → verify and fix → UI test automation (freeze).

### Step 6: Customer Profile Editing
- **Editable Fields** — From the detail view, edit first name, last name, and middle name only. Email is immutable (display-only). Password cannot be set, changed, or reset from the backoffice.
- **Audit Logging** — All profile edits are recorded in the audit log (visible in Security > Audit Log).
- **Testing (TDD)**: Unit tests → manual test cases → verify and fix → UI test automation (freeze).

### Step 7: Address Management — View and Add
- **Address Name** — Each address has a user-defined name (free text, e.g., "Home", "Office"). This is a new field on `CustomerAddress` (schema change).
- **View** — Display all addresses on the customer detail page.
- **Add** — Add new addresses from the detail view.
- **Audit Logging** — Address creation is recorded in the audit log.
- **Testing (TDD)**: Unit tests → manual test cases → verify and fix → UI test automation (freeze).

### Step 8: Address Management — Edit and Delete
- **Edit** — Edit existing customer addresses.
- **Delete** — Delete customer addresses with confirmation.
- **Audit Logging** — Address edit/delete are recorded in the audit log.
- **Testing (TDD)**: Unit tests → manual test cases → verify and fix → UI test automation (freeze).

### Step 9: Credit Card Lifecycle
- **Add and Delete** — Add and delete credit cards from the detail view. Credit cards can never be edited. The add dialog should follow the same behavior as the storefront payment form (see `specifications/features/STOREFRONT.md` § Payment): card vendor detection, auto-formatting, Luhn validation, and combined MM/YY expiry. CVV is not collected in the backoffice.
- **Masking** — The database only stores the masked card number (never the full number). After initial input, the card is only ever displayed in masked form. CVV is never captured or stored.
- **Security Note** — When adding a CC, the full card number is submitted via the form and briefly exists in transit and server memory before masking. This is a known gap; full PCI compliance (tokenization, encrypted transport) is out of scope and will follow in a future change.
- **Audit Logging** — Card add/delete are recorded in the audit log.
- **Testing (TDD)**: Unit tests → manual test cases → verify and fix → UI test automation (freeze).

### Step 10: Customer Module Dashboard
- **Scope** — This is the dashboard *within the Customers module* (at `/backoffice/customers/dashboard`), not the global backoffice dashboard. It is visible to the Customer Admin role. The global backoffice dashboard remains unchanged and is not accessible to Customer Admin.
- **Dashboard Tiles** — Replace the placeholder with a real dashboard showing four tiles:
  - Total customer count
  - Customers created in the last 24 hours
  - Customers with new orders in the last 24 hours
  - Customer creation over the last 24 hours as an EChart line/bar chart
- **Testing (TDD)**: Unit tests → manual test cases → verify and fix → UI test automation (freeze).

## Capabilities

### New Capabilities
- `backoffice-customer-access`: Role-based access control for the customer module — new "Customer Admin" role (customers-only, no dashboard), login/logout test baseline, user creation and role-based access verification
- `customer-number`: External auto-incrementing customer number alongside existing UUID primary key
- `storefront-customer-tracking`: Capture lastLogin in storefront login flow, initialize lastPasswordChange, and verify createdAt timestamp generation
- `backoffice-customer-indexing`: Asynchronous background queue for updating the Lucene customer search index on creation or modification
- `backoffice-customer-list`: Paginated, searchable (Lucene-style), sortable customer listing with configurable page sizes (25/50/100/200)
- `backoffice-customer-dashboard`: Customer module dashboard with tiles (total count, created 24h, orders 24h) and EChart visualization
- `backoffice-customer-detail`: Customer profile view with addresses, credit cards, and order summary
- `backoffice-customer-profile-edit`: Profile name editing with audit logging
- `backoffice-customer-addresses`: Address view, add, edit, delete with audit logging
- `backoffice-customer-credit-cards`: Credit card add and delete (masked storage, no edit) with audit logging
- `backoffice-customer-service`: Service layer for customer backoffice business logic

### Modified Capabilities
- `backoffice-audit-log`: Add customer-related audit event types to fix the broken enum mapping and support future customer audit logging
- `backoffice-roles`: Add new built-in "Customer Admin" role with `customers` module access (no dashboard)

## Impact

### Data Model (Step 1 & 7)
- **Schema change**: `customers` table gets a new `customer_number` column (integer, sequence, unique, not null). DB schema will break and be recreated. Addresses table gets a new `name` column.
- **Entity**: `CatalogCustomer` renamed to `Customer` and gains a `customerNumber` field. `CatalogAddress` renamed to `CustomerAddress` and gains a `name` field.

### Storefront (Step 2)
- **Authentication flow**: Must update `CustomerProfile.lastLogin` on successful login.
- **Password change**: Initialize `lastPasswordChange` to `createdAt`. Actual runtime updates deferred to `BUS-FEAT-9`.

### Backoffice (Steps 0, 3–10)
- **Roles**: New "Customer Admin" role with `customers`-only module access.
- **Entities**: `Customer` — no lock/unlock state needed.
- **Repository**: `CustomerRepository` needs search and pagination methods (Spring Data `Specification` or `@Query` with LIKE/ILIKE).
- **Service Layer**: New `CustomerService` class.
- **Controller**: `CustomersModuleController` is rewritten from stub to full CRUD controller.
- **Templates**: New Thymeleaf templates under `backoffice/customers/` (dashboard, list, detail, edit forms, address form, fragments).
- **CSS**: Minimal — reuses existing `backoffice.css` design system.
- **Security**: All endpoints inherit existing `CUSTOMERS` module access check via `ModuleAccessInterceptor`.
- **Test Automation**: New test package `com.xceptance.posters.backoffice.customers` in `test-automation/`.
- **Test Management**: New test domain `backoffice-customers` under `test-management/tests/`.
- **Audit Log**: New event types added to `AuditLogService`.

