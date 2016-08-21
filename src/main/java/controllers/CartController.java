package controllers;

import java.text.DecimalFormat;
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
import filters.SessionCustomerExistFilter;
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

    private final Optional<String> language = Optional.of("en");

    /**
     * Returns the cart overview page.
     * 
     * @param context
     * @return The cart overview page.
     */
    @FilterWith(SessionCustomerExistFilter.class)
    public Result cart(final Context context)
    {

        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
     
        final double subTotalPrice = Double.parseDouble(data.get("subTotalPrice").toString());
        final double subTotalTaxPrice = xcpConf.TAX * subTotalPrice;
        final double totalPrice = subTotalPrice + subTotalTaxPrice + xcpConf.SHIPPING_COSTS;

        // add currency
        data.put("currency", xcpConf.CURRENCY);
        // add tax in percent
        data.put("tax", (xcpConf.TAX * 100));
        // add SHIPPING_COSTS
        data.put("shippingCosts", getDoubleAsString(xcpConf.SHIPPING_COSTS));
        // add sub total price
        data.put("subOrderTotal", getDoubleAsString(subTotalPrice));
        // add sub total tax
        data.put("subOrderTotalTax", getDoubleAsString(subTotalTaxPrice));
        // add total price
        data.put("orderTotal", getDoubleAsString(totalPrice));
      
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
    public Result updateProductCount(@Param("cartProductId") final int cartProductId, @Param("productCount") final String productCount,
                                     final Context context)
    {
        // result is a json
        final Result result = Results.json();
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
            final Cart cart = Cart.getCartById(SessionHandling.getCartId(context, xcpConf));
            // get cart product by id
            final CartProduct cartProduct = Ebean.find(CartProduct.class, cartProductId);
            final Product product = cartProduct.getProduct();
            final int currentProductCount = cartProduct.getProductCount();
            final int difference = newProductCount - currentProductCount;
            
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

            result.render("unitPrice", cartProduct.getPriceAsString());
            result.render("totalUnitPrice", getDoubleAsString(cartProduct.getPrice() * newProductCount));

            // add new header
            result.render("headerCartOverview", prepareCartOverviewInHeader(cart));
            // add currency
            result.render("currency", xcpConf.CURRENCY);
            // add unit of length
            result.render("unitLength", xcpConf.UNIT_OF_LENGTH);

            // add sub order total price
            result.render("subTotalPrice", cart.getSubTotalPriceAsString());
            // add SHIPPING_COSTS
            result.render("shippingCosts", cart.getShippingCostsAsString());
            // add tax in percent
            result.render("tax", cart.getTax());
            // add sub total tax
            result.render("totalTaxPrice", cart.getTotalTaxPriceAsString());
            // add total price
            result.render("totalPrice", cart.getTotalPriceAsString());

            return result;
        }
    }

    /**
     * Returns all products of the cart as a json object.
     * 
     * @param context
     * @return
     */
    public Result getMiniCartElements(final Context context)
    {
        // get cart by session
        final Cart cart = Cart.getCartById(SessionHandling.getCartId(context, xcpConf));
        // get all products of the cart
        final List<Map<String, Object>> cartElements = new ArrayList<Map<String, Object>>();
        final List<CartProduct> cartProducts = Ebean.find(CartProduct.class).where().eq("cart", cart).orderBy("lastUpdate desc").findList();
        // prepare just some attributes
        for (final CartProduct cartProduct : cartProducts)
        {
            final Map<String, Object> product = new HashMap<String, Object>();
            product.put("productId", cartProduct.getProduct().getId());
            product.put("productName", cartProduct.getProduct().getName());
            product.put("productPrice", cartProduct.getPriceAsString());
            product.put("productCount", cartProduct.getProductCount());
            product.put("finish", cartProduct.getFinish());
            product.put("size", cartProduct.getSize());
            cartElements.add(product);
        }

        final Result result = Results.json();

        // add products
        result.render("productsInCartList", cartElements);
        // add currency
        result.render("currency", xcpConf.CURRENCY);
        // add unit of length
        result.render("unitLength", xcpConf.UNIT_OF_LENGTH);
        // add tax in percent
        result.render("tax", (xcpConf.TAX * 100));
        // add SHIPPING_COSTS
        result.render("shippingCosts", xcpConf.SHIPPING_COSTS);
        // add sub total price
        result.render("subOrderTotal", cart.getTotalPriceAsString());
        // add sub total price
        result.render("subOrderTotalTax", xcpConf.TAX);
        // add total price
        result.render("orderTotal", cart.getTotalPriceAsString(cart.getTotalPrice(), xcpConf.TAX, xcpConf.SHIPPING_COSTS));

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
    public Result addToCart(@Param("productId") final String productId, @Param("finish") final String finish,
                            @Param("size") final String size, final Context context)
    {
        final Result result = Results.json();
        // get product by id
        final Product product = Product.getProductById(Integer.parseInt(productId));
        // get cart by session
        final Cart cart = Cart.getCartById(SessionHandling.getCartId(context, xcpConf));
        // get poster size
        final String[] dummy = size.split(" ");
        final int width = Integer.parseInt(dummy[0]);
        final int height = Integer.parseInt(dummy[2]);
        final PosterSize posterSize = Ebean.find(PosterSize.class).where().eq("width", width).eq("height", height).findUnique();
        // add product to cart
        cart.addProduct(product, finish, posterSize);
        // get added cart product
        final CartProduct cartProduct = cart.getCartProduct(product, finish, posterSize);
        final Map<String, Object> updatedProduct = new HashMap<String, Object>();
        updatedProduct.put("productCount", cartProduct.getProductCount());
        updatedProduct.put("productName", cartProduct.getProduct().getName());
        updatedProduct.put("productId", cartProduct.getProduct().getId());
        updatedProduct.put("productPrice", cartProduct.getPriceAsString());
        updatedProduct.put("finish", finish);
        updatedProduct.put("size", cartProduct.getSize());

        // add product to result
        result.render("product", updatedProduct);
        // add new header to result
        result.render("headerCartOverview", prepareCartOverviewInHeader(cart));
        // add currency
        result.render("currency", xcpConf.CURRENCY);
        // add unit of length
        result.render("unitLength", xcpConf.UNIT_OF_LENGTH);
        // add tax in percent
        result.render("tax", (xcpConf.TAX * 100));
        // add SHIPPING_COSTS
        result.render("shippingCosts", xcpConf.SHIPPING_COSTS);
        // add sub total price
        result.render("subOrderTotal", cart.getSubTotalPriceAsString());
        // add sub total price
        result.render("subOrderTotalTax", xcpConf.TAX);
        // add total price
        result.render("orderTotal", cart.getTotalPriceAsString(cart.getSubTotalPrice(), xcpConf.TAX, xcpConf.SHIPPING_COSTS));

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
    public Result deleteFromCart(@Param("cartProductId") final int cartProductId, final Context context)
    {
        final CartProduct cartProduct = Ebean.find(CartProduct.class, cartProductId);
        // get cart by session
        final Cart cart = Cart.getCartById(SessionHandling.getCartId(context, xcpConf));
        // get count of this product
        final int countProduct = cartProduct.getProductCount();
        // delete all items of this products
        for (int i = 0; i < countProduct; i++)
        {
            cart.removeProduct(cartProduct);
        }
        final Result result = Results.json();

        // add new header
        result.render("headerCartOverview", prepareCartOverviewInHeader(cart));
        // add currency
        result.render("currency", xcpConf.CURRENCY);
        // add unit of length
        result.render("unitLength", xcpConf.UNIT_OF_LENGTH);
        // add tax in percent
        result.render("tax", (xcpConf.TAX * 100));
        // add SHIPPING_COSTS
        result.render("shippingCosts", xcpConf.SHIPPING_COSTS);
        // add sub total price
        result.render("subOrderTotal", cart.getTotalPriceAsString());
        // add sub total price tax
        result.render("subOrderTotalTax", xcpConf.TAX);
        // add total price
        result.render("orderTotal", cart.getTotalPriceAsString(cart.getTotalPrice(), xcpConf.TAX, xcpConf.SHIPPING_COSTS));

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
    public Result updatePrice(@Param("size") final String size, @Param("productId") final int productId, final Context context)
    {
        // split the size to the width and height
        final String[] dummy = size.split(" ");
        final int width = Integer.parseInt(dummy[0]);
        final int height = Integer.parseInt(dummy[2]);
        // get the specified poster size
        final PosterSize posterSize = Ebean.find(PosterSize.class).where().eq("width", width).eq("height", height).findUnique();
        // get the product
        final Product product = Product.getProductById(productId);
        // get the product poster size
        final ProductPosterSize productPosterSize = Ebean.find(ProductPosterSize.class).where().eq("product", product)
                                                         .eq("size", posterSize).findUnique();
        final Result result = Results.json();
        // add new price
        result.render("newPrice", xcpConf.CURRENCY + productPosterSize.getPriceAsString());
        return result;
    }

    /**
     * Returns the text for the cart overview in the header.
     * 
     * @param cart
     * @return
     */
    private String prepareCartOverviewInHeader(final Cart cart)
    {
        final StringBuilder headerCartOverview = new StringBuilder();
        headerCartOverview.append(cart.getProductCount());
        return headerCartOverview.toString();
    }

    /**
     * convert double into (price) string
     * 
     * @param price
     * @return
     */
    public String getDoubleAsString(final double value)
    {
        final DecimalFormat f = new DecimalFormat("#0.00");
        double temp = value;
        temp = temp * 100;
        temp = Math.round(temp);
        temp = temp / 100;
        return f.format(temp).replace(',', '.');
    }
}
