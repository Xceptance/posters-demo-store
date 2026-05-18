/*
 * Copyright 2026 Xceptance Software Technologies GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xceptance.posters.service.backoffice;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xceptance.posters.entity.AuditLogEntry;
import com.xceptance.posters.entity.Customer;
import com.xceptance.posters.entity.CustomerAddress;
import com.xceptance.posters.entity.CatalogCreditCard;
import com.xceptance.posters.entity.CustomerProfile;
import com.xceptance.posters.repository.AuditLogRepository;
import com.xceptance.posters.repository.CatalogOrderRepository;
import com.xceptance.posters.repository.CustomerAddressRepository;
import com.xceptance.posters.repository.CustomerProfileRepository;
import com.xceptance.posters.repository.CustomerRepository;
import com.xceptance.posters.service.CustomerSearchService;
import com.xceptance.posters.service.CustomerSearchService.CustomerSearchResult;

/**
 * Backoffice-specific service for customer management operations.
 *
 * <p>This service is intentionally separate from storefront controllers
 * and services to maintain clean architectural boundaries (see design.md,
 * "Architectural Separation" decision).</p>
 *
 * <p>Provides search/pagination via the Lucene-backed
 * {@link CustomerSearchService}, profile and address resolution,
 * and audit logging for all customer access.</p>
 *
 * <p>Created exclusively by AI (Claude Opus 4.6).</p>
 */
@Service("backofficeCustomerService")
@Transactional(readOnly = true)
public class CustomerService
{
    private final CustomerRepository customerRepository;
    private final CustomerProfileRepository profileRepository;
    private final CatalogOrderRepository orderRepository;
    private final CustomerAddressRepository addressRepository;
    private final CustomerSearchService searchService;
    private final AuditLogRepository auditLogRepository;

    /**
     * Constructs the backoffice customer service with all required dependencies.
     *
     * @param customerRepository repository for customer entities
     * @param profileRepository  repository for customer profile entities
     * @param orderRepository    repository for order aggregation queries
     * @param addressRepository  repository for customer addresses
     * @param searchService      Lucene-backed customer search indexer
     * @param auditLogRepository repository for persisting audit log entries
     */
    public CustomerService(final CustomerRepository customerRepository,
                           final CustomerProfileRepository profileRepository,
                           final CatalogOrderRepository orderRepository,
                           final CustomerAddressRepository addressRepository,
                           final CustomerSearchService searchService,
                           final AuditLogRepository auditLogRepository)
    {
        this.customerRepository = customerRepository;
        this.profileRepository = profileRepository;
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.searchService = searchService;
        this.auditLogRepository = auditLogRepository;
    }

    // ------------------------------------------------------------------
    // List / Search
    // ------------------------------------------------------------------

    /**
     * Searches and paginates customers, enriching each result with profile
     * data and an order count.
     *
     * @param query free-text search query (may be blank for all customers)
     * @param page  zero-based page number
     * @param size  page size
     * @return paginated result with enriched customer list items
     */
    public PaginatedCustomerResult searchCustomers(final String query,
                                                   final int page,
                                                   final int size,
                                                   final String sort,
                                                   final String dir)
    {
        final int offset = page * size;
        final CustomerSearchResult searchResult = searchService.search(query, offset, size, sort, dir);

        final List<CustomerListItem> items = searchResult.customerIds().stream()
            .map(this::toListItem)
            .filter(item -> item != null)
            .toList();

        return new PaginatedCustomerResult(items, searchResult.totalHits(), page, size);
    }

    /**
     * Converts a customer UUID into an enriched list item by loading the
     * customer, their profile, and their order count.
     *
     * <p>Returns {@code null} for stale index entries (customer deleted from DB
     * but not yet removed from the async Lucene index).</p>
     *
     * @param customerId UUID of the customer
     * @return enriched list item, or {@code null} if the customer no longer exists
     */
    private CustomerListItem toListItem(final UUID customerId)
    {
        final Customer customer = customerRepository.findById(customerId)
            .orElse(null);

        if (customer == null)
        {
            return null;
        }

        final CustomerProfile profile = profileRepository
            .findByCustomer_Id(customerId).orElse(null);
        final long orderCount = orderRepository.countByCustomer_Email(customer.getEmail());

        return new CustomerListItem(customer, profile, orderCount);
    }

