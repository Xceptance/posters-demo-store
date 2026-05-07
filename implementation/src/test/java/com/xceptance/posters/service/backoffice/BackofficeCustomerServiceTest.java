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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import com.xceptance.posters.service.backoffice.CustomerService.CustomerDetail;
import com.xceptance.posters.service.backoffice.CustomerService.CustomerListItem;
import com.xceptance.posters.service.backoffice.CustomerService.PaginatedCustomerResult;

/**
 * Unit tests for the backoffice {@link CustomerService}.
 *
 * <p>Covers search/pagination delegation, detail view loading,
 * and audit log creation on customer view.</p>
 *
 * <p>Created exclusively by AI (Claude Opus 4.6).</p>
 */
@ExtendWith(MockitoExtension.class)
class BackofficeCustomerServiceTest
{
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerProfileRepository profileRepository;

    @Mock
    private CatalogOrderRepository orderRepository;

    @Mock
    private CustomerAddressRepository addressRepository;

    @Mock
    private CustomerSearchService searchService;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private CustomerService customerService;

    // ------------------------------------------------------------------
    // searchCustomers
    // ------------------------------------------------------------------

    /**
     * Verifies that search results are enriched with profile and order
     * count data, and pagination metadata is calculated correctly.
     */
    @Test
    void searchCustomersEnrichesResultsWithProfileAndOrderCount()
    {
        // Arrange
        final UUID id1 = UUID.randomUUID();
        final UUID id2 = UUID.randomUUID();

        final Customer c1 = createCustomer(id1, "alice@example.com", "Alice", "Smith", 1001L);
        final Customer c2 = createCustomer(id2, "bob@example.com", "Bob", "Jones", 1002L);

        final CustomerProfile p1 = new CustomerProfile();
        p1.setCustomer(c1);

        when(searchService.search("alice", 0, 25))
            .thenReturn(new CustomerSearchResult(2, List.of(id1, id2)));
        when(customerRepository.findById(id1)).thenReturn(Optional.of(c1));
        when(customerRepository.findById(id2)).thenReturn(Optional.of(c2));
        when(profileRepository.findByCustomer_Id(id1)).thenReturn(Optional.of(p1));
        when(profileRepository.findByCustomer_Id(id2)).thenReturn(Optional.empty());
        when(orderRepository.countByCustomer_Id(id1)).thenReturn(5L);
        when(orderRepository.countByCustomer_Id(id2)).thenReturn(0L);

        // Act
        final PaginatedCustomerResult result = customerService.searchCustomers("alice", 0, 25);

        // Assert
        assertThat(result.items()).hasSize(2);
        assertThat(result.totalHits()).isEqualTo(2);
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.totalPages()).isEqualTo(1);

        final CustomerListItem item1 = result.items().get(0);
        assertThat(item1.customer().getEmail()).isEqualTo("alice@example.com");
        assertThat(item1.profile()).isNotNull();
        assertThat(item1.orderCount()).isEqualTo(5L);

