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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import io.ebean.DB;
import io.ebean.PagedList;
import io.ebean.Query;

import com.google.inject.Inject;

import conf.PosterConstants;
import conf.StatusConf;
import filters.SessionCustomerExistFilter;
import models.Product;
import models.SearchEngine;
import models.TopCategory;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.params.Param;
import ninja.params.PathParam;


/**
 * Controller class, that provides the search functionality.
 * 
 * @author sebastianloob
 */
public class SearchController
{

    @Inject
    Messages msg;

    @Inject
    PosterConstants xcpConf;

    @Inject
    SearchEngine searcher;

    @Inject
    StatusConf stsConf;

    private Optional<String> language = Optional.of("en");

    /**
     * Returns a product overview page with products, that matches the search text.
     * 
     * @param searchFormText
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerExistFilter.class)
    public Result searchProduct(@Param("searchText") final String searchFormText, final Context context, @PathParam("urlLocale") String locale) {
        language = Optional.of(locale);
        String searchText = searchFormText.trim();
        // search text is empty
        if (searchText.isEmpty()) {
            // show info message
            context.getFlashScope().put("error", msg.get("infoNoSearchTerm", language).get());
            // return index page
            return Results.redirect(context.getContextPath() + "/");
        } else {
            final Map<String, Object> data = new HashMap<String, Object>();
            // search for products
            final List<Product> products = searchForProducts(searchText, 1, data, locale);
            // load status configuration
            final Map<String, Object> status = new HashMap<String, Object>();
            stsConf.getStatus(status);
            // deliberately create incorrect behaviour if enabled (for testing and demo purposes)
            if (status.get("blockSearch").equals(true)) {
                if (status.get("blockedTerm").equals(searchText)) {
                    return Results.redirect(context.getContextPath() + "/" + locale + "/notFound");
                }
            }
            // no product was found
            if (products.isEmpty()) {
                // show info message
                context.getFlashScope().put("error", msg.get("infoNoSearchTerm", language).get());
                // return no result page
                return Results.redirect(context.getContextPath() + "/" + locale + "/notFound");
            }
            // at least one product was found
            else {
                data.put("products", products);
                WebShopController.setCommonData(data, context, xcpConf);
                data.put("searchText", searchText);// msg.get("searchProductMatch", language).get() + " '" + searchText
                                                   // + "'");
                data.put("searchTerm", searchText);
                data.put("currentPage", 1);
                // return product overview page
                return Results.html().render(data).template(xcpConf.TEMPLATE_PRODUCT_OVERVIEW);
            }
        }
    }

    /**
     * Returns a list of products as JSON, that matches the search text.
     * 
     * @param searchText
     * @param pageNumber
     * @param context
     * @return
     */ // data.put("productCounter", products.size());
    public Result getProductOfSearch(@Param("searchText") final String searchText, @Param("page") final int pageNumber,
            final Context context, @PathParam("urlLocale") String locale) {

        final Map<String, Object> data = new HashMap<String, Object>();

        // search for products
        final List<Product> products = searchForProducts(searchText, pageNumber, data, locale);

        // load the messages.properties file for the current locale to supply localized texts to JavaScript
        Properties mesprop = new Properties();
        try {
            InputStream input = new FileInputStream("./src/main/java/conf/messages_"+locale+".properties");
            mesprop.load(input);
            // text for buy here button
            data.put("BuyText", mesprop.getProperty("buttonBuyHere"));
            input.close();
        } catch (Exception e) {
            // The file could not be loaded or does not exist. Fall back to the default
            try {
                InputStream input = new FileInputStream("./src/main/java/conf/messages.properties");
                mesprop.load(input);
                // text for buy here button
                data.put("BuyText", mesprop.getProperty("buttonBuyHere"));
                input.close();
            } catch (Exception e2) {
                // Fall back to a hardcoded version
                data.put("BuyText", "Buy Here");
            }
        }

        // set some attributes to null to get a small-sized JSON
        for (int i = 0; i < products.size(); i++) {
            products.get(i).setAvailableSizes(null);
            products.get(i).setSubCategory(null);
            products.get(i).setTopCategory(null);
            products.get(i).setCart(null);
            products.get(i).setOrder(null);
        }

        data.put("products", products);
        data.put("currentPage", pageNumber);

        return Results.json().render(data);
    }

    /**
     * Returns a list of products that match the search text.
     * 
     * @param searchText The text to search for.
     * @param pageNumber The page number to fetch.
     * @param data       The data map to which pagination information will be added.
     * @return A list of products that match the search text.
     */
    private List<Product> searchForProducts(final String searchText, final int pageNumber, final Map<String, Object> data, String locale) {
        // load status configuration
        final Map<String, Object> status = new HashMap<String, Object>();
        stsConf.getStatus(status);
        // deliberately create incorrect behaviour if enabled (for testing and demo purposes)
        if (status.get("searchResultsChanged").equals(true))
        {
            if (locale.equals("de-DE"))
            {
                locale = "en-US";
            }
            else
            {
                locale = "de-DE";
            }
        }
        // Search products with search engine, second param is the limit for returned results
        List<Integer> resultIds = searcher.search(searchText, 20, locale);

        if (resultIds.isEmpty()) {
            return List.of();
        }
        else{
            // Create the query
        final Query<Product> query = DB.find(Product.class);

        // Add search conditions
        query.where().idIn(resultIds);
    
        final int pageSize = xcpConf.PRODUCTS_PER_PAGE;
    
        // Set pagination parameters
        query.setFirstRow((pageNumber - 1) * pageSize);
        query.setMaxRows(pageSize);
    
        // Create PagedList
        PagedList<Product> pagedList = query.findPagedList();
    
        // Load total row count in the background
        pagedList.loadCount();
    
        // Get the products of the current page
        List<Product> products = pagedList.getList();
    
        // Remove some data of the product list, to render a small-sized JSON
        for (Product product : products) {
            product.setAvailableSizes(null);
            product.setSubCategory(null);
            product.setTopCategory(null);
            product.setCart(null);
            product.setOrder(null);
        }
    
        // Get the total product count (waiting for the background task if necessary)
        int totalProductCount = pagedList.getTotalCount();
    
        // Calculate total pages
        int totalPages = (int) Math.ceil((double) totalProductCount / pageSize);
    
        // deliberately create incorrect behaviour if enabled (for testing and demo purposes)
        if (status.get("searchCounterWrong").equals(true)) {
            try {
                totalProductCount += (int)status.get("counterAdjustment");
            } catch (Exception e) {
                // since we only put ints in the map this should not happen, but better be safe here
            }
        }

        // Add the page count to the data map
        data.put("totalPages", totalPages);
        data.put("totalProductCount", totalProductCount);
        data.put("currentPage", pageNumber);

        return products;
        }
    }

    @FilterWith(SessionCustomerExistFilter.class)
    public Result noResult(final Context context)
    {
                // set categories

        
            final Map<String, Object> data = new HashMap<String, Object>();

            // set categories
            data.put("topCategory", TopCategory.getAllTopCategories());

            //Add category specific products from each category
        List<Product> productsListCat = new ArrayList<>();
        productsListCat.add(Product.getProductById(3));
        productsListCat.add(Product.getProductById(44));
        productsListCat.add(Product.getProductById(76));
        productsListCat.add(Product.getProductById(111));
        data.put("productslistcat", productsListCat);

            //final List<Product> products = searchForProducts(searchText, 1, data);
            WebShopController.setCommonData(data, context, xcpConf);

            // return no products found page
            return Results.html().render(data).template(xcpConf.TEMPLATE_PRODUCT_NOT_FOUND);

    }

}
