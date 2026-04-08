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
 */
package com.xceptance.posters.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xceptance.posters.entity.CatalogCustomer;
import com.xceptance.posters.entity.CatalogCustomer.AccountStatus;
import com.xceptance.posters.entity.CatalogCustomerRepository;
import com.xceptance.posters.entity.PasswordComplexityValidator;
import com.xceptance.posters.service.SessionService;

import jakarta.servlet.http.HttpSession;

/**
 * Handles customer registration, login, logout, account page, and order history.
 *
 * <p>Registration creates accounts in {@link AccountStatus#PENDING} state
 * until the email verification link is clicked. Existing-email registrations
 * gracefully redirect to the login page (soft redirect, US-06).
 */
@Controller
public class CustomerController
{
    /** Verification tokens expire after this many hours. */
    private static final int TOKEN_EXPIRY_HOURS = 24;

    private final CatalogCustomerRepository customerRepository;
    private final SessionService sessionService;
    private final PasswordComplexityValidator passwordValidator;

    public CustomerController(final CatalogCustomerRepository customerRepository,
                              final SessionService sessionService,
                              final PasswordComplexityValidator passwordValidator)
    {
        this.customerRepository = customerRepository;
        this.sessionService = sessionService;
        this.passwordValidator = passwordValidator;
    }

    // ==================== Login ====================

    @GetMapping("/{locale}/login")
    public String loginPage(@PathVariable final String locale)
    {
        return "customer/login";
    }

    @PostMapping("/{locale}/login")
    public String login(@PathVariable final String locale,
                        @RequestParam final String email,
                        @RequestParam final String password,
                        final HttpSession session,
                        final RedirectAttributes redirectAttributes)
    {
        // Task 2.6: Trim whitespace from email
        final String trimmedEmail = email.strip();

        final CatalogCustomer customer = customerRepository.findByEmail(trimmedEmail).orElse(null);

        if (customer != null && customer.checkPassword(password))
        {
            // Deny login for unverified accounts
            if (customer.isPending())
            {
                redirectAttributes.addFlashAttribute("error",
                    "Please verify your email address before logging in. Check your inbox for the activation link.");
                return "redirect:/" + locale + "/login";
            }

            sessionService.setCustomerId(session, customer.getId());
            return "redirect:/" + locale + "/";
        }

        redirectAttributes.addFlashAttribute("error", "Invalid email or password.");
        return "redirect:/" + locale + "/login";
    }

    // ==================== Logout ====================

    @GetMapping("/{locale}/logout")
    public String logout(@PathVariable final String locale, final HttpSession session)
    {
        sessionService.removeCustomerId(session);
        return "redirect:/" + locale + "/";
    }

    // ==================== Registration ====================

    @GetMapping("/{locale}/register")
    public String registerPage(@PathVariable final String locale)
    {
        return "customer/register";
    }

    /**
     * Handles the registration POST.
     *
     * <p>Task 2.5: If the email already exists, gracefully redirect to
     * the login page with a friendly message and pre-filled email.
     *
     * <p>Task 2.6: Emails are trimmed before any validation or lookup.
     *
     * <p>Task 2.3: Localized validation errors are returned to the model.
     */
    @PostMapping("/{locale}/register")
    public String register(@PathVariable final String locale,
                           @RequestParam final String email,
                           @RequestParam final String password,
                           @RequestParam final String firstName,
                           @RequestParam final String name,
                           final HttpSession session,
                           final RedirectAttributes redirectAttributes)
    {
        // Task 2.6: Trim whitespace from email
        final String trimmedEmail = email.strip();

        // Task 2.5: Soft redirect for existing emails
        if (customerRepository.existsByEmail(trimmedEmail))
        {
            redirectAttributes.addFlashAttribute("info",
                "Looks like you already have an account with us! Please log in instead.");
            redirectAttributes.addFlashAttribute("prefillEmail", trimmedEmail);
            return "redirect:/" + locale + "/login";
        }

        // Task 2.3: Password complexity validation
        final List<String> passwordErrors = passwordValidator.validate(password);
        if (!passwordErrors.isEmpty())
        {
            redirectAttributes.addFlashAttribute("errors", passwordErrors);
            redirectAttributes.addFlashAttribute("prefillEmail", trimmedEmail);
            redirectAttributes.addFlashAttribute("prefillFirstName", firstName);
            redirectAttributes.addFlashAttribute("prefillLastName", name);
            return "redirect:/" + locale + "/register";
        }

        // Create account in PENDING state with verification token
        final CatalogCustomer customer = new CatalogCustomer();
        customer.setEmail(trimmedEmail);
        customer.hashPassword(password);
        customer.setFirstName(firstName);
        customer.setLastName(name);
        customer.setAccountStatus(AccountStatus.PENDING);
        customer.setVerificationToken(UUID.randomUUID().toString());
        customer.setTokenExpiresAt(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS));

        customerRepository.save(customer);

        // TODO: Send verification email with magic link containing the token
        // emailService.sendVerificationEmail(customer);

