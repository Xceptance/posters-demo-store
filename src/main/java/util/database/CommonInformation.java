package util.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Ebean;
import com.google.inject.Inject;

import conf.XCPosterConf;

import models.Cart;
import models.CartProduct;
import models.Customer;
import models.Product;
import models.TopCategory;
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
     * @param xcpConf
     */
    public static void setCommonData(final Map<String, Object> data, Context context, XCPosterConf xcpConf)
    {
        // set categories
        data.put("topCategory", TopCategory.getAllTopCategories());
        // get basket by session
        Cart basket = Cart.getCartById(SessionHandling.getBasketId(context));
        // set basket stuff
        addCartDetailToMap(basket, data);
        // a customer is logged
        if (SessionHandling.isCustomerLogged(context))
        {
            // get customer by session
            Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
            // add information that a customer is logged
            data.put("isLogged", true);
            // add customer's first name to data map
            data.put("customerFirstName", customer.getFirstName());
        }
        // no customer is logged
        else
        {
            data.put("isLogged", false);
        }
        // set application url
        data.put("applUrlHttp", xcpConf.applicationUrlHttp);
        data.put("applUrlHttps", xcpConf.applicationUrlHttps);
        // set currency
        data.put("currency", xcpConf.currency);
        // add unit of length
        data.put("unitLength", xcpConf.unitLength);
    }

    /**
     * Adds all products of the given cart to the data map.
     * 
     * @param cart
     * @param data
     */
    private static void addCartDetailToMap(Cart cart, final Map<String, Object> data)
    {
        if (cart == null)
        {
            cart = Cart.createNewCart();
        }

        final Map<Product, Integer> products = new HashMap<Product, Integer>();
        int totalProductCount = 0;
        // get all products of the basket
        List<CartProduct> basketProducts = Ebean.find(CartProduct.class).where().eq("cart", cart)
                                                .orderBy("lastUpdate desc").findList();
        for (CartProduct basketProduct : basketProducts)
        {
            products.put(basketProduct.getProduct(), basketProduct.getProductCount());
            totalProductCount += basketProduct.getProductCount();
        }
        // add all products of the basket
        data.put("basketProducts", basketProducts);
        // add product count of basket
        data.put("basketProductCount", totalProductCount);
        // add basket id
        int basketId = cart.getId();
        data.put("basketId", basketId);
        // add total price of basket
        data.put("totalPrice", cart.getTotalPriceAsString());
    }
}
