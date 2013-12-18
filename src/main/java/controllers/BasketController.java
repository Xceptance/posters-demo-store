package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import models.Basket;
import models.Basket_Product;
import models.PosterSize;
import models.Product;
import models.Product_PosterSize;
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
        Result result = Results.json();
        if (!Pattern.matches(xcpConf.regexProductCount, productCount))
        {
            // show info message
            result.render("message", msg.get("infoProductCount", language).get());
            return result.status(Result.SC_400_BAD_REQUEST);
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
                    BasketInformation.addProductToBasket(basket, product, basketProduct.getFinish(),
                                                         basketProduct.getSize());
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
            // add new header
            result.render("headerCartOverview", prepareCartOverviewInHeader(basket));
            // add totalPrice
            result.render("totalPrice", (basket.getTotalPriceAsString() + xcpConf.currency));
            return result;
        }
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
            product.put("productPrice", basketProduct.getPriceAsString());
            product.put("finish", basketProduct.getFinish());
            product.put("size", basketProduct.getSize());
            results.add(product);
        }
        Result result = Results.json();
        // add products
        result.render("cartElements", results);
        // add currency
        result.render("currency", xcpConf.currency);
        // add unit of length
        result.render("unitLength", xcpConf.unitLength);
        // add total price
        result.render("totalPrice", basket.getTotalPriceAsString());
        return result;
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
    public Result addToCart(@Param("productId") String productId, @Param("finish") String finish,
                            @Param("size") String size, Context context)
    {
        Result result = Results.json();
        // get product by id
        Product product = ProductInformation.getProductById(Integer.parseInt(productId));
        // get basket by session
        Basket basket = BasketInformation.getBasketById(SessionHandling.getBasketId(context));
        // set customer to basket
        BasketInformation.setCustomerToBasket(context, basket);
        // get poster size
        String[] dummy = size.split(" ");
        int width = Integer.parseInt(dummy[0]);
        int height = Integer.parseInt(dummy[2]);
        PosterSize posterSize = Ebean.find(PosterSize.class).where().eq("width", width).eq("height", height)
                                     .findUnique();
        // add product to basket
        BasketInformation.addProductToBasket(basket, product, finish, posterSize);
        // get added basket product
        Basket_Product basketProduct = BasketInformation.getBasketProduct(basket, product, finish, posterSize);
        Map<String, Object> updatedProduct = new HashMap<String, Object>();
        updatedProduct.put("productCount", basketProduct.getCountProduct());
        updatedProduct.put("productName", basketProduct.getProduct().getName());
        updatedProduct.put("productId", basketProduct.getProduct().getId());
        updatedProduct.put("productPrice", basketProduct.getPriceAsString());
        updatedProduct.put("finish", finish);
        updatedProduct.put("size", basketProduct.getSize());
        // add product to result
        result.render("product", updatedProduct);
        // add currency
        result.render("currency", xcpConf.currency);
        // add unit of length
        result.render("unitLength", xcpConf.unitLength);
        // add new header to result
        result.render("headerCartOverview", prepareCartOverviewInHeader(basket));
        // add total price
        result.render("totalPrice", basket.getTotalPriceAsString());
        return result;
    }

    /**
     * Deletes the product from the cart.
     * 
     * @param productId
     * @param context
     * @return
     */
    @FilterWith(SessionTerminatedFilter.class)
    public Result deleteFromCart(@Param("basketProductId") int basketProductId, Context context)
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
        Result result = Results.json();
        // add new header
        result.render("headerCartOverview", prepareCartOverviewInHeader(basket));
        // add totalPrice
        result.render("totalPrice", (basket.getTotalPriceAsString() + xcpConf.currency));
        return result;
    }

    public Result updatePrice(@Param("size") String size, @Param("productId") int productId, Context context)
    {
        String[] dummy = size.split(" ");
        int width = Integer.parseInt(dummy[0]);
        int height = Integer.parseInt(dummy[2]);
        PosterSize posterSize = Ebean.find(PosterSize.class).where().eq("width", width).eq("height", height)
                                     .findUnique();
        Product product = ProductInformation.getProductById(productId);
        Product_PosterSize productPosterSize = Ebean.find(Product_PosterSize.class).where().eq("product", product)
                                                    .eq("size", posterSize).findUnique();
        Result result = Results.json();
        // add new price
        result.render("newPrice", productPosterSize.getPriceAsString() + xcpConf.currency);
        return result;
    }

    private String prepareCartOverviewInHeader(Basket basket)
    {
        StringBuilder headerCartOverview = new StringBuilder();
        headerCartOverview.append(" " + msg.get("basketOverviewTitle", language).get() + ": ");
        headerCartOverview.append(BasketInformation.getProductCount(basket));
        headerCartOverview.append(" " + msg.get("basketItem", language).get() + " - ");
        headerCartOverview.append(basket.getTotalPriceAsString() + xcpConf.currency);
        return headerCartOverview.toString();
    }
}
