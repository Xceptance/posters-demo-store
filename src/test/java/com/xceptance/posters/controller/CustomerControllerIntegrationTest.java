package com.xceptance.posters.controller;

import com.xceptance.posters.entity.CatalogCustomer;
import com.xceptance.posters.entity.CatalogCustomerRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the registration endpoint.
 * Uses full Spring context with MockMvc and a transactional rollback per test.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CustomerControllerIntegrationTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CatalogCustomerRepository customerRepository;

    private static final String REGISTER_URL = "/en_US/register";
    private static final String VALID_PASSWORD = "Abcdefgh1!";

    @Test
    void registerWithEmptyFieldsRedirectsWithError() throws Exception
    {
        mockMvc.perform(post(REGISTER_URL)
                .param("email", "")
                .param("password", "")
                .param("passwordConfirm", "")
                .param("firstName", "")
                .param("name", ""))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(REGISTER_URL))
            .andExpect(flash().attributeExists("error"));
    }

    @Test
    void registerWithInvalidEmailRedirectsWithError() throws Exception
    {
        mockMvc.perform(post(REGISTER_URL)
                .param("email", "notavalidemail")
                .param("password", VALID_PASSWORD)
                .param("passwordConfirm", VALID_PASSWORD)
                .param("firstName", "Jane")
                .param("name", "Doe"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(REGISTER_URL))
            .andExpect(flash().attributeExists("error"));
    }

    @Test
    void registerWithWeakPasswordRedirectsWithError() throws Exception
    {
        mockMvc.perform(post(REGISTER_URL)
                .param("email", "weak@example.com")
                .param("password", "short")
                .param("passwordConfirm", "short")
                .param("firstName", "Jane")
                .param("name", "Doe"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(REGISTER_URL))
            .andExpect(flash().attributeExists("error"));
    }

    @Test
    void registerWithMismatchedPasswordsRedirectsWithError() throws Exception
    {
        mockMvc.perform(post(REGISTER_URL)
                .param("email", "mismatch@example.com")
                .param("password", VALID_PASSWORD)
                .param("passwordConfirm", "DifferentP1!")
                .param("firstName", "Jane")
                .param("name", "Doe"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(REGISTER_URL))
            .andExpect(flash().attributeExists("error"));
    }

    @Test
    void registerWithValidInputSucceeds() throws Exception
    {
        String email = "integration-test-" + System.currentTimeMillis() + "@example.com";

        mockMvc.perform(post(REGISTER_URL)
                .param("email", email)
                .param("password", VALID_PASSWORD)
                .param("passwordConfirm", VALID_PASSWORD)
                .param("firstName", "Jane")
                .param("name", "Doe"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/en_US/"));

        // Verify customer was persisted
        assertThat(customerRepository.findByEmail(email)).isPresent();
    }

    @Test
    void registerWithDuplicateEmailRedirectsWithError() throws Exception
    {
        // Pre-create a customer with the same email
        String email = "duplicate-" + System.currentTimeMillis() + "@example.com";
        CatalogCustomer existing = new CatalogCustomer();
        existing.setEmail(email);
        existing.setFirstName("Existing");
        existing.setLastName("User");
        existing.hashPassword(VALID_PASSWORD);
        customerRepository.saveAndFlush(existing);

        // Try to register again with the same email
        mockMvc.perform(post(REGISTER_URL)
                .param("email", email)
                .param("password", VALID_PASSWORD)
                .param("passwordConfirm", VALID_PASSWORD)
                .param("firstName", "Jane")
                .param("name", "Doe"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(REGISTER_URL))
            .andExpect(flash().attributeExists("error"));
    }
}
