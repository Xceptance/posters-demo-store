package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import models.BillingAddress;
import models.Cart;
import models.CreditCard;
import models.Customer;
import models.Order;
import models.ShippingAddress;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.params.Param;
import util.date.DateUtils;
import util.session.SessionHandling;

import com.google.common.base.Optional;
import com.google.inject.Inject;

import conf.PosterConstants;
import filters.SessionCustomerFilter;
import filters.SessionTerminatedFilter;

/**
 * Controller class, that provides the checkout functionality.
 * 
 * @author sebastianloob
 */
public class CheckoutController
{
    @Inject
    Messages msg;

    @Inject
    PosterConstants xcpConf;

    private Optional<String> language = Optional.of("en");

    /**
     * Starts the checkout. Returns an error page, if the cart is empty, otherwise the page to enter a shipping address.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionTerminatedFilter.class)
    public Result checkout(Context context)
    {
        // get cart by session id
        Cart cart = Cart.getCartById(SessionHandling.getCartId(context));
        // check, if the cart is empty
        if (cart.getProducts().size() == 0)
        {
            // show info message
            context.getFlashCookie().put("info", msg.get("infoEmptyBasket", language).get());
            // return cart overview page
            return Results.redirect(context.getContextPath() + "/basket");
        }
        // start checkout, if the cart is not empty
        else
        {
            // create new order
            Order order = Order.createNewOrder();
            // delete old order from session
            SessionHandling.removeOrderId(context);
            // put new order id to session
            SessionHandling.setOrderId(context, order.getId());
            // add the products from the cart to the order
            order.addProductsFromCart(cart);
            // update order
            order.update();
            // return page to enter shipping address
            return Results.redirect(context.getContextPath() + "/enterShippingAddress");
        }
    }

    /**
     * Returns the page to select or enter a shipping address.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionTerminatedFilter.class)
    public Result enterShippingAddress(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // customer is logged
        if (SessionHandling.isCustomerLogged(context))
        {
            // get customer
            Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
            // add all shipping addresses
            data.put("deliveryAddresses", customer.getShippingAddress());
            // add all billing addresses
            data.put("billingAddresses", customer.getBillingAddress());
        }
        // show checkout bread crumb
        data.put("checkout", true);
        // get shipping address by order
        ShippingAddress address = Order.getOrderById(SessionHandling.getOrderId(context)).getShippingAddress();
        // add address to data map, if an address was already entered
        if (address != null)
        {
            data.put("address", address);
        }
        return Results.html().render(data).template(xcpConf.TEMPLATE_SHIPPING_ADDRESS);
    }

    /**
     * Creates a new shipping address. Returns the page to enter payment information, if the billing address is equal to
     * the shipping address, otherwise the page to enter a billing address.
     * 
     * @param name
     * @param company
     * @param addressLine
     * @param city
     * @param state
     * @param zip
     * @param country
     * @param billingEqualDelivery
     * @param context
     * @return
     */
    @FilterWith(SessionTerminatedFilter.class)
    public Result shippingAddressCompleted(@Param("fullName") String name, @Param("company") String company,
                                           @Param("addressLine") String addressLine, @Param("city") String city,
                                           @Param("state") String state, @Param("zip") String zip,
                                           @Param("country") String country,
                                           @Param("billEqualShipp") String billingEqualDelivery, Context context)
    {
        // check input
        if (!Pattern.matches(xcpConf.REGEX_ZIP, zip))
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            WebShopController.setCommonData(data, context, xcpConf);
            // show error message
            context.getFlashCookie().error(msg.get("errorWrongZip", language).get());
            // show inserted values in form
            Map<String, String> address = new HashMap<String, String>();
            address.put("name", name);
            address.put("company", company);
            address.put("addressLine", addressLine);
            address.put("city", city);
            address.put("state", state);
            address.put("zip", zip);
            address.put("country", country);
            data.put("address", address);
            // show checkout bread crumb
            data.put("checkout", true);
            // show page to enter shipping address again
            return Results.html().render(data).template(xcpConf.TEMPLATE_SHIPPING_ADDRESS);
        }
        // all input fields might be correct
        else
        {
            // create shipping address
            ShippingAddress shippingAddress = new ShippingAddress();
            shippingAddress.setName(name);
            shippingAddress.setCompany(company);
            shippingAddress.setAddressLine(addressLine);
            shippingAddress.setCity(city);
            shippingAddress.setState(state);
            shippingAddress.setZip(zip);
            shippingAddress.setCountry(country);
            // set new address to customer
            if (SessionHandling.isCustomerLogged(context))
            {
                Customer.getCustomerById(SessionHandling.getCustomerId(context)).addShippingAddress(shippingAddress);
            }
            // save shipping address
            else
            {
                shippingAddress.save();
            }
            // get order by session id
            Order order = Order.getOrderById(SessionHandling.getOrderId(context));
            // set shipping address to order
            order.setShippingAddress(shippingAddress);
            // update order
            order.update();
            // billing address is equal to shipping address
            if (billingEqualDelivery.equals("Yes"))
            {
                // create billing address
                BillingAddress billingAddress = new BillingAddress();
                billingAddress.setName(name);
                billingAddress.setCompany(company);
                billingAddress.setAddressLine(addressLine);
                billingAddress.setCity(city);
                billingAddress.setState(state);
                billingAddress.setZip(zip);
                billingAddress.setCountry(country);
                // set new address to customer
                if (SessionHandling.isCustomerLogged(context))
                {
                    Customer.getCustomerById(SessionHandling.getCustomerId(context)).addBillingAddress(billingAddress);
                }
                // save billing address
                else
                {
                    billingAddress.save();
                }
                // set billing address to order
                order.setBillingAddress(billingAddress);
                // update order
                order.update();
                // return page to enter payment information
                return Results.redirect(context.getContextPath() + "/enterPaymentMethod");
            }
            // billing and shipping address are not equal
            else
            {
                // return page to enter billing address
                return Results.redirect(context.getContextPath() + "/enterBillingAddress");
            }
        }
    }

    /**
     * Adds the shipping address to the order. Returns the page to enter the billing address.
     * 
     * @param addressId
     * @param context
     * @return
     */
    @FilterWith(SessionTerminatedFilter.class)
    public Result addShippingAddressToOrder(@Param("addressId") String addressId, Context context)
    {
        // get shipping address
        ShippingAddress shippingAddress = ShippingAddress.getShippingAddressById(Integer.parseInt(addressId));
        // get order by session id
        Order order = Order.getOrderById(SessionHandling.getOrderId(context));
        // set shipping address to order
        order.setShippingAddress(shippingAddress);
        // update order
        order.update();
        // return page to enter billing address
        return Results.redirect(context.getContextPath() + "/enterBillingAddress");
    }

    /**
     * Returns the page to select or enter a billing address.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionTerminatedFilter.class)
    public Result enterBillingAddress(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        if (SessionHandling.isCustomerLogged(context))
        {
            Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
            // add all shipping addresses
            data.put("deliveryAddresses", customer.getShippingAddress());
            // add all billing addresses
            data.put("billingAddresses", customer.getBillingAddress());
        }
        // get billing address by order
        BillingAddress address = Order.getOrderById(SessionHandling.getOrderId(context)).getBillingAddress();
        // add address to data map, if an address was already entered
        if (address != null)
        {
            data.put("address", address);
        }
        data.put("checkout", true);
        data.put("billingAddressActive", true);
        // return page to enter billing address
        return Results.html().render(data).template(xcpConf.TEMPLATE_BILLING_ADDRESS);
    }

    /**
     * Creates a new billing address. Returns the page to enter payment information.
     * 
     * @param name
     * @param company
     * @param addressLine
     * @param city
     * @param state
     * @param zip
     * @param country
     * @param context
     * @return
     */
    @FilterWith(SessionTerminatedFilter.class)
    public Result billingAddressCompleted(@Param("fullName") String name, @Param("company") String company,
                                          @Param("addressLine") String addressLine, @Param("city") String city,
                                          @Param("state") String state, @Param("zip") String zip,
                                          @Param("country") String country, Context context)
    {
        // check input
        if (!Pattern.matches(xcpConf.REGEX_ZIP, zip))
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            WebShopController.setCommonData(data, context, xcpConf);
            // show error message
            context.getFlashCookie().error(msg.get("errorWrongZip", language).get());
            // show inserted values in form
            Map<String, String> address = new HashMap<String, String>();
            address.put("name", name);
            address.put("company", company);
            address.put("addressLine", addressLine);
            address.put("city", city);
            address.put("state", state);
            address.put("zip", zip);
            address.put("country", country);
            data.put("address", address);
            // show checkout bread crumb
            data.put("checkout", true);
            data.put("billingAddressActive", true);
            // show page to enter billing address again
            return Results.html().render(data).template(xcpConf.TEMPLATE_BILLING_ADDRESS);
        }
        // all input fields might be correct
        else
        {
            // create new billing address
            BillingAddress billingAddress = new BillingAddress();
            billingAddress.setName(name);
            billingAddress.setCompany(company);
            billingAddress.setAddressLine(addressLine);
            billingAddress.setCity(city);
            billingAddress.setState(state);
            billingAddress.setZip(zip);
            billingAddress.setCountry(country);
            // set new address to customer
            if (SessionHandling.isCustomerLogged(context))
            {
                Customer.getCustomerById(SessionHandling.getCustomerId(context)).addBillingAddress(billingAddress);
            }
            // save billing address
            else
            {
                billingAddress.save();
            }
            // get order by session id
            Order order = Order.getOrderById(SessionHandling.getOrderId(context));
            // set billing address to order
            order.setBillingAddress(billingAddress);
            // update order
            order.update();
            // return page to enter payment information
            return Results.redirect(context.getContextPath() + "/enterPaymentMethod");
        }
    }

    /**
     * Adds the billing address to the order. Returns the page to enter payment information.
     * 
     * @param addressId
     * @param context
     * @return
     */
    @FilterWith(SessionTerminatedFilter.class)
    public Result addBillingAddressToOrder(@Param("addressId") String addressId, Context context)
    {
        // get billing address
        BillingAddress billingAddress = BillingAddress.getBillingAddressById(Integer.parseInt(addressId));
        // get order by session id
        Order order = Order.getOrderById(SessionHandling.getOrderId(context));
        // set billing address to order
        order.setBillingAddress(billingAddress);
        // update order
        order.update();
        // return page to enter payment information
        return Results.redirect(context.getContextPath() + "/enterPaymentMethod");
    }

    /**
     * Returns the page to select or enter a payment method.
     * 
     * @param context
     * @return
     */
    @FilterWith(
        {
            SessionTerminatedFilter.class, SessionCustomerFilter.class
        })
    public Result enterPaymentMethod(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        if (SessionHandling.isCustomerLogged(context))
        {
            // get customer by session
            Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
            // add payment information
            data.put("paymentOverview", customer.getCreditCard());
        }
        // get payment method by order
        CreditCard card = Order.getOrderById(SessionHandling.getOrderId(context)).getCreditCard();
        // add payment method to data map, if a payment method was already entered
        if (card != null)
        {
            data.put("card", card);
        }
        data.put("checkout", true);
        data.put("billingAddressActive", true);
        data.put("creditCardActive", true);
        // return page to enter payment method
        return Results.html().render(data).template(xcpConf.TEMPLATE_PAYMENT_METHOD);
    }

    /**
     * Creates a new credit card. Returns the page to get an overview of the checkout.
     * 
     * @param creditNumber
     * @param name
     * @param month
     * @param year
     * @param context
     * @return
     */
    @FilterWith(SessionTerminatedFilter.class)
    public Result paymentMethodCompleted(@Param("creditCardNumber") String creditNumber, @Param("name") String name,
                                         @Param("expirationDateMonth") int month,
                                         @Param("expirationDateYear") int year, Context context)
    {
        // replace spaces and dashes
        creditNumber = creditNumber.replaceAll("[ -]+", "");
        // check each regular expression
        for (String regExCreditCard : xcpConf.REGEX_CREDITCARD)
        {
            // credit card number is correct
            if (Pattern.matches(regExCreditCard, creditNumber))
            {
                // create new credit card
                CreditCard creditCard = new CreditCard();
                creditCard.setCardNumber(creditNumber);
                creditCard.setName(name);
                creditCard.setMonth(month);
                creditCard.setYear(year);
                // set new credit card to customer
                if (SessionHandling.isCustomerLogged(context))
                {
                    Customer.getCustomerById(SessionHandling.getCustomerId(context)).addCreditCard(creditCard);
                }
                // save credit card
                else
                {
                    creditCard.save();
                }
                // get order by session id
                Order order = Order.getOrderById(SessionHandling.getOrderId(context));
                // set credit card to order
                order.setCreditCard(creditCard);
                // update order
                order.update();
                // return page to get an overview of the checkout
                return Results.redirect(context.getContextPath() + "/checkoutOverview");
            }
        }
        // credit card was wrong
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // show error message
        context.getFlashCookie().error(msg.get("errorWrongCreditCard", language).get());
        // show inserted values in form
        Map<String, String> card = new HashMap<String, String>();
        card.put("name", name);
        card.put("cardNumber", creditNumber);
        data.put("card", card);
        data.put("checkout", true);
        data.put("billingAddressActive", true);
        data.put("creditCardActive", true);
        // show page to enter payment method again
        return Results.html().render(data).template(xcpConf.TEMPLATE_PAYMENT_METHOD);
    }

    /**
     * Adds the credit card to the order. Returns the page to get an overview of the checkout.
     * 
     * @param creditCardId
     * @param context
     * @return
     */
    @FilterWith(SessionTerminatedFilter.class)
    public Result addPaymentToOrder(@Param("cardId") String creditCardId, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // get credit card by id
        CreditCard creditCard = CreditCard.getCreditCardById(Integer.parseInt(creditCardId));
        // get order by session id
        Order order = Order.getOrderById(SessionHandling.getOrderId(context));
        // set credit card to order
        order.setCreditCard(creditCard);
        // update order
        order.update();
        return Results.redirect(context.getContextPath() + "/checkoutOverview");
    }

    /**
     * Returns the overview page of the checkout.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionTerminatedFilter.class)
    public Result checkoutOverview(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        data.put("checkout", true);
        data.put("billingAddressActive", true);
        data.put("creditCardActive", true);
        data.put("placeOrderActive", true);
        // calculate shipping costs and tax
        calculateShippingAndTax(context);
        // get order by session id
        Order order = Order.getOrderById(SessionHandling.getOrderId(context));
        // add order to data map
        data.put("order", order);
        // add all products of the order
        data.put("orderProducts", order.getProducts());
        return Results.html().render(data);
    }

    /**
     * Removes the cart and the order from the session. Cleans the cart, which means to remove all products from the
     * cart. Returns the page, that the order was successful.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionTerminatedFilter.class)
    public Result checkoutCompleted(Context context)
    {
        // get order by session id
        Order order = Order.getOrderById(SessionHandling.getOrderId(context));
        // set date to order
        order.setOrderDate(DateUtils.getCurrentDate());
        if (SessionHandling.isCustomerLogged(context))
        {
            // get customer by session id
            Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
            // remove basket from customer
            customer.setCart(null);
            // set customer to order
            order.setCustomer(customer);
        }
        // update order
        order.update();
        // get cart by session id
        Cart cart = Cart.getCartById(SessionHandling.getCartId(context));
        // remove cart
        cart.delete();
        // remove cart from session
        SessionHandling.removeCartId(context);
        // remove order from session
        SessionHandling.removeOrderId(context);
        // show success message
        context.getFlashCookie().success(msg.get("checkoutCompleted", language).get());
        return Results.redirect(context.getContextPath() + "/");
    }

    /**
     * Adds the shipping costs and tax to the price of the order.
     * 
     * @param context
     */
    private void calculateShippingAndTax(Context context)
    {
        // get order by session id
        Order order = Order.getOrderById(SessionHandling.getOrderId(context));
        if ((order.getShippingCosts() == 0.0) && (order.getTax() == 0.0))
        {
            // set shipping costs to order
            order.setShippingCosts(xcpConf.SHIPPING_COSTS);
            // set tax to order
            order.setTax(xcpConf.TAX);
            // calculate total costs
            order.addShippingCostsToTotalCosts();
            order.addTaxToTotalCosts();
            // update order
            order.update();
        }
    }
}
