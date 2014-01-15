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
import util.database.CommonInformation;
import util.database.CreditCardInformation;
import util.database.CustomerInformation;
import util.database.OrderInformation;
import util.date.DateUtils;
import util.session.SessionHandling;

import com.google.common.base.Optional;
import com.google.inject.Inject;

import conf.XCPosterConf;
import filters.SessionTerminatedFilter;

public class CheckoutController
{
    @Inject
    Messages msg;

    @Inject
    XCPosterConf xcpConf;

    private Optional language = Optional.of("en");

    /**
     * Starts the checkout. Returns an error page, if the basket is empty, otherwise the page to enter a delivery
     * address.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionTerminatedFilter.class)
    public Result checkout(Context context)
    {
        // get basket by session id
        Cart basket = Cart.getCartById(SessionHandling.getBasketId(context));
        // check, if the basket is empty
        if (basket.getProducts().size() == 0)
        {
            // show info
            context.getFlashCookie().put("info", msg.get("infoEmptyBasket", language).get());
            // return basket overview page
            return Results.redirect(context.getContextPath() + "/basket");
        }
        // start checkout, if the basket is not empty
        else
        {
            // create new order
            Order order = OrderInformation.createNewOrder();
            // delete old order from session
            SessionHandling.deleteOrderId(context);
            // put new order id to session
            SessionHandling.setOrderId(context, order.getId());
            // set basket to order
            order.addProductsFromCart(basket);
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
        CommonInformation.setCommonData(data, context, xcpConf);
        // customer is logged
        if (SessionHandling.isCustomerLogged(context))
        {
            // put address information of customer to data map
            CustomerInformation.addAddressOfCustomerToMap(context, data);
        }
        // show checkout bread crumb
        data.put("checkout", true);
        // get shipping address by order
        ShippingAddress address = OrderInformation.getOrderById(SessionHandling.getOrderId(context))
                                                  .getShippingAddress();
        // add address to data map, if an address was already entered
        if (address != null)
        {
            data.put("address", address);
        }
        return Results.html().render(data).template(xcpConf.templateDeliveryAddress);
    }

    /**
     * Creates a new delivery address. Returns the page to enter payment information, if the billing address is equal to
     * the delivery address, otherwise the page to enter a billing address.
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
    public Result deliveryAddressCompleted(@Param("fullName") String name, @Param("company") String company,
                                           @Param("addressLine") String addressLine, @Param("city") String city,
                                           @Param("state") String state, @Param("zip") String zip,
                                           @Param("country") String country,
                                           @Param("billEqualShipp") String billingEqualDelivery, Context context)
    {
        // check input
        if (!Pattern.matches(xcpConf.regexZip, zip))
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            CommonInformation.setCommonData(data, context, xcpConf);
            // error message
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
            // show page to enter delivery address again
            return Results.html().render(data).template(xcpConf.templateDeliveryAddress);
        }
        // all input fields might be correct
        else
        {
            // create delivery address
            ShippingAddress deliveryAddress = new ShippingAddress();
            deliveryAddress.setName(name);
            deliveryAddress.setCompany(company);
            deliveryAddress.setAddressLine(addressLine);
            deliveryAddress.setCity(city);
            deliveryAddress.setState(state);
            deliveryAddress.setZip(zip);
            deliveryAddress.setCountry(country);
            // set new address to customer
            if (SessionHandling.isCustomerLogged(context))
            {
                CustomerInformation.addDeliveryAddressToCustomer(context, deliveryAddress);
            }
            // save delivery address
            else
            {
                deliveryAddress.save();
            }
            // get order by session id
            Order order = OrderInformation.getOrderById(SessionHandling.getOrderId(context));
            // set delivery address to order
            order.setShippingAddress(deliveryAddress);
            // update order
            order.update();
            // billing address is equal to delivery address
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
                    CustomerInformation.addBillingAddressToCustomer(context, billingAddress);
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
            // billing and delivery address are not equal
            else
            {
                // return page to enter billing address
                return Results.redirect(context.getContextPath() + "/enterBillingAddress");
            }
        }
    }

    /**
     * Adds the delivery address to the order. Returns the page to enter the billing address.
     * 
     * @param addressId
     * @param context
     * @return
     */
    @FilterWith(SessionTerminatedFilter.class)
    public Result addDeliveryAddressToOrder(@Param("addressId") String addressId, Context context)
    {
        // get delivery address
        ShippingAddress deliveryAddress = ShippingAddress.getShippingAddressById(Integer.parseInt(addressId));
        // get order by session id
        Order order = OrderInformation.getOrderById(SessionHandling.getOrderId(context));
        // set delivery address to order
        order.setShippingAddress(deliveryAddress);
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
        CommonInformation.setCommonData(data, context, xcpConf);
        if (SessionHandling.isCustomerLogged(context))
        {
            // put address information of customer to data map
            CustomerInformation.addAddressOfCustomerToMap(context, data);
        }
        // get billing address by order
        BillingAddress address = OrderInformation.getOrderById(SessionHandling.getOrderId(context)).getBillingAddress();
        // add address to data map, if an address was already entered
        if (address != null)
        {
            data.put("address", address);
        }
        data.put("checkout", true);
        data.put("billingAddressActive", true);
        // return page to enter billing address
        return Results.html().render(data).template(xcpConf.templateBillingAddress);
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
        if (!Pattern.matches(xcpConf.regexZip, zip))
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            CommonInformation.setCommonData(data, context, xcpConf);
            // error message
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
            return Results.html().render(data).template(xcpConf.templateBillingAddress);
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
                CustomerInformation.addBillingAddressToCustomer(context, billingAddress);
            }
            // save billing address
            else
            {
                billingAddress.save();
            }
            // get order by session id
            Order order = OrderInformation.getOrderById(SessionHandling.getOrderId(context));
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
        Order order = OrderInformation.getOrderById(SessionHandling.getOrderId(context));
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
    @FilterWith(SessionTerminatedFilter.class)
    public Result enterPaymentMethod(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        CommonInformation.setCommonData(data, context, xcpConf);
        if (SessionHandling.isCustomerLogged(context))
        {
            // add payment method to map
            CustomerInformation.addPaymentOfCustomerToMap(context, data);
        }
        // get payment method by order
        CreditCard card = OrderInformation.getOrderById(SessionHandling.getOrderId(context)).getCreditCard();
        // add payment method to data map, if a payment method was already entered
        if (card != null)
        {
            data.put("card", card);
        }
        data.put("checkout", true);
        data.put("billingAddressActive", true);
        data.put("creditCardActive", true);
        // return page to enter payment method
        return Results.html().render(data).template(xcpConf.templatePaymentMethod);
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
        for (String regExCreditCard : xcpConf.regexCreditCard)
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
                    CustomerInformation.addPaymentToCustomer(context, creditCard);
                }
                // save credit card
                else
                {
                    creditCard.save();
                }
                // get order by session id
                Order order = OrderInformation.getOrderById(SessionHandling.getOrderId(context));
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
        CommonInformation.setCommonData(data, context, xcpConf);
        // error message
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
        return Results.html().render(data).template(xcpConf.templatePaymentMethod);
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
        CommonInformation.setCommonData(data, context, xcpConf);
        // get credit card by id
        CreditCard creditCard = CreditCardInformation.getCreditCardById(Integer.parseInt(creditCardId));
        // get order by session id
        Order order = OrderInformation.getOrderById(SessionHandling.getOrderId(context));
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
        CommonInformation.setCommonData(data, context, xcpConf);
        data.put("checkout", true);
        data.put("billingAddressActive", true);
        data.put("creditCardActive", true);
        data.put("placeOrderActive", true);
        // calculate shipping costs and tax
        calculateShippingAndTax(context);
        // get order by session id
        Order order = OrderInformation.getOrderById(SessionHandling.getOrderId(context));
        // add order to data map
        OrderInformation.addOrderToMap(order, data);
        OrderInformation.addProductsFromOrderToMap(order, data);
        return Results.html().render(data).template(xcpConf.templateCheckoutOverview);
    }

    /**
     * Removes the basket and the order from the session. Cleans the basket, which means to delete all products in the
     * basket. Returns the page, that the order was successful.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionTerminatedFilter.class)
    public Result checkoutCompleted(Context context)
    {
        // get order by session id
        Order order = OrderInformation.getOrderById(SessionHandling.getOrderId(context));
        // set date to order
        order.setOrderDate(DateUtils.getCurrentDate());
        if (SessionHandling.isCustomerLogged(context))
        {
            // get customer by session id
            Customer customer = CustomerInformation.getCustomerById(SessionHandling.getCustomerId(context));
            // remove basket from customer
            customer.setCart(null);
            // set customer to order
            order.setCustomer(customer);
        }
        // update order
        order.update();
        // get basket by session id
        Cart basket = Cart.getCartById(SessionHandling.getBasketId(context));
        // remove basket
        basket.delete();
        // remove basket from session
        SessionHandling.deleteBasketId(context);
        // remove order from session
        SessionHandling.deleteOrderId(context);
        // show success message
        context.getFlashCookie().success(msg.get("checkoutCompleted", language).get());
        return Results.redirect(context.getContextPath() + "/");
    }

    private void calculateShippingAndTax(Context context)
    {
        // get order by session id
        Order order = OrderInformation.getOrderById(SessionHandling.getOrderId(context));
        if ((order.getShippingCosts() == 0.0) && (order.getTax() == 0.0))
        {
            // set shipping costs to order
            order.setShippingCosts(xcpConf.shippingCosts);
            // set tax to order
            order.setTax(xcpConf.tax);
            // calculate total costs
            order.addShippingCostsToTotalCosts();
            order.addTaxToTotalCosts();
            // update order
            order.update();
        }
    }
}
