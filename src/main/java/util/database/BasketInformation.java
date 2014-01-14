package util.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Cart;
import models.CartProduct;
import models.Customer;
import models.PosterSize;
import models.Product;
import ninja.Context;
import util.session.SessionHandling;

import com.avaje.ebean.Ebean;

public abstract class BasketInformation
{

    /**
     * Creates and returns a new basket.
     * 
     * @return
     */
    public static Cart createNewBasket()
    {
        // create new basket
        Cart basket = new Cart();
        // save basket
        basket.save();
        // get new basket by id
        int id = basket.getId();
        Cart newBasket = Ebean.find(Cart.class, id);
        // return new basket
        return newBasket;
    }

    /**
     * Returns the basket by the given id. Returns null, if no basket was found.
     * 
     * @param id
     * @return
     */
    public static Cart getBasketById(int id)
    {
        return Ebean.find(Cart.class, id);
    }

    /**
     * Adds a given product to a given basket.
     * 
     * @param basket
     * @param productId
     */
    public static void addProductToBasket(final Cart basket, final Product product, final String finish,
                                          final PosterSize size)
    {
        basket.addProduct(product, finish, size);
        basket.update();
    }

    /**
     * Adds all products of the given basket to the data map.
     * 
     * @param basket
     * @param data
     */
    public static void addBasketDetailToMap(Cart basket, final Map<String, Object> data)
    {
        if (basket == null)
        {
            basket = createNewBasket();
        }

        final Map<Product, Integer> products = new HashMap<Product, Integer>();
        int totalProductCount = 0;
        // get all products of the basket
        List<CartProduct> basketProducts = Ebean.find(CartProduct.class).where().eq("basket", basket)
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
        int basketId = basket.getId();
        data.put("basketId", basketId);
        // add total price of basket
        data.put("totalPrice", basket.getTotalPriceAsString());
    }

    /**
     * Returns all baskets, which are stored in the data base.
     * 
     * @return
     */
    public static List<Cart> getAllBaskets()
    {
        return Ebean.find(Cart.class).findList();
    }

    /**
     * Removes the given basket from the database.
     * 
     * @param basket
     */
    public static void removeBasket(Cart basket)
    {
        basket.delete();
    }

    /**
     * Removes the given product from the given basket.
     * 
     * @param basket
     * @param product
     */
    public static void removeProductFromBasket(Cart basket, CartProduct basketProduct)
    {
        // delete product from basket
        basket.deleteProduct(basketProduct);
        basket.update();
    }

    /**
     * Sets the customer to the basket, if no one is set and a customer is logged.
     * 
     * @param context
     * @param basket
     */
    public static void setCustomerToBasket(Context context, Cart basket)
    {
        if (SessionHandling.isCustomerLogged(context) && basket.getCustomer() == null)
        {
            Customer customer = CustomerInformation.getCustomerById(SessionHandling.getCustomerId(context));
            basket.setCustomer(customer);
            basket.update();
            customer.setCart(basket);
            customer.update();
        }
    }

    /**
     * Returns the total product count of a basket.
     * 
     * @param basket
     * @return
     */
    public static int getProductCount(Cart basket)
    {
        int productCount = 0;
        // get all products of the basket
        List<CartProduct> basketProducts = Ebean.find(CartProduct.class).where().eq("basket", basket).findList();
        for (CartProduct basketProduct : basketProducts)
        {
            productCount += basketProduct.getProductCount();
        }
        return productCount;
    }

    public static CartProduct getBasketProduct(final Cart basket, final Product product, final String finish,
                                               final PosterSize size)
    {
        return Ebean.find(CartProduct.class).where().eq("basket", basket).eq("product", product).eq("finish", finish)
                    .eq("size", size).findUnique();
    }
}
