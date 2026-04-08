/*
 * Copyright 2013-2026 Xceptance Software Technologies GmbH
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
 *
 * AI-generated file — created by Claude Opus 4.6
 */
package com.xceptance.posters.controller;

import com.xceptance.posters.entity.CatalogCustomer;
import com.xceptance.posters.entity.CatalogCustomer.AccountStatus;
import com.xceptance.posters.entity.CatalogCustomerRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the registration flow in {@link CustomerController}.
 *
 * <p>Covers: new registration with activation loop, existing email soft redirect,
 * format-only HTMX validation, expired token handling, and pending account login denial.
 *
 * <p>Uses real H2 database — no mocks.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RegistrationFlowIntegrationTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CatalogCustomerRepository customerRepository;

    // ==================== New Registration ====================

    @Test
    void registerNewAccount_shouldCreatePendingAccountAndShowSuccess() throws Exception
    {
        mockMvc.perform(post("/en/register")
                .param("email", "newreg@test.com")
                .param("password", "Str0ng!Pass")
                .param("firstName", "Jane")
                .param("name", "Doe"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/en/register"))
            .andExpect(flash().attributeExists("success"));

        // Verify account was persisted in PENDING state
        final CatalogCustomer saved = customerRepository.findByEmail("newreg@test.com").orElse(null);
        assertThat(saved).isNotNull();
        assertThat(saved.getAccountStatus()).isEqualTo(AccountStatus.PENDING);
        assertThat(saved.getVerificationToken()).isNotNull();
        assertThat(saved.getTokenExpiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    void registerWithWeakPassword_shouldRejectWithErrors() throws Exception
    {
        mockMvc.perform(post("/en/register")
                .param("email", "weak@test.com")
                .param("password", "short")
                .param("firstName", "Jane")
                .param("name", "Doe"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/en/register"))
            .andExpect(flash().attributeExists("errors"));

        // Account should not have been created
        assertThat(customerRepository.findByEmail("weak@test.com")).isEmpty();
    }

    // ==================== Soft Redirect for Existing Email (US-06) ====================

    @Test
    void registerWithExistingEmail_shouldRedirectToLoginWithPrefill() throws Exception
    {
        // Setup: create an existing account
        final CatalogCustomer existing = new CatalogCustomer();
        existing.setEmail("exists@test.com");
        existing.setFirstName("E");
        existing.setLastName("X");
        existing.setAccountStatus(AccountStatus.ACTIVE);
        existing.hashPassword("Anything1!");
        customerRepository.save(existing);

        // Act: try to register with the same email
        mockMvc.perform(post("/en/register")
                .param("email", "exists@test.com")
                .param("password", "Str0ng!Pass")
                .param("firstName", "Jane")
                .param("name", "Doe"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/en/login"))
            .andExpect(flash().attribute("prefillEmail", "exists@test.com"))
            .andExpect(flash().attributeExists("info"));
    }

    // ==================== Activation Magic Link (Task 2.1) ====================

    @Test
    void activateValidToken_shouldActivateAccountAndLogin() throws Exception
    {
        final CatalogCustomer pending = createPendingCustomer("activate@test.com", "valid-token-123",
            LocalDateTime.now().plusHours(1));

        mockMvc.perform(get("/en/activate").param("token", "valid-token-123"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/en/"))
            .andExpect(flash().attributeExists("success"));

        // Verify account is now ACTIVE and token cleared
        final CatalogCustomer activated = customerRepository.findById(pending.getId()).orElseThrow();
        assertThat(activated.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(activated.getVerificationToken()).isNull();
    }

    @Test
    void activateExpiredToken_shouldRejectWithError() throws Exception
    {
        createPendingCustomer("expired@test.com", "expired-token",
            LocalDateTime.now().minusHours(1));

        mockMvc.perform(get("/en/activate").param("token", "expired-token"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/en/register"))
            .andExpect(flash().attributeExists("error"));
    }

    @Test
    void activateInvalidToken_shouldRejectWithError() throws Exception
    {
        mockMvc.perform(get("/en/activate").param("token", "does-not-exist"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/en/register"))
            .andExpect(flash().attributeExists("error"));
    }

    // ==================== HTMX Format Validation (Task 2.4) ====================

    @Test
    void validateEmail_invalidFormat_shouldReturnErrorFragment() throws Exception
    {
        mockMvc.perform(post("/en/validate")
                .param("field", "email")
                .param("value", "not-an-email"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("text-danger")));
    }

    @Test
    void validateEmail_validFormat_shouldReturnSuccessFragment() throws Exception
    {
        mockMvc.perform(post("/en/validate")
                .param("field", "email")
                .param("value", "valid@example.com"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("text-success")));
    }

    @Test
    void validatePassword_weak_shouldReturnErrorList() throws Exception
    {
        mockMvc.perform(post("/en/validate")
                .param("field", "password")
                .param("value", "abc"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("text-danger")))
            .andExpect(content().string(containsString("8 characters")));
    }

    @Test
    void validatePassword_strong_shouldReturnSuccess() throws Exception
    {
        mockMvc.perform(post("/en/validate")
                .param("field", "password")
                .param("value", "Str0ng!Pass"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("text-success")));
    }

    // ==================== Pending Account Login Denial ====================

    @Test
    void loginWithPendingAccount_shouldDenyWithVerifyMessage() throws Exception
    {
        final CatalogCustomer pending = createPendingCustomer("pending-login@test.com",
            "some-token", LocalDateTime.now().plusHours(1));
        pending.hashPassword("Str0ng!Pass");
        customerRepository.save(pending);

        mockMvc.perform(post("/en/login")
                .param("email", "pending-login@test.com")
                .param("password", "Str0ng!Pass"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/en/login"))
            .andExpect(flash().attribute("error", containsString("verify")));
    }

    // ==================== Email Trimming (Task 2.6) ====================

    @Test
    void registerWithTrailingSpaceEmail_shouldTrimAndProceed() throws Exception
    {
        mockMvc.perform(post("/en/register")
                .param("email", "  trimmed@test.com  ")
                .param("password", "Str0ng!Pass")
                .param("firstName", "Jane")
                .param("name", "Doe"))
            .andExpect(status().is3xxRedirection());

        // Verify stored email is trimmed
        assertThat(customerRepository.findByEmail("trimmed@test.com")).isPresent();
    }

    // ==================== Helpers ====================

    private CatalogCustomer createPendingCustomer(final String email,
                                                   final String token,
                                                   final LocalDateTime tokenExpiry)
    {
        final CatalogCustomer customer = new CatalogCustomer();
        customer.setEmail(email);
        customer.setFirstName("Test");
        customer.setLastName("User");
        customer.hashPassword("Dummy1!xx");
        customer.setAccountStatus(AccountStatus.PENDING);
        customer.setVerificationToken(token);
        customer.setTokenExpiresAt(tokenExpiry);
        return customerRepository.save(customer);
    }
}
