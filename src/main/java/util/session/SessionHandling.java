package util.session;

import java.util.UUID;

import conf.PosterConstants;
import models.Cart;
import models.Order;
import ninja.Context;
import ninja.session.Session;

/**
 * Provides methods for the session handling.
 * 
 * @author sebastianloob
 */
public class SessionHandling
{
    // The keys of session cookie values.
    private static String CART = "cart";

    private static String ORDER = "order";

    private static String USER = "user";

    private static String ADMIN = "admin";

    /**
     * Removes the user information from the session.
     * 
     * @param context
     */
    public static void removeCustomerId(final Context context)
    {
        context.getSession().remove(USER);
    }

    /**
     * Returns true, if an order-id is set in the session, otherwise false.
     * 
     * @param context
     * @return
     */
    public static boolean isOrderIdSet(final Context context)
    {
        if (context.getSession().get(ORDER) == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Adds an order id to the session.
     * 
     * @param context
     * @param orderId
     */
    public static void setOrderId(final Context context, final UUID orderId)
    {
        context.getSession().put(ORDER, orderId.toString());
    }

    /**
     * Returns the order id of the session. Creates a new order, if no is set.
     * 
     * @param context
     * @return
     */
    public static UUID getOrderId(final Context context)
    {
        Session cookie = context.getSession();
        // order id is not set, if the session is terminated
        if (cookie.get(ORDER) == null)
        {
            // create new order
            final Order order = Order.createNewOrder();
            // set id of new order
            setOrderId(context, order.getId());
            // get cookie again
            cookie = context.getSession();
        }
        return UUID.fromString(cookie.get(ORDER));
    }

    /**
     * Removes the order id from the session.
     * 
     * @param context
     */
    public static void removeOrderId(final Context context)
    {
        context.getSession().remove(ORDER);
    }

    /**
     * Adds the cart id to the session.
     * 
     * @param context
     * @param cartId
     */
    public static void setCartId(final Context context, final UUID cartId)
    {
        context.getSession().put(CART, cartId.toString());
    }

    /**
     * Returns the cart id of the session. Creates a new cart, if no is set.
     * 
     * @param context
     * @return
     */
    public static UUID getCartId(final Context context, final PosterConstants xcpConf)
    {
        Session cookie = context.getSession();
        // create new cart, if no cart is set
        if (cookie.get(CART) == null)
        {
            // create new cart
            final Cart cart = Cart.createNewCart(xcpConf.TAX, xcpConf.SHIPPING_COSTS);//xcpConf.TAX, xcpConf.SHIPPING_COSTS
            // add cart id to session
            setCartId(context, cart.getId());
            // get cookie again
            cookie = context.getSession();
        }
        Cart cart = Cart.getCartById(UUID.fromString(cookie.get(CART)));
        // cart was removed from database
        if (cart == null)
        {
            // create new cart
            cart = Cart.createNewCart(xcpConf.TAX, xcpConf.SHIPPING_COSTS);
            // add cart id to session
            setCartId(context, cart.getId());
            // get cookie again
            cookie = context.getSession();
        }
        return UUID.fromString(cookie.get(CART));
    }

    /**
     * Removes the cart id from the session.
     * 
     * @param context
     */
    public static void removeCartId(final Context context)
    {
        context.getSession().remove(CART);
    }

    /**
     * Returns true, if a customer is logged, otherwise false.
     * 
     * @param context
     * @return
     */
    public static boolean isCustomerLogged(final Context context)
    {
        boolean isLogged = true;
        if (context.getSession().get(USER) == null)
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
    public static void setCustomerId(final Context context, final UUID customerId)
    {
        context.getSession().put(USER, customerId.toString());
    }

    /**
     * Returns customer id of the session.
     * 
     * @param context
     * @return
     */
    public static UUID getCustomerId(final Context context)
    {
        return UUID.fromString(context.getSession().get(USER));
    }

    /**
     * Adds user id to the session.
     * 
     * @param context
     * @param userId
     */
    public static void setUserId(final Context context, final UUID userId)
    {
        context.getSession().put(ADMIN, userId.toString());
    }

    /**
     * Returns user id of the session.
     * 
     * @param context
     * @return
     */
    public static UUID getUserId(final Context context)
    {
        return UUID.fromString(context.getSession().get(ADMIN));
    }

    /**
     * Returns true, if a user is logged, otherwise false.
     * 
     * @param context
     * @return
     */
    public static boolean isUserLogged(final Context context)
    {
        boolean isLogged = true;
        if (context.getSession().get(ADMIN) == null)
        {
            isLogged = false;
        }
        return isLogged;
    }

    /**
     * Removes the user information from the session.
     * 
     * @param context
     */
    public static void removeUserId(final Context context)
    {
        context.getSession().remove(ADMIN);
    }
}
