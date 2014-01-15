package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import models.BillingAddress;
import models.Cart;
import models.CartProduct;
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
import util.session.SessionHandling;

import com.avaje.ebean.Ebean;
import com.google.common.base.Optional;
import com.google.inject.Inject;

import conf.XCPosterConf;
import filters.SessionCustomerFilter;

public class CustomerController
{

    @Inject
    Messages msg;

    @Inject
    XCPosterConf xcpConf;

    private Optional language = Optional.of("en");

    /**
     * Returns a page to log in to the customer backend.
     * 
     * @param context
     * @return
     */
    public Result loginForm(Context context)
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
    public Result login(@Param("email") String email, @Param("password") String password, Context context)
    {
        // is email valid
        if (!Pattern.matches(xcpConf.regexEmail, email))
        {
            // error message
            context.getFlashCookie().error(msg.get("errorValidEmail", language).get());
        }
        else
        {
            // exists the given email in the database
            boolean emailExist = Customer.emailExist(email);
            // get customer by the given email
            Customer customer = Customer.getCustomerByEmail(email);
            // is the password correct
            boolean correctPassowrd = customer.checkPasswd(password);
            // email and password are correct
            if (emailExist && correctPassowrd)
            {
                // put customer id to session
                SessionHandling.setCustomerId(context, customer.getId());
                // add products of current basket to customer's basket
                mergeCurrentCartAndCustomerCart(context);
                // delete current basket
                SessionHandling.removeCartId(context);
                // put customer's basket id to session
                Customer updatedCustomer = Customer.getCustomerByEmail(email);
                SessionHandling.setCartId(context, updatedCustomer.getCart().getId());
                // show home page
                return Results.redirect(context.getContextPath() + "/");
            }
            // user exist, wrong password
            else if (emailExist && !correctPassowrd)
            {
                // error message
                context.getFlashCookie().error(msg.get("errorIncorrectPassword", language).get());
            }
            // wrong email
            else
            {
                // error message
                context.getFlashCookie().error(msg.get("errorEmailExist", language).get());
            }
        }
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // show entered email address
        data.put("email", email);
        // show page to log-in again
        return Results.html().render(data).template(xcpConf.templateLoginForm);
    }

    /**
     * Logs off from the system. Returns the home page.
     * 
     * @param context
     * @return
     */
    public Result logout(Context context)
    {
        // remove customer from session
        SessionHandling.removeCustomerId(context);
        // remove basket from session
        SessionHandling.removeCartId(context);
        // show home page
        return Results.redirect(context.getContextPath() + "/");
    }

