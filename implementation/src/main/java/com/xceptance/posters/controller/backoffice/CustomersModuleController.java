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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.xceptance.posters.config.AdminUserPrincipal;
import com.xceptance.posters.controller.AbstractBackofficeController;
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
    @GetMapping
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
        return "backoffice/placeholder";
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
}
