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
package com.xceptance.posters.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.xceptance.posters.dto.OrderDto;
import com.xceptance.posters.entity.CatalogOrder;
import com.xceptance.posters.entity.Customer;
import com.xceptance.posters.entity.CustomerProfile;
import com.xceptance.posters.repository.CatalogOrderRepository;
import com.xceptance.posters.repository.CustomerProfileRepository;
import com.xceptance.posters.repository.CustomerRepository;
import com.xceptance.posters.service.CheckoutService;
import com.xceptance.posters.service.SessionService;

/**
 * Unit tests for {@link CustomerController}.
 *
 * <p>Covers storefront login (lastLogin update), registration
 * (CustomerProfile creation), and order overview rendering.</p>
 *
 * <p>Created exclusively by AI (Claude Opus 4.6).</p>
 */
@ExtendWith(MockitoExtension.class)
class CustomerControllerTest
{
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerProfileRepository customerProfileRepository;

    @Mock
    private CatalogOrderRepository orderRepository;

    @Mock
    private SessionService sessionService;

    @Mock
    private CheckoutService checkoutService;

    @InjectMocks
    private CustomerController customerController;

    // ------------------------------------------------------------------
    // Login
    // ------------------------------------------------------------------

    /**
     * Verifies that a successful login updates {@code lastLogin}
     * on the existing {@link CustomerProfile}.
     */
    @Test
    void loginUpdatesLastLoginOnProfile()
    {
        // Arrange
        final MockHttpSession session = new MockHttpSession();
        final RedirectAttributesModelMap redirectAttrs = new RedirectAttributesModelMap();
        final UUID customerId = UUID.randomUUID();

        final Customer customer = new Customer();
        customer.setEmail("login@example.com");
        customer.hashPassword("secret");
        // Simulate that the customer already has an ID after being loaded
        customer.setId(customerId);

        final CustomerProfile profile = new CustomerProfile();
        profile.setCustomer(customer);
        profile.setPassword(customer.getPassword());

        when(customerRepository.findByEmail("login@example.com"))
            .thenReturn(Optional.of(customer));
        when(customerProfileRepository.findByCustomer_Id(customerId))
            .thenReturn(Optional.of(profile));

        // Act
        final String viewName = customerController.login(
            "en-US", "login@example.com", "secret", session, redirectAttrs);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/en-US/");
        assertThat(profile.getLastLogin()).isNotNull();
        verify(customerProfileRepository).save(profile);
    }

    /**
     * Verifies that a successful login does not crash if no
     * {@link CustomerProfile} exists (legacy customer without profile).
     */
    @Test
    void loginSucceedsEvenWithoutProfile()
    {
        // Arrange
        final MockHttpSession session = new MockHttpSession();
        final RedirectAttributesModelMap redirectAttrs = new RedirectAttributesModelMap();
        final UUID customerId = UUID.randomUUID();

        final Customer customer = new Customer();
        customer.setEmail("noprofile@example.com");
        customer.hashPassword("secret");
        customer.setId(customerId);

        when(customerRepository.findByEmail("noprofile@example.com"))
            .thenReturn(Optional.of(customer));
        when(customerProfileRepository.findByCustomer_Id(customerId))
            .thenReturn(Optional.empty());

        // Act
        final String viewName = customerController.login(
            "en-US", "noprofile@example.com", "secret", session, redirectAttrs);

        // Assert – should still succeed
        assertThat(viewName).isEqualTo("redirect:/en-US/");
        verify(customerProfileRepository, never()).save(any());
    }

    /**
     * Verifies that a failed login (wrong password) does not touch
     * the profile and redirects back to the login page.
     */
    @Test
    void loginFailureDoesNotUpdateProfile()
    {
        // Arrange
        final MockHttpSession session = new MockHttpSession();
        final RedirectAttributesModelMap redirectAttrs = new RedirectAttributesModelMap();

        final Customer customer = new Customer();
        customer.setEmail("fail@example.com");
        customer.hashPassword("correctPassword");

        when(customerRepository.findByEmail("fail@example.com"))
            .thenReturn(Optional.of(customer));

        // Act
        final String viewName = customerController.login(
            "en-US", "fail@example.com", "wrongPassword", session, redirectAttrs);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/en-US/login");
        verify(customerProfileRepository, never()).findByCustomer_Id(any());
        verify(customerProfileRepository, never()).save(any());
    }

