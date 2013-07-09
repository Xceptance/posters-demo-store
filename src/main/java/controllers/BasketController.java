package controllers;

import java.util.HashMap;
import java.util.Map;

import models.Basket;
import models.Basket_Product;
import models.Product;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import util.database.BasketInformation;
import util.database.CommonInformation;
import util.database.ProductInformation;
import util.session.SessionHandling;

import com.avaje.ebean.Ebean;

public class BasketController
{

    /**
     * Adds one product, given by the product id, to the basket. The basket will be chosen by the session.
     * 
     * @param productId
     *            The ID of the product.
     * @param context
     * @return The basket overview page.
     */
    public Result addToBasket(@Param("productName") String productId, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();

        CommonInformation.setCommonData(data, context);

        // get product by id
        Product product = ProductInformation.getProductById(Integer.parseInt(productId));
        // get basket by session
        Basket basket = BasketInformation.getBasketById(SessionHandling.getBasketId(context));
        // put basket id to session
        SessionHandling.setBasketId(context, basket.getId());
        // add product to basket
        BasketInformation.addProductToBasket(basket, product);
        // put basket to data map
        BasketInformation.addBasketDetailToMap(basket, data);
        // return basket overview page
        return Results.html().render(data).template("views/BasketController/basketOverview.ftl.html");
    }

    /**
     * Deletes the product from the basket. Decrements the product count, if the product is in the basket more than
     * once.
     * 
     * @param productId
     * @param context
     * @return
     */
    public Result deleteFromBasket(@Param("productName") String productId, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();

        CommonInformation.setCommonData(data, context);

        // get product by id
        Product product = ProductInformation.getProductById(Integer.parseInt(productId));
        // get basket by session
        Basket basket = BasketInformation.getBasketById(SessionHandling.getBasketId(context));
        // put basket id to session
        SessionHandling.setBasketId(context, basket.getId());
        int countProduct = Ebean.find(Basket_Product.class).where().eq("basket", basket).eq("product", product)
                                .findUnique().getCountProduct();
        // delete all of this products
        for (int i = 0; i < countProduct; i++)
        {
            basket.deleteProduct(product);
        }
        // put basket to data map
        BasketInformation.addBasketDetailToMap(basket, data);
        // return basket overview page
        return Results.html().render(data).template("views/BasketController/basketOverview.ftl.html");
    }

    /**
     * Returns the basket overview page.
     * 
     * @param context
     * @return The basket overview page.
     */
    public Result basket(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();

        CommonInformation.setCommonData(data, context);
        // return basket overview page
        return Results.html().render(data).template("views/BasketController/basketOverview.ftl.html");
    }

    /**
     * Updates the product count of the given product.
     * 
     * @param productId
     * @param productCount
     * @param context
     * @return
     */
    public Result updateProductCount(@Param("productName") String productId,
                                     @Param("productCount") String productCount, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();

        CommonInformation.setCommonData(data, context);

        // get product by id
        Product product = ProductInformation.getProductById(Integer.parseInt(productId));
        // get basket by session
        Basket basket = BasketInformation.getBasketById(SessionHandling.getBasketId(context));
        // put basket id to session
        SessionHandling.setBasketId(context, basket.getId());

        int newProductCount = Integer.parseInt(productCount);
        // zero is minimum of product count
        if (newProductCount < 0)
        {
            newProductCount = 0;
        }
        int currentProductCount = Ebean.find(Basket_Product.class).where().eq("basket", basket).eq("product", product)
                                       .findUnique().getCountProduct();
        int difference = newProductCount - currentProductCount;
        // product must be added
        if (difference > 0)
        {
            for (int i = 0; i < difference; i++)
            {
                // add product to basket
                BasketInformation.addProductToBasket(basket, product);
            }
        }
        // product must be removed
        else
        {
            for (int i = difference; i < 0; i++)
            {
                // remove product from basket
                BasketInformation.removeProductFromBasket(basket, product);
            }
        }
        // put basket to data map
        BasketInformation.addBasketDetailToMap(basket, data);
        // return basket overview page
        return Results.html().render(data).template("views/BasketController/basketOverview.ftl.html");
    }
}
