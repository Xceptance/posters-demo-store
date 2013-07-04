package controllers;

import java.util.HashMap;
import java.util.Map;

import com.avaje.ebean.Ebean;

import models.CreditCard;
import models.Customer;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
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

        // create new customer
        Customer customer = new Customer();
        customer.setName(name);
        customer.setFirstName(firstName);
        customer.setEmail(email);
        customer.setPassword(password);
        // save customer
        Ebean.save(customer);
        // put customer id to session
        SessionHandling.setCustomerId(context, customer.getId());

        CommonInformation.setCommonData(data, context);

        return Results.html().render(data);
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
        // get customer by session id
        Customer customer = CustomerInformation.getCustomerById(SessionHandling.getCustomerId(context));
        // remove payment information
        customer.deleteCreditCard(CreditCardInformation.getCreditCardById(cardId));
        // update customer
        Ebean.save(customer);

        CommonInformation.setCommonData(data, context);
        // return info page
        return Results.html().render(data).template("views/info/savingComplete.ftl.html");
    }
}
