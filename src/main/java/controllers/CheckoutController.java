package controllers;

import java.util.HashMap;
import java.util.Map;

import models.Basket;
import models.BillingAddress;
import models.CreditCard;
import models.Customer;
import models.DeliveryAddress;
import models.Order;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import util.database.BasketInformation;
import util.database.CommonInformation;
import util.database.CreditCardInformation;
import util.database.CustomerInformation;
import util.database.OrderInformation;
import util.date.DateUtils;
import util.session.SessionHandling;

import com.avaje.ebean.Ebean;

public class CheckoutController
{

    /**
     * Starts the checkout. Returns an error page, if the basket is empty, otherwise the page to enter a delivery
     * address.
     * 
     * @param context
     * @return
     */
    public Result checkout(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();

        CommonInformation.setCommonData(data, context);

        String template = "";
        // get basket by session id
        Basket basket = BasketInformation.getBasketById(SessionHandling.getBasketId(context));
        // check, if the basket is empty
        if (basket.getProducts().size() == 0)
        {
            // return error page
            template = "views/error/emptyBasket.ftl.html";
        }
        // start checkout, if the basket is not empty
        else
        {
            // create new order
            Order order = OrderInformation.createNewOrder();
            // put order id to session
            SessionHandling.setOrderId(context, order.getId());
            // set basket to order
            order.addProductsFromBasket(basket);
            // return page to enter delivery address
            template = "views/CheckoutController/deliveryAddress.ftl.html";
            // customer is logged
            if (SessionHandling.isCustomerLogged(context))
            {
                // get customer by session id
                Customer customer = CustomerInformation.getCustomerById(SessionHandling.getCustomerId(context));
                // set customer to order
                order.setCustomer(customer);
                // put address information of customer to data map
                CustomerInformation.addAddressOfCustomerToMap(context, data);
            }
            // save order
            Ebean.save(order);
        }
        return Results.html().render(data).template(template);
    }

    /**
     * Creates a new delivery address. Returns the page to enter payment information, if the billing address is equal to
     * the delivery address, otherwise the page to enter a billing address.
     * 
     * @param name
     * @param addressLine1
     * @param addressLine2
     * @param city
     * @param state
     * @param zip
     * @param country
     * @param billingEqualDelivery
     * @param context
     * @return
     */
    public Result deliveryAddressCompleted(@Param("fullName") String name, @Param("addressLine1") String addressLine1,
                                           @Param("addressLine2") String addressLine2, @Param("city") String city,
                                           @Param("state") String state, @Param("zip") String zip,
                                           @Param("country") String country,
                                           @Param("billingAddress") String billingEqualDelivery, Context context)
    {

        final Map<String, Object> data = new HashMap<String, Object>();

        CommonInformation.setCommonData(data, context);

        // create delivery address
        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.setName(name);
        deliveryAddress.setAddressline1(addressLine1);
        deliveryAddress.setAddressline2(addressLine2);
        deliveryAddress.setCity(city);
        deliveryAddress.setState(state);
        deliveryAddress.setZip(Integer.parseInt(zip));
        deliveryAddress.setCountry(country);
        // get order by session id
        Order order = OrderInformation.getOrderById(SessionHandling.getOrderId(context));
        // set delivery address to order
        order.setDeliveryAddress(deliveryAddress);

        String template;

        // billing address is equal to delivery address
        if (billingEqualDelivery.equals("Yes"))
        {
            // create billing address
            BillingAddress billingAddress = new BillingAddress();
            billingAddress.setName(name);
            billingAddress.setAddressline1(addressLine1);
            billingAddress.setAddressline2(addressLine2);
            billingAddress.setCity(city);
            billingAddress.setState(state);
            billingAddress.setZip(Integer.parseInt(zip));
            billingAddress.setCountry(country);
            // set billing address to order
            order.setBillingAddress(billingAddress);
            // add payment information to map
            CustomerInformation.addPaymentOfCustomerToMap(context, data);
            // return page to enter payment information
            template = "views/CheckoutController/paymentMethod.ftl.html";
        }
        // billing and delivery address are not equal
        else
        {
            // return page to enter billing address
            template = "views/CheckoutController/billingAddress.ftl.html";
        }
        // update order
        order.update();
        return Results.html().render(data).template(template);
    }

