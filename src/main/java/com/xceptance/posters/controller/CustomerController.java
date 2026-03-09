package com.xceptance.posters.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xceptance.posters.entity.CatalogCustomer;
import com.xceptance.posters.entity.CatalogCustomerRepository;
import com.xceptance.posters.service.SessionService;

import jakarta.servlet.http.HttpSession;

/**
 * Handles customer registration, login, logout, account page, and order history.
 * Uses the new entity model (CatalogCustomer).
 */
@Controller
public class CustomerController
{
    private final CatalogCustomerRepository customerRepository;
    private final SessionService sessionService;

    public CustomerController(CatalogCustomerRepository customerRepository,
                              SessionService sessionService)
    {
        this.customerRepository = customerRepository;
        this.sessionService = sessionService;
    }

    @GetMapping("/{locale}/login")
    public String loginPage(@PathVariable String locale)
    {
        return "customer/login";
    }

    @PostMapping("/{locale}/login")
    public String login(@PathVariable String locale,
                        @RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes)
    {
        CatalogCustomer customer = customerRepository.findByEmail(email).orElse(null);
        if (customer != null && customer.checkPassword(password))
        {
            sessionService.setCustomerId(session, customer.getId());
            return "redirect:/" + locale + "/";
        }
        redirectAttributes.addFlashAttribute("error", "Invalid email or password.");
        return "redirect:/" + locale + "/login";
    }

    @GetMapping("/{locale}/logout")
    public String logout(@PathVariable String locale, HttpSession session)
    {
        sessionService.removeCustomerId(session);
        return "redirect:/" + locale + "/";
    }

    @GetMapping("/{locale}/register")
    public String registerPage(@PathVariable String locale)
    {
        return "customer/register";
    }

    @PostMapping("/{locale}/register")
    public String register(@PathVariable String locale,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String firstName,
                           @RequestParam String name,
                           HttpSession session,
                           RedirectAttributes redirectAttributes)
    {
        if (customerRepository.existsByEmail(email))
        {
            redirectAttributes.addFlashAttribute("error", "Email already in use.");
            return "redirect:/" + locale + "/register";
        }
        CatalogCustomer customer = new CatalogCustomer();
        customer.setEmail(email);
        customer.hashPassword(password);
        customer.setFirstName(firstName);
        customer.setLastName(name);
        customer = customerRepository.save(customer);
        sessionService.setCustomerId(session, customer.getId());
        return "redirect:/" + locale + "/";
    }

    @GetMapping("/{locale}/accountOverview")
    public String accountOverview(@PathVariable String locale, HttpSession session, Model model)
    {
        if (!sessionService.isCustomerLoggedIn(session))
        {
            return "redirect:/" + locale + "/login";
        }
        UUID customerId = sessionService.getCustomerId(session);
        CatalogCustomer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
        {
            sessionService.removeCustomerId(session);
            return "redirect:/" + locale + "/login";
        }
        model.addAttribute("customer", customer);
        return "customer/accountOverview";
    }

    @GetMapping("/{locale}/orderOverview")
    public String orderOverview(@PathVariable String locale, HttpSession session, Model model)
    {
        if (!sessionService.isCustomerLoggedIn(session))
        {
            return "redirect:/" + locale + "/login";
        }
        UUID customerId = sessionService.getCustomerId(session);
        CatalogCustomer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
        {
            return "redirect:/" + locale + "/login";
        }
        // Order overview is not yet implemented with new model — placeholder
        model.addAttribute("orders", java.util.List.of());
        model.addAttribute("customer", customer);
        return "customer/orderOverview";
    }

    @PostMapping("/{locale}/updateAccount")
    public String updateAccount(@PathVariable String locale,
                                @RequestParam String firstName,
                                @RequestParam String name,
                                @RequestParam String email,
                                HttpSession session,
                                RedirectAttributes redirectAttributes)
    {
        if (!sessionService.isCustomerLoggedIn(session))
        {
            return "redirect:/" + locale + "/login";
        }
        UUID customerId = sessionService.getCustomerId(session);
        CatalogCustomer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null)
        {
            customer.setFirstName(firstName);
            customer.setLastName(name);
            customer.setEmail(email);
            customerRepository.save(customer);
            redirectAttributes.addFlashAttribute("success", "Account updated.");
        }
        return "redirect:/" + locale + "/accountOverview";
    }
}
