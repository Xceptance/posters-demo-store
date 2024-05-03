/*
 * Copyright (c) 2013-2024 Xceptance Software Technologies GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import com.avaje.ebean.Ebean;
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
    
    public String session_form = "ship";

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
        session_form = "ship";
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("session_form", session_form);
        WebShopController.setCommonData(data, context, xcpConf);

        boolean userHasAShippingAddress = false;
        // Check if form data exists in the session
        //if (context.getSession().get("ses_formData").equals("TRUE")) {
            // Add form data from session to the data map
            data.put("ses_first_name", context.getSession().get("ses_first_name"));
            data.put("ses_last_name", context.getSession().get("ses_last_name"));
            data.put("ses_company", context.getSession().get("ses_company"));
            data.put("ses_addressLine", context.getSession().get("ses_addressLine"));
            data.put("ses_city", context.getSession().get("ses_city"));
            data.put("ses_state", context.getSession().get("ses_state"));
            data.put("ses_zip", context.getSession().get("ses_zip"));
            data.put("ses_country", context.getSession().get("ses_country"));

        //}
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
        public Result shippingAddressCompleted(@Param("firstName") final String firstName,
                                            @Param("lastName") final String lastName,
                                            @Param("company") final String company,
                                            @Param("addressLine") final String addressLine, @Param("city") final String city,
                                            @Param("state") final String state, @Param("zip") final String zip,
                                            @Param("country") final String country,
                                            @Param("billEqualShipp") final String billingEqualShipping, final Context context) 
        {

        final String name = firstName + " " + lastName;
        // check input
        if (!Pattern.matches(xcpConf.REGEX_ZIP, zip))
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            WebShopController.setCommonData(data, context, xcpConf);
            // show error message
            context.getFlashScope().error(msg.get("errorWrongZip", language).get());
            // show inserted values in form
            final Map<String, String> address = new HashMap<String, String>();
            address.put("name", lastName);
            address.put("firstName", firstName);
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

            //store form data in session
            context.getSession().put("ses_first_name", firstName );
            context.getSession().put("ses_last_name", lastName );
            context.getSession().put("ses_company", company );
            context.getSession().put("ses_addressLine", addressLine );
            context.getSession().put("ses_city", city );
            context.getSession().put("ses_state", state );
            context.getSession().put("ses_zip", zip );
            context.getSession().put("ses_country", country );
            context.getSession().put("ses_formData", "TRUE" );
            session_form = "ship";

            // create shipping address
            final ShippingAddress shippingAddress = new ShippingAddress();
            shippingAddress.setName(lastName);
            shippingAddress.setFirstName(firstName);
            shippingAddress.setCompany(company);
            shippingAddress.setAddressLine(addressLine);
            shippingAddress.setCity(city);
            shippingAddress.setState(state);
            shippingAddress.setZip(zip);
            shippingAddress.setCountry(country);
            
            // set new address to customer
            if (SessionHandling.isCustomerLogged(context))
            {
                //save shipping address in the table
                Customer.getCustomerById(SessionHandling.getCustomerId(context)).addShippingAddress(shippingAddress);
                // Create a copy of the shipping address with customer_id set to null
                final ShippingAddress copyAddress = new ShippingAddress();
                copyAddress.setName(shippingAddress.getName());
                copyAddress.setFirstName(shippingAddress.getFirstName());
                copyAddress.setCompany(shippingAddress.getCompany());
                copyAddress.setAddressLine(shippingAddress.getAddressLine());
                copyAddress.setCity(shippingAddress.getCity());
                copyAddress.setState(shippingAddress.getState());
                copyAddress.setZip(shippingAddress.getZip());
                copyAddress.setCountry(shippingAddress.getCountry());
                copyAddress.setCustomer(null);
                Ebean.save(copyAddress);
                //copyAddress.getId(); could be used to obtain id maybe
                final ShippingAddress retrievedAddress = ShippingAddress.getShippingAddressById(copyAddress.getId()); 
                // get order by session id
                final Order order = Order.getOrderById(SessionHandling.getOrderId(context));
                // set shipping address to order
                order.setShippingAddress(retrievedAddress);
                // update order
                order.update();
            }
            // save shipping address
            else
            {
                shippingAddress.save();
            // get order by session id
            final Order order = Order.getOrderById(SessionHandling.getOrderId(context));
            // set shipping address to order
            order.setShippingAddress(shippingAddress);
            // update order
            order.update();
            }

            // billing address is equal to shipping address
            if (billingEqualShipping.equals("Yes"))
            {
                // create billing address
                final BillingAddress billingAddress = new BillingAddress();
                billingAddress.setName(name);
                billingAddress.setFirstName(firstName);
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
                    
                    // Create a copy of the Billing address with customer_id set to null
                    final BillingAddress copyAddress = new BillingAddress();
                    copyAddress.setName(billingAddress.getName());
                    copyAddress.setFirstName(billingAddress.getFirstName());
                    copyAddress.setCompany(billingAddress.getCompany());
                    copyAddress.setAddressLine(billingAddress.getAddressLine());
                    copyAddress.setCity(billingAddress.getCity());
                    copyAddress.setState(billingAddress.getState());
                    copyAddress.setZip(billingAddress.getZip());
                    copyAddress.setCountry(billingAddress.getCountry());
                    copyAddress.setCustomer(null);
                    Ebean.save(copyAddress);

                    final BillingAddress retreivedAddress = BillingAddress.getBillingAddressById(copyAddress.getId());
                    // get order by session id
                    final Order order = Order.getOrderById(SessionHandling.getOrderId(context));
                    // set billing address to order
                    order.setBillingAddress(retreivedAddress);
                    // update order
                    order.update();
                    // return page to enter payment information
                    return Results.redirect(context.getContextPath() + "/enterPaymentMethod");
                }
                // save billing address
                else
                {
                    billingAddress.save();
                    
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
        final ShippingAddress shippingAddress = ShippingAddress.getShippingAddressById(Integer.parseInt(addressId));
        // Create a duplicate of the shipping address with customer_id set to null
        final ShippingAddress copyAddress = new ShippingAddress();
        copyAddress.setName(shippingAddress.getName());
        copyAddress.setFirstName(shippingAddress.getFirstName());
        copyAddress.setCompany(shippingAddress.getCompany());
        copyAddress.setAddressLine(shippingAddress.getAddressLine());
        copyAddress.setCity(shippingAddress.getCity());
        copyAddress.setState(shippingAddress.getState());
        copyAddress.setZip(shippingAddress.getZip());
        copyAddress.setCountry(shippingAddress.getCountry());
        copyAddress.setCustomer(null);
         Ebean.save(copyAddress);
        //save the duplicate shipping address in the table
        Customer.getCustomerById(SessionHandling.getCustomerId(context)).addShippingAddress(copyAddress);
        //retrieving copied address to add it to the order
        final ShippingAddress retrievedAddress = ShippingAddress.getShippingAddressById(copyAddress.getId());

        // Parse the addressId to an integer
        // int id = Integer.parseInt(addressId);
        // // Increment the addressId by 1
        // id += 1;
        // get shipping address
        //final ShippingAddress shippingAddress = ShippingAddress.getShippingAddressById(id);
        // get order by session id
        final Order order = Order.getOrderById(SessionHandling.getOrderId(context));
        // set shipping address to order
        order.setShippingAddress(retrievedAddress);
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
        session_form = "bill";
        
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("session_form", session_form);
        WebShopController.setCommonData(data, context, xcpConf);

        boolean userHasBillingAddress = false;
        // Check if form data exists in the session
        //if (context.getSession().get("ses_formData").equals("TRUE")) {
            // Add form data from session to the data map
            data.put("ses_bill_first_name", context.getSession().get("ses_bill_first_name"));
            data.put("ses_bill_last_name", context.getSession().get("ses_bill_last_name"));
            data.put("ses_bill_company", context.getSession().get("ses_bill_company"));
            data.put("ses_bill_addressLine", context.getSession().get("ses_bill_addressLine"));
            data.put("ses_bill_city", context.getSession().get("ses_bill_city"));
            data.put("ses_bill_state", context.getSession().get("ses_bill_state"));
            data.put("ses_bill_zip", context.getSession().get("ses_bill_zip"));
            data.put("ses_bill_country", context.getSession().get("ses_bill_country"));

        //}
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
    public Result billingAddressCompleted(@Param("firstName") final String firstName,
                                            @Param("lastName") final String lastName,
                                            @Param("company") final String company,
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
            address.put("name", lastName);
            address.put("firstName", firstName);
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
            //store form data in session
            context.getSession().put("ses_bill_first_name", firstName );
            context.getSession().put("ses_bill_last_name", lastName );
            context.getSession().put("ses_bill_company", company );
            context.getSession().put("ses_bill_addressLine", addressLine );
            context.getSession().put("ses_bill_city", city );
            context.getSession().put("ses_bill_state", state );
            context.getSession().put("ses_bill_zip", zip );
            context.getSession().put("ses_bill_country", country );
            context.getSession().put("ses_bill_formData", "TRUE" );

            // create new billing address
            final BillingAddress billingAddress = new BillingAddress();
            billingAddress.setName(lastName);
            billingAddress.setFirstName(firstName);
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
                // Create a copy of the shipping address with customer_id set to null
                final BillingAddress copyAddress = new BillingAddress();
                copyAddress.setName(billingAddress.getName());
                copyAddress.setFirstName(billingAddress.getFirstName());
                copyAddress.setCompany(billingAddress.getCompany());
                copyAddress.setAddressLine(billingAddress.getAddressLine());
                copyAddress.setCity(billingAddress.getCity());
                copyAddress.setState(billingAddress.getState());
                copyAddress.setZip(billingAddress.getZip());
                copyAddress.setCountry(billingAddress.getCountry());
                copyAddress.setCustomer(null);
                Ebean.save(copyAddress);

                //retrieving copied address to add it to the order
                final BillingAddress retrievedAddress = BillingAddress.getBillingAddressById(copyAddress.getId());

                // get order by session id
                final Order order = Order.getOrderById(SessionHandling.getOrderId(context));
                // set billing address to order
                order.setBillingAddress(retrievedAddress);
                // update order
                order.update();
                // return page to enter payment information
                return Results.redirect(context.getContextPath() + "/enterPaymentMethod");
            }
            // save billing address
            else
            {
                billingAddress.save();
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
        final BillingAddress billingAddress = BillingAddress.getBillingAddressById(Integer.parseInt(addressId));
        // Create a duplicate of the Billing address with customer_id set to null
        final BillingAddress copyAddress = new BillingAddress();
        copyAddress.setName(billingAddress.getName());
        copyAddress.setFirstName(billingAddress.getFirstName());
        copyAddress.setCompany(billingAddress.getCompany());
        copyAddress.setAddressLine(billingAddress.getAddressLine());
        copyAddress.setCity(billingAddress.getCity());
        copyAddress.setState(billingAddress.getState());
        copyAddress.setZip(billingAddress.getZip());
        copyAddress.setCountry(billingAddress.getCountry());
        copyAddress.setCustomer(null);
         Ebean.save(copyAddress);
        //save the duplicate Billing address in the table
        Customer.getCustomerById(SessionHandling.getCustomerId(context)).addBillingAddress(copyAddress);
        //retrieving copied address to add it to the order
        final BillingAddress retrievedAddress = BillingAddress.getBillingAddressById(copyAddress.getId());

        // get order by session id
        final Order order = Order.getOrderById(SessionHandling.getOrderId(context));
        // set billing address to order
        order.setBillingAddress(retrievedAddress);
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
                //For creating a copy of the shipping address with customer_id set to null
                final CreditCard copyCreditCard = new CreditCard();
                creditCard.setCardNumber(creditNumber);
                creditCard.setName(name);
                creditCard.setMonth(month);
                creditCard.setYear(year);
                // set new credit card to customer
                if (SessionHandling.isCustomerLogged(context))
                {
                    Customer.getCustomerById(SessionHandling.getCustomerId(context)).addCreditCard(creditCard);
                    // Create a copy of the shipping address with customer_id set to null
                    copyCreditCard.setName(creditCard.getName());
                    copyCreditCard.setCardNumber(creditCard.getCardNumber());
                    copyCreditCard.setMonth(creditCard.getMonth());
                    copyCreditCard.setYear(creditCard.getYear());
                    copyCreditCard.setCustomer(null);
                    Ebean.save(copyCreditCard);
                
                    // get order by session id
                    final Order order = Order.getOrderById(SessionHandling.getOrderId(context));

                    //retrieving copied Card to add it to the order
                    final CreditCard retrievedCard = CreditCard.getCreditCardById(copyCreditCard.getId());
                    order.setCreditCard(retrievedCard);
                    // update order
                    order.update();
                    // return page to get an overview of the checkout
                    return Results.redirect(context.getContextPath() + "/checkoutOverview");

                }
                // save credit card
                else
                {
                    creditCard.save();
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

        final CreditCard creditCard = CreditCard.getCreditCardById(Integer.parseInt(creditCardId));
        // Create a duplicate of the Card with customer_id set to null
        final CreditCard copyCard = new CreditCard();
        copyCard.setName(creditCard.getName());
        copyCard.setCardNumber(creditCard.getCardNumber());
        copyCard.setMonth(creditCard.getMonth());
        copyCard.setYear(creditCard.getYear());
        copyCard.setCustomer(null);
        Ebean.save(copyCard);
        //save the duplicate Card in the table
        Customer.getCustomerById(SessionHandling.getCustomerId(context)).addCreditCard(copyCard);
        //retrieving copied Card to add it to the order
        final CreditCard retrievedCard = CreditCard.getCreditCardById(copyCard.getId());
        // get order by session id
        final Order order = Order.getOrderById(SessionHandling.getOrderId(context));
        // set credit card to order
        order.setCreditCard(retrievedCard);
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
     * @return
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