    // ------------------------------------------------------------------
    // Detail view
    // ------------------------------------------------------------------

    /**
     * Loads customer detail data without recording an audit event.
     * Use this for non-interactive lookups (e.g. modal pre-population)
     * where an audit trail is not required.
     *
     * @param customerId UUID of the customer to load
     * @return customer detail aggregate
     * @throws IllegalArgumentException if the customer does not exist
     */
    public CustomerDetail getCustomerDetailNoAudit(final UUID customerId)
    {
        final Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Customer not found: " + customerId));

        final CustomerProfile profile = profileRepository
            .findByCustomer_Id(customerId).orElse(null);

        final List<CustomerAddress> addresses = addressRepository
            .findByCustomer_Id(customerId);

        return new CustomerDetail(customer, profile, addresses);
    }

    /**
     * Loads a full customer detail view (profile, addresses) and logs a
     * {@code CUSTOMER_VIEWED} audit event.
     *
     * @param customerId UUID of the customer to view
     * @param adminId    ID of the admin user viewing the customer
     * @param adminName  display name of the admin user
     * @return customer detail aggregate
     * @throws IllegalArgumentException if the customer does not exist
     */
    @Transactional
    public CustomerDetail getCustomerDetails(final UUID customerId,
                                             final Long adminId,
                                             final String adminName)
    {
        final CustomerDetail detail = getCustomerDetailNoAudit(customerId);

        // Audit: log view event
        final AuditLogEntry audit = new AuditLogEntry();
        audit.setUserId(adminId);
        audit.setUsername(adminName);
        audit.setAction(AuditLogEntry.Action.CUSTOMER_VIEWED);
        audit.setTargetType("Customer");
        audit.setTargetId(detail.customer().getCustomerNumber());
        audit.setDetails("Customer details viewed");
        auditLogRepository.save(audit);

        return detail;
    }

    // ------------------------------------------------------------------
    // Profile Editing / Creation
    // ------------------------------------------------------------------

