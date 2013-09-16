package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import models.BillingAddress;
import models.CreditCard;
import models.Customer;
import models.DeliveryAddress;
import models.Order;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.params.Param;
import util.database.AddressInformation;
import util.database.CarouselInformation;
import util.database.CommonInformation;
import util.database.CreditCardInformation;
import util.database.CustomerInformation;
import util.database.OrderInformation;
import util.session.SessionHandling;

import com.avaje.ebean.Ebean;
import com.google.common.base.Optional;
import com.google.inject.Inject;

import conf.XCPosterConf;

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
        CommonInformation.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }

    /**
     * Logs in to the system with email and password. Returns the home page, if the email and the password are correct,
     * otherwise an error page.
     * 
     * @param email
     * @param password
     * @param context
     * @return
     */
    public Result login(@Param("email") String email, @Param("password") String password, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        // is email valid
        if (!Pattern.matches(xcpConf.regexEmail, email))
        {
            // error message
            data.put("errorMessage", msg.get("errorValidEmail", language).get());
        }
        else
        {
            // exists the given email in the database
            boolean emailExist = CustomerInformation.emailExist(email);
            // is the password correct
            boolean correctPassowrd = CustomerInformation.correctPassword(email, password);
            // email and password are correct
            if (emailExist && correctPassowrd)
            {
                // get customer by the given email
                Customer customer = CustomerInformation.getCustomerByEmail(email);
                // put customer id to session
                SessionHandling.setCustomerId(context, customer.getId());
                // add products of current basket to customer's basket
                CustomerInformation.mergeCurrentBasketAndCustomerBasket(context);
                // delete current basket
                SessionHandling.deleteBasketId(context);
                // put customer's basket id to session
                Customer updatedCustomer = CustomerInformation.getCustomerByEmail(email);
                SessionHandling.setBasketId(context, updatedCustomer.getBasket().getId());
            }
            // user exist, wrong password
            else if (emailExist && !correctPassowrd)
            {
                // error message
                data.put("errorMessage", msg.get("errorIncorrectPassword", language).get());
            }
            // wrong email
            else
            {
                // error message
                data.put("errorMessage", msg.get("errorEmailExist", language).get());
            }
        }
        // put products for carousel to data map
        CarouselInformation.getCarouselProducts(data);
        CommonInformation.setCommonData(data, context, xcpConf);
        return Results.html().render(data).template(xcpConf.templateIndex);
    }

    /**
     * Logs off from the system. Returns the home page.
     * 
     * @param context
     * @return
     */
    public Result logout(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        // remove customer from session
        SessionHandling.deleteCustomerId(context);
        // remove basket from session
        SessionHandling.deleteBasketId(context);
        CommonInformation.setCommonData(data, context, xcpConf);
        // put products for carousel to data map
        CarouselInformation.getCarouselProducts(data);

        return Results.html().render(data).template(xcpConf.templateIndex);
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
        CommonInformation.setCommonData(data, context, xcpConf);
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
        final Map<String, Object> data = new HashMap<String, Object>();
        String template;
        boolean failure = false;
        // email must be unique
        if (!Ebean.find(Customer.class).where().eq("email", email).findList().isEmpty())
        {
            // error message
            data.put("errorMessage", msg.get("errorAccountExist", language).get());
            failure = true;
        }
        // is email valid
        else if (!Pattern.matches(xcpConf.regexEmail, email))
        {
            // error message
            data.put("errorMessage", msg.get("errorValidEmail", language).get());
            failure = true;
        }
        // passwords don't match
        else if (!password.equals(passwordAgain))
        {
            // error message
            data.put("errorMessage", msg.get("errorPasswordMatch", language).get());
            failure = true;
        }
        if (failure)
        {
            Map<String, String> registration = new HashMap<String, String>();
            registration.put("name", name);
            registration.put("firstName", firstName);
            registration.put("email", email);
            data.put("registration", registration);
            // show registration page again
            template = xcpConf.templateRegistration;
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
            // put customer id to session
            SessionHandling.setCustomerId(context, customer.getId());
            // put products for carousel to data map
            CarouselInformation.getCarouselProducts(data);
            template = xcpConf.templateIndex;
        }
        CommonInformation.setCommonData(data, context, xcpConf);
        return Results.html().render(data).template(template);
    }

    /**
     * Returns an overview page of the account of the customer.
     * 
     * @param context
     * @return
     */
    public Result accountOverview(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        String template = xcpConf.templateAccountOverview;
        CommonInformation.setCommonData(data, context, xcpConf);
        // no customer is logged
        if (!SessionHandling.isCustomerLogged(context))
        {
            // error message
            data.put("errorMessage", msg.get("errorNoLoggedCustomer", language).get());
            CarouselInformation.getCarouselProducts(data);
            template = xcpConf.templateIndex;
        }
        return Results.html().render(data).template(template);
    }

    /**
     * Returns an overview page of completed orders of the customer.
     * 
     * @param context
     * @return
     */
    public Result orderOverview(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        String template;
        CommonInformation.setCommonData(data, context, xcpConf);
        // no customer is logged
        if (!SessionHandling.isCustomerLogged(context))
        {
            // error message
            data.put("errorMessage", msg.get("errorNoLoggedCustomer", language).get());
            CarouselInformation.getCarouselProducts(data);
            template = xcpConf.templateIndex;
        }
        else
        {
            CustomerInformation.addOrderOfCustomerToMap(context, data);
            template = xcpConf.templateOrderOverview;
        }
        return Results.html().render(data).template(template);
    }

    /**
     * Returns an overview page of payment information of the customer.
     * 
     * @param context
     * @return
     */
    public Result paymentOverview(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        String template;
        CommonInformation.setCommonData(data, context, xcpConf);
        // no customer is logged
        if (!SessionHandling.isCustomerLogged(context))
        {
            // error message
            data.put("errorMessage", msg.get("errorNoLoggedCustomer", language).get());
            CarouselInformation.getCarouselProducts(data);
            template = xcpConf.templateIndex;
        }
        else
        {
            CustomerInformation.addPaymentOfCustomerToMap(context, data);
            template = xcpConf.templatePaymentOverview;
        }
        return Results.html().render(data).template(template);
    }

    /**
     * Returns an overview page of account settings of the customer.
     * 
     * @param context
     * @return
     */
    public Result settingOverview(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        String template;
        CommonInformation.setCommonData(data, context, xcpConf);
        // no customer is logged
        if (!SessionHandling.isCustomerLogged(context))
        {
            // error message
            data.put("errorMessage", msg.get("errorNoLoggedCustomer", language).get());
            CarouselInformation.getCarouselProducts(data);
            template = xcpConf.templateIndex;
        }
        else
        {
            CustomerInformation.addCustomerToMap(context, data);
            template = xcpConf.templateSettingOverview;
        }
        return Results.html().render(data).template(template);
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
    public Result addPaymentToCustomerCompleted(@Param("creditCardNumber") String creditNumber,
                                                @Param("name") String name, @Param("expirationDateMonth") int month,
                                                @Param("expirationDateYear") int year, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        CommonInformation.setCommonData(data, context, xcpConf);
        String template = "";
        // no customer is logged
        if (!SessionHandling.isCustomerLogged(context))
        {
            // error message
            data.put("errorMessage", msg.get("errorNoLoggedCustomer", language).get());
            CarouselInformation.getCarouselProducts(data);
            template = xcpConf.templateIndex;
        }
        else
        {
            // replace spaces and dashes
            creditNumber = creditNumber.replaceAll("[ -]+", "");
            boolean wrongCreditCard = true;
            // check input
            for (String regExCreditCard : xcpConf.regexCreditCard)
            {
                // credit card number is correct
                if (Pattern.matches(regExCreditCard, creditNumber))
                {
                    wrongCreditCard = false;
                    // get customer by session id
                    Customer customer = CustomerInformation.getCustomerById(SessionHandling.getCustomerId(context));
                    // create new credit card
                    CreditCard creditCard = new CreditCard();
                    creditCard.setCardNumber(creditNumber);
                    creditCard.setName(name);
                    creditCard.setMonth(month);
                    creditCard.setYear(year);
                    // add credit card to customer
                    customer.addCreditCard(creditCard);
                    // update customer
                    customer.update();
                    // success message
                    data.put("successMessage", msg.get("successSave", language).get());
                    template = xcpConf.templateAccountOverview;
                }
            }
            if (wrongCreditCard)
            {
                // error message
                data.put("errorMessage", msg.get("errorWrongCreditCard", language).get());
                // show inserted values in form
                Map<String, String> card = new HashMap<String, String>();
                card.put("name", name);
                card.put("cardNumber", creditNumber);
                data.put("card", card);
                // show page to enter payment information again
                template = xcpConf.templateAddPaymentToCustomer;
            }
        }
        return Results.html().render(data).template(template);
    }

    /**
     * Returns a page to enter payment information.
     * 
     * @param context
     * @return
     */
    public Result addPaymentToCustomer(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        CommonInformation.setCommonData(data, context, xcpConf);
        String template;
        // no customer is logged
        if (!SessionHandling.isCustomerLogged(context))
        {
            // error message
            data.put("errorMessage", msg.get("errorNoLoggedCustomer", language).get());
            CarouselInformation.getCarouselProducts(data);
            template = xcpConf.templateIndex;
        }
        else
        {
            template = xcpConf.templateAddPaymentToCustomer;
        }
        return Results.html().render(data).template(template);
    }

    /**
     * Removes a payment information of the customer.
     * 
     * @param cardId
     * @param context
     * @return
     */
    public Result deletePayment(@Param("password") String password, @Param("cardId") int cardId, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        String template;
        // no customer is logged
        if (!SessionHandling.isCustomerLogged(context))
        {
            // error message
            data.put("errorMessage", msg.get("errorNoLoggedCustomer", language).get());
            CarouselInformation.getCarouselProducts(data);
            template = xcpConf.templateIndex;
        }
        else
        {
            Customer customer = CustomerInformation.getCustomerById(SessionHandling.getCustomerId(context));
            // correct password
            if (customer.checkPasswd(password))
            {
                CreditCardInformation.deleteCreditCardFromCustomer(cardId);
                template = xcpConf.templateAccountOverview;
                // success message
                data.put("successMessage", msg.get("successDelete", language).get());
            }
            // incorrect password
            else
            {
                data.put("errorMessage", msg.get("errorIncorrectPassword", language).get());
                template = xcpConf.templateConfirmDeletePayment;
                data.put("cardId", cardId);
            }
        }
        CommonInformation.setCommonData(data, context, xcpConf);
        return Results.html().render(data).template(template);
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
        CommonInformation.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }

    /**
     * Returns an overview page of billing and delivery addresses of the customer.
     * 
     * @param context
     * @return
     */
    public Result addressOverview(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        String template;
        // no customer is logged
        if (!SessionHandling.isCustomerLogged(context))
        {
            // error message
            data.put("errorMessage", msg.get("errorNoLoggedCustomer", language).get());
            CarouselInformation.getCarouselProducts(data);
            template = xcpConf.templateIndex;
        }
        else
        {
            CustomerInformation.addAddressOfCustomerToMap(context, data);
            template = xcpConf.templateAddressOverview;
        }
        CommonInformation.setCommonData(data, context, xcpConf);
        return Results.html().render(data).template(template);
    }

    /**
     * Removes a billing address of the customer.
     * 
     * @param cardId
     * @param context
     * @return
     */
    public Result deleteBillingAddress(@Param("password") String password, @Param("addressId") int addressId,
                                       Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        String template;
        // no customer is logged
        if (!SessionHandling.isCustomerLogged(context))
        {
            // error message
            data.put("errorMessage", msg.get("errorNoLoggedCustomer", language).get());
            CarouselInformation.getCarouselProducts(data);
            template = xcpConf.templateIndex;
        }
        else
        {
            Customer customer = CustomerInformation.getCustomerById(SessionHandling.getCustomerId(context));
            // correct password
            if (customer.checkPasswd(password))
            {
                // remove billing address
                AddressInformation.deleteBillingAddressFromCustomer(addressId);
                // success message
                data.put("successMessage", msg.get("successDelete", language).get());
                template = xcpConf.templateAccountOverview;
            }
            // incorrect password
            else
            {
                data.put("errorMessage", msg.get("errorIncorrectPassword", language).get());
                template = xcpConf.templateConfirmDeleteAddress;
                data.put("deleteAddressURL", "deleteBillingAddress");
                data.put("addressId", addressId);
            }
        }
        CommonInformation.setCommonData(data, context, xcpConf);
        return Results.html().render(data).template(template);
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
        CommonInformation.setCommonData(data, context, xcpConf);
        return Results.html().render(data).template(xcpConf.templateConfirmDeleteAddress);
    }

    /**
     * Removes a delivery address of the customer.
     * 
     * @param cardId
     * @param context
     * @return
     */
    public Result deleteDeliveryAddress(@Param("password") String password, @Param("addressId") int addressId,
                                        Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        String template;
        // no customer is logged
        if (!SessionHandling.isCustomerLogged(context))
        {
            // error message
            data.put("errorMessage", msg.get("errorNoLoggedCustomer", language).get());
            CarouselInformation.getCarouselProducts(data);
            template = xcpConf.templateIndex;
        }
        else
        {
            Customer customer = CustomerInformation.getCustomerById(SessionHandling.getCustomerId(context));
            // correct password
            if (customer.checkPasswd(password))
            {
                // remove billing address
                AddressInformation.deleteDeliveryAddressFromCustomer(addressId);
                // success message
                data.put("successMessage", msg.get("successDelete", language).get());
                template = xcpConf.templateAccountOverview;
            }
            // incorrect password
            else
            {
                data.put("errorMessage", msg.get("errorIncorrectPassword", language).get());
                template = xcpConf.templateConfirmDeleteAddress;
                data.put("deleteAddressURL", "deleteDeliveryAddress");
                data.put("addressId", addressId);
            }
        }
        CommonInformation.setCommonData(data, context, xcpConf);
        return Results.html().render(data).template(template);
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
        CommonInformation.setCommonData(data, context, xcpConf);
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
        data.put("address", AddressInformation.getDeliveryAddressById(addressId));
        CommonInformation.setCommonData(data, context, xcpConf);
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
        final Map<String, Object> data = new HashMap<String, Object>();
        CommonInformation.setCommonData(data, context, xcpConf);
        String template;
        // check input
        if (!Pattern.matches(xcpConf.regexZip, zip))
        {
            // error message
            data.put("errorMessage", msg.get("errorWrongZip", language).get());
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
            template = xcpConf.templateUpdateDeliveryAddress;
        }
        // all input fields might be correct
        else
        {
            DeliveryAddress address = AddressInformation.getDeliveryAddressById(Integer.parseInt(addressId));
            address.setName(name);
            address.setCompany(company);
            address.setAddressLine(addressLine);
            address.setCity(city);
            address.setState(state);
            address.setZip(zip);
            address.setCountry(country);
            address.update();
            // success message
            data.put("successMessage", msg.get("successUpdate", language).get());
            template = xcpConf.templateAccountOverview;
        }
        return Results.html().render(data).template(template);
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
        data.put("address", AddressInformation.getBillingAddressById(addressId));
        CommonInformation.setCommonData(data, context, xcpConf);
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
        final Map<String, Object> data = new HashMap<String, Object>();
        CommonInformation.setCommonData(data, context, xcpConf);
        String template;
        // check input
        if (!Pattern.matches(xcpConf.regexZip, zip))
        {
            // error message
            data.put("errorMessage", msg.get("errorWrongZip", language).get());
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
            template = xcpConf.templateUpdateBillingAddress;
        }
        // all input fields might be correct
        else
        {
            BillingAddress address = AddressInformation.getBillingAddressById(Integer.parseInt(addressId));

            address.setName(name);
            address.setCompany(company);
            address.setAddressLine(addressLine);
            address.setCity(city);
            address.setState(state);
            address.setZip(zip);
            address.setCountry(country);
            address.update();
            // success message
            data.put("successMessage", msg.get("successUpdate", language).get());
            template = xcpConf.templateAccountOverview;
        }
        return Results.html().render(data).template(template);
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
        CommonInformation.setCommonData(data, context, xcpConf);
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
        CommonInformation.setCommonData(data, context, xcpConf);
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
    public Result addDeliveryAddressToCustomerCompleted(@Param("fullName") String name,
                                                        @Param("company") String company,
                                                        @Param("addressLine") String addressLine,
                                                        @Param("city") String city, @Param("state") String state,
                                                        @Param("zip") String zip, @Param("country") String country,
                                                        @Param("addressId") String addressId, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        CommonInformation.setCommonData(data, context, xcpConf);
        String template;
        // no customer is logged
        if (!SessionHandling.isCustomerLogged(context))
        {
            // error message
            data.put("errorMessage", msg.get("errorNoLoggedCustomer", language).get());
            CarouselInformation.getCarouselProducts(data);
            template = xcpConf.templateIndex;
        }
        else
        {
            // check input
            if (!Pattern.matches(xcpConf.regexZip, zip))
            {
                // error message
                data.put("errorMessage", msg.get("errorWrongZip", language).get());
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
                template = xcpConf.templateAddDeliveryAddressToCustomer;
            }
            // all input fields might be correct
            else
            {
                DeliveryAddress address = new DeliveryAddress();

                address.setName(name);
                address.setCompany(company);
                address.setAddressLine(addressLine);
                address.setCity(city);
                address.setState(state);
                address.setZip(zip);
                address.setCountry(country);
                // add address to customer
                Customer customer = CustomerInformation.getCustomerById(SessionHandling.getCustomerId(context));
                customer.addDeliveryAddress(address);
                customer.update();
                // success message
                data.put("successMessage", msg.get("successSave", language).get());
                template = xcpConf.templateAccountOverview;
            }
        }
        return Results.html().render(data).template(template);
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
    public Result addBillingAddressToCustomerCompleted(@Param("fullName") String name,
                                                       @Param("company") String company,
                                                       @Param("addressLine") String addressLine,
                                                       @Param("city") String city, @Param("state") String state,
                                                       @Param("zip") String zip, @Param("country") String country,
                                                       @Param("addressId") String addressId, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        CommonInformation.setCommonData(data, context, xcpConf);
        String template;
        // no customer is logged
        if (!SessionHandling.isCustomerLogged(context))
        {
            // error message
            data.put("errorMessage", msg.get("errorNoLoggedCustomer", language).get());
            CarouselInformation.getCarouselProducts(data);
            template = xcpConf.templateIndex;
        }
        else
        {
            // check input
            if (!Pattern.matches("[0-9]*", zip))
            {
                // error message
                data.put("errorMessage", msg.get("errorWrongZip", language).get());
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
                template = xcpConf.templateAddBillingAddressToCustomer;
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
                Customer customer = CustomerInformation.getCustomerById(SessionHandling.getCustomerId(context));
                customer.addBillingAddress(address);
                customer.update();
                // success message
                data.put("successMessage", msg.get("successSave", language).get());
                template = xcpConf.templateAccountOverview;
            }
        }
        return Results.html().render(data).template(template);
    }

    /**
     * Returns a page to update the name and the email address of a customer.
     * 
     * @param customerId
     * @param context
     * @return
     */
    public Result changeNameOrEmail(@Param("customerId") String customerId, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        CommonInformation.setCommonData(data, context, xcpConf);
        Customer customer = CustomerInformation.getCustomerById(Integer.parseInt(customerId));
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
    public Result changeNameOrEmailCompleted(@Param("name") String name, @Param("firstName") String firstName,
                                             @Param("eMail") String email, @Param("password") String password,
                                             Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        String template;
        // no customer is logged
        if (!SessionHandling.isCustomerLogged(context))
        {
            // error message
            data.put("errorMessage", msg.get("errorNoLoggedCustomer", language).get());
            CarouselInformation.getCarouselProducts(data);
            template = xcpConf.templateIndex;
        }
        else
        {
            boolean failure = false;
            Customer customer = CustomerInformation.getCustomerById(SessionHandling.getCustomerId(context));
            // incorrect password
            if (!CustomerInformation.correctPassword(customer.getEmail(), password))
            {
                // error message
                data.put("errorMessage", msg.get("errorIncorrectPassword", language).get());
                failure = true;
            }
            else if (!Pattern.matches(xcpConf.regexEmail, email))
            {
                // error message
                data.put("errorMessage", msg.get("errorValidEmail", language).get());
                failure = true;
            }
            if (failure)
            {
                Map<String, String> updatedCustomer = new HashMap<String, String>();
                updatedCustomer.put("name", name);
                updatedCustomer.put("firstName", firstName);
                updatedCustomer.put("email", email);
                data.put("customer", customer);
                // show page to update name or email again
                template = xcpConf.templateChangeNameOrEmail;
            }
            else
            {
                customer.setName(name);
                customer.setFirstName(firstName);
                customer.setEmail(email);
                customer.update();
                // success message
                data.put("successMessage", msg.get("successUpdate", language).get());
                template = xcpConf.templateAccountOverview;
            }
        }
        CommonInformation.setCommonData(data, context, xcpConf);
        return Results.html().render(data).template(template);
    }

    /**
     * Returns a page to update the current password of a customer.
     * 
     * @param customerId
     * @param context
     * @return
     */
    public Result changePassword(@Param("customerId") String customerId, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        CommonInformation.setCommonData(data, context, xcpConf);
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
    public Result changePasswordCompleted(@Param("oldPassword") String oldPassword, @Param("password") String password,
                                          @Param("passwordAgain") String passwordAgain, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        CommonInformation.setCommonData(data, context, xcpConf);
        String template;
        // no customer is logged
        if (!SessionHandling.isCustomerLogged(context))
        {
            // error message
            data.put("errorMessage", msg.get("errorNoLoggedCustomer", language).get());
            CarouselInformation.getCarouselProducts(data);
            template = xcpConf.templateIndex;
        }
        else
        {
            boolean failure = false;
            Customer customer = CustomerInformation.getCustomerById(SessionHandling.getCustomerId(context));
            // incorrect password
            if (!CustomerInformation.correctPassword(customer.getEmail(), oldPassword))
            {
                // error message
                data.put("errorMessage", msg.get("errorIncorrectPassword", language).get());
                failure = true;
            }
            // passwords don't match
            else if (!password.equals(passwordAgain))
            {
                // error message
                data.put("errorMessage", msg.get("errorPasswordMatch", language).get());
                failure = true;
            }
            if (failure)
            {
                // show page to change password again
                template = xcpConf.templateChangePassword;
            }
            else
            {
                customer.hashPasswd(password);
                customer.update();
                // success message
                data.put("successMessage", msg.get("successUpdate", language).get());
                template = xcpConf.templateAccountOverview;
            }
        }
        return Results.html().render(data).template(template);
    }

    /**
     * Deletes the current customer and all its addresses, orders and payment information.
     * 
     * @param customerId
     * @param context
     * @return
     */
    public Result deleteAccount(@Param("password") String password, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        String template;
        // no customer is logged
        if (!SessionHandling.isCustomerLogged(context))
        {
            // error message
            data.put("errorMessage", msg.get("errorNoLoggedCustomer", language).get());
            CarouselInformation.getCarouselProducts(data);
            template = xcpConf.templateIndex;
        }
        else
        {
            Customer customer = CustomerInformation.getCustomerById(SessionHandling.getCustomerId(context));
            // correct password
            if (customer.checkPasswd(password))
            {
                // remove customer from session
                SessionHandling.deleteCustomerId(context);
                // remove basket from session
                SessionHandling.deleteBasketId(context);
                // remove customers orders, deletes also customer, deletes also addresses and payment information
                List<Order> orders = OrderInformation.getAllOrdersOfCustomer(customer);
                for (Order order : orders)
                {
                    Ebean.delete(order);
                }
                // delete customer, if customer has no order
                if (orders.isEmpty())
                {
                    Ebean.delete(customer);
                }
                // put products for carousel to data map
                CarouselInformation.getCarouselProducts(data);
                template = xcpConf.templateIndex;
                data.put("successMessage", msg.get("successDeleteAccount", language).get());
            }
            // incorrect password
            else
            {
                data.put("errorMessage", msg.get("errorIncorrectPassword", language).get());
                template = xcpConf.templateConfirmDeleteAccount;
            }
        }
        CommonInformation.setCommonData(data, context, xcpConf);
        return Results.html().render(data).template(template);
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
        CommonInformation.setCommonData(data, context, xcpConf);
        return Results.html().render(data);
    }
}
