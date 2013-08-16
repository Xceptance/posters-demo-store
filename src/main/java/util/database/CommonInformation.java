package util.database;

import java.util.Map;

import com.google.inject.Inject;

import conf.XCPosterConf;

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
     * @param xcpConf TODO
     */
    public static void setCommonData(final Map<String, Object> data, Context context, XCPosterConf xcpConf)
    {
        // set categories
        CategoryInformation.addCategoriesToMap(data);
        // get basket by session
        Basket basket = BasketInformation.getBasketById(SessionHandling.getBasketId(context));
        if (basket == null)
        {
            basket = new Basket();
            basket.save();
            // put basket id to session
            SessionHandling.setBasketId(context, basket.getId());
        }
        // set basket stuff
        BasketInformation.addBasketDetailToMap(basket, data);
        // set logged customer
        CustomerInformation.addCustomerFirstNameToMap(context, data);
        // set application url
        data.put("applUrlHttp", xcpConf.applicationUrlHttp);
        data.put("applUrlHttps", xcpConf.applicationUrlHttps);
        // set currency
        data.put("currency", xcpConf.currency);
    }
}
