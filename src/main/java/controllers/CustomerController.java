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

import io.ebean.Ebean;
import com.google.inject.Inject;

import conf.PosterConstants;
import conf.StatusConf;
import filters.SessionCustomerExistFilter;
import filters.SessionCustomerIsLoggedFilter;
import models.BillingAddress;
import models.Cart;
import models.CartProduct;
import models.CreditCard;
import models.Customer;
import models.ShippingAddress;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.params.Param;
import ninja.params.PathParam;
import util.session.SessionHandling;

/**
 * Controller class, that provides the customer functionality.
 * 
 * @author sebastianloob
 */
public class CustomerController
{

    @Inject
    Messages msg;

    @Inject
    PosterConstants xcpConf;

    @Inject
    StatusConf stsConf;

    private Optional<String> language = Optional.of("en");

    /**
     * Returns a page to log in to the customer backend.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerExistFilter.class)
    public Result loginForm(final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }

    /**
     * Logs in to the system with email and password. Returns the home page, if the email and the password are correct,
     * otherwise the page to log-in again.
     * 
     * @param email
     * @param password
     * @param context
     * @return
     */
    @Inject
    StatusConf config;
    @FilterWith(SessionCustomerExistFilter.class)
    public Result login(@Param("email") final String email, @Param("password") final String password, final Context context, @PathParam("urlLocale") String locale)
    {
        language = Optional.of(locale);
        
        // email is not valid
        if (!Pattern.matches(xcpConf.REGEX_EMAIL, email))
        {
            // show error message
            context.getFlashScope().error(msg.get("errorValidEmail", language).get());
        }
        else
        {
            // exists the given email in the database
            final boolean emailExist = Customer.emailExist(email);
            // get customer by the given email
            final Customer customer = Customer.getCustomerByEmail(email);
            // is the password correct
            boolean correctPassword = false;
            // check password, if the email exist
            if (emailExist)
            {
                correctPassword = customer.checkPasswd(password);
                // the following is here to allow us to enable an error state at will:
                final Map<String, Object> status = new HashMap<String, Object>();
                config.getStatus(status);
                if (status.get("openLogin").equals(true))
                {
                    correctPassword = true;
                }
            }
            // email and password are correct
            if (emailExist && correctPassword)
            {
                // put customer id to session
                SessionHandling.setCustomerId(context, customer.getId());
                // add products of current cart to customer's cart
                mergeCurrentCartAndCustomerCart(context);
                // delete current cart
                SessionHandling.removeCartId(context);
                // put customer's cart id to session
                final Customer updatedCustomer = Customer.getCustomerByEmail(email);
                SessionHandling.setCartId(context, updatedCustomer.getCart().getId());
                // show home page
                context.getFlashScope().success(msg.get("successLogIn", language).get());
                return Results.redirect(context.getContextPath() + "/" + locale + "/accountOverview");

            }
            // user exist, wrong password
            else if (emailExist && !correctPassword)
            {
                // error message
                context.getFlashScope().error(msg.get("errorIncorrectPassword", language).get());
            }
            // wrong email
            else
            {
                // error message
                context.getFlashScope().error(msg.get("errorEmailExist", language).get());
            }
        }
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // show entered email address
        data.put("email", email);
        // show page to log-in again
        return Results.html().render(data).template(xcpConf.TEMPLATE_LOGIN_FORM);
    }

    /**
     * Logs off from the system. Returns the home page.
     * 
     * @param context
     * @return
     */
    public Result logout(final Context context, @PathParam("urlLocale") String locale)
    {
        language = Optional.of(locale);
        // remove customer from session
        SessionHandling.removeCustomerId(context);
        // remove cart from session
        SessionHandling.removeCartId(context);
        // show home page
        context.getFlashScope().success(msg.get("successLogOut", language).get());
        return Results.redirect(context.getContextPath() + "/" + locale);
    }

