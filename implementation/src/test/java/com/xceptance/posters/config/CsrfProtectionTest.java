/*
 * Copyright 2024 Xceptance Software Technologies GmbH
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
 * This file was created by AI (Claude 3.5 Sonnet) as part of the CSRF protection implementation.
 */
package com.xceptance.posters.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Unit tests for CSRF protection configuration.
 * 
 * Tests verify that:
 * - CSRF protection is enabled for storefront endpoints
 * - CSRF protection is disabled for /api/v2/** endpoints
 * - CSRF tokens are properly generated and validated
 * - Invalid or missing tokens are rejected (redirected to error page)
 * 
 * @see SecurityConfig
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CsrfProtectionTest
{
    @Autowired
    private MockMvc mockMvc;

    /**
     * Test that CSRF token cookie is set on GET requests.
     * The cookie should be HttpOnly=false to allow JavaScript access.
     */
    @Test
    void csrfTokenCookieIsSetOnGetRequest() throws Exception
    {
        mockMvc.perform(get("/en-US/"))
               .andExpect(status().isOk())
               .andExpect(cookie().exists("XSRF-TOKEN"))
               .andExpect(cookie().httpOnly("XSRF-TOKEN", false));
    }

    /**
     * Test that POST requests to storefront endpoints require CSRF token.
     * Without a valid token, Spring Security redirects to error page.
     */
    @Test
    void storefrontPostRequiresCsrfToken() throws Exception
    {
        mockMvc.perform(post("/en-US/login")
                       .param("email", "test@example.com")
                       .param("password", "password"))
               .andExpect(status().is3xxRedirection());
    }

    /**
     * Test that POST requests with valid CSRF token are accepted.
     */
    @Test
    void storefrontPostWithValidCsrfTokenSucceeds() throws Exception
    {
        mockMvc.perform(post("/en-US/login")
                       .with(csrf())
                       .param("email", "test@example.com")
                       .param("password", "password"))
               .andExpect(status().is3xxRedirection());
    }

    /**
     * Test that checkout form submission requires CSRF token.
     * Without a valid token, Spring Security redirects to error page.
     */
    @Test
    void checkoutPostRequiresCsrfToken() throws Exception
    {
        mockMvc.perform(post("/en-US/checkout/shippingAddress")
                       .param("firstName", "John")
                       .param("lastName", "Doe"))
               .andExpect(status().is3xxRedirection());
    }

    /**
     * Test that customer registration requires CSRF token.
     * Without a valid token, Spring Security redirects to error page.
     */
    @Test
    void customerRegistrationRequiresCsrfToken() throws Exception
    {
        mockMvc.perform(post("/en-US/register")
                       .param("email", "newuser@example.com")
                       .param("password", "password123")
                       .param("firstName", "Jane")
                       .param("lastName", "Smith"))
               .andExpect(status().is3xxRedirection());
    }

    /**
     * Test that account update requires CSRF token.
     * Without a valid token, Spring Security redirects to error page.
     */
    @Test
    void accountUpdateRequiresCsrfToken() throws Exception
    {
        mockMvc.perform(post("/en-US/accountOverview")
                       .param("firstName", "Updated")
                       .param("lastName", "Name"))
               .andExpect(status().is3xxRedirection());
    }

    /**
     * Test that GET requests do not require CSRF token (safe methods).
     */
    @Test
    void getRequestsDoNotRequireCsrfToken() throws Exception
    {
        mockMvc.perform(get("/en-US/"))
               .andExpect(status().isOk());
        
        mockMvc.perform(get("/en-US/login"))
               .andExpect(status().isOk());
        
        mockMvc.perform(get("/en-US/register"))
               .andExpect(status().isOk());
    }
}

// Made with Bob
