package util.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Basket;
import models.Basket_Product;
import models.Product;

import com.avaje.ebean.Ebean;

public abstract class BasketInformation
{

    /**
     * Returns the basket by the given id. Creates a new basket, if there is no basket with the given id.
     * 
     * @param id
     * @return
     */
    public static Basket getBasketById(int id)
    {
        Basket basket = Ebean.find(Basket.class, id);
        // create new basket, if there is no basket with the given id
        if (basket == null)
        {
            basket = new Basket();
            // save basket
            Ebean.save(basket);
        }
        return basket;
    }

    /**
     * Adds a given product to a given basket.
     * 
     * @param basket
     * @param productId
     */
    public static void addProductToBasket(final Basket basket, final Product product)
    {
        basket.addProduct(product);
        basket.update();
    }

    /**
     * Adds all products of the given basket to the data map.
     * 
     * @param basket
     * @param data
     */
    public static void addBasketDetailToMap(final Basket basket, final Map<String, Object> data)
    {
        final Map<Product, Integer> products = new HashMap<Product, Integer>();
        int totalProductCount = 0;
        // get all products of the basket
        List<Basket_Product> basketProducts = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();
        for (Basket_Product basketProduct : basketProducts)
        {
            products.put(basketProduct.getProduct(), basketProduct.getCountProduct());
            totalProductCount += basketProduct.getCountProduct();
        }
        // add all products of the basket
        data.put("basketProducts", basketProducts);
        // add product count of basket
        data.put("basketProductCount", totalProductCount);
        // add basket id
        int basketId = basket.getId();
        data.put("basketId", basketId);
        // add total price of basket
        double totalPrice = basket.getTotalPrice();
        data.put("totalPrice", totalPrice);
    }

    /**
     * Returns all baskets, which are stored in the data base.
     * 
     * @return
     */
    public static List<Basket> getAllBaskets()
    {
        return Ebean.find(Basket.class).findList();
    }

    /**
     * Removes the given basket from the database.
     * 
     * @param basket
     */
    public static void removeBasket(Basket basket)
    {
        basket.delete();
    }

    public static void removeProductFromBasket(Basket basket, Product product)
    {
        // delete product from basket
        basket.deleteProduct(product);
        basket.update();
    }
}
