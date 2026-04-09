package com.xceptance.posters.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xceptance.posters.entity.CatalogCart;
import com.xceptance.posters.entity.CatalogOrder;
import com.xceptance.posters.entity.CatalogCustomer;
import com.xceptance.posters.entity.CatalogCustomerRepository;
import com.xceptance.posters.entity.CreditCardMasker;
import com.xceptance.posters.entity.CreditCardVendor;
import com.xceptance.posters.service.CheckoutService;
import com.xceptance.posters.service.SessionService;

import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles the checkout flow: shipping, billing, payment, place order, order confirmation.
 * Uses the new entity model (CatalogCart, CartAddress, CartCreditCard, CatalogOrder).
 */
@Controller
public class CheckoutController
{
    private final CheckoutService checkoutService;
    private final CatalogCustomerRepository customerRepository;
    private final SessionService sessionService;

    public CheckoutController(CheckoutService checkoutService,
                              CatalogCustomerRepository customerRepository,
                              SessionService sessionService)
    {
        this.checkoutService = checkoutService;
        this.customerRepository = customerRepository;
        this.sessionService = sessionService;
    }

    // ─── DTOs for JSON Agents ──────────────────────────────────────────

    public record CheckoutAddressDto(String name, String firstName, String company, String addressLine, String city, String state, String zip, String country) {}
    public record CheckoutPaymentDto(String cardNumber, String name, String expiry, String cvv) {}
    public record CheckoutCustomerDto(String email, String firstName, String lastName) {}

    public record CheckoutRequestDto(
        CheckoutAddressDto shippingAddress,
        CheckoutAddressDto billingAddress,
        CheckoutPaymentDto payment,
        CheckoutCustomerDto customer
    ) {}

    public record CheckoutResponseDto(boolean success, java.util.List<String> errors, String orderNumber) {}

    // ─── WebMCP Checkout JSON API ──────────────────────────────────────