        redirectAttributes.addFlashAttribute("success",
            "Your account has been created! Please check your email to verify your address.");
        return "redirect:/" + locale + "/register";
    }

    // ==================== Task 2.1: Activation Endpoint ====================

    /**
     * Handles magic link activation. Validates the token, activates the
     * account, and logs the user in.
     */
    @GetMapping("/{locale}/activate")
    public String activateAccount(@PathVariable final String locale,
                                  @RequestParam final String token,
                                  final HttpSession session,
                                  final RedirectAttributes redirectAttributes)
    {
        final CatalogCustomer customer = customerRepository.findByVerificationToken(token).orElse(null);

        if (customer == null)
        {
            redirectAttributes.addFlashAttribute("error",
                "This activation link is invalid. Please request a new one.");
            return "redirect:/" + locale + "/register";
        }

        if (customer.isTokenExpired())
        {
            redirectAttributes.addFlashAttribute("error",
                "This activation link has expired. Please request a new one.");
            redirectAttributes.addFlashAttribute("expiredToken", token);
            return "redirect:/" + locale + "/register";
        }

        // Activate and clear token
        customer.activate();
        customerRepository.save(customer);

        // Auto-login after activation
        sessionService.setCustomerId(session, customer.getId());
        redirectAttributes.addFlashAttribute("success",
            "Your email has been verified. Welcome!");
        return "redirect:/" + locale + "/";
    }

    // ==================== Task 2.2: Resend Activation ====================

    /**
     * Resends a fresh verification token for pending accounts.
     */
    @PostMapping("/{locale}/resendActivation")
    public String resendActivation(@PathVariable final String locale,
                                   @RequestParam final String email,
                                   final RedirectAttributes redirectAttributes)
    {
        final String trimmedEmail = email.strip();
        final CatalogCustomer customer = customerRepository.findByEmail(trimmedEmail).orElse(null);

        if (customer != null && customer.isPending())
        {
            customer.setVerificationToken(UUID.randomUUID().toString());
            customer.setTokenExpiresAt(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS));
            customerRepository.save(customer);

            // TODO: Send verification email
            // emailService.sendVerificationEmail(customer);
        }

        // Always show success to prevent enumeration
        redirectAttributes.addFlashAttribute("success",
            "If your account exists and is pending verification, a new email has been sent.");
        return "redirect:/" + locale + "/register";
    }

    // ==================== Task 2.4: HTMX Inline Format Validation ====================

    /**
     * Format-only validation endpoint for HTMX inline feedback.
     *
     * <p><strong>Security:</strong> This endpoint MUST NOT query the database
     * for existing users to prevent enumeration attacks. It only performs
     * client-facing format checks.
     *
     * @param field the field name ("email" or "password")
     * @param value the current field value
     * @return an HTML fragment with error messages, or empty string if valid
     */
    @PostMapping("/{locale}/validate")
    @ResponseBody
    public String validateField(@PathVariable final String locale,
                                @RequestParam final String field,
                                @RequestParam final String value)
    {
        final String rawValue = value == null ? "" : value;

        return switch (field)
        {
            case "email" -> validateEmailFormat(rawValue.strip());
            case "password" -> validatePasswordFormat(rawValue);
            default -> "";
        };
    }

    /**
     * Validates email format without any database lookup.
     */
    private String validateEmailFormat(final String email)
    {
        if (email.isEmpty())
        {
            return "<span class=\"text-danger\" role=\"alert\">Email is required.</span>";
        }

        // Basic RFC-like format check
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
        {
            return "<span class=\"text-danger\" role=\"alert\">Please enter a valid email format.</span>";
        }

        return "<span class=\"text-success\" role=\"alert\">✓</span>";
    }

    /**
     * Validates password complexity without any database lookup.
     */
    private String validatePasswordFormat(final String password)
    {
        if (password.isEmpty())
        {
            return "<span class=\"text-danger\" role=\"alert\">Password is required.</span>";
        }

        final List<String> errors = passwordValidator.validate(password);
        if (!errors.isEmpty())
        {
            final StringBuilder sb = new StringBuilder();
            sb.append("<ul class=\"text-danger list-unstyled mb-0\" role=\"alert\">");
            for (final String error : errors)
            {
                sb.append("<li>").append(error).append("</li>");
            }
            sb.append("</ul>");
            return sb.toString();
        }

        return "<span class=\"text-success\" role=\"alert\">✓ Strong password</span>";
    }

    // ==================== Account Overview ====================

    @GetMapping("/{locale}/accountOverview")
    public String accountOverview(@PathVariable final String locale, final HttpSession session, final Model model)
    {
        if (!sessionService.isCustomerLoggedIn(session))
        {
            return "redirect:/" + locale + "/login";
        }
        final UUID customerId = sessionService.getCustomerId(session);
        final CatalogCustomer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
        {
            sessionService.removeCustomerId(session);
            return "redirect:/" + locale + "/login";
        }
        model.addAttribute("customer", customer);
        return "customer/accountOverview";
    }

    // ==================== Order Overview ====================

    @GetMapping("/{locale}/orderOverview")
    public String orderOverview(@PathVariable final String locale, final HttpSession session, final Model model)
    {
        if (!sessionService.isCustomerLoggedIn(session))
        {
            return "redirect:/" + locale + "/login";
        }
        final UUID customerId = sessionService.getCustomerId(session);
        final CatalogCustomer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
        {
            return "redirect:/" + locale + "/login";
        }
        // Order overview is not yet implemented with new model — placeholder
        model.addAttribute("orders", java.util.List.of());
        model.addAttribute("customer", customer);
        return "customer/orderOverview";
    }

    // ==================== Update Account ====================

    @PostMapping("/{locale}/updateAccount")
    public String updateAccount(@PathVariable final String locale,
                                @RequestParam final String firstName,
                                @RequestParam final String name,
                                @RequestParam final String email,
                                final HttpSession session,
                                final RedirectAttributes redirectAttributes)
    {
        if (!sessionService.isCustomerLoggedIn(session))
        {
            return "redirect:/" + locale + "/login";
        }
        final UUID customerId = sessionService.getCustomerId(session);
        final CatalogCustomer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null)
        {
            customer.setFirstName(firstName);
            customer.setLastName(name);
            customer.setEmail(email.strip());
            customerRepository.save(customer);
            redirectAttributes.addFlashAttribute("success", "Account updated.");
        }
        return "redirect:/" + locale + "/accountOverview";
    }
}