    /**
     * Creates a new customer with the given details, including a profile.
     * Logs a {@code CUSTOMER_CREATED} audit event.
     */
    @Transactional
    public Customer createCustomer(final String firstName,
                                   final String middleName,
                                   final String lastName,
                                   final String email,
                                   final String plainPassword,
                                   final Long adminId,
                                   final String adminName)
    {
        if (customerRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        final Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setMiddleName(middleName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.hashPassword(plainPassword);

        final Customer savedCustomer = customerRepository.saveAndFlush(customer);

        final CustomerProfile profile = new CustomerProfile();
        profile.setCustomer(savedCustomer);
        profile.setPassword(savedCustomer.getPassword());
        profile.setLastPasswordChange(java.time.LocalDateTime.now());
        profileRepository.saveAndFlush(profile);

        // Async re-index for Lucene search
        searchService.indexCustomerAsync(savedCustomer.getId());

        // Audit: log creation event
        final AuditLogEntry audit = new AuditLogEntry();
        audit.setUserId(adminId);
        audit.setUsername(adminName);
        // Using a generic CUSTOMER_UPDATED action as CUSTOMER_CREATED is not in enum. Wait, let me check the enum. I'll just use CUSTOMER_UPDATED or add CUSTOMER_CREATED if needed.
        // The spec said "Add customer-related action types...".
        // Let's assume CUSTOMER_UPDATED for now, but I should probably add CUSTOMER_CREATED. Wait, the spec says "Add customer-related action types...". I'll use CUSTOMER_UPDATED and we can add CUSTOMER_CREATED to AuditLogEntry.Action later if needed. But the prompt said it's pre-registered. 
        // I will use CUSTOMER_CREATED. 
        // Let me check AuditLogEntry.Action first.
        
        audit.setAction(AuditLogEntry.Action.CUSTOMER_CREATED);
        audit.setTargetType("Customer");
        audit.setTargetId(savedCustomer.getCustomerNumber());
        audit.setDetails("Customer created via backoffice");
        auditLogRepository.save(audit);

        return savedCustomer;
    }

    /**
     * Updates the first, middle, and last name of a customer. Uses JPA's optimistic
     * locking (@Version) to prevent concurrent modification overwrites.
     *
     * @param customerId UUID of the customer to update
     * @param firstName  the new first name
     * @param middleName the new middle name (may be null)
     * @param lastName   the new last name
     * @param version    the expected current version for optimistic locking
     * @param adminId    ID of the admin user making the change
     * @param adminName  display name of the admin user
     * @return the updated customer
     * @throws IllegalArgumentException if the customer is not found
     * @throws org.springframework.orm.ObjectOptimisticLockingFailureException if the version does not match
     */
    @Transactional
    public Customer updateCustomerNames(final UUID customerId,
                                        final String firstName,
                                        final String middleName,
                                        final String lastName,
                                        final Integer version,
                                        final Long adminId,
                                        final String adminName)
    {
        final Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Customer not found: " + customerId));

        if (!customer.getVersion().equals(version))
        {
            throw new org.springframework.orm.ObjectOptimisticLockingFailureException(Customer.class, customerId);
        }

        customer.setFirstName(firstName);
        customer.setMiddleName(middleName);
        customer.setLastName(lastName);

        final Customer updatedCustomer = customerRepository.saveAndFlush(customer);

        // Async re-index for Lucene search
        searchService.indexCustomerAsync(customerId);

        // Audit: log update event
        final AuditLogEntry audit = new AuditLogEntry();
        audit.setUserId(adminId);
        audit.setUsername(adminName);
        audit.setAction(AuditLogEntry.Action.CUSTOMER_UPDATED);
        audit.setTargetType("Customer");
        audit.setTargetId(customer.getCustomerNumber());
        audit.setDetails("Customer names updated via inline edit");
        auditLogRepository.save(audit);

        return updatedCustomer;
    }

    /**
     * Deletes a customer and their associated profile, addresses, and credit cards.
     * Logs a {@code CUSTOMER_DELETED} audit event.
     */
    @Transactional
    public void deleteCustomer(final UUID customerId,
                               final Long adminId,
                               final String adminName)
    {
        final Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        final Long customerNumber = customer.getCustomerNumber();

        // Remove addresses
        final List<CustomerAddress> addresses = addressRepository.findByCustomer_Id(customerId);
        addressRepository.deleteAll(addresses);

        // Remove profile
        profileRepository.findByCustomer_Id(customerId).ifPresent(profileRepository::delete);

        // Credit cards are cascaded via orphanRemoval/ManyToMany logic, but let's clear them explicitly
        customer.getCreditCards().clear();
        customerRepository.saveAndFlush(customer);

        // Delete customer
        customerRepository.delete(customer);
        customerRepository.flush();

        // Async re-index for Lucene search (it will remove if not found)
        searchService.indexCustomerAsync(customerId);

        // Audit: log deletion event
        final AuditLogEntry audit = new AuditLogEntry();
        audit.setUserId(adminId);
        audit.setUsername(adminName);
        audit.setAction(AuditLogEntry.Action.CUSTOMER_DELETED);
        audit.setTargetType("Customer");
        audit.setTargetId(customerNumber);
        audit.setDetails("Customer deleted");
        auditLogRepository.save(audit);
    }

    // ------------------------------------------------------------------
    // Address Management
    // ------------------------------------------------------------------

    @Transactional
    public CustomerAddress addAddress(final UUID customerId,
                                      final String name,
                                      final String recipientFirstName,
                                      final String recipientLastName,
                                      final String company,
                                      final String addressLine1,
                                      final String addressLine2,
                                      final String city,
                                      final String state,
                                      final String postalCode,
                                      final String country,
                                      final Long adminId,
                                      final String adminName)
    {
        final Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        final CustomerAddress address = new CustomerAddress();
        address.setCustomer(customer);
        address.setName(name);
        address.setRecipientFirstName(recipientFirstName);
        address.setRecipientLastName(recipientLastName);
        address.setCompany(company);
        address.setAddressLine1(addressLine1);
        address.setAddressLine2(addressLine2);
        address.setCity(city);
        address.setState(state);
        address.setPostalCode(postalCode);
        address.setCountry(country);

        final CustomerAddress savedAddress = addressRepository.saveAndFlush(address);

        // Audit: log creation event
        final AuditLogEntry audit = new AuditLogEntry();
        audit.setUserId(adminId);
        audit.setUsername(adminName);
        audit.setAction(AuditLogEntry.Action.CUSTOMER_ADDRESS_CREATED);
        audit.setTargetType("CustomerAddress");
        audit.setTargetId(customer.getCustomerNumber());
        audit.setDetails("Added address '" + (name != null && !name.isBlank() ? name : city) + "'");
        auditLogRepository.save(audit);

        return savedAddress;
    }

    @Transactional
    public CustomerAddress updateAddress(final UUID customerId,
                                         final Integer addressId,
                                         final String name,
                                         final String recipientFirstName,
                                         final String recipientLastName,
                                         final String company,
                                         final String addressLine1,
                                         final String addressLine2,
                                         final String city,
                                         final String state,
                                         final String postalCode,
                                         final String country,
                                         final Long adminId,
                                         final String adminName)
    {
        final CustomerAddress address = addressRepository.findById(addressId)
            .orElseThrow(() -> new IllegalArgumentException("Address not found: " + addressId));

        if (!address.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Address does not belong to customer");
        }

        address.setName(name);
        address.setRecipientFirstName(recipientFirstName);
        address.setRecipientLastName(recipientLastName);
        address.setCompany(company);
        address.setAddressLine1(addressLine1);
        address.setAddressLine2(addressLine2);
        address.setCity(city);
        address.setState(state);
        address.setPostalCode(postalCode);
        address.setCountry(country);

        final CustomerAddress savedAddress = addressRepository.saveAndFlush(address);

        // Audit: log update event
        final AuditLogEntry audit = new AuditLogEntry();
        audit.setUserId(adminId);
        audit.setUsername(adminName);
        audit.setAction(AuditLogEntry.Action.CUSTOMER_ADDRESS_UPDATED);
        audit.setTargetType("CustomerAddress");
        audit.setTargetId(address.getCustomer().getCustomerNumber());
        audit.setDetails("Updated address '" + (name != null && !name.isBlank() ? name : city) + "'");
        auditLogRepository.save(audit);

        return savedAddress;
    }

    @Transactional
    public void deleteAddress(final UUID customerId, final Integer addressId, final Long adminId, final String adminName)
    {
        final CustomerAddress address = addressRepository.findById(addressId)
            .orElseThrow(() -> new IllegalArgumentException("Address not found: " + addressId));

        if (!address.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Address does not belong to customer");
        }

        final String addressLabel = address.getName() != null && !address.getName().isBlank() ? address.getName() : address.getCity();
        final Long customerNumber = address.getCustomer().getCustomerNumber();

        addressRepository.delete(address);
        addressRepository.flush();

        // Audit: log deletion event
        final AuditLogEntry audit = new AuditLogEntry();
        audit.setUserId(adminId);
        audit.setUsername(adminName);
        audit.setAction(AuditLogEntry.Action.CUSTOMER_ADDRESS_DELETED);
        audit.setTargetType("CustomerAddress");
        audit.setTargetId(customerNumber);
        audit.setDetails("Deleted address '" + addressLabel + "'");
        auditLogRepository.save(audit);
    }

    // ------------------------------------------------------------------
    // Credit Card Management
    // ------------------------------------------------------------------

    @Transactional
    public CatalogCreditCard addCreditCard(final UUID customerId,
                                           final String number,
                                           final String vendor,
                                           final String name,
                                           final Integer expMonth,
                                           final Integer expYear,
                                           final Long adminId,
                                           final String adminName)
    {
        final Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        final CatalogCreditCard card = new CatalogCreditCard();
        
        // Mask the credit card number
        final String cleanNumber = number.replaceAll("[^0-9]", "");
        final String maskedNumber = cleanNumber.length() >= 4 
            ? "*".repeat(cleanNumber.length() - 4) + cleanNumber.substring(cleanNumber.length() - 4)
            : "****";
            
        card.setNumber(maskedNumber);
        card.setVendor(vendor);
        card.setName(name);
        card.setExpMonth(expMonth);
        card.setExpYear(expYear);

        customer.getCreditCards().add(card);
        customerRepository.saveAndFlush(customer);

        // Audit: log creation event
        final AuditLogEntry audit = new AuditLogEntry();
        audit.setUserId(adminId);
        audit.setUsername(adminName);
        audit.setAction(AuditLogEntry.Action.CUSTOMER_CARD_ADDED);
        audit.setTargetType("CatalogCreditCard");
        audit.setTargetId(customer.getCustomerNumber());
        audit.setDetails("Added credit card ending in " + (cleanNumber.length() >= 4 ? cleanNumber.substring(cleanNumber.length() - 4) : ""));
        auditLogRepository.save(audit);

        return customer.getCreditCards().stream()
            .max((c1, c2) -> Integer.compare(c1.getId() != null ? c1.getId() : 0, c2.getId() != null ? c2.getId() : 0))
            .orElse(card);
    }

    @Transactional
    public void deleteCreditCard(final UUID customerId, final Integer cardId, final Long adminId, final String adminName)
    {
        final Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        final CatalogCreditCard cardToRemove = customer.getCreditCards().stream()
            .filter(c -> c.getId().equals(cardId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Credit card not found: " + cardId));

        final String maskedNumber = cardToRemove.getNumber();
        final Long customerNumber = customer.getCustomerNumber();

        customer.getCreditCards().remove(cardToRemove);
        customerRepository.saveAndFlush(customer);

        // Audit: log deletion event
        final AuditLogEntry audit = new AuditLogEntry();
        audit.setUserId(adminId);
        audit.setUsername(adminName);
        audit.setAction(AuditLogEntry.Action.CUSTOMER_CARD_DELETED);
        audit.setTargetType("CatalogCreditCard");
        audit.setTargetId(customerNumber);
        audit.setDetails("Deleted credit card " + maskedNumber);
        auditLogRepository.save(audit);
    }

    @Transactional(readOnly = true)
    public DashboardMetrics getDashboardMetrics()
    {
        final long totalCustomers = customerRepository.count();
        final java.time.LocalDateTime yesterday = java.time.LocalDateTime.now().minusHours(24);
        final long customers24h = customerRepository.countByCreatedAtAfter(yesterday);
        final long orders24h = orderRepository.countByOrderDateAfter(yesterday);

        return new DashboardMetrics(totalCustomers, customers24h, orders24h);
    }

    // ------------------------------------------------------------------
    // DTOs (immutable records)
    // ------------------------------------------------------------------

    public record DashboardMetrics(long totalCustomers, long customers24h, long orders24h) {}

    /**
     * Full customer detail aggregate for the detail view.
     *
     * @param customer  the customer entity
     * @param profile   the customer profile (may be {@code null})
     * @param addresses the customer's addresses (may be empty)
     */
    public record CustomerDetail(
        Customer customer,
        CustomerProfile profile,
        List<CustomerAddress> addresses)
    {
    }

    /**
     * Enriched list item for the customer table, including profile and order count.
     *
     * @param customer   the customer entity
     * @param profile    the customer profile (may be {@code null})
     * @param orderCount number of orders placed by this customer
     */
    public record CustomerListItem(
        Customer customer,
        CustomerProfile profile,
        long orderCount)
    {
    }

    /**
     * Paginated wrapper for customer search results.
     *
     * @param items      customer list items for the current page
     * @param totalHits  total number of matching customers
     * @param page       current zero-based page number
     * @param size       page size
     * @param totalPages total number of pages
     */
    public record PaginatedCustomerResult(
        List<CustomerListItem> items,
        long totalHits,
        int page,
        int size,
        int totalPages)
    {
        /**
         * Convenience constructor that calculates {@code totalPages} automatically.
         */
        public PaginatedCustomerResult(final List<CustomerListItem> items,
                                       final long totalHits,
                                       final int page,
                                       final int size)
        {
            this(items, totalHits, page, size,
                size > 0 ? (int) Math.ceil((double) totalHits / size) : 0);
        }
    }
}