    /**
     * Returns the page to create a new account.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerExistFilter.class)
    public Result registration(final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }

    /**
     * Creates a new customer account.
     * 
     * @param name
     * @param firstName
     * @param email
     * @param password
     * @param passwordAgain
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerExistFilter.class)
    public Result registrationCompleted(@Param("lastName") final String name, @Param("firstName") final String firstName,
                                        @Param("eMail") final String email, @Param("password") final String password,
                                        @Param("passwordAgain") final String passwordAgain, final Context context,
                                        @PathParam("urlLocale") String locale)
    {
        language = Optional.of(locale);
        boolean failure = false;
        // account with this email already exist
        if (!Ebean.find(Customer.class).where().eq("email", email).findList().isEmpty())
        {
            // show error message
            context.getFlashScope().error(msg.get("errorAccountExist", language).get());
            failure = true;
        }
        // email is not valid
        else if (!Pattern.matches(xcpConf.REGEX_EMAIL, email))
        {
            // show error message
            context.getFlashScope().error(msg.get("errorValidEmail", language).get());
            failure = true;
        }
        // passwords don't match
        else if (!password.equals(passwordAgain))
        {
            // show error message
            context.getFlashScope().error(msg.get("errorPasswordMatch", language).get());
            failure = true;
        }
        if (failure)
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            WebShopController.setCommonData(data, context, xcpConf);
            final Map<String, String> registration = new HashMap<String, String>();
            registration.put("name", name);
            registration.put("firstName", firstName);
            registration.put("email", email);
            data.put("registration", registration);
            // show registration page again
            return Results.html().render(data).template(xcpConf.TEMPLATE_REGISTRATION);
        }
        // all input fields might be correct
        else
        {
            // create new customer
            final Customer customer = new Customer();
            customer.setName(name);
            customer.setFirstName(firstName);
            customer.setEmail(email);
            customer.hashPasswd(password);
            // save customer
            Ebean.save(customer);
            // show success message
            context.getFlashScope().success(msg.get("successCreateAccount", language).get());
            // show page to log-in
            return Results.redirect(context.getContextPath() + "/" + locale + "/" + "login");
        }
    }

    /**
     * Returns an overview page of the account of the customer.
     * 
     * @param context
     * @return
     */
    @FilterWith(
        {
            SessionCustomerIsLoggedFilter.class, SessionCustomerExistFilter.class
        })
    public Result accountOverview(final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }

    /**
     * Returns an overview page of completed orders of the customer.
     * 
     * @param context
     * @return
     */
    @FilterWith(
        {
            SessionCustomerIsLoggedFilter.class, SessionCustomerExistFilter.class
        })
    public Result orderOverview(final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        data.put("orderOverview", Customer.getCustomerById(SessionHandling.getCustomerId(context)).getAllOrders(stsConf));
        return Results.html().render(data);
    }

    /**
     * Returns an overview page of payment methods of the customer.
     * 
     * @param context
     * @return
     */
    @FilterWith(
        {
            SessionCustomerIsLoggedFilter.class, SessionCustomerExistFilter.class
        })
    public Result paymentOverview(final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // get customer by session
        final Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
        // add payment methods
        data.put("paymentOverview", customer.getCreditCard());
        return Results.html().render(data);
    }

    /**
     * Returns an overview page of account settings of the customer.
     * 
     * @param context
     * @return
     */
    @FilterWith(
        {
            SessionCustomerIsLoggedFilter.class, SessionCustomerExistFilter.class
        })
    public Result settingOverview(final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // add customer to data map
        data.put("customer", Customer.getCustomerById(SessionHandling.getCustomerId(context)));
        return Results.html().render(data);
    }

