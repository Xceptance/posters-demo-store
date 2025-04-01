/*
 * Copyright (c) 2013-2024 Xceptance Software Technologies GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Cart;
import models.CartProduct;
import models.Customer;
import models.Product;
import models.TopCategory;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.PathParam;
import util.session.SessionHandling;

import io.ebean.Ebean;
import com.google.inject.Inject;

import conf.PosterConstants;
import filters.SessionCustomerExistFilter;

public class WebShopController
{

    @Inject
    PosterConstants xcpConf;

    /**
     * Returns the main page of the web shop.
     * 
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerExistFilter.class)
    public Result index(final Context context, @PathParam("urlLocale") String locale)
    {
        if(locale == null)
        {
            locale = "en-US";
        }
        final Map<String, Object> data = new HashMap<String, Object>();
        setCommonData(data, context, xcpConf);
        // get all products, which should be shown in the carousel on the main page.
        final List<Product> productsCarousel = Ebean.find(Product.class).where().eq("showInCarousel", true).findList();

        // add specific products to data map
        List<Product> productsList = new ArrayList<>();
        productsList.add(Product.getProductById(6));
        productsList.add(Product.getProductById(15));
        productsList.add(Product.getProductById(33));
        productsList.add(Product.getProductById(27));
        productsList.add(Product.getProductById(44));
        productsList.add(Product.getProductById(54));
        productsList.add(Product.getProductById(65));
        productsList.add(Product.getProductById(76));
        productsList.add(Product.getProductById(81));
        productsList.add(Product.getProductById(90));
        productsList.add(Product.getProductById(103));
        productsList.add(Product.getProductById(115));

        //Add category specific products from each category
        List<Product> productsListCat = new ArrayList<>();
        productsListCat.add(Product.getProductById(3));
        productsListCat.add(Product.getProductById(44));
        productsListCat.add(Product.getProductById(76));
        productsListCat.add(Product.getProductById(111));

        // add products to data map
        data.put("carousel", productsCarousel);
        data.put("productslist", productsList);
        data.put("productslistcat", productsListCat);
        Result result = Results.html().render(data);
        return result;

    }

    /**
     * Adds all categories to the map data. Sets the session cookie, if its unknown. Adds some information of the
     * current user's cart. Should be called before each page view.
     * 
     * @param data
     * @param context
     * @param xcpConf
     */
    public static void setCommonData(final Map<String, Object> data, final Context context, final PosterConstants xcpConf)
    {
        // set categories
        data.put("topCategory", TopCategory.getAllTopCategories());
        // get cart by session
        final Cart cart = Cart.getCartById(SessionHandling.getCartId(context, xcpConf));
        // set cart stuff
        addCartDetailToMap(cart, data, xcpConf);
        // a customer is logged
        if (SessionHandling.isCustomerLogged(context))
        {
            // get customer by session
            final Customer customer = Customer.getCustomerById(SessionHandling.getCustomerId(context));
            // add information that a customer is logged
            data.put("isLogged", true);
            // add customer's first name to data map
            data.put("customerFirstName", customer.getFirstName());
        }
        // no customer is logged
        else
        {
            data.put("isLogged", false);
        }
        // set application url
        data.put("applUrlHttp", xcpConf.APPLICATION_URL_HTTP + context.getContextPath());
        data.put("applUrlHttps", xcpConf.APPLICATION_URL_HTTPS + context.getContextPath());
        // set currency
        data.put("currency", xcpConf.CURRENCY);
        // add version information
        data.put("currentVersion", xcpConf.APPLICATION_VERSION);
        // add unit of length
        data.put("unitLength", xcpConf.UNIT_OF_LENGTH);
        // add email regex
        data.put("regexEmail", xcpConf.REGEX_EMAIL);
        // add name regex
        data.put("regexName", xcpConf.REGEX_NAME);
        // add creditcard regex
        data.put("regexCreditCard", xcpConf.REGEX_CREDITCARD);
        // add zip regex
        data.put("regexZip", xcpConf.REGEX_ZIP);
        // add product count regex
        data.put("regexProductCount", xcpConf.REGEX_PRODUCT_COUNT);
        // add current request path
        data.put("currPath", context.getRequestPath());
        // add current request path without locale
        data.put("staticPath", context.getRequestPath().replace("/"+context.getPathParameter("urlLocale"), ""));
        // add supported languages
        List<String> supportedLanguages = new ArrayList<>();
        String appLang = xcpConf.APPLICATION_LANGUAGES;
        supportedLanguages = Arrays.asList(appLang.split(","));
        data.put("supportedLanguages", supportedLanguages);
    }

    /**
     * Adds all products of the given cart to the data map.
     * 
     * @param cart
     * @param data
     */
    private static void addCartDetailToMap(Cart cart, final Map<String, Object> data, final PosterConstants xcpConf)
    {
        if (cart == null)
        {
            cart = Cart.createNewCart(xcpConf.TAX, xcpConf.SHIPPING_COSTS);
        }
        final Map<Product, Integer> products = new HashMap<Product, Integer>();
        int totalProductCount = 0;
        // get all products of the cart
        final List<CartProduct> cartProducts = Ebean.find(CartProduct.class).where().eq("cart", cart).orderBy("lastUpdate desc").findList();
        for (final CartProduct cartProduct : cartProducts)
        {
            products.put(cartProduct.getProduct(), cartProduct.getProductCount());
            totalProductCount += cartProduct.getProductCount();
        }
        
        cart.calculateTotalTaxPrice();
        cart.calculateTotalPrice();
        
        // add all products of the cart
        data.put("cartProducts", cartProducts);
        // add product count of cart
        data.put("cartProductCount", totalProductCount);
        // add cart id
        data.put("cartId", cart.getId());
        // add sub total price of cart
        data.put("subTotalPrice", cart.getSubTotalPriceAsString());
        data.put("subOrderTotalTax", cart.getTotalTaxPriceAsString());
        data.put("totalPrice", cart.getTotalPriceAsString());
    }
}