    // ------------------------------------------------------------------
    // Registration
    // ------------------------------------------------------------------

    /**
     * Verifies that registering a new customer also creates a
     * {@link CustomerProfile} with {@code lastPasswordChange} set.
     */
    @Test
    void registerCreatesCustomerProfile()
    {
        // Arrange
        final MockHttpSession session = new MockHttpSession();
        final RedirectAttributesModelMap redirectAttrs = new RedirectAttributesModelMap();
        final UUID customerId = UUID.randomUUID();

        when(customerRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation ->
        {
            final Customer c = invocation.getArgument(0);
            c.setId(customerId);
            return c;
        });

        // Act
        final String viewName = customerController.register(
            "en-US", "new@example.com", "password123", "Alice", "Wonder",
            session, redirectAttrs);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/en-US/");

        final ArgumentCaptor<CustomerProfile> profileCaptor =
            ArgumentCaptor.forClass(CustomerProfile.class);
        verify(customerProfileRepository).save(profileCaptor.capture());

        final CustomerProfile savedProfile = profileCaptor.getValue();
        assertThat(savedProfile.getCustomer()).isNotNull();
        assertThat(savedProfile.getLastPasswordChange()).isNotNull();
        assertThat(savedProfile.getPassword()).isNotNull();
    }

    /**
     * Verifies that registering with an existing email rejects
     * and does not create any profile.
     */
    @Test
    void registerWithExistingEmailDoesNotCreateProfile()
    {
        // Arrange
        final MockHttpSession session = new MockHttpSession();
        final RedirectAttributesModelMap redirectAttrs = new RedirectAttributesModelMap();

        when(customerRepository.existsByEmail("dupe@example.com")).thenReturn(true);

        // Act
        final String viewName = customerController.register(
            "en-US", "dupe@example.com", "password123", "Bob", "Dupe",
            session, redirectAttrs);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/en-US/register");
        verify(customerRepository, never()).save(any());
        verify(customerProfileRepository, never()).save(any());
    }

    // ------------------------------------------------------------------
    // Order overview (existing test, cleaned up)
    // ------------------------------------------------------------------

    /**
     * Verifies that the order overview injects properly mapped
     * {@link OrderDto} instances into the model.
     */
    @Test
    void orderOverviewInjectsOrderDtos()
    {
        // Arrange
        final MockHttpSession session = new MockHttpSession();
        final ConcurrentModel model = new ConcurrentModel();
        final UUID customerId = UUID.randomUUID();

        final Customer customer = new Customer();
        customer.setEmail("test@example.com");

        final CatalogOrder order = new CatalogOrder();
        order.setOrderDate(LocalDateTime.now());

        final OrderDto mockDto = new OrderDto(
            "ORD-1", LocalDateTime.now(), null, null, null, null,
            null, null, null, null, List.of());

        when(sessionService.isCustomerLoggedIn(session)).thenReturn(true);
        when(sessionService.getCustomerId(session)).thenReturn(customerId);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(orderRepository.findByCustomer_EmailOrderByOrderDateDesc("test@example.com"))
            .thenReturn(List.of(order));
        when(checkoutService.toOrderDto(any(CatalogOrder.class))).thenReturn(mockDto);

        // Act
        final String viewName = customerController.orderOverview("en_US", session, model);

        // Assert
        assertThat(viewName).isEqualTo("customer/orderOverview");
        assertThat(model.getAttribute("customer")).isEqualTo(customer);

        @SuppressWarnings("unchecked")
        final List<OrderDto> mappedDtos = (List<OrderDto>) model.getAttribute("orderDtos");

        assertThat(mappedDtos).isNotNull();
        assertThat(mappedDtos).hasSize(1);
        assertThat(mappedDtos.get(0).orderNumber()).isEqualTo("ORD-1");
    }
}
