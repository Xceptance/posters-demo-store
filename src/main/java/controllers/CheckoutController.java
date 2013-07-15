package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
import util.database.AddressInformation;
import util.database.BasketInformation;
import util.database.CarouselInformation;
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
        String template;

        // get basket by session id
        Basket basket = BasketInformation.getBasketById(SessionHandling.getBasketId(context));
        // check, if the basket is empty
        if (basket.getProducts().size() == 0)
        {
            // show info
            data.put("infoMessage", "The basket is empty! Browse for some nice posters and put them in the basket.");
            template = "views/BasketController/basketOverview.ftl.html";
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
            order.addProductsFromBasket(basket);
            // return page to enter delivery address
            template = "views/CheckoutController/deliveryAddress.ftl.html";
            // customer is logged
            if (SessionHandling.isCustomerLogged(context))
            {
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
        String template;

        // check input
        if (!Pattern.matches("[0-9]*", zip))
        {
            // error message
            data.put("errorMessage", "Wrong ZIP, please type again.");
            // show inserted values in form
            Map<String, String> address = new HashMap<String, String>();
            address.put("name", name);
            address.put("addressline1", addressLine1);
            address.put("addressline2", addressLine2);
            address.put("city", city);
            address.put("state", state);
            address.put("zip", zip);
            address.put("country", country);
            data.put("address", address);
            // show page to enter delivery address again
            template = "views/CheckoutController/deliveryAddress.ftl.html";
        }
        // all input fields might be correct
        else
        {
            // create delivery address
            DeliveryAddress deliveryAddress = new DeliveryAddress();
            deliveryAddress.setName(name);
            deliveryAddress.setAddressline1(addressLine1);
            deliveryAddress.setAddressline2(addressLine2);
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
            order.setDeliveryAddress(deliveryAddress);

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
                // add payment information to map
                CustomerInformation.addPaymentOfCustomerToMap(context, data);
                // return page to enter payment information
                template = "views/CheckoutController/paymentMethod.ftl.html";
            }
            // billing and delivery address are not equal
            else
            {
                // put address information of customer to data map
                CustomerInformation.addAddressOfCustomerToMap(context, data);
                // return page to enter billing address
                template = "views/CheckoutController/billingAddress.ftl.html";
            }
            // update order
            order.update();
        }
        return Results.html().render(data).template(template);
    }

    /**
     * Adds the delivery address to the order. Returns the page to enter the billing address.
     * 
     * @param addressId
     * @param context
     * @return
     */
    public Result addDeliveryAddressToOrder(@Param("addressId") String addressId, Context context)
    {

        final Map<String, Object> data = new HashMap<String, Object>();
        CommonInformation.setCommonData(data, context);
        String template;

        // get delivery address
        DeliveryAddress deliveryAddress = AddressInformation.getDeliveryAddressById(Integer.parseInt(addressId));
        // get order by session id
        Order order = OrderInformation.getOrderById(SessionHandling.getOrderId(context));
        // set delivery address to order
        order.setDeliveryAddress(deliveryAddress);
        template = "views/CheckoutController/billingAddress.ftl.html";
        // put address information of customer to data map
        CustomerInformation.addAddressOfCustomerToMap(context, data);
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
        String template;

        // check input
        if (!Pattern.matches("[0-9]*", zip))
        {
            // error message
            data.put("errorMessage", "Wrong ZIP, please type again.");
            // show inserted values in form
            Map<String, String> address = new HashMap<String, String>();
            address.put("name", name);
            address.put("addressline1", addressLine1);
            address.put("addressline2", addressLine2);
            address.put("city", city);
            address.put("state", state);
            address.put("zip", zip);
            address.put("country", country);
            data.put("address", address);
            // show page to enter billing address again
            template = "views/CheckoutController/billingAddress.ftl.html";
        }
        // all input fields might be correct
        else
        {
            // create new billing address
            BillingAddress billingAddress = new BillingAddress();
            billingAddress.setName(name);
            billingAddress.setAddressline1(addressLine1);
            billingAddress.setAddressline2(addressLine2);
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
            // return page to enter payment information
            template = "views/CheckoutController/paymentMethod.ftl.html";
            // add payment information to map
            CustomerInformation.addPaymentOfCustomerToMap(context, data);
            // update order
            Ebean.save(order);
        }
        return Results.html().render(data).template(template);
    }

    /**
     * Adds the billing address to the order. Returns the page to enter payment information.
     * 
     * @param addressId
     * @param context
     * @return
     */
    public Result addBillingAddressToOrder(@Param("addressId") String addressId, Context context)
    {

        final Map<String, Object> data = new HashMap<String, Object>();
        CommonInformation.setCommonData(data, context);
        String template;

        // get billing address
        BillingAddress billingAddress = AddressInformation.getBillingAddressById(Integer.parseInt(addressId));
        // get order by session id
        Order order = OrderInformation.getOrderById(SessionHandling.getOrderId(context));
        // set billing address to order
        order.setBillingAddress(billingAddress);
        template = "views/CheckoutController/paymentMethod.ftl.html";
        // add payment information to map
        CustomerInformation.addPaymentOfCustomerToMap(context, data);
        // update order
        order.update();
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
    public Result paymentMethodCompleted(@Param("creditCardNumber") String creditNumber, @Param("name") String name,
                                         @Param("expirationDateMonth") int month,
                                         @Param("expirationDateYear") int year, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        CommonInformation.setCommonData(data, context);
        String template;
        // replace spaces and dashes
        creditNumber = creditNumber.replaceAll("[ -]+", "");
        // check input
        if (!Pattern.matches("4[0-9]{12}(?:[0-9]{3})?", creditNumber))
        {
            // error message
            data.put("errorMessage", "Wrong credit card number, please type again.");
            // show inserted values in form
            Map<String, String> card = new HashMap<String, String>();
            card.put("name", name);
            card.put("cardNumber", creditNumber);
            data.put("card", card);
            // show page to enter delivery address again
            template = "views/CheckoutController/paymentMethod.ftl.html";
        }
        // all input fields might be correct
        else
        {
            // create new credit card
            CreditCard creditCard = new CreditCard();
            creditCard.setCardNumber(creditNumber);
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
            template = "views/CheckoutController/checkoutOverview.ftl.html";
            // update order
            Ebean.save(order);
        }
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
        order.setOrderDate(DateUtils.getCurrentDate());
        if (SessionHandling.isCustomerLogged(context))
        {
            // get customer by session id
            Customer customer = CustomerInformation.getCustomerById(SessionHandling.getCustomerId(context));
            // remove basket from customer
            customer.setBasket(null);
            // set customer to order
            order.setCustomer(customer);
        }
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
        // show success message
        data.put("successMessage", "Thank you for buying at XC-Poster!");
        // remember products for carousel
        CarouselInformation.getCarouselProducts(data);
        return Results.html().render(data).template("views/WebShopController/index.ftl.html");
    }
}
