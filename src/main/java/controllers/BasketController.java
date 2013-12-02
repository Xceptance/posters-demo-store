package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import models.Basket;
import models.Basket_Product;
import models.Product;
import ninja.Context;
import ninja.FilterWith;
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
import filters.SessionTerminatedFilter;

public class BasketController
{

    @Inject
    Messages msg;

    @Inject
    XCPosterConf xcpConf;

    private Optional language = Optional.of("en");

    /**
     * Deletes the product from the basket.
     * 
     * @param productId
     * @param context
     * @return
     */
    @FilterWith(SessionTerminatedFilter.class)
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
    @FilterWith(SessionTerminatedFilter.class)
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

    /**
     * Returns all products of the basket as a json object.
     * 
     * @param context
     * @return
     */
    public Result getCartElementSlider(Context context)
    {
        // get basket by session
        Basket basket = BasketInformation.getBasketById(SessionHandling.getBasketId(context));
        // get all products of the basket
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        List<Basket_Product> basketProducts = Ebean.find(Basket_Product.class).where().eq("basket", basket)
                                                   .orderBy("lastUpdate desc").findList();
        // prepare just some attributes
        for (Basket_Product basketProduct : basketProducts)
        {
            Map<String, Object> product = new HashMap<String, Object>();
            product.put("productCount", basketProduct.getCountProduct());
            product.put("productName", basketProduct.getProduct().getName());
            product.put("productId", basketProduct.getProduct().getId());
            product.put("productPrice", basketProduct.getProduct().getPrice());
            product.put("finish", basketProduct.getFinish());
            results.add(product);
        }
        return Results.json().render("cartElements", results);
    }

    /**
     * Adds one product to the cart.
     * 
     * @param productId
     * @param finish
     * @param context
     * @return
     */
    @FilterWith(SessionTerminatedFilter.class)
    public Result addToCart(@Param("productId") String productId, @Param("finish") String finish, Context context)
    {
        Result result = Results.json();
        // get product by id
        Product product = ProductInformation.getProductById(Integer.parseInt(productId));
        // get basket by session
        Basket basket = BasketInformation.getBasketById(SessionHandling.getBasketId(context));
        // set customer to basket
        BasketInformation.setCustomerToBasket(context, basket);
        // add product to basket
        BasketInformation.addProductToBasket(basket, product, finish);
        // get added basket product
        Basket_Product basketProduct = BasketInformation.getBasketProduct(basket, product, finish);
        Map<String, Object> updatedProduct = new HashMap<String, Object>();
        updatedProduct.put("productCount", basketProduct.getCountProduct());
        updatedProduct.put("productName", basketProduct.getProduct().getName());
        updatedProduct.put("productId", basketProduct.getProduct().getId());
        updatedProduct.put("productPrice", basketProduct.getProduct().getPrice());
        updatedProduct.put("finish", finish);
        // add product to result
        result.render("product", updatedProduct);
        // get product count
        int productCount = BasketInformation.getProductCount(basket);
        // total price
        double totalPrice = basket.getTotalPrice();
        // prepare updated cart in header
        String headerCartOverview = " " + msg.get("basketOverviewTitle", language).get() + ": " + productCount + " "
                                    + msg.get("basketItem", language).get() + " - " + totalPrice + xcpConf.currency;
        // add new header to result
        result.render("headerCartOverview", headerCartOverview);
        return result;
    }
}
