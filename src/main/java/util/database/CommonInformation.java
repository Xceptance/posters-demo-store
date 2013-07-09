package util.database;

import java.util.Map;

import models.Basket;
import ninja.Context;
import util.session.SessionHandling;

public abstract class CommonInformation
{

    /**
     * Adds all categories to the map data. Sets the session cookie, if its unknown. Adds some information of the basket
     * of the current user. Should be called before each page view.
     * 
     * @param data
     * @param context
     */
    public static void setCommonData(final Map<String, Object> data, Context context)
    {
        // remember categories
        CategoryInformation.addCategoriesToMap(data);
        // set session cookie
        SessionHandling.setUnknownUser(context);
        // get basket by session
        Basket basket = BasketInformation.getBasketById(SessionHandling.getBasketId(context));
        // put basket id to session
        SessionHandling.setBasketId(context, basket.getId());
        // remember basket stuff
        BasketInformation.addBasketDetailToMap(basket, data);
        // remember logged customer
        CustomerInformation.addCustomerFirstNameToMap(context, data);
    }
}
