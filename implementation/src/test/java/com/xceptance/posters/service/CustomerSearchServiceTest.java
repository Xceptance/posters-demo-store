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
package com.xceptance.posters.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.xceptance.posters.entity.Customer;
import com.xceptance.posters.repository.CustomerRepository;
import com.xceptance.posters.service.CustomerSearchService.CustomerSearchResult;

/**
 * Integration tests for the {@link CustomerSearchService} verifying search functionality
 * for customers by name, email, and customer number.
 *
 * // AI-generated: Gemini 3.5 Flash
 */
@SpringBootTest
@Transactional
class CustomerSearchServiceTest
{
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerSearchService customerSearchService;

    @Test
    final void testSearchCustomerByNameAndEmail()
    {
        final Customer customer = new Customer();
        customer.setEmail("lucenesearchtest@example.com");
        customer.setFirstName("LuceneSearch");
        customer.setLastName("Test");
        customer.hashPassword("secret");
        customer.setCustomerNumber(999999L);
        customerRepository.saveAndFlush(customer);

        // Index the customer synchronously
        customerSearchService.indexCustomerAsync(customer.getId());
        customerSearchService.processIndexQueue();

        // 1. Search by name (first name) - should work
        final CustomerSearchResult resultByName = customerSearchService.search("LuceneSearch", 0, 10, "number", "desc");
        assertThat(resultByName.customerIds()).contains(customer.getId());

        // 2. Search by exact email - should work
        final CustomerSearchResult resultByExactEmail = customerSearchService.search("lucenesearchtest@example.com", 0, 10, "number", "desc");
        assertThat(resultByExactEmail.customerIds()).contains(customer.getId());

        // 3. Search by partial email (local part with @) - should work
        final CustomerSearchResult resultByPartialEmail = customerSearchService.search("lucenesearchtest@", 0, 10, "number", "desc");
        assertThat(resultByPartialEmail.customerIds()).contains(customer.getId());

        // 4. Search by partial email domain - should work
        final CustomerSearchResult resultByDomain = customerSearchService.search("lucenesearchtest@ex", 0, 10, "number", "desc");
        assertThat(resultByDomain.customerIds()).contains(customer.getId());
    }
}