    /**
     * Adds a new payment method to the customer.
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
            SessionCustomerIsLoggedFilter.class, SessionCustomerExistFilter.class
        })
    public Result addPaymentToCustomerCompleted(@Param("creditCardNumber") String creditNumber, @Param("name") final String name,
                                                @Param("expirationDateMonth") final int month, @Param("expirationDateYear") final int year,
                                                final Context context, @PathParam("urlLocale") String locale)
    {
        language = Optional.of(locale);
        // replace spaces and dashes
        creditNumber = creditNumber.replaceAll("[ -]+", "");
        // check input
        for (final String regExCreditCard : xcpConf.REGEX_CREDITCARD)
        {
            // credit card number is correct
            if (Pattern.matches(regExCreditCard, creditNumber))
            {
                // get customer by session id
                final Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
                // create new credit card
                final CreditCard creditCard = new CreditCard();
                creditCard.setCardNumber(creditNumber);
                creditCard.setName(name);
                creditCard.setMonth(month);
                creditCard.setYear(year);
                // add credit card to customer
                customer.addCreditCard(creditCard);
                // success message
                context.getFlashScope().success(msg.get("successSave", language).get());
                // show payment overview page
                return Results.redirect(context.getContextPath() + "/" + locale + "/paymentOverview");
            }
        }
        // credit card number is not valid
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // show error message
        context.getFlashScope().error(msg.get("errorWrongCreditCard", language).get());
        // show inserted values in form
        final Map<String, String> card = new HashMap<String, String>();
        card.put("name", name);
        card.put("cardNumber", creditNumber);
        data.put("card", card);
        // show page to enter payment information again
        return Results.html().render(data).template(xcpConf.TEMPLATE_ADD_PAYMENT_TO_CUSTOMER);
    }

    /**
     * Returns a page to enter a payment method.
     * 
     * @param context
     * @return
     */
    @FilterWith(
        {
            SessionCustomerIsLoggedFilter.class, SessionCustomerExistFilter.class
        })
    public Result addPaymentToCustomer(final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        //@TODO

        DateFormat dateFormatYear = new SimpleDateFormat("yyyy");
        DateFormat dateFormatMonth = new SimpleDateFormat("MM");
        Date date = new Date();

        // get current month and year
        data.put("currentYear", Integer.valueOf(dateFormatYear.format(date)));
        data.put("currentMonth", Integer.valueOf(dateFormatMonth.format(date)));

        data.put("expirationDateStartYear", Integer.valueOf(dateFormatYear.format(date)));
        
        return Results.html().render(data);
    }

    /**
     * Removes a payment method of the customer.
     * 
     * @param cardId
     * @param context
     * @return
     */
    @FilterWith(
        {
            SessionCustomerIsLoggedFilter.class, SessionCustomerExistFilter.class
        })
    public Result deletePayment(@Param("cardId") final int cardId, final Context context, @PathParam("urlLocale") String locale)
    {
            language = Optional.of(locale);
            CreditCard.removeCustomerFromCreditCard(cardId);
            // show success message
            context.getFlashScope().success(msg.get("successDelete", language).get());
            // show payment overview page
            return Results.redirect(context.getContextPath() + "/" + locale + "/paymentOverview");
    }

    /**
     * Returns an overview page of billing and shipping addresses of the customer.
     * 
     * @param context
     * @return
     */
    @FilterWith(
        {
            SessionCustomerIsLoggedFilter.class, SessionCustomerExistFilter.class
        })
    public Result addressOverview(final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        final Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
        // add all shipping addresses
        data.put("shippingAddresses", customer.getShippingAddress());
        // add all billing addresses
        data.put("billingAddresses", customer.getBillingAddress());
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }

    /**
     * Removes a billing address of the customer.
     * 
     * @param password
     * @param addressId
     * @param context
     * @return
     */
    @FilterWith(
        {
            SessionCustomerIsLoggedFilter.class, SessionCustomerExistFilter.class
        })
    public Result deleteBillingAddress(@Param("addressIdBill") final int addressId,
                                       final Context context, @PathParam("urlLocale") String locale)
    {
            language = Optional.of(locale);
            // remove billing address
            BillingAddress.removeCustomerFromBillingAddress(addressId);
            // show success message
            context.getFlashScope().success(msg.get("successDelete", language).get());
            return Results.redirect(context.getContextPath() + "/" + locale + "/addressOverview");

    }

