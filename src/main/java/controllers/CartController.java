package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import models.Cart;
import models.CartProduct;
import models.PosterSize;
import models.Product;
import models.ProductPosterSize;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.params.Param;
import util.session.SessionHandling;

import com.avaje.ebean.Ebean;
import com.google.common.base.Optional;
import com.google.inject.Inject;

import conf.PosterConstants;
import filters.SessionTerminatedFilter;

public class CartController
{

    @Inject
    Messages msg;

    @Inject
    PosterConstants xcpConf;

    private Optional language = Optional.of("en");

    /**
     * Returns the basket overview page.
     * 
     * @param context
     * @return The basket overview page.
     */
    public Result cart(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // return basket overview page
        return Results.html().render(data).template(xcpConf.TEMPLATE_CART_OVERVIEW);
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
        if (!Pattern.matches(xcpConf.REGEX_PRODUCT_COUNT, productCount))
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
            Cart basket = Cart.getCartById(SessionHandling.getCartId(context));
            // get basket product by id
            CartProduct basketProduct = Ebean.find(CartProduct.class, basketProductId);
            Product product = basketProduct.getProduct();
            int currentProductCount = basketProduct.getProductCount();
            int difference = newProductCount - currentProductCount;
            // product must be added
            if (difference > 0)
            {
                for (int i = 0; i < difference; i++)
                {
                    // add product to basket
                    basket.addProduct(product, basketProduct.getFinish(), basketProduct.getSize());
                }
            }
            // product must be removed
            else
            {
                for (int i = difference; i < 0; i++)
                {
                    // remove product from basket
                    basket.removeProduct(basketProduct);
                }
            }
            // add new header
            result.render("headerCartOverview", prepareCartOverviewInHeader(basket));
            // add totalPrice
            result.render("totalPrice", (basket.getTotalPriceAsString() + xcpConf.CURRENCY));
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
        Cart basket = Cart.getCartById(SessionHandling.getCartId(context));
        // get all products of the basket
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        List<CartProduct> basketProducts = Ebean.find(CartProduct.class).where().eq("cart", basket)
                                                .orderBy("lastUpdate desc").findList();
        // prepare just some attributes
        for (CartProduct basketProduct : basketProducts)
        {
            Map<String, Object> product = new HashMap<String, Object>();
            product.put("productCount", basketProduct.getProductCount());
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
        result.render("currency", xcpConf.CURRENCY);
        // add unit of length
        result.render("unitLength", xcpConf.UNIT_OF_LENGTH);
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
        Product product = Product.getProductById(Integer.parseInt(productId));
        // get basket by session
        Cart basket = Cart.getCartById(SessionHandling.getCartId(context));
        // get poster size
        String[] dummy = size.split(" ");
        int width = Integer.parseInt(dummy[0]);
        int height = Integer.parseInt(dummy[2]);
        PosterSize posterSize = Ebean.find(PosterSize.class).where().eq("width", width).eq("height", height)
                                     .findUnique();
        // add product to basket
        basket.addProduct(product, finish, posterSize);
        // get added basket product
        CartProduct basketProduct = Cart.getCartProduct(basket, product, finish, posterSize);
        Map<String, Object> updatedProduct = new HashMap<String, Object>();
        updatedProduct.put("productCount", basketProduct.getProductCount());
        updatedProduct.put("productName", basketProduct.getProduct().getName());
        updatedProduct.put("productId", basketProduct.getProduct().getId());
        updatedProduct.put("productPrice", basketProduct.getPriceAsString());
        updatedProduct.put("finish", finish);
        updatedProduct.put("size", basketProduct.getSize());
        // add product to result
        result.render("product", updatedProduct);
        // add currency
        result.render("currency", xcpConf.CURRENCY);
        // add unit of length
        result.render("unitLength", xcpConf.UNIT_OF_LENGTH);
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
        CartProduct basketProduct = Ebean.find(CartProduct.class, basketProductId);
        // get basket by session
        Cart basket = Cart.getCartById(SessionHandling.getCartId(context));
        // get count of this product
        int countProduct = basketProduct.getProductCount();
        // delete all items of this products
        for (int i = 0; i < countProduct; i++)
        {
            basket.removeProduct(basketProduct);
        }
        Result result = Results.json();
        // add new header
        result.render("headerCartOverview", prepareCartOverviewInHeader(basket));
        // add totalPrice
        result.render("totalPrice", (basket.getTotalPriceAsString() + xcpConf.CURRENCY));
        return result;
    }

    public Result updatePrice(@Param("size") String size, @Param("productId") int productId, Context context)
    {
        String[] dummy = size.split(" ");
        int width = Integer.parseInt(dummy[0]);
        int height = Integer.parseInt(dummy[2]);
        PosterSize posterSize = Ebean.find(PosterSize.class).where().eq("width", width).eq("height", height)
                                     .findUnique();
        Product product = Product.getProductById(productId);
        ProductPosterSize productPosterSize = Ebean.find(ProductPosterSize.class).where().eq("product", product)
                                                   .eq("size", posterSize).findUnique();
        Result result = Results.json();
        // add new price
        result.render("newPrice", productPosterSize.getPriceAsString() + xcpConf.CURRENCY);
        return result;
    }

    private String prepareCartOverviewInHeader(Cart basket)
    {
        StringBuilder headerCartOverview = new StringBuilder();
        headerCartOverview.append(" " + msg.get("basketOverviewTitle", language).get() + ": ");
        headerCartOverview.append(basket.getProductCount());
        headerCartOverview.append(" " + msg.get("basketItem", language).get() + " - ");
        headerCartOverview.append(basket.getTotalPriceAsString() + xcpConf.CURRENCY);
        return headerCartOverview.toString();
    }
}
