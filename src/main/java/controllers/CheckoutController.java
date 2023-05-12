package controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import com.google.inject.Inject;

import conf.PosterConstants;
import filters.SessionCustomerExistFilter;
import filters.SessionOrderExistFilter;
import filters.SessionTerminatedFilter;
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

    private final Optional<String> language = Optional.of("en");

    /**
     * Starts the checkout. Returns an error page, if the cart is empty, otherwise the page to enter a shipping address.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionTerminatedFilter.class)
    public Result checkout(final Context context)
    {
        // get cart by session id
        final Cart cart = Cart.getCartById(SessionHandling.getCartId(context, xcpConf));
        // check, if the cart is empty
        if (cart.getProducts().size() == 0)
        {
            // show info message
            context.getFlashScope().put("info", msg.get("infoEmptyCart", language).get());
            // return cart overview page
            return Results.redirect(context.getContextPath() + "/cart");
        }
        // start checkout, if the cart is not empty
        else
        {
            // create new order
            final Order order = Order.createNewOrder(xcpConf.TAX, xcpConf.SHIPPING_COSTS);
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
    @FilterWith(
        {
            SessionTerminatedFilter.class, SessionCustomerExistFilter.class, SessionOrderExistFilter.class
        })
    public Result enterShippingAddress(final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);

        boolean userHasAShippingAddress = false;

        // customer is logged
        if (SessionHandling.isCustomerLogged(context))
        {
            // get customer
            final Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));

            // user has a shipping address
            if (!customer.getShippingAddress().isEmpty())
            {
                // add all shipping addresses
                data.put("shippingAddresses", customer.getShippingAddress());
                userHasAShippingAddress = true;
            }
            // add all billing addresses
            data.put("billingAddresses", customer.getBillingAddress());
        }
        // show checkout bread crumb
        data.put("checkout", true);
        data.put("shippingAddressesActive", true);
/*
        // get shipping address by order
        final ShippingAddress address = Order.getOrderById(SessionHandling.getOrderId(context)).getShippingAddress();
        // add address to data map, if an address was already entered
        if (address != null)
        {
            //data.put("address", address);
        }
*/
        // customer not sign in or user has no address
        if (SessionHandling.isCustomerLogged(context))
        {
            if (userHasAShippingAddress == true)
            {
                return Results.html().render(data).template(xcpConf.TEMPLATE_SHIPPING_ADDRESS);
            }
            else
            {
                return Results.html().render(data).template(xcpConf.TEMPLATE_SHIPPING_ADDRESS_GUEST);
            }
        }
        else
        {
            return Results.html().render(data).template(xcpConf.TEMPLATE_SHIPPING_ADDRESS_GUEST);
        }
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
     * @param billingEqualShipping
     * @param context
     * @return
     */
    @FilterWith(
        {
            SessionTerminatedFilter.class, SessionCustomerExistFilter.class, SessionOrderExistFilter.class
        })
    public Result shippingAddressCompleted(@Param("fullName") final String name, @Param("company") final String company,
                                           @Param("addressLine") final String addressLine, @Param("city") final String city,
                                           @Param("state") final String state, @Param("zip") final String zip,
                                           @Param("country") final String country,
                                           @Param("billEqualShipp") final String billingEqualShipping, final Context context)
    {
        // check input
        if (!Pattern.matches(xcpConf.REGEX_ZIP, zip))
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            WebShopController.setCommonData(data, context, xcpConf);
            // show error message
            context.getFlashScope().error(msg.get("errorWrongZip", language).get());
            // show inserted values in form
            final Map<String, String> address = new HashMap<String, String>();
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
            data.put("shippingAddressesActive", true);
            // show page to enter shipping address again
            return Results.html().render(data).template(xcpConf.TEMPLATE_SHIPPING_ADDRESS);
        }
        // all input fields might be correct
        else
        {
            // create shipping address
            final ShippingAddress shippingAddress = new ShippingAddress();
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
            final Order order = Order.getOrderById(SessionHandling.getOrderId(context));
            // set shipping address to order
            order.setShippingAddress(shippingAddress);
            // update order
            order.update();
            // billing address is equal to shipping address
            if (billingEqualShipping.equals("Yes"))
            {
                // create billing address
                final BillingAddress billingAddress = new BillingAddress();
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
    @FilterWith(
        {
            SessionTerminatedFilter.class, SessionOrderExistFilter.class
        })
    public Result addShippingAddressToOrder(@Param("addressId") final String addressId, final Context context)
    {
        // get shipping address
        final ShippingAddress shippingAddress = ShippingAddress.getShippingAddressById(Integer.parseInt(addressId));
        // get order by session id
        final Order order = Order.getOrderById(SessionHandling.getOrderId(context));
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
    @FilterWith(
        {
            SessionTerminatedFilter.class, SessionCustomerExistFilter.class, SessionOrderExistFilter.class
        })
    public Result enterBillingAddress(final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);

        boolean userHasBillingAddress = false;

        if (SessionHandling.isCustomerLogged(context))
        {
            final Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));

            // user has a shipping address
            if (!customer.getBillingAddress().isEmpty())
            {
                // add all shipping addresses
                data.put("shippingAddresses", customer.getShippingAddress());
                // add all billing addresses
                data.put("billingAddresses", customer.getBillingAddress());

                userHasBillingAddress = true;
            }
        }
/*        // get billing address by order
        final BillingAddress address = Order.getOrderById(SessionHandling.getOrderId(context)).getBillingAddress();
        // add address to data map, if an address was already entered
        if (address != null)
        {
            data.put("address", address);
        }*/
        data.put("checkout", true);
        data.put("billingAddressActive", true);

        // customer not sign in
        if (SessionHandling.isCustomerLogged(context))
        {
            if (userHasBillingAddress == true)
            {
                return Results.html().render(data).template(xcpConf.TEMPLATE_BILLING_ADDRESS);
            }
            else
            {
                return Results.html().render(data).template(xcpConf.TEMPLATE_BILLING_ADDRESS_GUEST);
            }
        }
        else
        {
            return Results.html().render(data).template(xcpConf.TEMPLATE_BILLING_ADDRESS_GUEST);
        }
        // return page to enter billing address
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
    @FilterWith(
        {
            SessionTerminatedFilter.class, SessionCustomerExistFilter.class, SessionOrderExistFilter.class
        })
    public Result billingAddressCompleted(@Param("fullName") final String name, @Param("company") final String company,
                                          @Param("addressLine") final String addressLine, @Param("city") final String city,
                                          @Param("state") final String state, @Param("zip") final String zip,
                                          @Param("country") final String country, final Context context)
    {
        // check input
        if (!Pattern.matches(xcpConf.REGEX_ZIP, zip))
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            WebShopController.setCommonData(data, context, xcpConf);
            // show error message
            context.getFlashScope().error(msg.get("errorWrongZip", language).get());
            // show inserted values in form
            final Map<String, String> address = new HashMap<String, String>();
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
            final BillingAddress billingAddress = new BillingAddress();
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
            final Order order = Order.getOrderById(SessionHandling.getOrderId(context));
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
    @FilterWith(
        {
            SessionTerminatedFilter.class, SessionOrderExistFilter.class
        })
    public Result addBillingAddressToOrder(@Param("addressId") final String addressId, final Context context)
    {
        // get billing address
        final BillingAddress billingAddress = BillingAddress.getBillingAddressById(Integer.parseInt(addressId));
        // get order by session id
        final Order order = Order.getOrderById(SessionHandling.getOrderId(context));
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
            SessionTerminatedFilter.class, SessionCustomerExistFilter.class, SessionOrderExistFilter.class
        })
    public Result enterPaymentMethod(final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        
        boolean userHasPaymentMethod = false;
        
        if (SessionHandling.isCustomerLogged(context))
        {
            // get customer by session
            final Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
            if (!customer.getCreditCard().isEmpty())
            {
                // add payment information
                data.put("paymentOverview", customer.getCreditCard());
                userHasPaymentMethod = true;
            }
        }
        // get payment method by order
        final CreditCard card = Order.getOrderById(SessionHandling.getOrderId(context)).getCreditCard();

        DateFormat dateFormatYear = new SimpleDateFormat("yyyy");
        DateFormat dateFormatMonth = new SimpleDateFormat("MM");
        Date date = new Date();

        // add payment method to data map, if a payment method was already entered
        if (card != null)
        {
            //data.put("card", card);
        }
        
        // get current month and year
        data.put("currentYear", Integer.valueOf(dateFormatYear.format(date)));
        data.put("currentMonth", Integer.valueOf(dateFormatMonth.format(date)));
        
        data.put("expirationDateStartYear", Integer.valueOf(dateFormatYear.format(date)));

        data.put("checkout", true);
        data.put("billingAddressActive", true);
        data.put("creditCardActive", true);
        // System.out.println(data.toString());

        // customer not sign in
        if (SessionHandling.isCustomerLogged(context))
        {
            // return page to enter payment method
            if (userHasPaymentMethod == true)
            {
                return Results.html().render(data).template(xcpConf.TEMPLATE_PAYMENT_METHOD);
            }
            else
            {
                return Results.html().render(data).template(xcpConf.TEMPLATE_PAYMENT_METHOD_GUEST);
            }

        }
        else
        {
            // return page to enter payment method
            return Results.html().render(data).template(xcpConf.TEMPLATE_PAYMENT_METHOD_GUEST);
        }
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
    @FilterWith(
        {
            SessionTerminatedFilter.class, SessionCustomerExistFilter.class, SessionOrderExistFilter.class
        })
    public Result paymentMethodCompleted(@Param("creditCardNumber") String creditNumber, @Param("name") final String name,
                                         @Param("expirationDateMonth") final int month, @Param("expirationDateYear") final int year,
                                         final Context context)
    {
        // replace spaces and dashes
        creditNumber = creditNumber.replaceAll("[ -]+", "");
        // check each regular expression
        for (final String regExCreditCard : xcpConf.REGEX_CREDITCARD)
        {
            // credit card number is correct
            if (Pattern.matches(regExCreditCard, creditNumber))
            {
                // create new credit card
                final CreditCard creditCard = new CreditCard();
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
                final Order order = Order.getOrderById(SessionHandling.getOrderId(context));
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
        context.getFlashScope().error(msg.get("errorWrongCreditCard", language).get());
        // show inserted values in form
        final Map<String, String> card = new HashMap<String, String>();
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
    @FilterWith(
        {
            SessionTerminatedFilter.class, SessionCustomerExistFilter.class, SessionOrderExistFilter.class
        })
    public Result addPaymentToOrder(@Param("cardId") final String creditCardId, final Context context)
    {
        // get credit card by id
        final CreditCard creditCard = CreditCard.getCreditCardById(Integer.parseInt(creditCardId));
        // get order by session id
        final Order order = Order.getOrderById(SessionHandling.getOrderId(context));
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
    @FilterWith(
        {
            SessionTerminatedFilter.class, SessionCustomerExistFilter.class, SessionOrderExistFilter.class
        })
    public Result checkoutOverview(final Context context)
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
        final Order order = Order.getOrderById(SessionHandling.getOrderId(context));
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
    @FilterWith(
        {
            SessionTerminatedFilter.class, SessionCustomerExistFilter.class, SessionOrderExistFilter.class
        })
    public Result checkoutCompleted(final Context context)
    {
        // get order by session id
        final Order order = Order.getOrderById(SessionHandling.getOrderId(context));
        // set date to order
        order.setOrderDate(DateUtils.getCurrentDate());
        if (SessionHandling.isCustomerLogged(context))
        {
            // get customer by session id
            final Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
            // remove cart from customer
            customer.setCart(null);
            // set customer to order
            order.setCustomer(customer);
        }
        // update order
        order.update();
        // get cart by session id
        final Cart cart = Cart.getCartById(SessionHandling.getCartId(context, xcpConf));
        // remove cart
        cart.delete();
        // remove cart from session
        SessionHandling.removeCartId(context);
        // remove order from session
        SessionHandling.removeOrderId(context);
        // show success message
        context.getFlashScope().success(msg.get("checkoutCompleted", language).get());
        return Results.redirect(context.getContextPath() + "/orderConfirmation");
    }

    /**
     * Shows the confirmation page of an order
     *
     * @param context
     */
    public Result orderConfirmation(final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }

    /**
     * Adds the shipping costs and tax to the price of the order.
     * 
     * @param context
     */
    private void calculateShippingAndTax(final Context context)
    {
        // get order by session id
        final Order order = Order.getOrderById(SessionHandling.getOrderId(context));
        if ((order.getShippingCosts() == 0.0) && (order.getTax() == 0.0))
        {
            // set shipping costs to order
            order.setShippingCosts(xcpConf.SHIPPING_COSTS);
            // set tax to order
            order.setTax(xcpConf.TAX);
            order.setTotalTaxCosts(order.getSubTotalCosts() * order.getTax());

            // recalculate total costs
            order.setTotalCosts(order.getSubTotalCosts() + order.getTotalTaxCosts() + order.getShippingCosts());

            // update order
            order.update();
        }
    }
}