    /**
     * Returns the page to enter account information to create a new one.
     * 
     * @param context
     * @return
     */
    public Result registration(Context context)
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
    public Result registrationCompleted(@Param("name") String name, @Param("firstName") String firstName,
                                        @Param("eMail") String email, @Param("password") String password,
                                        @Param("passwordAgain") String passwordAgain, Context context)
    {
        boolean failure = false;
        // email must be unique
        if (!Ebean.find(Customer.class).where().eq("email", email).findList().isEmpty())
        {
            // error message
            context.getFlashCookie().error(msg.get("errorAccountExist", language).get());
            failure = true;
        }
        // is email valid
        else if (!Pattern.matches(xcpConf.regexEmail, email))
        {
            // error message
            context.getFlashCookie().error(msg.get("errorValidEmail", language).get());
            failure = true;
        }
        // passwords don't match
        else if (!password.equals(passwordAgain))
        {
            // error message
            context.getFlashCookie().error(msg.get("errorPasswordMatch", language).get());
            failure = true;
        }
        if (failure)
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            WebShopController.setCommonData(data, context, xcpConf);
            Map<String, String> registration = new HashMap<String, String>();
            registration.put("name", name);
            registration.put("firstName", firstName);
            registration.put("email", email);
            data.put("registration", registration);
            // show registration page again
            return Results.html().render(data).template(xcpConf.templateRegistration);
        }
        // all input fields might be correct
        else
        {
            // create new customer
            Customer customer = new Customer();
            customer.setName(name);
            customer.setFirstName(firstName);
            customer.setEmail(email);
            customer.hashPasswd(password);
            // save customer
            Ebean.save(customer);
            // show success message
            context.getFlashCookie().success(msg.get("successCreateAccount", language).get());
            // show page to log-in
            return Results.redirect(context.getContextPath() + "/login");
        }
    }

    /**
     * Returns an overview page of the account of the customer.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerFilter.class)
    public Result accountOverview(Context context)
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
    @FilterWith(SessionCustomerFilter.class)
    public Result orderOverview(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        data.put("orderOverview", Customer.getCustomerById(SessionHandling.getCustomerId(context)).getAllOrders());
        return Results.html().render(data);
    }

    /**
     * Returns an overview page of payment information of the customer.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerFilter.class)
    public Result paymentOverview(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // get customer by session
        Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
        // add payment information
        data.put("paymentOverview", customer.getCreditCard());
        return Results.html().render(data);
    }

    /**
     * Returns an overview page of account settings of the customer.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerFilter.class)
    public Result settingOverview(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // add customer to data map
        data.put("customer", Customer.getCustomerById(SessionHandling.getCustomerId(context)));
        return Results.html().render(data);
    }

    /**
     * Adds a new payment information to the customer.
     * 
     * @param creditNumber
     * @param name
     * @param month
     * @param year
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerFilter.class)
    public Result addPaymentToCustomerCompleted(@Param("creditCardNumber") String creditNumber,
                                                @Param("name") String name, @Param("expirationDateMonth") int month,
                                                @Param("expirationDateYear") int year, Context context)
    {
        // replace spaces and dashes
        creditNumber = creditNumber.replaceAll("[ -]+", "");
        // check input
        for (String regExCreditCard : xcpConf.regexCreditCard)
        {
            // credit card number is correct
            if (Pattern.matches(regExCreditCard, creditNumber))
            {
                // get customer by session id
                Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
                // create new credit card
                CreditCard creditCard = new CreditCard();
                creditCard.setCardNumber(creditNumber);
                creditCard.setName(name);
                creditCard.setMonth(month);
                creditCard.setYear(year);
                // add credit card to customer
                customer.addCreditCard(creditCard);
                // success message
                context.getFlashCookie().success(msg.get("successSave", language).get());
                // show payment overview page
                return Results.redirect(context.getContextPath() + "/paymentOverview");
            }
        }
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // error message
        context.getFlashCookie().error(msg.get("errorWrongCreditCard", language).get());
        // show inserted values in form
        Map<String, String> card = new HashMap<String, String>();
        card.put("name", name);
        card.put("cardNumber", creditNumber);
        data.put("card", card);
        // show page to enter payment information again
        return Results.html().render(data).template(xcpConf.templateAddPaymentToCustomer);
    }

    /**
     * Returns a page to enter payment information.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerFilter.class)
    public Result addPaymentToCustomer(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }

    /**
     * Removes a payment information of the customer.
     * 
     * @param cardId
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerFilter.class)
    public Result deletePayment(@Param("password") String password, @Param("cardId") int cardId, Context context)
    {
        Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
        // correct password
        if (customer.checkPasswd(password))
        {
            CreditCard.removeCreditCardFromCustomer(cardId);
            // success message
            context.getFlashCookie().success(msg.get("successDelete", language).get());
            // show payment overview page
            return Results.redirect(context.getContextPath() + "/paymentOverview");
        }
        // incorrect password
        else
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            context.getFlashCookie().error(msg.get("errorIncorrectPassword", language).get());
            data.put("cardId", cardId);
            WebShopController.setCommonData(data, context, xcpConf);
            // show page again
            return Results.html().render(data).template(xcpConf.templateConfirmDeletePayment);
        }
    }

    /**
     * Returns the page to confirm the deletion of a credit card.
     * 
     * @param cardId
     * @param context
     * @return
     */
    public Result confirmDeletePayment(@Param("cardId") int cardId, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("cardId", cardId);
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }

    /**
     * Returns an overview page of billing and delivery addresses of the customer.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerFilter.class)
    public Result addressOverview(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
        // add all delivery addresses
        data.put("deliveryAddresses", customer.getShippingAddress());
        // add all billing addresses
        data.put("billingAddresses", customer.getBillingAddress());
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }

    /**
     * Removes a billing address of the customer.
     * 
     * @param cardId
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerFilter.class)
    public Result deleteBillingAddress(@Param("password") String password, @Param("addressId") int addressId,
                                       Context context)
    {
        Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
        // correct password
        if (customer.checkPasswd(password))
        {
            // remove billing address
            BillingAddress.deleteBillingAddressFromCustomer(addressId);
            // success message
            context.getFlashCookie().success(msg.get("successDelete", language).get());
            return Results.redirect(context.getContextPath() + "/addressOverview");
        }
        // incorrect password
        else
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            WebShopController.setCommonData(data, context, xcpConf);
            context.getFlashCookie().error(msg.get("errorIncorrectPassword", language).get());
            data.put("deleteAddressURL", "deleteBillingAddress");
            data.put("addressId", addressId);
            // show page again
            return Results.html().render(data).template(xcpConf.templateConfirmDeleteAddress);
        }
    }

    /**
     * Returns the page to confirm the deletion of a billing address.
     * 
     * @param addressId
     * @param context
     * @return
     */
    public Result confirmDeleteBillingAddress(@Param("addressId") int addressId, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("deleteAddressURL", "deleteBillingAddress");
        data.put("addressId", addressId);
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data).template(xcpConf.templateConfirmDeleteAddress);
    }

    /**
     * Removes a delivery address of the customer.
     * 
     * @param cardId
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerFilter.class)
    public Result deleteDeliveryAddress(@Param("password") String password, @Param("addressId") int addressId,
                                        Context context)
    {
        Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
        // correct password
        if (customer.checkPasswd(password))
        {
            // remove shipping address
            ShippingAddress.removeCustomerFromShippingAddress(addressId);
            // success message
            context.getFlashCookie().success(msg.get("successDelete", language).get());
            // show address overview page
            return Results.redirect(context.getContextPath() + "/addressOverview");
        }
        // incorrect password
        else
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            WebShopController.setCommonData(data, context, xcpConf);
            context.getFlashCookie().error(msg.get("errorIncorrectPassword", language).get());
            data.put("deleteAddressURL", "deleteDeliveryAddress");
            data.put("addressId", addressId);
            return Results.html().render(data).template(xcpConf.templateConfirmDeleteAddress);
        }
    }

    /**
     * Returns the page to confirm the deletion of a delivery address.
     * 
     * @param addressId
     * @param context
     * @return
     */
    public Result confirmDeleteDeliveryAddress(@Param("addressId") int addressId, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("deleteAddressURL", "deleteDeliveryAddress");
        data.put("addressId", addressId);
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data).template(xcpConf.templateConfirmDeleteAddress);
    }

    /**
     * Returns the page to update a delivery address of the customer.
     * 
     * @param cardId
     * @param context
     * @return
     */
    public Result updateDeliveryAddress(@Param("addressId") int addressId, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("address", ShippingAddress.getShippingAddressById(addressId));
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }

    /**
     * Updates a delivery address of the customer.
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
    public Result updateDeliveryAddressCompleted(@Param("fullName") String name, @Param("company") String company,
                                                 @Param("addressLine") String addressLine, @Param("city") String city,
                                                 @Param("state") String state, @Param("zip") String zip,
                                                 @Param("country") String country,
                                                 @Param("addressId") String addressId, Context context)
    {
        // check input
        if (!Pattern.matches(xcpConf.regexZip, zip))
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            WebShopController.setCommonData(data, context, xcpConf);
            // error message
            context.getFlashCookie().error(msg.get("errorWrongZip", language).get());
            // show inserted values in form
            Map<String, String> address = new HashMap<String, String>();
            address.put("id", addressId);
            address.put("name", name);
            address.put("company", company);
            address.put("addressLine", addressLine);
            address.put("city", city);
            address.put("state", state);
            address.put("zip", zip);
            address.put("country", country);
            data.put("address", address);
            // show page to enter delivery address again
            return Results.html().render(data).template(xcpConf.templateUpdateDeliveryAddress);
        }
        // all input fields might be correct
        else
        {
            ShippingAddress address = ShippingAddress.getShippingAddressById(Integer.parseInt(addressId));
            address.setName(name);
            address.setCompany(company);
            address.setAddressLine(addressLine);
            address.setCity(city);
            address.setState(state);
            address.setZip(zip);
            address.setCountry(country);
            // update address
            address.update();
            // success message
            context.getFlashCookie().success(msg.get("successUpdate", language).get());
            return Results.redirect(context.getContextPath() + "/addressOverview");
        }
    }

    /**
     * Returns the page to update a billing address of the customer.
     * 
     * @param cardId
     * @param context
     * @return
     */
    public Result updateBillingAddress(@Param("addressId") int addressId, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("address", BillingAddress.getBillingAddressById(addressId));
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
    public Result updateBillingAddressCompleted(@Param("fullName") String name, @Param("company") String company,
                                                @Param("addressLine") String addressLine, @Param("city") String city,
                                                @Param("state") String state, @Param("zip") String zip,
                                                @Param("country") String country, @Param("addressId") String addressId,
                                                Context context)
    {
        // check input
        if (!Pattern.matches(xcpConf.regexZip, zip))
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            WebShopController.setCommonData(data, context, xcpConf);
            // error message
            context.getFlashCookie().error(msg.get("errorWrongZip", language).get());
            // show inserted values in form
            Map<String, String> address = new HashMap<String, String>();
            address.put("id", addressId);
            address.put("name", name);
            address.put("company", company);
            address.put("addressLine", addressLine);
            address.put("city", city);
            address.put("state", state);
            address.put("zip", zip);
            address.put("country", country);
            data.put("address", address);
            // show page to enter billing address again
            return Results.html().render(data).template(xcpConf.templateUpdateBillingAddress);
        }
        // all input fields might be correct
        else
        {
            BillingAddress address = BillingAddress.getBillingAddressById(Integer.parseInt(addressId));

            address.setName(name);
            address.setCompany(company);
            address.setAddressLine(addressLine);
            address.setCity(city);
            address.setState(state);
            address.setZip(zip);
            address.setCountry(country);
            address.update();
            // success message
            context.getFlashCookie().success(msg.get("successUpdate", language).get());
            return Results.redirect(context.getContextPath() + "/addressOverview");
        }
    }

    /**
     * Returns a page to enter a new delivery address.
     * 
     * @param context
     * @return
     */
    public Result addDeliveryAddressToCustomer(Context context)
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
    public Result addBillingAddressToCustomer(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }

    /**
     * Adds a new delivery address to a customer.
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
    @FilterWith(SessionCustomerFilter.class)
    public Result addDeliveryAddressToCustomerCompleted(@Param("fullName") String name,
                                                        @Param("company") String company,
                                                        @Param("addressLine") String addressLine,
                                                        @Param("city") String city, @Param("state") String state,
                                                        @Param("zip") String zip, @Param("country") String country,
                                                        @Param("addressId") String addressId, Context context)
    {
        // check input
        if (!Pattern.matches(xcpConf.regexZip, zip))
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            WebShopController.setCommonData(data, context, xcpConf);
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
            // show page to enter delivery address again
            return Results.html().render(data).template(xcpConf.templateAddDeliveryAddressToCustomer);
        }
        // all input fields might be correct
        else
        {
            ShippingAddress address = new ShippingAddress();
            address.setName(name);
            address.setCompany(company);
            address.setAddressLine(addressLine);
            address.setCity(city);
            address.setState(state);
            address.setZip(zip);
            address.setCountry(country);
            // add address to customer
            Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
            customer.addShippingAddress(address);
            customer.update();
            // success message
            context.getFlashCookie().success(msg.get("successSave", language).get());
            return Results.redirect(context.getContextPath() + "/addressOverview");
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
    @FilterWith(SessionCustomerFilter.class)
    public Result addBillingAddressToCustomerCompleted(@Param("fullName") String name,
                                                       @Param("company") String company,
                                                       @Param("addressLine") String addressLine,
                                                       @Param("city") String city, @Param("state") String state,
                                                       @Param("zip") String zip, @Param("country") String country,
                                                       @Param("addressId") String addressId, Context context)
    {
        // check input
        if (!Pattern.matches("[0-9]*", zip))
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            WebShopController.setCommonData(data, context, xcpConf);
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
            // show page to enter billing address again
            return Results.html().render(data).template(xcpConf.templateAddBillingAddressToCustomer);
        }
        // all input fields might be correct
        else
        {
            BillingAddress address = new BillingAddress();

            address.setName(name);
            address.setCompany(company);
            address.setAddressLine(addressLine);
            address.setCity(city);
            address.setState(state);
            address.setZip(zip);
            address.setCountry(country);
            // add address to customer
            Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
            customer.addBillingAddress(address);
            customer.update();
            // success message
            context.getFlashCookie().success(msg.get("successSave", language).get());
            return Results.redirect(context.getContextPath() + "/addressOverview");
        }
    }

    /**
     * Returns a page to update the name and the email address of a customer.
     * 
     * @param customerId
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerFilter.class)
    public Result changeNameOrEmail(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
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
    @FilterWith(SessionCustomerFilter.class)
    public Result changeNameOrEmailCompleted(@Param("name") String name, @Param("firstName") String firstName,
                                             @Param("eMail") String email, @Param("password") String password,
                                             Context context)
    {
        Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
        // incorrect password
        if (!customer.checkPasswd(password))
        {
            // error message
            context.getFlashCookie().error(msg.get("errorIncorrectPassword", language).get());
            return Results.redirect(context.getContextPath() + "/changeNameOrEmail");
        }
        // email isn't valid
        else if (!Pattern.matches(xcpConf.regexEmail, email))
        {
            // error message
            context.getFlashCookie().error(msg.get("errorValidEmail", language).get());
            return Results.redirect(context.getContextPath() + "/changeNameOrEmail");
        }
        // email already exist
        else if (!Ebean.find(Customer.class).where().eq("email", email).findList().isEmpty())
        {
            // error message
            context.getFlashCookie().error(msg.get("errorAccountExist", language).get());
            return Results.redirect(context.getContextPath() + "/changeNameOrEmail");
        }
        customer.setName(name);
        customer.setFirstName(firstName);
        customer.setEmail(email);
        customer.update();
        // success message
        context.getFlashCookie().success(msg.get("successUpdate", language).get());
        return Results.redirect(context.getContextPath() + "/settingOverview");
    }

    /**
     * Returns a page to update the current password of a customer.
     * 
     * @param customerId
     * @param context
     * @return
     */
    public Result changePassword(Context context)
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
    @FilterWith(SessionCustomerFilter.class)
    public Result changePasswordCompleted(@Param("oldPassword") String oldPassword, @Param("password") String password,
                                          @Param("passwordAgain") String passwordAgain, Context context)
    {
        boolean failure = false;
        Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
        // incorrect password
        if (!customer.checkPasswd(oldPassword))
        {
            // error message
            context.getFlashCookie().error(msg.get("errorIncorrectPassword", language).get());
            failure = true;
        }
        // passwords don't match
        else if (!password.equals(passwordAgain))
        {
            // error message
            context.getFlashCookie().error(msg.get("errorPasswordMatch", language).get());
            failure = true;
        }
        if (failure)
        {
            // show page to change password again
            return Results.redirect(context.getContextPath() + "/changePassword");
        }
        else
        {
            customer.hashPasswd(password);
            customer.update();
            // success message
            context.getFlashCookie().success(msg.get("successUpdate", language).get());
            return Results.redirect(context.getContextPath() + "/settingOverview");
        }
    }

    /**
     * Returns the page to confirm the deletion of customer's account.
     * 
     * @param context
     * @return
     */
    public Result confirmDeleteAccount(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }

    /**
     * Deletes the current customer and all its addresses, orders and payment information.
     * 
     * @param customerId
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerFilter.class)
    public Result deleteAccount(@Param("password") String password, Context context)
    {
        Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
        // correct password
        if (customer.checkPasswd(password))
        {
            // remove customer from session
            SessionHandling.removeCustomerId(context);
            // remove basket from session
            SessionHandling.removeCartId(context);
            // remove customer's basket
            Cart basket = Ebean.find(Cart.class).where().eq("customer", customer).findUnique();
            if (basket != null)
            {
                basket.setCustomer(null);
                basket.update();
            }
            // remove customers orders --> deletes also customer --> deletes also addresses and payment information
            List<Order> orders = customer.getOrder();
            for (Order order : orders)
            {
                Ebean.delete(order);
            }
            // delete customer, if customer has no order
            if (orders.isEmpty())
            {
                Ebean.delete(customer);
            }
            context.getFlashCookie().success(msg.get("successDeleteAccount", language).get());
            // return home page
            return Results.redirect(context.getContextPath() + "/");
        }
        // incorrect password
        else
        {
            context.getFlashCookie().error(msg.get("errorIncorrectPassword", language).get());
            // show page again
            return Results.redirect(context.getContextPath() + "/confirmDeleteAccount");
        }
    }

    private static void mergeCurrentCartAndCustomerCart(Context context)
    {
        if (SessionHandling.isCustomerLogged(context))
        {
            // get current basket
            Cart currentCart = Cart.getCartById(SessionHandling.getCartId(context));
            // get basket of customer
            Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
            if (customer.getCart() == null)
            {
                customer.setCart(new Cart());
                customer.update();
            }
            Cart customerCart = Cart.getCartById(customer.getCart().getId());
            for (CartProduct cartProduct : currentCart.getProducts())
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
