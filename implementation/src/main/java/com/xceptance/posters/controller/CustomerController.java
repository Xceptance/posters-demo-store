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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xceptance.posters.entity.Customer;
import com.xceptance.posters.entity.CustomerProfile;
import com.xceptance.posters.repository.CustomerRepository;
import com.xceptance.posters.repository.CustomerProfileRepository;
import com.xceptance.posters.entity.CatalogOrder;
import com.xceptance.posters.repository.CatalogOrderRepository;
import com.xceptance.posters.service.CheckoutService;
import com.xceptance.posters.service.CustomerSearchService;
import com.xceptance.posters.service.SessionService;
import com.xceptance.posters.dto.OrderDto;

import jakarta.servlet.http.HttpSession;

/**
 * Handles customer registration, login, logout, account page, and order history.
 * Uses the new entity model (Customer).
 */
@Controller
public class CustomerController
{
    private final CustomerRepository customerRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final SessionService sessionService;
    private final CatalogOrderRepository orderRepository;
    private final CheckoutService checkoutService;
    private final CustomerSearchService customerSearchService;

    public CustomerController(CustomerRepository customerRepository,
                              CustomerProfileRepository customerProfileRepository,
                              CatalogOrderRepository orderRepository,
                              SessionService sessionService,
                              CheckoutService checkoutService,
                              CustomerSearchService customerSearchService)
    {
        this.customerRepository = customerRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.orderRepository = orderRepository;
        this.sessionService = sessionService;
        this.checkoutService = checkoutService;
        this.customerSearchService = customerSearchService;
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
        Customer customer = customerRepository.findByEmail(email).orElse(null);
        if (customer != null && customer.checkPassword(password))
        {
            sessionService.setCustomerId(session, customer.getId());
            
            // Update lastLogin in CustomerProfile
            final CustomerProfile profile = customerProfileRepository.findByCustomer_Id(customer.getId()).orElse(null);
            if (profile != null)
            {
                profile.setLastLogin(LocalDateTime.now());
                customerProfileRepository.save(profile);
            }
            
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
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.hashPassword(password);
        customer.setFirstName(firstName);
        customer.setLastName(name);
        customer = customerRepository.save(customer);

        // Create CustomerProfile
        final CustomerProfile profile = new CustomerProfile();
        profile.setCustomer(customer);
        profile.setPassword(customer.getPassword());
        profile.setLastPasswordChange(LocalDateTime.now());
        customerProfileRepository.save(profile);

        customerSearchService.indexCustomerAsync(customer.getId());

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
        Customer customer = customerRepository.findById(customerId).orElse(null);
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
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
        {
            return "redirect:/" + locale + "/login";
        }
        List<CatalogOrder> orders = orderRepository.findByCustomer_EmailOrderByOrderDateDesc(customer.getEmail());
        
        final List<OrderDto> orderDtos = orders.stream()
                .map(checkoutService::toOrderDto)
                .toList();

        model.addAttribute("orderDtos", orderDtos);
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
        Customer customer = customerRepository.findById(customerId).orElse(null);
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
