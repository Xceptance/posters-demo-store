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
    public Result addToBasket(@Param("productId") String productId, @Param("finish") String finish, Context context)
    {
        // get product by id
        Product product = ProductInformation.getProductById(Integer.parseInt(productId));
        // get basket by session
        Basket basket = BasketInformation.getBasketById(SessionHandling.getBasketId(context));
        // set customer to basket
        BasketInformation.setCustomerToBasket(context, basket);
        // add product to basket
        BasketInformation.addProductToBasket(basket, product, finish);
        // return basket overview page
        return Results.redirect(context.getContextPath() + "/basket");
    }

    /**
     * Deletes the product from the basket.
     * 
     * @param productId
     * @param context
     * @return
     */
    public Result deleteFromBasket(@Param("basketProductId") int basketProductId, Context context)
    {
        Basket_Product basketProduct = Ebean.find(Basket_Product.class, basketProductId);
        // get basket by session
        Basket basket = BasketInformation.getBasketById(SessionHandling.getBasketId(context));
        // get count of this product
        int countProduct = basketProduct.getCountProduct();
        // delete all items of this products
        for (int i = 0; i < countProduct; i++)
        {
            BasketInformation.removeProductFromBasket(basket, basketProduct);
        }
        // return basket overview page
        return Results.redirect(context.getContextPath() + "/basket");
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
     * @param basketProductId
     * @param productCount
     * @param context
     * @return
     */
    public Result updateProductCount(@Param("basketProductId") int basketProductId,
                                     @Param("productCount") String productCount, Context context)
    {
        if (!Pattern.matches(xcpConf.regexProductCount, productCount))
        {
            // show info message
            context.getFlashCookie().put("info", msg.get("infoProductCount", language).get());
        }
        // product count is OK
        else
        {
            int newProductCount = Integer.parseInt(productCount);
            // zero is minimum of product count
            if (newProductCount < 0)
            {
                newProductCount = 0;
            }
            // get basket by session
            Basket basket = BasketInformation.getBasketById(SessionHandling.getBasketId(context));
            // get basket product by id
            Basket_Product basketProduct = Ebean.find(Basket_Product.class, basketProductId);
            Product product = basketProduct.getProduct();
            int currentProductCount = basketProduct.getCountProduct();
            int difference = newProductCount - currentProductCount;
            // product must be added
            if (difference > 0)
            {
                for (int i = 0; i < difference; i++)
                {
                    // add product to basket
                    BasketInformation.addProductToBasket(basket, product, basketProduct.getFinish());
                }
            }
            // product must be removed
            else
            {
                for (int i = difference; i < 0; i++)
                {
                    // remove product from basket
                    BasketInformation.removeProductFromBasket(basket, basketProduct);
                }
            }
        }
        // return basket overview page
        return Results.redirect(context.getContextPath() + "/basket");
    }

    public Result addToBasketAjax(@Param("productId") String productId, @Param("finish") String finish, Context context)
    {
        // get product by id
        Product product = ProductInformation.getProductById(Integer.parseInt(productId));
        // get basket by session
        Basket basket = BasketInformation.getBasketById(SessionHandling.getBasketId(context));
        // set customer to basket
        BasketInformation.setCustomerToBasket(context, basket);
        // add product to basket
        BasketInformation.addProductToBasket(basket, product, finish);
        // get product count
        int productCount = BasketInformation.getProductCount(basket);
        // total price
        double totalPrice = basket.getTotalPrice();
        String headerCartOverview = " " + msg.get("basketOverviewTitle", language).get() + ": " + productCount + " "
                                    + msg.get("basketItem", language).get() + " - " + totalPrice + xcpConf.currency;
        return Results.json().render("headerCartOverview", headerCartOverview);
    }
}