    /**
     * Removes a shipping address of the customer.
     * 
     * @param password
     * @param addressId
     * @param context
     * @return
     */
    @FilterWith(
        {
            SessionCustomerIsLoggedFilter.class, SessionCustomerExistFilter.class
        })
    public Result deleteShippingAddress(@Param("addressIdShip") final int addressId, final Context context, @PathParam("urlLocale") String locale)
    {
            language = Optional.of(locale);
            // remove shipping address
            ShippingAddress.removeCustomerFromShippingAddress(addressId);
            // show success message
            context.getFlashScope().success(msg.get("successDelete", language).get());
            // show address overview page
            return Results.redirect(context.getContextPath() + "/" + locale + "/addressOverview");
    }

    /**
     * Returns the page to update a payment method of the customer.
     * 
     * @param cardId
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerExistFilter.class)
    public Result updatePaymentMethod(@Param("cardId") final int cardId, final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("card", CreditCard.getCreditCardById(cardId));
        WebShopController.setCommonData(data, context, xcpConf);
        DateFormat dateFormatYear = new SimpleDateFormat("yyyy");
        DateFormat dateFormatMonth = new SimpleDateFormat("MM");
        Date date = new Date();

        // get current month and year
        data.put("currentYear", Integer.valueOf(dateFormatYear.format(date)));
        data.put("currentMonth", Integer.valueOf(dateFormatMonth.format(date)));

        data.put("expirationDateStartYear", Integer.valueOf(dateFormatYear.format(date)));
        return Results.html().render(data);
    }
    
    /**
     * Updates a payment method of a customer.
     * 
     * @param creditNumber
     * @param name
     * @param month
     * @param year
     * @param cardId
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerExistFilter.class)
    public Result updatePaymentMethodCompleted(@Param("creditCardNumber") String creditNumber, @Param("name") final String name,
                                                @Param("expirationDateMonth") final int month, @Param("expirationDateYear") final int year, 
                                                @Param("cardId") final int cardId, final Context context, @PathParam("urlLocale") String locale)
    {
        language = Optional.of(locale);
        // replace spaces and dashes
        creditNumber = creditNumber.replaceAll("[ -]+", "");
        // check input
        for (final String regExCreditCard : xcpConf.REGEX_CREDITCARD)
        {
            // credit card number is correct
            if (Pattern.matches(regExCreditCard, creditNumber))
            {
                // get creditCard by ID
                final CreditCard creditCard = CreditCard.getCreditCardById(cardId);
                creditCard.setCardNumber(creditNumber);
                creditCard.setName(name);
                creditCard.setMonth(month);
                creditCard.setYear(year);
                // update creditCard
                creditCard.update();
                // success message
                context.getFlashScope().success(msg.get("successSave", language).get());
                // show payment overview page
                return Results.redirect(context.getContextPath() + "/" + locale + "/paymentOverview");
            }
        }
        // credit card number is not valid
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // show error message
        context.getFlashScope().error(msg.get("errorWrongCreditCard", language).get());
        // show inserted values in form
        final Map<String, String> card = new HashMap<String, String>();
        card.put("name", name);
        card.put("cardNumber", creditNumber);
        data.put("card", card);
        // show page to enter payment information again
        return Results.html().render(data).template(xcpConf.TEMPLATE_ADD_PAYMENT_TO_CUSTOMER);
    }
    
    /**
     * Returns the page to update a shipping address of the customer.
     * 
     * @param addressId
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerExistFilter.class)
    public Result updateShippingAddress(@Param("addressId") final int addressId, final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        
        // Fetch the shipping address from the database
        ShippingAddress shippingAddress = ShippingAddress.getShippingAddressById(addressId);
            
            // Add first name and last name to the data map
            data.put("firstName", shippingAddress.getFirstName());
            data.put("lastName", shippingAddress.getName());

        data.put("address", shippingAddress);
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }    
    
    /**
     * Updates a shipping address of the customer.
     * 
     * @param name
     * @param company
     * @param addressLine
     * @param city
     * @param state
     * @param zip
     * @param country
     * @param addressId
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerExistFilter.class)
    public Result updateShippingAddressCompleted(@Param("firstName") final String firstName, @Param("lastName") final String lastName, 
                                                 @Param("company") final String company,
                                                 @Param("addressLine") final String addressLine, @Param("city") final String city,
                                                 @Param("state") final String state, @Param("zip") final String zip,
                                                 @Param("country") final String country, @Param("addressId") final String addressId,
                                                 final Context context, @PathParam("urlLocale") String locale)
    {
        language = Optional.of(locale);
        // check input
        if (!Pattern.matches(xcpConf.REGEX_ZIP, zip))
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            WebShopController.setCommonData(data, context, xcpConf);
            // show error message
            context.getFlashScope().error(msg.get("errorWrongZip", language).get());
            // show inserted values in form
            //final Map<String, String> address = new HashMap<String, String>();
            data.put("id", addressId);
            data.put("name", lastName);
            data.put("firstName", firstName);
            data.put("company", company);
            data.put("addressLine", addressLine);
            data.put("city", city);
            data.put("state", state);
            data.put("zip", zip);
            data.put("country", country);
            //data.put("address", address);
            // show page to enter shipping address again
            return Results.html().render(data).template(xcpConf.TEMPLATE_UPDATE_SHIPPING_ADDRESS);
        }
        // all input fields might be correct
        else
        {
            final ShippingAddress address = ShippingAddress.getShippingAddressById(Integer.parseInt(addressId));
            address.setName(lastName);
            address.setFirstName(firstName);
            address.setCompany(company);
            address.setAddressLine(addressLine);
            address.setCity(city);
            address.setState(state);
            address.setZip(zip);
            address.setCountry(country);
            // update address
            address.update();
            // show success message
            context.getFlashScope().success(msg.get("successUpdate", language).get());
            return Results.redirect(context.getContextPath() + "/" + locale + "/addressOverview");
        }
    }

    /**
     * Returns the page to update a billing address of the customer.
     * 
     * @param addressId
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerExistFilter.class)
    public Result updateBillingAddress(@Param("addressId") final int addressId, final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        // Fetch the shipping address from the database
        BillingAddress billingAddress = BillingAddress.getBillingAddressById(addressId);
    
            
        // Add first name and last name to the data map
        data.put("firstName", billingAddress.getFirstName());
        data.put("lastName", billingAddress.getName());

        data.put("address", billingAddress);
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }

    /**
     * Updates a billing address of the customer.
     * 
     * @param name
     * @param company
     * @param addressLine
     * @param city
     * @param state
     * @param zip
     * @param country
     * @param addressId
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerExistFilter.class)
    public Result updateBillingAddressCompleted(@Param("firstName") final String firstName, @Param("lastName") final String lastName, 
                                                @Param("company") final String company,
                                                @Param("addressLine") final String addressLine, @Param("city") final String city,
                                                @Param("state") final String state, @Param("zip") final String zip,
                                                @Param("country") final String country, @Param("addressId") final String addressId,
                                                final Context context, @PathParam("urlLocale") String locale)
    {
        language = Optional.of(locale);
        // check input
        if (!Pattern.matches(xcpConf.REGEX_ZIP, zip))
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            WebShopController.setCommonData(data, context, xcpConf);
            // show error message
            context.getFlashScope().error(msg.get("errorWrongZip", language).get());
            // show inserted values in form
            final Map<String, String> address = new HashMap<String, String>();
            address.put("id", addressId);
            address.put("name", lastName);
            address.put("name", firstName);
            address.put("company", company);
            address.put("addressLine", addressLine);
            address.put("city", city);
            address.put("state", state);
            address.put("zip", zip);
            address.put("country", country);
            data.put("address", address);
            // show page to enter billing address again
            return Results.html().render(data).template(xcpConf.TEMPLATE_UPDATE_BILLING_ADDRESS);
        }
        // all input fields might be correct
        else
        {
            final BillingAddress address = BillingAddress.getBillingAddressById(Integer.parseInt(addressId));

            address.setName(lastName);
            address.setFirstName(firstName);
            address.setCompany(company);
            address.setAddressLine(addressLine);
            address.setCity(city);
            address.setState(state);
            address.setZip(zip);
            address.setCountry(country);
            address.update();
            // show success message
            context.getFlashScope().success(msg.get("successUpdate", language).get());
            return Results.redirect(context.getContextPath() + "/" + locale + "/addressOverview");
        }
    }

    /**
     * Returns a page to enter a new shipping address.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerExistFilter.class)
    public Result addShippingAddressToCustomer(final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }

    /**
     * Returns a page to enter a new billing address.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerExistFilter.class)
    public Result addBillingAddressToCustomer(final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }

    /**
 * Adds a new shipping address to a customer.
 * 
 * @param firstName
 * @param lastName
 * @param company
 * @param addressLine
 * @param city
 * @param state
 * @param zip
 * @param country
 * @param addressId
 * @param context
 * @return
 */
    @FilterWith({
        SessionCustomerIsLoggedFilter.class, SessionCustomerExistFilter.class
    })
    public Result addShippingAddressToCustomerCompleted(@Param("firstName") final String firstName, @Param("lastName") final String lastName,
                                                        @Param("company") final String company, @Param("addressLine") final String addressLine,
                                                        @Param("city") final String city, @Param("state") final String state, @Param("zip") final String zip,
                                                        @Param("country") final String country, @Param("addressId") final String addressId,
                                                        final Context context, @PathParam("urlLocale") String locale) {

        language = Optional.of(locale);
        final String name = firstName + " " + lastName;
        // Check input
        if (!Pattern.matches(xcpConf.REGEX_ZIP, zip)) {
            final Map<String, Object> data = new HashMap<>();
            WebShopController.setCommonData(data, context, xcpConf);
            // Show error message
            context.getFlashScope().error(msg.get("errorWrongZip", language).get());
            // Show inserted values in form
            final Map<String, String> address = new HashMap<>();
            address.put("firstName", firstName);
            address.put("lastName", lastName);
            address.put("name", name);
            address.put("company", company);
            address.put("addressLine", addressLine);
            address.put("city", city);
            address.put("state", state);
            address.put("zip", zip);
            address.put("country", country);
            data.put("address", address);
            // Show page to enter shipping address again
            return Results.html().render(data).template(xcpConf.TEMPLATE_ADD_SHIPPING_ADDRESS_TO_CUSTOMER);
        } else {
            final ShippingAddress address = new ShippingAddress();
            address.setName(lastName);
            address.setFirstName(firstName);
            address.setCompany(company);
            address.setAddressLine(addressLine);
            address.setCity(city);
            address.setState(state);
            address.setZip(zip);
            address.setCountry(country);
            // Add address to customer
            final Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
            customer.addShippingAddress(address);
            
            // Show success message
            context.getFlashScope().success(msg.get("successSave", language).get());
            return Results.redirect(context.getContextPath() + "/" + locale + "/addressOverview");
        }
    }


