package com.xceptance.posters.service;

import com.xceptance.posters.entity.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import java.util.Optional;

/**
 * Service layer for checkout operations using the new entity model.
 * Handles cart management and cart-to-order conversion.
 */
@Service
public class CheckoutService {

    private static final Logger log = LoggerFactory.getLogger(CheckoutService.class);

    private final CatalogCartRepository cartRepository;
    private final CatalogOrderRepository orderRepository;
    private final EntityManager entityManager;
    private final CreditCardValidator creditCardValidator;

    public CheckoutService(final CatalogCartRepository cartRepository,
                           final CatalogOrderRepository orderRepository,
                           final EntityManager entityManager,
                           final CreditCardValidator creditCardValidator) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.entityManager = entityManager;
        this.creditCardValidator = creditCardValidator;
    }

    /**
     * Saves or updates a cart.
     */
    @Transactional
    public CatalogCart saveCart(CatalogCart cart) {
        log.info("Saving cart: {}", cart.getId());
        return cartRepository.save(cart);
    }

    /**
     * Retrieves a cart by its ID.
     */
    @Transactional(readOnly = true)
    public Optional<CatalogCart> getCart(final java.util.UUID cartId) {
        return cartRepository.findById(cartId);
    }

    /**
     * Updates the shipping address for a cart.
     *
     * @param cart the cart to update
     * @param name the recipient's last name
     * @param firstName the recipient's first name
     * @param company the recipient's company
     * @param addressLine the address line
     * @param city the city
     * @param state the state
     * @param zip the postal code
     * @param country the country
     * @return the updated cart
     */
    @Transactional
    public CatalogCart updateShippingAddress(final CatalogCart cart,
                                             final String name,
                                             final String firstName,
                                             final String company,
                                             final String addressLine,
                                             final String city,
                                             final String state,
                                             final String zip,
                                             final String country) {
        CartAddress address = cart.getShippingAddress();
        if (address == null) {
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
        return saveCart(cart);
    }

    /**
     * Updates the billing address for a cart.
     *
     * @param cart the cart to update
     * @param name the recipient's last name
     * @param firstName the recipient's first name
     * @param company the recipient's company
     * @param addressLine the address line
     * @param city the city
     * @param state the state
     * @param zip the postal code
     * @param country the country
     * @return the updated cart
     */
    @Transactional
    public CatalogCart updateBillingAddress(final CatalogCart cart,
                                            final String name,
                                            final String firstName,
                                            final String company,
                                            final String addressLine,
                                            final String city,
                                            final String state,
                                            final String zip,
                                            final String country) {
        CartAddress address = cart.getBillingAddress();
        if (address == null) {
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
        return saveCart(cart);
    }

    /**
     * Updates the payment for a cart after validating the credit card details.
     *
     * @param cart the cart to update
     * @param cardNumber the credit card number
     * @param name the name on the card
     * @param expiry the expiration string (MM/YY)
     * @param cvv the security code
     * @return a list of validation errors, empty if successful
     */
    @Transactional
    public java.util.List<String> updatePayment(final CatalogCart cart,
                                                final String cardNumber,
                                                final String name,
                                                final String expiry,
                                                final String cvv) {
        final java.util.List<String> errors = new java.util.ArrayList<>();
        
        final String digits = cardNumber == null ? "" : cardNumber.replaceAll("\\D", "");
        final CreditCardVendor vendor = CreditCardVendor.detect(digits);

        int month = 0;
        int year = 0;
        boolean expiryParsed = false;
        if (expiry != null && expiry.matches("\\d{2}/\\d{2}")) {
            month = Integer.parseInt(expiry.substring(0, 2));
            year = Integer.parseInt(expiry.substring(3, 5));
            expiryParsed = true;
        }

        if (digits.isEmpty()) {
            errors.add("cardNumber:Please enter a card number.");
        } else {
            if (!creditCardValidator.isLuhnValid(digits)) {
                errors.add("cardNumber:Please enter a valid credit card number.");
            }
            if (vendor != null && !creditCardValidator.isValidLength(digits, vendor)) {
                errors.add("cardNumber:Card number length is invalid for " + vendor.getDisplayName() + ".");
            }
        }

        if (name == null || name.isBlank()) {
            errors.add("name:Please enter the cardholder name.");
        }

        if (!expiryParsed) {
            errors.add("expiry:Please enter expiry in MM/YY format.");
        } else if (!creditCardValidator.isExpiryValid(month, year)) {
            errors.add("expiry:Card is expired or expiry date is invalid.");
        }

        if (!creditCardValidator.isCvvValid(cvv, vendor)) {
            int expectedLen = vendor != null ? vendor.getCvvLength() : 3;
            errors.add("cvv:CVV must be " + expectedLen + " digits.");
        }

        if (!errors.isEmpty()) {
            return errors;
        }

        CartCreditCard card = cart.getCreditCard();
        if (card == null) {
            card = new CartCreditCard();
            card.setCart(cart);
        }
        card.setNumber(digits);
        card.setName(name);
        card.setVendor(vendor != null ? vendor.getDisplayName() : "Unknown");
        card.setExpMonth(month);
        card.setExpYear(2000 + year);
        cart.setCreditCard(card);
        saveCart(cart);

        return errors;
    }

    /**
     * Converts a cart to an order using CartToOrderConverter, persists the order,
     * and deletes the cart.
     *
     * @param cartId the UUID of the cart to checkout
     * @param customerEmail the customer's email
     * @param customerFirstName the customer's first name
     * @param customerLastName the customer's last name
     * @return the persisted CatalogOrder
     * @throws RuntimeException if the cart does not exist
     */
    @Transactional
    public CatalogOrder checkout(java.util.UUID cartId,
                                  String customerEmail,
                                  String customerFirstName,
                                  String customerLastName) {
        CatalogCart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> {
                String msg = "ERROR: Cart not found for checkout: " + cartId;
                log.error(msg);
                return new RuntimeException(msg);
            });

        CatalogOrder order = CartToOrderConverter.convert(cart, "USD",
            customerEmail, customerFirstName, customerLastName);
        orderRepository.save(order);

        // 1. Clear FK references on cart so child rows can be deleted
        entityManager.createNativeQuery("UPDATE catalog_carts SET shipping_address_id = NULL, billing_address_id = NULL, credit_card_id = NULL WHERE id = :cid")
            .setParameter("cid", cart.getId()).executeUpdate();
        // 2. Delete child entities
        entityManager.createNativeQuery("DELETE FROM catalog_cart_lineitems WHERE cart_id = :cid")
            .setParameter("cid", cart.getId()).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM catalog_cart_credit_cards WHERE cart_id = :cid")
            .setParameter("cid", cart.getId()).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM catalog_cart_addresses WHERE cart_id = :cid")
            .setParameter("cid", cart.getId()).executeUpdate();
        // 3. Delete the cart itself
        entityManager.createNativeQuery("DELETE FROM catalog_carts WHERE id = :cid")
            .setParameter("cid", cart.getId()).executeUpdate();
        entityManager.flush();
        entityManager.clear();

        log.info("Checkout complete: cart {} → order {}", cartId, order.getOrderNumber());
        return order;
    }

    /**
     * Retrieves an order by its ID.
     */
    @Transactional(readOnly = true)
    public Optional<CatalogOrder> getOrder(java.util.UUID orderId) {
        return orderRepository.findById(orderId);
    }

    /**
     * Retrieves an order by its order number.
     */
    @Transactional(readOnly = true)
    public Optional<CatalogOrder> getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }
}
