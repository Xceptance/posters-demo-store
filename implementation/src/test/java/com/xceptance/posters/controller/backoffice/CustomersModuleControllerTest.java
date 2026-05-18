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
package com.xceptance.posters.controller.backoffice;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.xceptance.posters.entity.Customer;
import com.xceptance.posters.entity.CustomerProfile;
import com.xceptance.posters.repository.CustomerProfileRepository;
import com.xceptance.posters.repository.CustomerRepository;

/**
 * Integration tests for the backoffice {@link CustomersModuleController}.
 *
 * <p>Uses {@code @SpringBootTest} with full application context to verify
 * that the customer list and detail endpoints render correctly, including
 * Thymeleaf template resolution and Spring Security access control.</p>
 *
 * <p>Created exclusively by AI (Claude Opus 4.6).</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CustomersModuleControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    // ------------------------------------------------------------------
    // Unauthenticated access
    // ------------------------------------------------------------------

    /**
     * Verifies that unauthenticated requests to the customer list
     * are redirected to the login page.
     */
    @Test
    void unauthenticatedUserIsRedirectedFromCustomerList() throws Exception
    {
        mockMvc.perform(get("/backoffice/customers"))
            .andExpect(status().is3xxRedirection());
    }

    /**
     * Verifies that unauthenticated requests to a customer detail page
     * are redirected to the login page.
     */
    @Test
    void unauthenticatedUserIsRedirectedFromCustomerDetail() throws Exception
    {
        mockMvc.perform(get("/backoffice/customers/00000000-0000-0000-0000-000000000001"))
            .andExpect(status().is3xxRedirection());
    }

    // ------------------------------------------------------------------
    // Authenticated access – list view
    // ------------------------------------------------------------------

    /**
     * Verifies that an authenticated admin can access the customer list
     * and the view renders with the expected model attributes.
     */
    @Test
    void authenticatedAdminCanAccessCustomerList() throws Exception
    {
        mockMvc.perform(get("/backoffice/customers")
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(view().name("backoffice/customers/list"))
            .andExpect(model().attributeExists("result"))
            .andExpect(model().attributeExists("q"));
    }

    /**
     * Verifies that the customer list supports a search query parameter.
     */
    @Test
    void customerListAcceptsSearchQuery() throws Exception
    {
        mockMvc.perform(get("/backoffice/customers")
                .param("q", "doe")
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(view().name("backoffice/customers/list"))
            .andExpect(model().attributeExists("result"));
    }

    // ------------------------------------------------------------------
    // Authenticated access – detail view
    // ------------------------------------------------------------------

    /**
     * Verifies that an authenticated admin can view a specific customer's
     * detail page and the model contains the expected detail aggregate.
     */
    @Test
    void authenticatedAdminCanAccessCustomerDetail() throws Exception
    {
        // Arrange – create a customer in the database
        final Customer customer = new Customer();
        customer.setEmail("detail-test@example.com");
        customer.setFirstName("Detail");
        customer.setLastName("Test");
        customer.hashPassword("secret");
        customerRepository.saveAndFlush(customer);

        final CustomerProfile profile = new CustomerProfile();
        profile.setCustomer(customer);
        profile.setPassword(customer.getPassword());
        customerProfileRepository.saveAndFlush(profile);

        // Act & Assert
        mockMvc.perform(get("/backoffice/customers/" + customer.getId())
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(view().name("backoffice/customers/detail"))
            .andExpect(model().attributeExists("detail"));
    }

    // ------------------------------------------------------------------
    // Authenticated access – inline profile editing
    // ------------------------------------------------------------------

    /**
     * Verifies that the inline edit form renders correctly via HTMX.
     */
    @Test
    void canAccessInlineEditForm() throws Exception
    {
        final Customer customer = new Customer();
        customer.setEmail("edit-test@example.com");
        customer.setFirstName("Old");
        customer.setLastName("Name");
        customer.hashPassword("secret");
        final Customer saved = customerRepository.saveAndFlush(customer);

        mockMvc.perform(get("/backoffice/customers/" + saved.getId() + "/edit-name")
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(view().name("backoffice/customers/fragments/profile-name-form :: profile-name-form"))
            .andExpect(model().attributeExists("customer"));
    }

    /**
     * Verifies that updating the customer name returns the display fragment and a success toast.
     */
    @Test
    void updateNameReturnsDisplayFragmentAndSuccessToast() throws Exception
    {
        final Customer customer = new Customer();
        customer.setEmail("update-test@example.com");
        customer.setFirstName("Old");
        customer.setLastName("Name");
        customer.hashPassword("secret");
        customer.setVersion(0);
        final Customer saved = customerRepository.saveAndFlush(customer);

        mockMvc.perform(post("/backoffice/customers/" + saved.getId() + "/edit-name")
                .with(user("admin").roles("ADMIN"))
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                .param("firstName", "New")
                .param("middleName", "M")
                .param("lastName", "Name")
                .param("version", "0"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("backoffice/customers/fragments/profile-name-display :: profile-name-display"))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().exists("HX-Trigger"))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("HX-Trigger", org.hamcrest.Matchers.containsString("success")));
    }

    /**
     * Verifies that submitting an outdated version triggers the optimistic locking
     * exception handler, returning the form fragment with a danger toast.
     */
    @Test
    void concurrentUpdateTriggersOptimisticLockingFailure() throws Exception
    {
        final Customer customer = new Customer();
        customer.setEmail("conflict-test@example.com");
        customer.setFirstName("Old");
        customer.setLastName("Name");
        customer.hashPassword("secret");
        final Customer saved = customerRepository.saveAndFlush(customer);
        
        // Modify and save again to increment the version in the DB (version becomes 1)
        saved.setFirstName("Modified by someone else");
        customerRepository.saveAndFlush(saved);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/backoffice/customers/" + saved.getId() + "/edit-name")
                .with(user("admin").roles("ADMIN"))
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                .param("firstName", "New")
                .param("lastName", "Name")
                .param("version", "0")) // Submit with stale version 0
            .andExpect(status().isOk())
            .andExpect(view().name("backoffice/customers/fragments/profile-name-form :: conflict-error"))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().exists("HX-Trigger"))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("HX-Trigger", org.hamcrest.Matchers.containsString("danger")));
    }

    // ------------------------------------------------------------------
    // Placeholder sub-module routes
    // ------------------------------------------------------------------

    /**
     * Verifies that the dashboard placeholder renders for authenticated admins.
     */
    @Test
    void dashboardRendersForAdmin() throws Exception
    {
        mockMvc.perform(get("/backoffice/customers/dashboard")
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("metrics"))
            .andExpect(view().name("backoffice/customers/dashboard"));
    }

    /**
     * Verifies that the settings placeholder renders for authenticated admins.
     */
    @Test
    void settingsPlaceholderRendersForAdmin() throws Exception
    {
        mockMvc.perform(get("/backoffice/customers/settings")
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(view().name("backoffice/placeholder"));
    }
}