    /**
     * Adds a new billing address to a customer.
     * 
     * @param name
     * @param company
     * @param addressLine
     * @param city
     * @param state
     * @param zip
     * @param country
     * @param addressId
     * @param context
     * @return
     */
    @FilterWith(
        {
            SessionCustomerIsLoggedFilter.class, SessionCustomerExistFilter.class
        })
        public Result addBillingAddressToCustomerCompleted(@Param("firstName") final String firstName,
                                                            @Param("lastName") final String lastName,
                                                            @Param("company") final String company,
                                                            @Param("addressLine") final String addressLine, @Param("city") final String city,
                                                            @Param("state") final String state, @Param("zip") final String zip,
                                                            @Param("country") final String country, @Param("addressId") final String addressId,
                                                            final Context context, @PathParam("urlLocale") String locale)
    {
        language = Optional.of(locale);
        final String name = firstName + " " + lastName;
        // check input
        if (!Pattern.matches("[0-9]*", zip))
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            WebShopController.setCommonData(data, context, xcpConf);
            // show error message
            context.getFlashScope().error(msg.get("errorWrongZip", language).get());
            // show inserted values in form
            final Map<String, String> address = new HashMap<String, String>();
            address.put("firstName", firstName);
            address.put("lastName", lastName);
            address.put("name", name);
            address.put("company", company);
            address.put("addressLine", addressLine);
            address.put("city", city);
            address.put("state", state);
            address.put("zip", zip);
            address.put("country", country);
            data.put("address", address);
            // show page to enter billing address again
            return Results.html().render(data).template(xcpConf.TEMPLATE_ADD_BILLING_ADDRESS_TO_CUSTOMER);
        }
        // all input fields might be correct
        else
        {
            final BillingAddress address = new BillingAddress();
            address.setName(lastName);
            address.setFirstName(firstName);            
            address.setCompany(company);
            address.setAddressLine(addressLine);
            address.setCity(city);
            address.setState(state);
            address.setZip(zip);
            address.setCountry(country);
            // add address to customer
            final Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
            customer.addBillingAddress(address);
            // show success message
            context.getFlashScope().success(msg.get("successSave", language).get());
            return Results.redirect(context.getContextPath() + "/" + locale + "/addressOverview");
        }
    }

    /**
     * Returns a page to update the name and the email address of a customer.
     * 
     * @param context
     * @return
     */
    @FilterWith(
        {
            SessionCustomerIsLoggedFilter.class, SessionCustomerExistFilter.class
        })
    public Result changeNameOrEmail(final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        final Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
        data.put("customer", customer);
        return Results.html().render(data);
    }

    /**
     * Updates the name and the email address of a customer.
     * 
     * @param name
     * @param firstName
     * @param email
     * @param password
     * @param context
     * @return
     */
    @FilterWith(
        {
            SessionCustomerIsLoggedFilter.class, SessionCustomerExistFilter.class
        })
    public Result changeNameOrEmailCompleted(@Param("lastName") final String name, @Param("firstName") final String firstName,
                                             @Param("eMail") final String email, @Param("password") final String password,
                                             final Context context, @PathParam("urlLocale") String locale)
    {
        language = Optional.of(locale);
        final Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
        // incorrect password
        if (!customer.checkPasswd(password))
        {
            // show error message
            context.getFlashScope().error(msg.get("errorIncorrectPassword", language).get());
            return Results.redirect(context.getContextPath() + "/" + locale + "/changeNameOrEmail");
        }
        // email isn't valid
        else if (!Pattern.matches(xcpConf.REGEX_EMAIL, email))
        {
            // show error message
            context.getFlashScope().error(msg.get("errorValidEmail", language).get());
            return Results.redirect(context.getContextPath() + "/" + locale + "/changeNameOrEmail");
        }
        // email already exist
        else if (!Ebean.find(Customer.class).where().eq("email", email).ne("id", customer.getId()).findList().isEmpty())
        {
            // show error message
            context.getFlashScope().error(msg.get("errorAccountExist", language).get());
            return Results.redirect(context.getContextPath() + "/" + locale + "/changeNameOrEmail");
        }
        customer.setName(name);
        customer.setFirstName(firstName);
        customer.setEmail(email);
        customer.update();
        // show success message
        context.getFlashScope().success(msg.get("successUpdate", language).get());
        return Results.redirect(context.getContextPath() + "/" + locale + "/settingOverview");
    }

    /**
     * Returns a page to update the current password of a customer.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerExistFilter.class)
    public Result changePassword(final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }

    /**
     * Updates the current password of a customer.
     * 
     * @param oldPassword
     * @param password
     * @param passwordAgain
     * @param context
     * @return
     */
    @FilterWith(
        {
            SessionCustomerIsLoggedFilter.class, SessionCustomerExistFilter.class
        })
    public Result changePasswordCompleted(@Param("oldPassword") final String oldPassword, @Param("password") final String password,
                                          @Param("passwordAgain") final String passwordAgain, final Context context, @PathParam("urlLocale") String locale)
    {
        language = Optional.of(locale);
        boolean failure = false;
        final Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
        // incorrect password
        if (!customer.checkPasswd(oldPassword))
        {
            // show error message
            context.getFlashScope().error(msg.get("errorIncorrectPassword", language).get());
            failure = true;
        }
        // passwords don't match
        else if (!password.equals(passwordAgain))
        {
            // show error message
            context.getFlashScope().error(msg.get("errorPasswordMatch", language).get());
            failure = true;
        }
        if (failure)
        {
            // show page to change password again
            return Results.redirect(context.getContextPath() + "/" + locale + "/changePassword");
        }
        else
        {
            customer.hashPasswd(password);
            customer.update();
            // show success message
            context.getFlashScope().success(msg.get("successUpdate", language).get());
            return Results.redirect(context.getContextPath() + "/" + locale + "/settingOverview");
        }
    }

    /**
     * Returns the page to confirm the deletion of customer's account.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerExistFilter.class)
    public Result confirmDeleteAccount(final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }

    /**
     * Deletes the current customer and all its addresses, orders and payment information.
     * 
     * @param password
     * @param context
     * @return
     */
    @FilterWith(
        {
            SessionCustomerIsLoggedFilter.class, SessionCustomerExistFilter.class
        })
        public Result deleteAccount(@Param("password") final String password, final Context context, @PathParam("urlLocale") String locale) {
        language = Optional.of(locale);    
        final Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
            // get customer
            // correct password
            if (customer.checkPasswd(password)) {
                // remove customer from session
                SessionHandling.removeCustomerId(context);
                // remove cart from session
                SessionHandling.removeCartId(context);
                   
                Ebean.delete(customer);

                // show success message
                context.getFlashScope().success(msg.get("successDeleteAccount", language).get());
                // return home page
                return Results.redirect(context.getContextPath() + "/" + locale + "/");
            } else {
                // incorrect password
                context.getFlashScope().error(msg.get("errorIncorrectPassword", language).get());
                // show page again
                return Results.redirect(context.getContextPath() + "/" + locale + "/confirmDeleteAccount");
            }
        }
        
        
                    
        
    /**
     * Merges the current cart from the session and the customer's cart.
     * 
     * @param context
     */
    private void mergeCurrentCartAndCustomerCart(final Context context)
    {
        if (SessionHandling.isCustomerLogged(context))
        {
            // get current cart
            final Cart currentCart = Cart.getCartById(SessionHandling.getCartId(context, xcpConf));
            // get cart of customer
            final Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
            if (customer.getCart() == null)
            {
                customer.setCart(new Cart());
                customer.update();
            }
            final Cart customerCart = Cart.getCartById(customer.getCart().getId());
            for (final CartProduct cartProduct : currentCart.getProducts())
            {
                for (int i = 0; i < cartProduct.getProductCount(); i++)
                {
                    customerCart.addProduct(cartProduct.getProduct(), cartProduct.getFinish(), cartProduct.getSize());
                }
            }
            customerCart.setCustomer(customer);
            customerCart.update();
        }
    }
}
