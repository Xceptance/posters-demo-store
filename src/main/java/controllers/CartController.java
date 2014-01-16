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

/**
 * Controller class, that provides the cart functionality.
 * 
 * @author sebastianloob
 */
public class CartController
{

    @Inject
    Messages msg;

    @Inject
    PosterConstants xcpConf;

    private Optional<String> language = Optional.of("en");

    /**
     * Returns the cart overview page.
     * 
     * @param context
     * @return The cart overview page.
     */
    public Result cart(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // return cart overview page
        return Results.html().render(data).template(xcpConf.TEMPLATE_CART_OVERVIEW);
    }

    /**
     * Updates the product count of the given product.
     * 
     * @param cartProductId
     * @param productCount
     * @param context
     * @return
     */
    @FilterWith(SessionTerminatedFilter.class)
    public Result updateProductCount(@Param("basketProductId") int cartProductId,
                                     @Param("productCount") String productCount, Context context)
    {
        // result is a json
        Result result = Results.json();
        // product count doesn't match regex
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
            // get cart by session
            Cart cart = Cart.getCartById(SessionHandling.getCartId(context));
            // get cart product by id
            CartProduct cartProduct = Ebean.find(CartProduct.class, cartProductId);
            Product product = cartProduct.getProduct();
            int currentProductCount = cartProduct.getProductCount();
            int difference = newProductCount - currentProductCount;
            // product must be added
            if (difference > 0)
            {
                for (int i = 0; i < difference; i++)
                {
                    // add product to cart
                    cart.addProduct(product, cartProduct.getFinish(), cartProduct.getSize());
                }
            }
            // product must be removed
            else
            {
                for (int i = difference; i < 0; i++)
                {
                    // remove product from cart
                    cart.removeProduct(cartProduct);
                }
            }
            // add new header
            result.render("headerCartOverview", prepareCartOverviewInHeader(cart));
            // add totalPrice
            result.render("totalPrice", (cart.getTotalPriceAsString() + xcpConf.CURRENCY));
            return result;
        }
    }

    /**
     * Returns all products of the cart as a json object.
     * 
     * @param context
     * @return
     */
    public Result getCartElementSlider(Context context)
    {
        // get cart by session
        Cart cart = Cart.getCartById(SessionHandling.getCartId(context));
        // get all products of the cart
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        List<CartProduct> cartProducts = Ebean.find(CartProduct.class).where().eq("cart", cart)
                                              .orderBy("lastUpdate desc").findList();
        // prepare just some attributes
        for (CartProduct cartProduct : cartProducts)
        {
            Map<String, Object> product = new HashMap<String, Object>();
            product.put("productCount", cartProduct.getProductCount());
            product.put("productName", cartProduct.getProduct().getName());
            product.put("productId", cartProduct.getProduct().getId());
            product.put("productPrice", cartProduct.getPriceAsString());
            product.put("finish", cartProduct.getFinish());
            product.put("size", cartProduct.getSize());
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
        result.render("totalPrice", cart.getTotalPriceAsString());
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
        // get cart by session
        Cart cart = Cart.getCartById(SessionHandling.getCartId(context));
        // get poster size
        String[] dummy = size.split(" ");
        int width = Integer.parseInt(dummy[0]);
        int height = Integer.parseInt(dummy[2]);
        PosterSize posterSize = Ebean.find(PosterSize.class).where().eq("width", width).eq("height", height)
                                     .findUnique();
        // add product to cart
        cart.addProduct(product, finish, posterSize);
        // get added cart product
        CartProduct cartProduct = Cart.getCartProduct(cart, product, finish, posterSize);
        Map<String, Object> updatedProduct = new HashMap<String, Object>();
        updatedProduct.put("productCount", cartProduct.getProductCount());
        updatedProduct.put("productName", cartProduct.getProduct().getName());
        updatedProduct.put("productId", cartProduct.getProduct().getId());
        updatedProduct.put("productPrice", cartProduct.getPriceAsString());
        updatedProduct.put("finish", finish);
        updatedProduct.put("size", cartProduct.getSize());
        // add product to result
        result.render("product", updatedProduct);
        // add currency
        result.render("currency", xcpConf.CURRENCY);
        // add unit of length
        result.render("unitLength", xcpConf.UNIT_OF_LENGTH);
        // add new header to result
        result.render("headerCartOverview", prepareCartOverviewInHeader(cart));
        // add total price
        result.render("totalPrice", cart.getTotalPriceAsString());
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
        // get cart by session
        Cart cart = Cart.getCartById(SessionHandling.getCartId(context));
        // get count of this product
        int countProduct = basketProduct.getProductCount();
        // delete all items of this products
        for (int i = 0; i < countProduct; i++)
        {
            cart.removeProduct(basketProduct);
        }
        Result result = Results.json();
        // add new header
        result.render("headerCartOverview", prepareCartOverviewInHeader(cart));
        // add totalPrice
        result.render("totalPrice", (cart.getTotalPriceAsString() + xcpConf.CURRENCY));
        return result;
    }

    /**
     * Returns the price of the product with the given size.
     * 
     * @param size
     * @param productId
     * @param context
     * @return
     */
    public Result updatePrice(@Param("size") String size, @Param("productId") int productId, Context context)
    {
        // split the size to the width and height
        String[] dummy = size.split(" ");
        int width = Integer.parseInt(dummy[0]);
        int height = Integer.parseInt(dummy[2]);
        // get the specified poster size
        PosterSize posterSize = Ebean.find(PosterSize.class).where().eq("width", width).eq("height", height)
                                     .findUnique();
        // get the product
        Product product = Product.getProductById(productId);
        // get the product poster size
        ProductPosterSize productPosterSize = Ebean.find(ProductPosterSize.class).where().eq("product", product)
                                                   .eq("size", posterSize).findUnique();
        Result result = Results.json();
        // add new price
        result.render("newPrice", productPosterSize.getPriceAsString() + xcpConf.CURRENCY);
        return result;
    }

    /**
     * Returns the text for the cart overview in the header.
     * 
     * @param cart
     * @return
     */
    private String prepareCartOverviewInHeader(Cart cart)
    {
        StringBuilder headerCartOverview = new StringBuilder();
        headerCartOverview.append(" " + msg.get("basketOverviewTitle", language).get() + ": ");
        headerCartOverview.append(cart.getProductCount());
        headerCartOverview.append(" " + msg.get("basketItem", language).get() + " - ");
        headerCartOverview.append(cart.getTotalPriceAsString() + xcpConf.CURRENCY);
        return headerCartOverview.toString();
    }
}
