package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import models.Basket;
import models.Basket_Product;
import models.Product;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.params.Param;
import util.database.BasketInformation;
import util.database.CommonInformation;
import util.database.ProductInformation;
import util.session.SessionHandling;

import com.avaje.ebean.Ebean;
import com.google.common.base.Optional;
import com.google.inject.Inject;

import conf.XCPosterConf;

public class BasketController
{

    @Inject
    Messages msg;

    @Inject
    XCPosterConf xcpConf;

    private Optional language = Optional.of("en");

    /**
     * Adds one product to the basket. The basket will be chosen by the session.
     * 
     * @param productId
     *            The ID of the product.
     * @param context
     * @return The basket overview page.
     */
    public Result addToBasket(@Param("productName") String productId, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        CommonInformation.setCommonData(data, context, xcpConf);
        // get product by id
        Product product = ProductInformation.getProductById(Integer.parseInt(productId));
        // get basket by session
        Basket basket = BasketInformation.getBasketById(SessionHandling.getBasketId(context));
        // put basket id to session
        SessionHandling.setBasketId(context, basket.getId());
        // set customer to basket
        BasketInformation.setCustomerToBasket(context, basket);
        // add product to basket
        BasketInformation.addProductToBasket(basket, product);
        // put basket to data map
        BasketInformation.addBasketDetailToMap(basket, data);
        // return basket overview page
        return Results.html().render(data).template(xcpConf.templateBasketOverview).redirect(context.getContextPath() + "/basket");
    }

    /**
     * Deletes the product from the basket.
     * 
     * @param productId
     * @param context
     * @return
     */
    public Result deleteFromBasket(@Param("productName") String productId, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        CommonInformation.setCommonData(data, context, xcpConf);
        // get product by id
        Product product = ProductInformation.getProductById(Integer.parseInt(productId));
        // get basket by session
        Basket basket = BasketInformation.getBasketById(SessionHandling.getBasketId(context));
        // put basket id to session
        SessionHandling.setBasketId(context, basket.getId());
        // get count of this product
        int countProduct = Ebean.find(Basket_Product.class).where().eq("basket", basket).eq("product", product)
                                .findUnique().getCountProduct();
        // delete all items of this products
        for (int i = 0; i < countProduct; i++)
        {
            BasketInformation.removeProductFromBasket(basket, product);
        }
        // put basket to data map
        BasketInformation.addBasketDetailToMap(basket, data);
        // return basket overview page
        return Results.html().render(data).template(xcpConf.templateBasketOverview).redirect(context.getContextPath() + "/basket");
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
        CommonInformation.setCommonData(data, context, xcpConf);
        // return basket overview page
        return Results.html().render(data).template(xcpConf.templateBasketOverview);
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
        Result result = Results.html().template(xcpConf.templateBasketOverview);
        CommonInformation.setCommonData(data, context, xcpConf);
        // get basket by session
        Basket basket = BasketInformation.getBasketById(SessionHandling.getBasketId(context));
        if (!Pattern.matches(xcpConf.regexProductCount, productCount))
        {
            data.put("infoMessage", msg.get("infoProductCount", language).get());
            // return basket overview page
            return result.render(data);
        }
        // product count is OK
        else
        {
            // get product by id
            Product product = ProductInformation.getProductById(Integer.parseInt(productId));

            // put basket id to session
            SessionHandling.setBasketId(context, basket.getId());

            int newProductCount = Integer.parseInt(productCount);
            // zero is minimum of product count
            if (newProductCount < 0)
            {
                newProductCount = 0;
            }
            int currentProductCount = Ebean.find(Basket_Product.class).where().eq("basket", basket)
                                           .eq("product", product).findUnique().getCountProduct();
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
            // update basket in data map
            BasketInformation.addBasketDetailToMap(basket, data);
        }
        // return basket overview page
        return result.render(data).redirect(context.getContextPath() + "/basket");
    }
}
