package controllers;

import java.util.HashMap;
import java.util.Map;

import com.avaje.ebean.Ebean;

import models.BillingAddress;
import models.CreditCard;
import models.Customer;
import models.DeliveryAddress;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import util.database.AddressInformation;
import util.database.CarouselInformation;
import util.database.CommonInformation;
import util.database.CreditCardInformation;
import util.database.CustomerInformation;
import util.session.SessionHandling;

public class CustomerController
{

    /**
     * Logs on to the system with email and password. Returns the home page, if the email and the password are correct,
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
        // exists the given email in the database
        boolean emailExist = CustomerInformation.emailExist(email);
        // is the password correct
        boolean correctPassowrd = CustomerInformation.correctPassword(email, password);
        String template;
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
            SessionHandling.setBasketId(context, customer.getBasket().getId());
            // put products for carousel to data map
            CarouselInformation.getCarouselProducts(data);
            // return home page
            template = "views/WebShopController/index.ftl.html";
        }
        // user exist, wrong password
        else if (emailExist && !correctPassowrd)
        {
            // show error page
            template = "views/error/mainError.ftl.html";
        }
        // wrong email
        else
        {
            // show error page
            template = "views/error/mainError.ftl.html";
        }

        CommonInformation.setCommonData(data, context);

        return Results.html().render(data).template(template);
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

        CommonInformation.setCommonData(data, context);
        // put products for carousel to data map
        CarouselInformation.getCarouselProducts(data);

        return Results.html().render(data).template("views/WebShopController/index.ftl.html");
    }

    /**
     * Starts a registration to get an account. Returns the page to enter account information.
     * 
     * @param context
     * @return
     */
    public Result registration(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();

        CommonInformation.setCommonData(data, context);

        return Results.html().render(data);
    }

    /**
     * Creates a new Customer.
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
        if (password.equals(passwordAgain))
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
            template = "views/CustomerController/registrationCompleted.ftl.html";
        }
        else
        {
            template = "views/error/mainError.ftl.html";
        }
        CommonInformation.setCommonData(data, context);
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

        CommonInformation.setCommonData(data, context);
        return Results.html().render(data);
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

        CustomerInformation.addOrderOfCustomerToMap(context, data);

        CommonInformation.setCommonData(data, context);
        return Results.html().render(data);
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

        CustomerInformation.addPaymentOfCustomerToMap(context, data);

        CommonInformation.setCommonData(data, context);
        return Results.html().render(data);
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

        CustomerInformation.addCustomerToMap(context, data);

        CommonInformation.setCommonData(data, context);
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
    public Result addPaymentToCustomerCompleted(@Param("creditCardNumber") int creditNumber,
                                                @Param("name") String name, @Param("expirationDateMonth") int month,
                                                @Param("expirationDateYear") int year, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();

        CommonInformation.setCommonData(data, context);

        // get customer by session id
        Customer customer = CustomerInformation.getCustomerById(SessionHandling.getCustomerId(context));
        // create new credit card
        CreditCard creditCard = new CreditCard();
        creditCard.setNumber(creditNumber);
        creditCard.setName(name);
        creditCard.setMonth(month);
        creditCard.setYear(year);
        // add credit card to customer
        customer.addCreditCard(creditCard);
        // update customer
        Ebean.save(customer);
        // return info page
        return Results.html().render(data).template("views/info/savingComplete.ftl.html");
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

        CommonInformation.setCommonData(data, context);

        return Results.html().render(data);
    }

    /**
     * Removes a payment information of the customer.
     * 
     * @param cardId
     * @param context
     * @return
     */
    public Result deletePayment(@Param("cardId") int cardId, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();

        CreditCardInformation.deleteCreditCardFromCustomer(cardId);

        CommonInformation.setCommonData(data, context);
        // return info page
        return Results.html().render(data).template("views/info/savingComplete.ftl.html");
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

        CustomerInformation.addAddressOfCustomerToMap(context, data);

        CommonInformation.setCommonData(data, context);
        return Results.html().render(data);
    }

    /**
     * Removes a billing address of the customer.
     * 
     * @param cardId
     * @param context
     * @return
     */
    public Result deleteBillingAddress(@Param("addressId") int addressId, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();

        // remove billing address
        AddressInformation.deleteBillingAddressFromCustomer(addressId);

        CommonInformation.setCommonData(data, context);
        // return info page
        return Results.html().render(data).template("views/info/savingComplete.ftl.html");
    }

    /**
     * Removes a delivery address of the customer.
     * 
     * @param cardId
     * @param context
     * @return
     */
    public Result deleteDeliveryAddress(@Param("addressId") int addressId, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();

        // remove delivery address
        AddressInformation.deleteDeliveryAddressFromCustomer(addressId);

        CommonInformation.setCommonData(data, context);
        // return info page
        return Results.html().render(data).template("views/info/savingComplete.ftl.html");
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

        AddressInformation.addDeliveryAddressToMap(AddressInformation.getDeliveryAddressById(addressId), data);

        CommonInformation.setCommonData(data, context);
        // return info page
        return Results.html().render(data);
    }