    /**
     * Dedicated JSON endpoint for the AI agent (WebMCP) to perform a full checkout securely.
     */
    @PostMapping(value = "/api/v2/checkout", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public org.springframework.http.ResponseEntity<CheckoutResponseDto> apiCheckout(@RequestBody final CheckoutRequestDto payload,
                                           final HttpSession session)
    {
        final java.util.List<String> errors = new java.util.ArrayList<>();
        
        if (payload.shippingAddress() == null) {
            errors.add("shippingAddress:Missing required shipping information.");
        } else {
            if (payload.shippingAddress().name() == null) errors.add("shippingAddress.name:Missing field.");
            if (payload.shippingAddress().firstName() == null) errors.add("shippingAddress.firstName:Missing field.");
            if (payload.shippingAddress().addressLine() == null) errors.add("shippingAddress.addressLine:Missing field.");
            if (payload.shippingAddress().city() == null) errors.add("shippingAddress.city:Missing field.");
            if (payload.shippingAddress().state() == null) errors.add("shippingAddress.state:Missing field.");
            if (payload.shippingAddress().zip() == null) errors.add("shippingAddress.zip:Missing field.");
            if (payload.shippingAddress().country() == null) errors.add("shippingAddress.country:Missing field.");
        }

        if (payload.billingAddress() == null) {
            errors.add("billingAddress:Missing required billing information.");
        } else {
            if (payload.billingAddress().name() == null) errors.add("billingAddress.name:Missing field.");
            if (payload.billingAddress().firstName() == null) errors.add("billingAddress.firstName:Missing field.");
            if (payload.billingAddress().addressLine() == null) errors.add("billingAddress.addressLine:Missing field.");
            if (payload.billingAddress().city() == null) errors.add("billingAddress.city:Missing field.");
            if (payload.billingAddress().state() == null) errors.add("billingAddress.state:Missing field.");
            if (payload.billingAddress().zip() == null) errors.add("billingAddress.zip:Missing field.");
            if (payload.billingAddress().country() == null) errors.add("billingAddress.country:Missing field.");
        }

        if (payload.payment() == null) {
            errors.add("payment:Missing required payment information.");
        } else {
            if (payload.payment().cardNumber() == null) errors.add("payment.cardNumber:Missing field.");
            if (payload.payment().name() == null) errors.add("payment.name:Missing field.");
            if (payload.payment().expiry() == null) errors.add("payment.expiry:Missing field.");
            if (payload.payment().cvv() == null) errors.add("payment.cvv:Missing field.");
        }

        if (!errors.isEmpty()) {
            return org.springframework.http.ResponseEntity.badRequest().body(new CheckoutResponseDto(false, errors, null));
        }

        final CatalogCart cart = sessionService.getCart(session);

        checkoutService.updateShippingAddress(cart,
            payload.shippingAddress().name(),
            payload.shippingAddress().firstName(),
            payload.shippingAddress().company(),
            payload.shippingAddress().addressLine(),
            payload.shippingAddress().city(),
            payload.shippingAddress().state(),
            payload.shippingAddress().zip(),
            payload.shippingAddress().country()
        );

        checkoutService.updateBillingAddress(cart,
            payload.billingAddress().name(),
            payload.billingAddress().firstName(),
            payload.billingAddress().company(),
            payload.billingAddress().addressLine(),
            payload.billingAddress().city(),
            payload.billingAddress().state(),
            payload.billingAddress().zip(),
            payload.billingAddress().country()
        );

        final java.util.List<String> paymentErrors = checkoutService.updatePayment(cart,
            payload.payment().cardNumber(),
            payload.payment().name(),
            payload.payment().expiry(),
            payload.payment().cvv()
        );

        if (!paymentErrors.isEmpty()) {
            return org.springframework.http.ResponseEntity.badRequest().body(new CheckoutResponseDto(false, paymentErrors, null));
        }

        String email = "guest@example.com";
        String fName = "Guest";
        String lName = "Customer";

        if (sessionService.isCustomerLoggedIn(session)) {
            final UUID customerId = sessionService.getCustomerId(session);
            final Optional<CatalogCustomer> customerOpt = customerRepository.findById(customerId);
            if (customerOpt.isPresent()) {
                final CatalogCustomer c = customerOpt.get();
                email = c.getEmail();
                fName = c.getFirstName();
                lName = c.getLastName();
            }
        } else if (payload.customer() != null) {
            email = payload.customer().email() != null ? payload.customer().email() : email;
            fName = payload.customer().firstName() != null ? payload.customer().firstName() : fName;
            lName = payload.customer().lastName() != null ? payload.customer().lastName() : lName;
        }

        try {
            final CatalogOrder order = checkoutService.checkout(cart.getId(), email, fName, lName);
            sessionService.setOrderId(session, order.getId());
            sessionService.removeCartId(session);
            return org.springframework.http.ResponseEntity.ok(new CheckoutResponseDto(true, java.util.List.of(), order.getOrderNumber()));
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().body(new CheckoutResponseDto(false, java.util.List.of("checkout:Failed to checkout - " + e.getMessage()), null));
        }
    }

    @GetMapping("/{locale}/checkout/shippingAddress")
    public String shippingAddress(@PathVariable String locale, HttpSession session, Model model)
    {
        addCustomerDataToModel(session, model);
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
        checkoutService.updateShippingAddress(cart, name, firstName, company, addressLine, city, state, zip, country);

        return "redirect:/" + locale + "/checkout/billingAddress";
    }

    @GetMapping("/{locale}/checkout/billingAddress")
    public String billingAddress(@PathVariable String locale, HttpSession session, Model model)
    {
        addCustomerDataToModel(session, model);
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
        checkoutService.updateBillingAddress(cart, name, firstName, company, addressLine, city, state, zip, country);

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
                                @RequestParam String expiry,
                                @RequestParam(required = false) String cvv,
                                HttpSession session,
                                Model model)
    {
        CatalogCart cart = sessionService.getCart(session);
        List<String> errors = checkoutService.updatePayment(cart, cardNumber, name, expiry, cvv);

        // If validation fails, re-render with errors and masked card
        if (!errors.isEmpty())
        {
            addCustomerDataToModel(session, model);
            model.addAttribute("validationErrors", errors);
            String digits = cardNumber == null ? "" : cardNumber.replaceAll("\\D", "");
            model.addAttribute("maskedCardNumber", CreditCardMasker.mask(digits));
            model.addAttribute("cardName", name);
            model.addAttribute("expiry", expiry);
            CreditCardVendor vendor = CreditCardVendor.detect(digits);
            model.addAttribute("detectedVendor", vendor != null ? vendor.getDisplayName() : null);
            return "checkout/payment";
        }

        return "redirect:/" + locale + "/checkout/placeOrder";
    }

    @GetMapping("/{locale}/checkout/placeOrder")
    public String placeOrder(@PathVariable String locale, HttpSession session, Model model)
    {
        CatalogCart cart = sessionService.getCart(session);
        model.addAttribute("cart", cart);

        // Add masked card number for display
        if (cart.getCreditCard() != null)
        {
            model.addAttribute("maskedCardNumber", CreditCardMasker.mask(cart.getCreditCard().getNumber()));
        }

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
        try {
            CatalogOrder order = checkoutService.checkout(cart.getId(), email, firstName, lastName);
            sessionService.setOrderId(session, order.getId());

            // Create a new empty cart for the session
            sessionService.removeCartId(session);

            return "redirect:/" + locale + "/checkout/orderConfirmation";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/" + locale + "/cart";
        }
    }

    @GetMapping("/{locale}/checkout/orderConfirmation")
    public String orderConfirmation(@PathVariable String locale, HttpSession session, Model model)
    {
        UUID orderId = sessionService.getOrderId(session);
        if (orderId != null)
        {
            checkoutService.getOrder(orderId).ifPresent(order -> {
                model.addAttribute("order", order);

                // Add masked card number for display
                if (order.getCreditCard() != null)
                {
                    model.addAttribute("maskedCardNumber", CreditCardMasker.mask(order.getCreditCard().getNumber()));
                    model.addAttribute("cardVendor", order.getCreditCard().getVendor());
                }
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

