package util.session;

import java.util.Map;

import models.Cart;
import models.Order;
import ninja.Context;
import ninja.session.SessionCookie;
import util.database.BasketInformation;
import util.database.OrderInformation;

public class SessionHandling
{
    // The keys of session cookie values.
    private static String BASKET = "basket";

    private static String ORDER = "order";

    private static String USER = "user";

    /**
     * Returns the id of the session.
     * 
     * @param context
     * @return
     */
    public static String getId(Context context)
    {
        // get id of the session
        String id = context.getSessionCookie().getId();
        return id;
    }

    /**
     * Returns all data of the session.
     * 
     * @param context
     * @return
     */
    public static Map<String, String> getData(Context context)
    {
        Map<String, String> data = context.getSessionCookie().getData();
        return data;
    }

    /**
     * Returns the authenticity token of the session.
     * 
     * @param context
     * @return
     */
    public static String getToken(Context context)
    {
        return context.getSessionCookie().getAuthenticityToken();
    }

    /**
     * Adds a key-value-pair to the session.
     * 
     * @param context
     * @param key
     * @param value
     */
    public static void put(Context context, String key, String value)
    {
        context.getSessionCookie().put(key, value);
    }

    /**
     * Overrides the user information in the session to guest.
     * 
     * @param context
     */
    public static void deleteCustomerId(Context context)
    {
        SessionCookie cookie = context.getSessionCookie();
        cookie.remove(USER);
    }

    /**
     * Adds an order id to the session.
     * 
     * @param context
     * @param orderId
     */
    public static void setOrderId(Context context, int orderId)
    {
        SessionCookie cookie = context.getSessionCookie();
        if (cookie.get(ORDER) == null)
        {
            cookie.put(ORDER, Integer.toString(orderId));
        }
    }

    /**
     * Returns the order id of the session. Creates a new order, if no is set.
     * 
     * @param context
     * @return
     */
    public static int getOrderId(Context context)
    {
        int orderId;
        SessionCookie cookie = context.getSessionCookie();
        // order id is not set, if the session is terminated
        if (cookie.get(ORDER) == null)
        {
            // create new order
            Order order = OrderInformation.createNewOrder();
            // set id of new order
            setOrderId(context, order.getId());
            // get cookie again
            cookie = context.getSessionCookie();
        }
        orderId = Integer.parseInt(cookie.get(ORDER));
        return orderId;
    }

    /**
     * Deletes the order id from the session.
     * 
     * @param context
     */
    public static void deleteOrderId(Context context)
    {
        SessionCookie cookie = context.getSessionCookie();
        cookie.remove(ORDER);
    }

    /**
     * Adds the basket id to the session.
     * 
     * @param context
     * @param basketId
     */
    public static void setBasketId(Context context, int basketId)
    {
        SessionCookie cookie = context.getSessionCookie();
        if (cookie.get(BASKET) == null)
        {
            cookie.put(BASKET, Integer.toString(basketId));
        }
    }

    /**
     * Returns the basket id of the session. Creates a new basket, if no is set.
     * 
     * @param context
     * @return
     */
    public static int getBasketId(Context context)
    {
        SessionCookie cookie = context.getSessionCookie();
        // create new basket, if no basket is set
        if (cookie.get(BASKET) == null)
        {
            Cart basket = BasketInformation.createNewBasket();
            setBasketId(context, basket.getId());
            cookie = context.getSessionCookie();
        }
        return Integer.parseInt(cookie.get(BASKET));
    }

    /**
     * Deletes the basket id from the session.
     * 
     * @param context
     */
    public static void deleteBasketId(Context context)
    {
        SessionCookie cookie = context.getSessionCookie();
        cookie.remove(BASKET);
    }

    /**
     * Returns session information by key.
     * 
     * @param context
     * @param key
     * @return
     */
    public static String get(Context context, String key)
    {
        return context.getSessionCookie().get(key);
    }

    /**
     * Returns true, if a customer is logged, otherwise false.
     * 
     * @param context
     * @return
     */
    public static boolean isCustomerLogged(Context context)
    {
        boolean isLogged = true;
        SessionCookie cookie = context.getSessionCookie();
        if (cookie.get(USER) == null)
        {
            isLogged = false;
        }
        return isLogged;
    }

    /**
     * Adds customer id to the session.
     * 
     * @param context
     * @param customerId
     */
    public static void setCustomerId(Context context, int customerId)
    {
        SessionCookie cookie = context.getSessionCookie();
        cookie.put(USER, Integer.toString(customerId));
    }

    /**
     * Returns customer id of the session, if an id is set, otherwise -1.
     * 
     * @param context
     * @return
     */
    public static int getCustomerId(Context context)
    {
        int customerId;
        String customer = context.getSessionCookie().get(USER);
        if (customer == null)
        {
            customerId = -1;
        }
        else
        {
            customerId = Integer.parseInt(customer);
        }
        return customerId;
    }
}
