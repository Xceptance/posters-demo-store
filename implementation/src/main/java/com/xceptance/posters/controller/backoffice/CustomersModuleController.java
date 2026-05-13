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

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import jakarta.servlet.http.HttpServletResponse;

import com.xceptance.posters.config.AdminUserPrincipal;
import com.xceptance.posters.controller.AbstractBackofficeController;
import com.xceptance.posters.entity.Customer;
import com.xceptance.posters.entity.CustomerAddress;
import com.xceptance.posters.service.backoffice.CustomerService;
import com.xceptance.posters.service.backoffice.CustomerService.CustomerDetail;
import com.xceptance.posters.service.backoffice.CustomerService.PaginatedCustomerResult;

/**
 * Backoffice controller for the Customers module.
 *
 * <p>Handles the customer list (with search and pagination),
 * individual customer detail views, and placeholder sub-module routes
 * (dashboard, import/export, settings).</p>
 *
 * <p>Created exclusively by AI (Claude Opus 4.6).</p>
 */
@Controller
@RequestMapping("/backoffice/customers")
public class CustomersModuleController extends AbstractBackofficeController
{
    private final CustomerService customerService;

    /**
     * Constructs the controller with the backoffice customer service.
     *
     * @param customerService service for customer search and detail retrieval
     */
    public CustomersModuleController(final CustomerService customerService)
    {
        this.customerService = customerService;
    }

    // ------------------------------------------------------------------
    // Customer list
    // ------------------------------------------------------------------

    /**
     * Displays the paginated, searchable customer list.
     *
     * @param q     free-text search query (optional, defaults to empty)
     * @param page  zero-based page number (optional, defaults to 0)
     * @param size  page size (optional, defaults to 25)
     * @param model Spring MVC model
     * @return the customer list view
     */
    @GetMapping({"", "/", "/list"})
    public String listCustomers(
            @RequestParam(required = false, defaultValue = "") final String q,
            @RequestParam(required = false, defaultValue = "0") final int page,
            @RequestParam(required = false, defaultValue = "25") final int size,
            final Model model)
    {
        final PaginatedCustomerResult result = customerService.searchCustomers(q, page, size);

        model.addAttribute("result", result);
        model.addAttribute("q", q);

        return "backoffice/customers/list";
    }

    // ------------------------------------------------------------------
    // Customer detail
    // ------------------------------------------------------------------

    /**
     * Displays the read-only customer detail view and logs a
     * {@code CUSTOMER_VIEWED} audit event.
     *
     * @param id    UUID of the customer to view
     * @param model Spring MVC model
     * @return the customer detail view
     */
    @GetMapping("/{id}")
    public String viewCustomer(@PathVariable final UUID id, final Model model)
    {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long adminId = null;
        String adminName = "System";

        if (auth != null && auth.getPrincipal() instanceof final AdminUserPrincipal principal)
        {
            adminId = principal.getUserId();
            adminName = principal.getDisplayName();
        }

        final CustomerDetail detail = customerService.getCustomerDetails(id, adminId, adminName);
        model.addAttribute("detail", detail);

        return "backoffice/customers/detail";
    }

    // ------------------------------------------------------------------
    // Profile Editing
    // ------------------------------------------------------------------

    /**
     * Returns the inline edit form for the customer's name.
     */
    @GetMapping("/{id}/edit-name")
    public String editNameForm(@PathVariable final UUID id, final Model model)
    {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long adminId = null;
        String adminName = "System";

        if (auth != null && auth.getPrincipal() instanceof final AdminUserPrincipal principal)
        {
            adminId = principal.getUserId();
            adminName = principal.getDisplayName();
        }

        final CustomerDetail detail = customerService.getCustomerDetails(id, adminId, adminName);
        model.addAttribute("customer", detail.customer());

        return "backoffice/customers/fragments/profile-name-form :: profile-name-form";
    }

    /**
     * Processes the inline edit form submission, updates the customer,
     * and returns the read-only display fragment with a success toast.
     */
    @PostMapping("/{id}/edit-name")
    public String updateName(@PathVariable final UUID id,
                             @RequestParam final String firstName,
                             @RequestParam(required = false) final String middleName,
                             @RequestParam final String lastName,
                             @RequestParam final Integer version,
                             final Model model,
                             final HttpServletResponse response)
    {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long adminId = null;
        String adminName = "System";

        if (auth != null && auth.getPrincipal() instanceof final AdminUserPrincipal principal)
        {
            adminId = principal.getUserId();
            adminName = principal.getDisplayName();
        }

        final Customer updatedCustomer = customerService.updateCustomerNames(
            id, firstName, middleName, lastName, version, adminId, adminName);

        model.addAttribute("customer", updatedCustomer);

        // Add HX-Trigger header for the success toast
        response.setHeader("HX-Trigger", "{\"show-toast\": {\"message\": \"Customer updated successfully\", \"type\": \"success\"}}");

        return "backoffice/customers/fragments/profile-name-display :: profile-name-display";
    }

