package util.session;

import java.util.Map;

import ninja.Context;
import ninja.session.SessionCookie;

public class SessionHandling
{

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
     * Sets user information to guest, if no user is registered in the session.
     * 
     * @param context
     */
    public static void setUnknownUser(Context context)
    {
        SessionCookie cookie = context.getSessionCookie();
        if (cookie.get("user") == null)
        {
            cookie.put("user", "guest");
        }
    }

    /**
     * Overrides the user information in the session to guest.
     * 
     * @param context
     */
    public static void deleteCustomerId(Context context)
    {
        SessionCookie cookie = context.getSessionCookie();
        cookie.put("user", "guest");
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
        if (cookie.get("order") == null)
        {
            cookie.put("order", Integer.toString(orderId));
        }
    }

    /**
     * Returns the order id of the session.
     * 
     * @param context
     * @return
     */
    public static int getOrderId(Context context)
    {
        int orderId;
        SessionCookie cookie = context.getSessionCookie();
        orderId = Integer.parseInt(cookie.get("order"));
        return orderId;
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
        if (cookie.get("basket") == null)
        {
            cookie.put("basket", Integer.toString(basketId));
        }
    }

    /**
     * Returns the basket id of the session. Returns -1, if the session has no basket.
     * 
     * @param context
     * @return
     */
    public static int getBasketId(Context context)
    {
        int basketId = -1;
        SessionCookie cookie = context.getSessionCookie();
        if (cookie.get("basket") != null)
        {
            basketId = Integer.parseInt(cookie.get("basket"));
        }
        return basketId;
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
        if (cookie.get("user") == null || cookie.get("user").equals("guest"))
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
        cookie.put("user", Integer.toString(customerId));
    }

    /**
     * Returns customer id of the session.
     * 
     * @param context
     * @return
     */
    public static int getCustomerId(Context context)
    {
        return Integer.parseInt(context.getSessionCookie().get("user"));
    }

}
