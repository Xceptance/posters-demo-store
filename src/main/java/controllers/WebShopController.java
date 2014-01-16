package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Cart;
import models.CartProduct;
import models.Customer;
import models.Product;
import models.TopCategory;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import util.session.SessionHandling;

import com.avaje.ebean.Ebean;
import com.google.inject.Inject;

import conf.PosterConstants;

public class WebShopController
{

    @Inject
    PosterConstants xcpConf;

    /**
     * Returns the main page of the web shop.
     * 
     * @param context
     * @return
     */
    public Result index(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        setCommonData(data, context, xcpConf);
        // get all products, which should be shown in the carousel on the main page.
        List<Product> products = Ebean.find(Product.class).where().eq("showInCarousel", true).findList();
        // add products to data map
        data.put("carousel", products);
        return Results.html().render(data);
    }

    /**
     * Adds all categories to the map data. Sets the session cookie, if its unknown. Adds some information of the
     * current user's cart. Should be called before each page view.
     * 
     * @param data
     * @param context
     * @param xcpConf
     */
    public static void setCommonData(final Map<String, Object> data, Context context, PosterConstants xcpConf)
    {
        // set categories
        data.put("topCategory", TopCategory.getAllTopCategories());
        // get cart by session
        Cart cart = Cart.getCartById(SessionHandling.getCartId(context));
        // set cart stuff
        addCartDetailToMap(cart, data);
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
        data.put("applUrlHttp", xcpConf.APPLICATION_URL_HTTP);
        data.put("applUrlHttps", xcpConf.APPLICATION_URL_HTTPS);
        // set currency
        data.put("currency", xcpConf.CURRENCY);
        // add unit of length
        data.put("unitLength", xcpConf.UNIT_OF_LENGTH);
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
        // get all products of the cart
        List<CartProduct> cartProducts = Ebean.find(CartProduct.class).where().eq("cart", cart)
                                              .orderBy("lastUpdate desc").findList();
        for (CartProduct cartProduct : cartProducts)
        {
            products.put(cartProduct.getProduct(), cartProduct.getProductCount());
            totalProductCount += cartProduct.getProductCount();
        }
        // add all products of the cart
        data.put("basketProducts", cartProducts);
        // add product count of cart
        data.put("basketProductCount", totalProductCount);
        // add cart id
        data.put("basketId", cart.getId());
        // add total price of basket
        data.put("totalPrice", cart.getTotalPriceAsString());
    }
}