    /**
     * Handles concurrent modification exceptions globally for this controller.
     * Re-renders the form with an inline conflict warning and a danger toast.
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public String handleOptimisticLockingFailure(
        final ObjectOptimisticLockingFailureException ex,
        final HttpServletResponse response,
        final Model model)
    {
        // Extract the customer ID from the exception if possible, or we could pass it differently.
        // For simplicity, we can return an alert fragment, or just let the user refresh.
        // The best UX is to tell them to refresh the page.
        response.setHeader("HX-Trigger", "{\"show-toast\": {\"message\": \"Data was modified by someone else. Please refresh.\", \"type\": \"danger\"}}");
        
        // Return a generic error fragment
        return "backoffice/customers/fragments/profile-name-form :: conflict-error";
    }

    // ------------------------------------------------------------------
    // Address Management
    // ------------------------------------------------------------------

    @GetMapping("/{id}/addresses/new")
    public String addAddressForm(@PathVariable final UUID id, final Model model)
    {
        final CustomerService.CustomerDetail detail = getCustomerDetail(id);
        model.addAttribute("customer", detail.customer());
        return "backoffice/customers/fragments/address-form :: address-form";
    }

    @PostMapping("/{id}/addresses/new")
    public String addAddress(@PathVariable final UUID id,
                             @RequestParam(required = false) final String name,
                             @RequestParam final String recipientFirstName,
                             @RequestParam final String recipientLastName,
                             @RequestParam(required = false) final String company,
                             @RequestParam final String addressLine1,
                             @RequestParam(required = false) final String addressLine2,
                             @RequestParam final String city,
                             @RequestParam final String state,
                             @RequestParam final String postalCode,
                             @RequestParam final String country,
                             final Model model,
                             final HttpServletResponse response)
    {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long adminId = null;
        String adminName = "System";

        if (auth != null && auth.getPrincipal() instanceof final AdminUserPrincipal principal)
        {
            adminId = principal.getUserId();
            adminName = principal.getDisplayName();
        }

        customerService.addAddress(id, name, recipientFirstName, recipientLastName, company,
            addressLine1, addressLine2, city, state, postalCode, country, adminId, adminName);

        final CustomerService.CustomerDetail detail = getCustomerDetail(id);
        model.addAttribute("detail", detail);

        response.setHeader("HX-Trigger", "{\"show-toast\": {\"message\": \"Address added successfully\", \"type\": \"success\"}}");

        return "backoffice/customers/fragments/address-list :: address-list";
    }

    @GetMapping("/{id}/addresses/{addressId}/edit")
    public String editAddressForm(@PathVariable final UUID id, @PathVariable final Integer addressId, final Model model)
    {
        final CustomerService.CustomerDetail detail = getCustomerDetail(id);
        final CustomerAddress address = detail.addresses().stream()
            .filter(a -> a.getId().equals(addressId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Address not found"));
            
        model.addAttribute("customer", detail.customer());
        model.addAttribute("address", address);
        return "backoffice/customers/fragments/address-form :: address-form";
    }

    @PostMapping("/{id}/addresses/{addressId}/edit")
    public String editAddress(@PathVariable final UUID id,
                              @PathVariable final Integer addressId,
                              @RequestParam(required = false) final String name,
                              @RequestParam final String recipientFirstName,
                              @RequestParam final String recipientLastName,
                              @RequestParam(required = false) final String company,
                              @RequestParam final String addressLine1,
                              @RequestParam(required = false) final String addressLine2,
                              @RequestParam final String city,
                              @RequestParam final String state,
                              @RequestParam final String postalCode,
                              @RequestParam final String country,
                              final Model model,
                              final HttpServletResponse response)
    {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long adminId = null;
        String adminName = "System";

        if (auth != null && auth.getPrincipal() instanceof final AdminUserPrincipal principal)
        {
            adminId = principal.getUserId();
            adminName = principal.getDisplayName();
        }

        customerService.updateAddress(id, addressId, name, recipientFirstName, recipientLastName, company,
            addressLine1, addressLine2, city, state, postalCode, country, adminId, adminName);

        final CustomerService.CustomerDetail detail = getCustomerDetail(id);
        model.addAttribute("detail", detail);

        response.setHeader("HX-Trigger", "{\"show-toast\": {\"message\": \"Address updated successfully\", \"type\": \"success\"}}");

        return "backoffice/customers/fragments/address-list :: address-list";
    }

    @DeleteMapping("/{id}/addresses/{addressId}")
    public String deleteAddress(@PathVariable final UUID id,
                                @PathVariable final Integer addressId,
                                final Model model,
                                final HttpServletResponse response)
    {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long adminId = null;
        String adminName = "System";

        if (auth != null && auth.getPrincipal() instanceof final AdminUserPrincipal principal)
        {
            adminId = principal.getUserId();
            adminName = principal.getDisplayName();
        }

        customerService.deleteAddress(id, addressId, adminId, adminName);

        final CustomerService.CustomerDetail detail = getCustomerDetail(id);
        model.addAttribute("detail", detail);

        response.setHeader("HX-Trigger", "{\"show-toast\": {\"message\": \"Address deleted successfully\", \"type\": \"success\"}}");

        return "backoffice/customers/fragments/address-list :: address-list";
    }

    // ------------------------------------------------------------------
    // Credit Card Management
    // ------------------------------------------------------------------

    @GetMapping("/{id}/credit-cards/new")
    public String addCreditCardForm(@PathVariable final UUID id, final Model model)
    {
        final CustomerService.CustomerDetail detail = getCustomerDetail(id);
        model.addAttribute("customer", detail.customer());
        return "backoffice/customers/fragments/credit-card-form :: credit-card-form";
    }

    @PostMapping("/{id}/credit-cards/new")
    public String addCreditCard(@PathVariable final UUID id,
                                @RequestParam final String number,
                                @RequestParam final String vendor,
                                @RequestParam final String name,
                                @RequestParam final Integer expMonth,
                                @RequestParam final Integer expYear,
                                final Model model,
                                final HttpServletResponse response)
    {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long adminId = null;
        String adminName = "System";

        if (auth != null && auth.getPrincipal() instanceof final AdminUserPrincipal principal)
        {
            adminId = principal.getUserId();
            adminName = principal.getDisplayName();
        }

        customerService.addCreditCard(id, number, vendor, name, expMonth, expYear, adminId, adminName);

        final CustomerService.CustomerDetail detail = getCustomerDetail(id);
        model.addAttribute("detail", detail);

        response.setHeader("HX-Trigger", "{\"show-toast\": {\"message\": \"Credit card added successfully\", \"type\": \"success\"}}");

        return "backoffice/customers/fragments/credit-card-list :: credit-card-list";
    }

    @DeleteMapping("/{id}/credit-cards/{cardId}")
    public String deleteCreditCard(@PathVariable final UUID id,
                                   @PathVariable final Integer cardId,
                                   final Model model,
                                   final HttpServletResponse response)
    {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long adminId = null;
        String adminName = "System";

        if (auth != null && auth.getPrincipal() instanceof final AdminUserPrincipal principal)
        {
            adminId = principal.getUserId();
            adminName = principal.getDisplayName();
        }

        customerService.deleteCreditCard(id, cardId, adminId, adminName);

        final CustomerService.CustomerDetail detail = getCustomerDetail(id);
        model.addAttribute("detail", detail);

        response.setHeader("HX-Trigger", "{\"show-toast\": {\"message\": \"Credit card deleted successfully\", \"type\": \"success\"}}");

        return "backoffice/customers/fragments/credit-card-list :: credit-card-list";
    }

    // ------------------------------------------------------------------
    // Placeholder sub-modules (to be implemented in future changes)
    // ------------------------------------------------------------------

    /**
     * Customers dashboard placeholder.
     *
     * @param model Spring MVC model
     * @return the generic placeholder view
     */
    @GetMapping("/dashboard")
    public String dashboard(final Model model)
    {
        model.addAttribute("moduleTitle", "Customers Dashboard");
        model.addAttribute("moduleIcon", "dashboard");
        model.addAttribute("metrics", customerService.getDashboardMetrics());
        return "backoffice/customers/dashboard";
    }

    /**
     * Customers import/export placeholder.
     *
     * @param model Spring MVC model
     * @return the generic placeholder view
     */
    @GetMapping("/import-export")
    public String importExport(final Model model)
    {
        model.addAttribute("moduleTitle", "Customers Import / Export");
        model.addAttribute("moduleIcon", "sync_alt");
        return "backoffice/placeholder";
    }

    /**
     * Customers settings placeholder.
     *
     * @param model Spring MVC model
     * @return the generic placeholder view
     */
    @GetMapping("/settings")
    public String settings(final Model model)
    {
        model.addAttribute("moduleTitle", "Customers Settings");
        model.addAttribute("moduleIcon", "settings");
        return "backoffice/placeholder";
    }

    private CustomerService.CustomerDetail getCustomerDetail(final UUID id)
    {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long adminId = null;
        String adminName = "System";

        if (auth != null && auth.getPrincipal() instanceof final AdminUserPrincipal principal)
        {
            adminId = principal.getUserId();
            adminName = principal.getDisplayName();
        }
        return customerService.getCustomerDetails(id, adminId, adminName);
    }
}
