## 0. Role-Based Access Control & Access Verification

- [x] 0.1 Add "Customer Admin" role to Role enum and seed data
- [x] 0.2 Create manual test cases for Backoffice Login/Logout
- [x] 0.3 Create manual test cases for User Creation & Role-Based Access
- [x] 0.4 Enforce access control in ModuleAccessInterceptor for /backoffice/customers/**
- [x] 0.5 Update sidebar navigation to only show Customers for authorized roles

## 1. External Customer Number

- [x] 1.1 Add customer_number sequence and field to Customer entity
- [x] 1.2 Rename CatalogCustomer to Customer and CatalogAddress to CustomerAddress
- [x] 1.3 Seed 10 test customers via XML import on startup
- [x] 1.4 Trigger async search indexer after XML import

## 2. Storefront Customer Data Enhancements

- [x] 2.1 Update CustomerProfile.lastLogin on successful storefront authentication
- [x] 2.2 Initialize CustomerProfile.lastPasswordChange to createdAt on creation
- [x] 2.3 Verify Customer.createdAt is populated via @PrePersist

## 3. Fix Broken Audit Log

- [x] 3.1 Extend AuditLogEntry.Action enum with all customer-related action types
- [x] 3.2 Verify the audit log page loads without exceptions

## 4. Customer List with Search

- [x] 4.1 Implement asynchronous Lucene search indexing for Customer
- [x] 4.2 Create CustomerService for search, pagination, and order count queries
- [x] 4.3 Create CustomersModuleController for list view
- [x] 4.4 Implement text-based search (exact/quoted and free-text)
- [x] 4.5 Build sortable and paginated customer list table UI with empty state handling

## 5. Customer Detail View

- [x] 5.1 Implement read-only profile summary in CustomerService and Controller
- [x] 5.2 Build detail page UI with sections for profile, addresses, and credit cards
- [x] 5.3 Add navigation to detail view from list
- [x] 5.4 Log CUSTOMER_VIEWED event in audit log when accessing detail view

## 6. Customer Profile Editing

- [ ] 6.1 Implement optimistic locking (@Version) for CustomerProfile
- [ ] 6.2 Add HTMX-based inline editing for first, last, and middle names
- [ ] 6.3 Handle OptimisticLockingFailureException gracefully with UI alerts
- [ ] 6.4 Log CUSTOMER_UPDATED event in audit log on profile edit

## 7. Address Management - View and Add

- [ ] 7.1 Add 'name' field to CustomerAddress entity
- [ ] 7.2 Display list of addresses on customer detail page
- [ ] 7.3 Implement 'Add Address' functionality in Controller and UI
- [ ] 7.4 Log CUSTOMER_ADDRESS_CREATED event in audit log

## 8. Address Management - Edit and Delete

- [ ] 8.1 Implement 'Edit Address' functionality in Controller and UI
- [ ] 8.2 Implement 'Delete Address' functionality with Bootstrap popover confirmation
- [ ] 8.3 Log CUSTOMER_ADDRESS_UPDATED and CUSTOMER_ADDRESS_DELETED events in audit log

## 9. Credit Card Lifecycle

- [ ] 9.1 Implement 'Add Credit Card' functionality with validation and formatting
- [ ] 9.2 Implement masked storage for credit cards in backend
- [ ] 9.3 Implement 'Delete Credit Card' functionality with confirmation
- [ ] 9.4 Log CUSTOMER_CARD_ADDED and CUSTOMER_CARD_DELETED events in audit log

## 10. Customer Module Dashboard

- [ ] 10.1 Create dashboard endpoint at /backoffice/customers/dashboard
- [ ] 10.2 Implement metrics queries (total count, created 24h, orders 24h)
- [ ] 10.3 Build dashboard UI with tiles and EChart visualization
