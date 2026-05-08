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
                                                   final int size)
    {
        final int offset = page * size;
        final CustomerSearchResult searchResult = searchService.search(query, offset, size);

        final List<CustomerListItem> items = searchResult.customerIds().stream()
            .map(this::toListItem)
            .toList();

        return new PaginatedCustomerResult(items, searchResult.totalHits(), page, size);
    }

    /**
     * Converts a customer UUID into an enriched list item by loading the
     * customer, their profile, and their order count.
     *
     * @param customerId UUID of the customer
     * @return enriched list item
     * @throws IllegalStateException if the customer exists in the index but
     *                               not in the database (stale index)
     */
    private CustomerListItem toListItem(final UUID customerId)
    {
        final Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalStateException(
                "Customer in index not found in DB: " + customerId));

        final CustomerProfile profile = profileRepository
            .findByCustomer_Id(customerId).orElse(null);
        final long orderCount = orderRepository.countByCustomer_Email(customer.getEmail());

        return new CustomerListItem(customer, profile, orderCount);
    }

    // ------------------------------------------------------------------
    // Detail view
    // ------------------------------------------------------------------

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
        final Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Customer not found: " + customerId));

        final CustomerProfile profile = profileRepository
            .findByCustomer_Id(customerId).orElse(null);

        final List<CustomerAddress> addresses = addressRepository
            .findByCustomer_Id(customerId);

        // Audit: log view event
        final AuditLogEntry audit = new AuditLogEntry();
        audit.setUserId(adminId);
        audit.setUsername(adminName);
        audit.setAction(AuditLogEntry.Action.CUSTOMER_VIEWED);
        audit.setTargetType("Customer");
        audit.setTargetId(customer.getCustomerNumber());
        audit.setDetails("Customer details viewed");
        auditLogRepository.save(audit);

        return new CustomerDetail(customer, profile, addresses);
    }

    // ------------------------------------------------------------------
    // Profile Editing
    // ------------------------------------------------------------------

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

    // ------------------------------------------------------------------
    // DTOs (immutable records)
    // ------------------------------------------------------------------

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