        final CustomerListItem item2 = result.items().get(1);
        assertThat(item2.profile()).isNull();
        assertThat(item2.orderCount()).isEqualTo(0L);
    }

    /**
     * Verifies that an empty search result returns an empty list
     * with correct pagination metadata.
     */
    @Test
    void searchCustomersReturnsEmptyResultGracefully()
    {
        // Arrange
        when(searchService.search("nonexistent", 0, 25))
            .thenReturn(new CustomerSearchResult(0, List.of()));

        // Act
        final PaginatedCustomerResult result = customerService.searchCustomers("nonexistent", 0, 25);

        // Assert
        assertThat(result.items()).isEmpty();
        assertThat(result.totalHits()).isEqualTo(0);
        assertThat(result.totalPages()).isEqualTo(0);
    }

    /**
     * Verifies that pagination calculates total pages correctly
     * for multi-page results.
     */
    @Test
    void searchCustomersPaginatesCorrectly()
    {
        // Arrange: 51 total hits, page size 25 → 3 pages
        final UUID id = UUID.randomUUID();
        final Customer customer = createCustomer(id, "page@example.com", "Page", "Test", 1001L);

        when(searchService.search("", 50, 25))
            .thenReturn(new CustomerSearchResult(51, List.of(id)));
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(profileRepository.findByCustomer_Id(id)).thenReturn(Optional.empty());
        when(orderRepository.countByCustomer_Id(id)).thenReturn(0L);

        // Act
        final PaginatedCustomerResult result = customerService.searchCustomers("", 2, 25);

        // Assert
        assertThat(result.page()).isEqualTo(2);
        assertThat(result.totalPages()).isEqualTo(3);
    }

    // ------------------------------------------------------------------
    // getCustomerDetails
    // ------------------------------------------------------------------

    /**
     * Verifies that detail view loads the customer, profile, and
     * addresses, and creates a CUSTOMER_VIEWED audit log entry.
     */
    @Test
    void getCustomerDetailsLoadsAllDataAndCreatesAuditEntry()
    {
        // Arrange
        final UUID customerId = UUID.randomUUID();
        final Customer customer = createCustomer(customerId, "detail@example.com", "Detail", "Test", 42L);

        final CustomerProfile profile = new CustomerProfile();
        profile.setCustomer(customer);

        final CustomerAddress address = new CustomerAddress();
        address.setCustomer(customer);
        address.setCity("Berlin");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(profileRepository.findByCustomer_Id(customerId)).thenReturn(Optional.of(profile));
        when(addressRepository.findByCustomer_Id(customerId)).thenReturn(List.of(address));

        // Act
        final CustomerDetail detail = customerService.getCustomerDetails(customerId, 1L, "admin");

        // Assert – data loaded
        assertThat(detail.customer().getEmail()).isEqualTo("detail@example.com");
        assertThat(detail.profile()).isNotNull();
        assertThat(detail.addresses()).hasSize(1);
        assertThat(detail.addresses().get(0).getCity()).isEqualTo("Berlin");

        // Assert – audit log created
        final ArgumentCaptor<AuditLogEntry> auditCaptor =
            ArgumentCaptor.forClass(AuditLogEntry.class);
        verify(auditLogRepository).save(auditCaptor.capture());

        final AuditLogEntry audit = auditCaptor.getValue();
        assertThat(audit.getAction()).isEqualTo(AuditLogEntry.Action.CUSTOMER_VIEWED);
        assertThat(audit.getUserId()).isEqualTo(1L);
        assertThat(audit.getUsername()).isEqualTo("admin");
        assertThat(audit.getTargetType()).isEqualTo("Customer");
        assertThat(audit.getTargetId()).isEqualTo(42L);
    }

    /**
     * Verifies that requesting details for a non-existent customer
     * throws an {@link IllegalArgumentException}.
     */
    @Test
    void getCustomerDetailsThrowsForUnknownCustomer()
    {
        // Arrange
        final UUID unknownId = UUID.randomUUID();
        when(customerRepository.findById(unknownId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> customerService.getCustomerDetails(unknownId, 1L, "admin"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining(unknownId.toString());
    }

    /**
     * Verifies that detail view works gracefully when no profile exists.
     */
    @Test
    void getCustomerDetailsWorksWithoutProfile()
    {
        // Arrange
        final UUID customerId = UUID.randomUUID();
        final Customer customer = createCustomer(customerId, "noprof@example.com", "No", "Profile", 99L);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(profileRepository.findByCustomer_Id(customerId)).thenReturn(Optional.empty());
        when(addressRepository.findByCustomer_Id(customerId)).thenReturn(List.of());

        // Act
        final CustomerDetail detail = customerService.getCustomerDetails(customerId, 1L, "admin");

        // Assert
        assertThat(detail.profile()).isNull();
        assertThat(detail.addresses()).isEmpty();
        verify(auditLogRepository).save(any(AuditLogEntry.class));
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Creates a test customer with the given properties.
     */
    private Customer createCustomer(final UUID id,
                                    final String email,
                                    final String firstName,
                                    final String lastName,
                                    final Long customerNumber)
    {
        final Customer c = new Customer();
        c.setId(id);
        c.setEmail(email);
        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setCustomerNumber(customerNumber);
        return c;
    }
}
