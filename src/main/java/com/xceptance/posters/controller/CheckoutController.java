package com.xceptance.posters.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xceptance.posters.entity.CartAddress;
import com.xceptance.posters.entity.CartCreditCard;
import com.xceptance.posters.entity.CatalogCart;
import com.xceptance.posters.entity.CatalogCartRepository;
import com.xceptance.posters.entity.CatalogOrder;
import com.xceptance.posters.entity.CheckoutService;
import com.xceptance.posters.entity.CatalogCustomer;
import com.xceptance.posters.entity.CatalogCustomerRepository;
import com.xceptance.posters.service.SessionService;
import com.xceptance.posters.util.CountryList;

import jakarta.servlet.http.HttpSession;

import java.util.Optional;
import java.util.UUID;

/**
 * Handles the checkout flow: shipping, billing, payment, place order, order confirmation.
 * Uses the new entity model (CatalogCart, CartAddress, CartCreditCard, CatalogOrder).
 */
@Controller
public class CheckoutController
{
    private final CatalogCartRepository cartRepository;
    private final CheckoutService checkoutService;
    private final CatalogCustomerRepository customerRepository;
    private final SessionService sessionService;

    public CheckoutController(CatalogCartRepository cartRepository,
                              CheckoutService checkoutService,
                              CatalogCustomerRepository customerRepository,
                              SessionService sessionService)
    {
        this.cartRepository = cartRepository;
        this.checkoutService = checkoutService;
        this.customerRepository = customerRepository;
        this.sessionService = sessionService;
    }

    @GetMapping("/{locale}/checkout/shippingAddress")
    public String shippingAddress(@PathVariable String locale, HttpSession session, Model model)
    {
        addCustomerDataToModel(session, model);
        model.addAttribute("countries", CountryList.getCountries());
        return "checkout/shippingAddress";
    }

    @PostMapping("/{locale}/checkout/shippingAddress")
    public String submitShippingAddress(@PathVariable String locale,
                                        @RequestParam String name,
                                        @RequestParam String firstName,
                                        @RequestParam(required = false) String company,
                                        @RequestParam String addressLine,
                                        @RequestParam String city,
                                        @RequestParam String state,
                                        @RequestParam String zip,
                                        @RequestParam String country,
                                        HttpSession session)
    {
        CatalogCart cart = sessionService.getCart(session);

        CartAddress address = cart.getShippingAddress();
        if (address == null)
        {
            address = new CartAddress();
            address.setCart(cart);
        }
        address.setRecipientLastName(name);
        address.setRecipientFirstName(firstName);
        address.setCompany(company);
        address.setAddressLine1(addressLine);
        address.setCity(city);
        address.setState(state);
        address.setPostalCode(zip);
        address.setCountry(country);
        cart.setShippingAddress(address);
        cartRepository.save(cart);

        return "redirect:/" + locale + "/checkout/billingAddress";
    }

    @GetMapping("/{locale}/checkout/billingAddress")
    public String billingAddress(@PathVariable String locale, HttpSession session, Model model)
    {
        addCustomerDataToModel(session, model);
        model.addAttribute("countries", CountryList.getCountries());
        CatalogCart cart = sessionService.getCart(session);
        model.addAttribute("shippingAddress", cart.getShippingAddress());
        return "checkout/billingAddress";
    }

    @PostMapping("/{locale}/checkout/billingAddress")
    public String submitBillingAddress(@PathVariable String locale,
                                       @RequestParam String name,
                                       @RequestParam String firstName,
                                       @RequestParam(required = false) String company,
                                       @RequestParam String addressLine,
                                       @RequestParam String city,
                                       @RequestParam String state,
                                       @RequestParam String zip,
                                       @RequestParam String country,
                                       HttpSession session)
    {
        CatalogCart cart = sessionService.getCart(session);

        CartAddress address = cart.getBillingAddress();
        if (address == null)
        {
            address = new CartAddress();
            address.setCart(cart);
        }
        address.setRecipientLastName(name);
        address.setRecipientFirstName(firstName);
        address.setCompany(company);
        address.setAddressLine1(addressLine);
        address.setCity(city);
        address.setState(state);
        address.setPostalCode(zip);
        address.setCountry(country);
        cart.setBillingAddress(address);
        cartRepository.save(cart);

        return "redirect:/" + locale + "/checkout/payment";
    }

    @GetMapping("/{locale}/checkout/payment")
    public String payment(@PathVariable String locale, HttpSession session, Model model)
    {
        addCustomerDataToModel(session, model);
        return "checkout/payment";
    }

    @PostMapping("/{locale}/checkout/payment")
    public String submitPayment(@PathVariable String locale,
                                @RequestParam String cardNumber,
                                @RequestParam String name,
                                @RequestParam int month,
                                @RequestParam int year,
                                HttpSession session)
    {
        CatalogCart cart = sessionService.getCart(session);

        CartCreditCard card = cart.getCreditCard();
        if (card == null)
        {
            card = new CartCreditCard();
            card.setCart(cart);
        }
        card.setNumber(cardNumber);
        card.setName(name);
        card.setVendor("Visa"); // Default vendor
        card.setExpMonth(month);
        card.setExpYear(year);
        cart.setCreditCard(card);
        cartRepository.save(cart);

        return "redirect:/" + locale + "/checkout/placeOrder";
    }

    @GetMapping("/{locale}/checkout/placeOrder")
    public String placeOrder(@PathVariable String locale, HttpSession session, Model model)
    {
        CatalogCart cart = sessionService.getCart(session);
        model.addAttribute("cart", cart);
        return "checkout/placeOrder";
    }

    @PostMapping("/{locale}/checkout/placeOrder")
    public String submitOrder(@PathVariable String locale, HttpSession session,
                              RedirectAttributes redirectAttributes)
    {
        CatalogCart cart = sessionService.getCart(session);

        // Determine customer info
        String email = "guest@example.com";
        String firstName = "Guest";
        String lastName = "Customer";

        if (sessionService.isCustomerLoggedIn(session))
        {
            UUID customerId = sessionService.getCustomerId(session);
            Optional<CatalogCustomer> customerOpt = customerRepository.findById(customerId);
            if (customerOpt.isPresent())
            {
                CatalogCustomer c = customerOpt.get();
                email = c.getEmail();
                firstName = c.getFirstName();
                lastName = c.getLastName();
            }
        }

        // Use CheckoutService to convert cart to order and persist
        CatalogOrder order = checkoutService.checkout(cart.getId(), email, firstName, lastName);
        sessionService.setOrderId(session, order.getId());

        // Create a new empty cart for the session
        sessionService.removeCartId(session);

        return "redirect:/" + locale + "/checkout/orderConfirmation";
    }

    @GetMapping("/{locale}/checkout/orderConfirmation")
    public String orderConfirmation(@PathVariable String locale, HttpSession session, Model model)
    {
        UUID orderId = sessionService.getOrderId(session);
        if (orderId != null)
        {
            checkoutService.getOrder(orderId).ifPresent(order -> {
                model.addAttribute("order", order);
            });
        }
        return "checkout/orderConfirmation";
    }

    private void addCustomerDataToModel(HttpSession session, Model model)
    {
        if (sessionService.isCustomerLoggedIn(session))
        {
            UUID customerId = sessionService.getCustomerId(session);
            customerRepository.findById(customerId).ifPresent(customer -> {
                model.addAttribute("customer", customer);
            });
        }
    }
}
