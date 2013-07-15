package util.database;

import java.util.List;
import java.util.Map;

import models.Basket;
import models.Basket_Product;
import models.BillingAddress;
import models.CreditCard;
import models.Customer;
import models.DeliveryAddress;
import ninja.Context;
import util.session.SessionHandling;

import com.avaje.ebean.Ebean;

public abstract class CustomerInformation
{

    /**
     * Returns true, if there is a customer with the given email address, otherwise false.
     * 
     * @param email
     * @return
     */
    public static boolean emailExist(String email)
    {
        boolean exist = true;
        // get a list of customers, which have the given email address
        List<Customer> loginExist = Ebean.find(Customer.class).where().eq("email", email).findList();
        // no customer has this email address
        if (loginExist.size() == 0)
        {
            exist = false;
        }
        // more than one customer has this email address
        else if (loginExist.size() > 1)
        {
            // FAILURE
        }
        return exist;
    }

    /**
     * Returns true, if the given password is equal to the customer's password, otherwise false.
     * 
     * @param email
     * @param password
     * @return
     */
    public static boolean correctPassword(String email, String password)
    {
        boolean correctPassword = false;
        // get customer by email
        Customer customer = Ebean.find(Customer.class).where().eq("email", email).findUnique();
        // customer exist
        if (customer != null)
        {
            // the given password is equal to customer's password
            if (customer.checkPasswd(password))
            {
                correctPassword = true;
            }
        }
        return correctPassword;
    }

    /**
     * Adds the customer's first name to the data map. Adds the information, whether or not a customer is logged.
     * 
     * @param context
     * @param data
     */
    public static void addCustomerFirstNameToMap(Context context, final Map<String, Object> data)
    {
        // a customer is logged
        if (SessionHandling.isCustomerLogged(context))
        {
            // add information that a customer is logged
            data.put("isLogged", true);
            // get customer by session
            Customer customer = getCustomerById(Integer.parseInt(context.getSessionCookie().get("user")));
            // add customer's first name to data map
            data.put("customerFirstName", customer.getFirstName());
        }
        // no customer is logged
        else
        {
            data.put("isLogged", false);
        }
    }

    /**
     * Adds a customer to the data map, if a customer is logged.
     * 
     * @param context
     * @param data
     */
    public static void addCustomerToMap(Context context, final Map<String, Object> data)
    {
        // customer is logged
        if (SessionHandling.isCustomerLogged(context))
        {
            data.put("isLogged", true);
            // get customer by session
            Customer customer = getCustomerById(Integer.parseInt(context.getSessionCookie().get("user")));
            // add customer to data map
            data.put("customer", customer);
        }
        // no customer is logged
        else
        {
            data.put("isLogged", false);
        }
    }

    /**
     * Adds all orders of a customer to the data map.
     * 
     * @param context
     * @param data
     */
    public static void addOrderOfCustomerToMap(Context context, final Map<String, Object> data)
    {
        // get customer by session
        Customer customer = Ebean.find(Customer.class, SessionHandling.getCustomerId(context));
        // add all orders to the data map
        data.put("orderOverview", customer.getOrder());
    }

    /**
     * Returns the customer by the customer's id.
     * 
     * @param customerId
     * @return
     */
    public static Customer getCustomerById(int customerId)
    {
        // get customer by id
        Customer customer = Ebean.find(Customer.class, customerId);
        return customer;
    }

    /**
     * Returns the customer by the customer's email address.
     * 
     * @param email
     * @return
     */
    public static Customer getCustomerByEmail(String email)
    {
        // get customer by email address
        return Ebean.find(Customer.class).where().eq("email", email).findUnique();
    }

    /**
     * Returns all customers, which are stored in the database.
     * 
     * @return
     */
    public static List<Customer> getAllCustomers()
    {
        // get all customers
        return Ebean.find(Customer.class).findList();
    }

    /**
     * Adds all payment methods of a customer to the data map.
     * 
     * @param context
     * @param data
     */
    public static void addPaymentOfCustomerToMap(Context context, Map<String, Object> data)
    {
        // just add, if customer is logged
        if (SessionHandling.isCustomerLogged(context))
        {
            // get customer by session
            Customer customer = Ebean.find(Customer.class, SessionHandling.getCustomerId(context));
            // add payment information
            data.put("paymentOverview", customer.getCreditCard());
        }
    }

    /**
     * Adds all delivery and billing addresses of a customer to the data base.
     * 
     * @param context
     * @param data
     */
    public static void addAddressOfCustomerToMap(Context context, final Map<String, Object> data)
    {
        Customer customer = Ebean.find(Customer.class, SessionHandling.getCustomerId(context));
        // add all delivery addresses
        data.put("deliveryAddresses", customer.getDeliveryAddress());
        // add all billing addresses
        data.put("billingAddresses", customer.getBillingAddress());
    }

    /**
     * Adds the given credit card to the customer.
     * 
     * @param context
     * @param creditCard
     */
    public static void addPaymentToCustomer(Context context, CreditCard creditCard)
    {
        // just add, if customer is logged
        if (SessionHandling.isCustomerLogged(context))
        {
            Customer customer = getCustomerById(SessionHandling.getCustomerId(context));
            customer.addCreditCard(creditCard);
            customer.update();
        }
    }

    public static void addDeliveryAddressToCustomer(Context context, DeliveryAddress deliveryAddress)
    {
        // just add, if customer is logged
        if (SessionHandling.isCustomerLogged(context))
        {
            Customer customer = getCustomerById(SessionHandling.getCustomerId(context));
            customer.addDeliveryAddress(deliveryAddress);
            customer.update();
        }
    }

    public static void addBillingAddressToCustomer(Context context, BillingAddress billingAddress)
    {
        // just add, if customer is logged
        if (SessionHandling.isCustomerLogged(context))
        {
            Customer customer = getCustomerById(SessionHandling.getCustomerId(context));
            customer.addBillingAddress(billingAddress);
            customer.update();
        }
    }

    public static void mergeCurrentBasketAndCustomerBasket(Context context)
    {
        // get current basket
        Basket currentBasket = BasketInformation.getBasketById(SessionHandling.getBasketId(context));
        // get basket of customer
        Customer customer = CustomerInformation.getCustomerById(SessionHandling.getCustomerId(context));
        if (customer.getBasket() == null)
        {
            customer.setBasket(new Basket());
            customer.update();
        }
        Basket customerBasket = BasketInformation.getBasketById(customer.getBasket().getId());
        if (customerBasket == null)
        {
            customerBasket = new Basket();
            customerBasket.save();
        }
        for (Basket_Product basket_Product : currentBasket.getProducts())
        {
            for (int i = 0; i < basket_Product.getCountProduct(); i++)
            {
                customerBasket.addProduct(basket_Product.getProduct());
            }
        }
        customerBasket.update();
    }
}
