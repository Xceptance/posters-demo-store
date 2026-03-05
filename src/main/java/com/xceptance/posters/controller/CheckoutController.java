package com.xceptance.posters.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xceptance.posters.model.*;
import com.xceptance.posters.repository.*;
import com.xceptance.posters.service.SessionService;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Handles the checkout flow: shipping, billing, payment, place order, order confirmation.
 */
@Controller
public class CheckoutController
{
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final SessionService sessionService;

    public CheckoutController(CartRepository cartRepository,
                              OrderRepository orderRepository,
                              CustomerRepository customerRepository,
                              SessionService sessionService)
    {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.sessionService = sessionService;
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
        Cart cart = sessionService.getCart(session);
        ShippingAddress address = new ShippingAddress();
        address.setName(name);
        address.setFirstName(firstName);
        address.setCompany(company);
        address.setAddressLine(addressLine);
        address.setCity(city);
        address.setState(state);
        address.setZip(zip);
        address.setCountry(country);
        cart.setShippingAddress(address);
        cartRepository.save(cart);
        return "redirect:/" + locale + "/checkout/billingAddress";
    }

    @GetMapping("/{locale}/checkout/billingAddress")
    public String billingAddress(@PathVariable String locale, HttpSession session, Model model)
    {
        addCustomerDataToModel(session, model);
        Cart cart = sessionService.getCart(session);
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
        Cart cart = sessionService.getCart(session);
        BillingAddress address = new BillingAddress();
        address.setName(name);
        address.setFirstName(firstName);
        address.setCompany(company);
        address.setAddressLine(addressLine);
        address.setCity(city);
        address.setState(state);
        address.setZip(zip);
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
        Cart cart = sessionService.getCart(session);
        CreditCard card = new CreditCard();
        card.setCardNumber(cardNumber);
        card.setName(name);
        card.setMonth(month);
        card.setYear(year);
        cart.setCreditCard(card);
        cartRepository.save(cart);
        return "redirect:/" + locale + "/checkout/placeOrder";
    }

    @GetMapping("/{locale}/checkout/placeOrder")
    public String placeOrder(@PathVariable String locale, HttpSession session, Model model)
    {
        Cart cart = sessionService.getCart(session);
        model.addAttribute("cart", cart);
        return "checkout/placeOrder";
    }

    @PostMapping("/{locale}/checkout/placeOrder")
    public String submitOrder(@PathVariable String locale, HttpSession session,
                              RedirectAttributes redirectAttributes)
    {
        Cart cart = sessionService.getCart(session);

        // Create order from cart
        Order order = new Order();
        order.setShippingAddress(cart.getShippingAddress());
        order.setBillingAddress(cart.getBillingAddress());
        order.setCreditCard(cart.getCreditCard());
        order.setSubTotalCosts(cart.getSubTotalPrice());
        order.setTotalTaxCosts(cart.getTotalTaxPrice());
        order.setShippingCosts(cart.getShippingCosts());
        order.setTotalCosts(cart.getTotalPrice());
        order.setTax(cart.getTax());
        order.setOrderDate(LocalDateTime.now());

        // Copy cart products to order products
        for (CartProduct cp : cart.getProducts())
        {
            OrderProduct op = new OrderProduct();
            op.setProduct(cp.getProduct());
            op.setSize(cp.getSize());
            op.setFinish(cp.getFinish());
            op.setProductCount(cp.getProductCount());
            op.setPrice(cp.getPrice());
            order.getProducts().add(op);
        }

        // Associate with customer if logged in
        if (sessionService.isCustomerLoggedIn(session))
        {
            UUID customerId = sessionService.getCustomerId(session);
            Customer customer = customerRepository.findById(customerId).orElse(null);
            if (customer != null)
            {
                order.setCustomer(customer);
                customer.getOrders().add(order);
                customerRepository.save(customer);
            }
        }

        Order savedOrder = orderRepository.save(order);
        sessionService.setOrderId(session, savedOrder.getId());

        // Clear cart
        cart.getProducts().clear();
        cart.setSubTotalPrice(BigDecimal.ZERO);
        cart.setTotalTaxPrice(BigDecimal.ZERO);
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setShippingAddress(null);
        cart.setBillingAddress(null);
        cart.setCreditCard(null);
        cartRepository.save(cart);

        return "redirect:/" + locale + "/checkout/orderConfirmation";
    }

    @GetMapping("/{locale}/checkout/orderConfirmation")
    public String orderConfirmation(@PathVariable String locale, HttpSession session, Model model)
    {
        UUID orderId = sessionService.getOrderId(session);
        if (orderId != null)
        {
            Order order = orderRepository.findById(orderId).orElse(null);
            model.addAttribute("order", order);
        }
        return "checkout/orderConfirmation";
    }

    private void addCustomerDataToModel(HttpSession session, Model model)
    {
        if (sessionService.isCustomerLoggedIn(session))
        {
            UUID customerId = sessionService.getCustomerId(session);
            Customer customer = customerRepository.findById(customerId).orElse(null);
            if (customer != null)
            {
                model.addAttribute("customer", customer);
            }
        }
    }
}