    /**
     * Updates a delivery address of the customer.
     * 
     * @param name
     * @param addressLine1
     * @param addressLine2
     * @param city
     * @param state
     * @param zip
     * @param country
     * @param addressId
     * @param context
     * @return
     */
    public Result updateDeliveryAddressCompleted(@Param("fullName") String name,
                                                 @Param("addressLine1") String addressLine1,
                                                 @Param("addressLine2") String addressLine2,
                                                 @Param("city") String city, @Param("state") String state,
                                                 @Param("zip") String zip, @Param("country") String country,
                                                 @Param("addressId") String addressId, Context context)
    {

        final Map<String, Object> data = new HashMap<String, Object>();

        CommonInformation.setCommonData(data, context);

        DeliveryAddress address = AddressInformation.getDeliveryAddressById(Integer.parseInt(addressId));

        address.setName(name);
        address.setAddressline1(addressLine1);
        address.setAddressline2(addressLine2);
        address.setCity(city);
        address.setState(state);
        address.setZip(Integer.parseInt(zip));
        address.setCountry(country);

        address.update();

        // return info page
        return Results.html().render(data).template("views/info/savingComplete.ftl.html");
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

        AddressInformation.addBillingAddressToMap(AddressInformation.getBillingAddressById(addressId), data);

        CommonInformation.setCommonData(data, context);
        // return info page
        return Results.html().render(data);
    }

    /**
     * Updates a billing address of the customer.
     * 
     * @param name
     * @param addressLine1
     * @param addressLine2
     * @param city
     * @param state
     * @param zip
     * @param country
     * @param addressId
     * @param context
     * @return
     */
    public Result updateBillingAddressCompleted(@Param("fullName") String name,
                                                @Param("addressLine1") String addressLine1,
                                                @Param("addressLine2") String addressLine2, @Param("city") String city,
                                                @Param("state") String state, @Param("zip") String zip,
                                                @Param("country") String country, @Param("addressId") String addressId,
                                                Context context)
    {

        final Map<String, Object> data = new HashMap<String, Object>();

        CommonInformation.setCommonData(data, context);

        BillingAddress address = AddressInformation.getBillingAddressById(Integer.parseInt(addressId));

        address.setName(name);
        address.setAddressline1(addressLine1);
        address.setAddressline2(addressLine2);
        address.setCity(city);
        address.setState(state);
        address.setZip(Integer.parseInt(zip));
        address.setCountry(country);

        address.update();

        // return info page
        return Results.html().render(data).template("views/info/savingComplete.ftl.html");
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

        CommonInformation.setCommonData(data, context);

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

        CommonInformation.setCommonData(data, context);

        return Results.html().render(data);
    }

    public Result addDeliveryAddressToCustomerCompleted(@Param("fullName") String name,
                                                        @Param("addressLine1") String addressLine1,
                                                        @Param("addressLine2") String addressLine2,
                                                        @Param("city") String city, @Param("state") String state,
                                                        @Param("zip") String zip, @Param("country") String country,
                                                        @Param("addressId") String addressId, Context context)
    {

        final Map<String, Object> data = new HashMap<String, Object>();

        CommonInformation.setCommonData(data, context);

        DeliveryAddress address = new DeliveryAddress();

        address.setName(name);
        address.setAddressline1(addressLine1);
        address.setAddressline2(addressLine2);
        address.setCity(city);
        address.setState(state);
        address.setZip(Integer.parseInt(zip));
        address.setCountry(country);
        // add address to customer
        Customer customer = CustomerInformation.getCustomerById(SessionHandling.getCustomerId(context));
        customer.addDeliveryAddress(address);
        customer.update();

        // return info page
        return Results.html().render(data).template("views/info/savingComplete.ftl.html");
    }

    public Result addBillingAddressToCustomerCompleted(@Param("fullName") String name,
                                                       @Param("addressLine1") String addressLine1,
                                                       @Param("addressLine2") String addressLine2,
                                                       @Param("city") String city, @Param("state") String state,
                                                       @Param("zip") String zip, @Param("country") String country,
                                                       @Param("addressId") String addressId, Context context)
    {

        final Map<String, Object> data = new HashMap<String, Object>();

        CommonInformation.setCommonData(data, context);

        BillingAddress address = new BillingAddress();

        address.setName(name);
        address.setAddressline1(addressLine1);
        address.setAddressline2(addressLine2);
        address.setCity(city);
        address.setState(state);
        address.setZip(Integer.parseInt(zip));
        address.setCountry(country);
        // add address to customer
        Customer customer = CustomerInformation.getCustomerById(SessionHandling.getCustomerId(context));
        customer.addBillingAddress(address);
        customer.update();

        // return info page
        return Results.html().render(data).template("views/info/savingComplete.ftl.html");
    }
}