    /**
     * Creates a new billing address. Returns the page to enter payment information.
     * 
     * @param name
     * @param addressLine1
     * @param addressLine2
     * @param city
     * @param state
     * @param zip
     * @param country
     * @param context
     * @return
     */
    public Result billingAddressCompleted(@Param("fullName") String name, @Param("addressLine1") String addressLine1,
                                          @Param("addressLine2") String addressLine2, @Param("city") String city,
                                          @Param("state") String state, @Param("zip") String zip,
                                          @Param("country") String country, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();

        CommonInformation.setCommonData(data, context);

        // create new billing address
        BillingAddress billingAddress = new BillingAddress();
        billingAddress.setName(name);
        billingAddress.setAddressline1(addressLine1);
        billingAddress.setAddressline2(addressLine2);
        billingAddress.setCity(city);
        billingAddress.setState(state);
        billingAddress.setZip(Integer.parseInt(zip));
        billingAddress.setCountry(country);
        // get order by session id
        Order order = OrderInformation.getOrderById(SessionHandling.getOrderId(context));
        // set billing address to order
        order.setBillingAddress(billingAddress);
        // return page to enter payment information
        String template = "views/CheckoutController/paymentMethod.ftl.html";
        // update order
        Ebean.save(order);
        return Results.html().render(data).template(template);
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
    public Result paymentMethodCompleted(@Param("creditCardNumber") int creditNumber, @Param("name") String name,
                                         @Param("expirationDateMonth") int month,
                                         @Param("expirationDateYear") int year, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();

        CommonInformation.setCommonData(data, context);

        // create new credit card
        CreditCard creditCard = new CreditCard();
        creditCard.setNumber(creditNumber);
        creditCard.setName(name);
        creditCard.setMonth(month);
        creditCard.setYear(year);
        // get order by session id
        Order order = OrderInformation.getOrderById(SessionHandling.getOrderId(context));
        // set credit card to order
        order.setCreditCard(creditCard);
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
        // set shipping costs to order
        order.setShippingCosts(6.99);
        // set tax to order
        order.setTax(0.06);
        // calculate total costs
        order.addShippingCostsToTotalCosts();
        order.addTaxToTotalCosts();
        // add order to data map
        OrderInformation.addOrderToMap(order, data);
        OrderInformation.addProductsFromOrderToMap(order, data);
        // return page to get an overview of the checkout
        String template = "views/CheckoutController/checkoutOverview.ftl.html";
        // update order
        Ebean.save(order);
        return Results.html().render(data).template(template);
    }

    /**
     * Adds the credit card to the order. Returns the page to get an overview of the checkout.
     * 
     * @param creditCardId
     * @param context
     * @return
     */
    public Result addPaymentToOrder(@Param("cardId") String creditCardId, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();

        CommonInformation.setCommonData(data, context);
        // get credit card by id
        CreditCard creditCard = CreditCardInformation.getCreditCardById(Integer.parseInt(creditCardId));
        // get order by session id
        Order order = OrderInformation.getOrderById(SessionHandling.getOrderId(context));
        // set credit card to order
        order.setCreditCard(creditCard);
        // set shipping costs to order
        order.setShippingCosts(6.99);
        // set tax to order
        order.setTax(0.06);
        // calculate total costs
        order.addShippingCostsToTotalCosts();
        order.addTaxToTotalCosts();
        // add order to data map
        OrderInformation.addOrderToMap(order, data);
        OrderInformation.addProductsFromOrderToMap(order, data);
        // return page to get an overview of the checkout
        String template = "views/CheckoutController/checkoutOverview.ftl.html";
        // update order
        Ebean.save(order);
        return Results.html().render(data).template(template);
    }

    /**
     * Removes the basket and the order from the session. Cleans the basket, which means to delete all products in the
     * basket. Returns the page, that the order was successful.
     * 
     * @param context
     * @return
     */
    public Result checkoutCompleted(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        // get order by session id
        Order order = OrderInformation.getOrderById(SessionHandling.getOrderId(context));
        // set date to order
        order.setDate(DateUtils.getCurrentDate());
        // update order
        Ebean.save(order);
        // get basket by session id
        Basket basket = BasketInformation.getBasketById(SessionHandling.getBasketId(context));
        // remove basket
        BasketInformation.removeBasket(basket);
        // remove order, if no customer is set
        OrderInformation.removeOrder(order);
        // remove basket from session
        SessionHandling.deleteBasketId(context);
        // remove order from session
        SessionHandling.deleteOrderId(context);

        CommonInformation.setCommonData(data, context);

        return Results.html().render(data);
    }
}
