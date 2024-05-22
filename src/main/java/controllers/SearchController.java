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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

import org.h2.mvstore.Page;

import io.ebean.Ebean;
import io.ebean.PagedList;
import io.ebean.Query;

import com.google.inject.Inject;

import conf.PosterConstants;
import filters.SessionCustomerExistFilter;
import models.Product;
import models.TopCategory;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.params.Param;


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

    private final Optional<String> language = Optional.of("en");

    /**
     * Returns a product overview page with products, that matches the search text.
     * 
     * @param searchFormText
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerExistFilter.class)
    public Result searchProduct(@Param("searchText") final String searchFormText, final Context context)
    {
        String searchText = searchFormText.trim();
        // search text is empty
        if (searchText.isEmpty())
        {
            // show info message
            context.getFlashScope().put("error", msg.get("infoNoSearchTerm", language).get());
            // return index page
            return Results.redirect(context.getContextPath() + "/");
        }
        else
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            // search for products
            final List<Product> products = searchForProducts(searchText, 1, data);
            // no product was found
            if (products.isEmpty())
            {
                // show info message
                context.getFlashScope().put("error", msg.get("infoNoSearchTerm", language).get());
                // return index page
                return Results.redirect(context.getContextPath() + "/notFound");
            }
            // at least one product was found
            else
            {
                data.put("products", products);
                WebShopController.setCommonData(data, context, xcpConf);
                data.put("searchText", searchText);//msg.get("searchProductMatch", language).get() + " '" + searchText + "'");
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
     */                //data.put("productCounter", products.size());
    public Result getProductOfSearch(@Param("searchText") final String searchText, @Param("page") final int pageNumber,
                                     final Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        // search for products
        final List<Product> products = searchForProducts(searchText, pageNumber, data);
        // set some attributes to null to get a small-sized JSON
        for (int i = 0; i < products.size(); i++)
        {
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
     * Returns a list of products, that matches the search text.
     * 
     * @param searchText
     * @param pageNumber
     * @param data
     * @return
     */
    private List<Product> searchForProducts(final String searchText, final int pageNumber, final Map<String, Object> data)
    {
        // build query string
        String queryString = "find product where ";
        // divide search text by spaces
        final String[] searchTerms = searchText.split(" ");
        // search in description detail
        queryString += "LOWER(description_detail) LIKE LOWER('%" + searchTerms[0] + "%')";
        // search in name
        queryString += " OR LOWER(name) LIKE LOWER('%" + searchTerms[0] + "%')";
        if (searchTerms.length > 1)
        {
            // add next search term to query
            for (int i = 1; i < searchTerms.length; i++)
            {
                queryString += " OR LOWER(description_detail) LIKE LOWER('%" + searchTerms[i] + "%')";
                queryString += " OR LOWER(name) LIKE LOWER('%" + searchTerms[i] + "%')";
            }
        }
        // create query
        final Query<Product> query = Ebean.createQuery(Product.class, queryString);
        final int pageSize = xcpConf.PRODUCTS_PER_PAGE;
        // get paging list
        final PagedList<Product> pagingList = query.findPagedList();
        // get all products to
        final int totalProductCount = query.findList().size();
        // get row count in background

        pagingList.getFutureCount();
        // get the current page
        //final PagedList<Product> page = pagingList.getp
        // get the products of the current page
        final List<Product> products = pagingList.getList();
        // remove some data of the product list, to render a small-sized JSON
        for (int i = 0; i < products.size(); i++)
        {
            products.get(i).setAvailableSizes(null);
            products.get(i).setSubCategory(null);
            products.get(i).setTopCategory(null);
            products.get(i).setCart(null);
            products.get(i).setOrder(null);
        }
        // add the page count to the data map
        data.put("totalPages", Math.ceil(totalProductCount / (double) pageSize));
        
        data.put("totalProductCount", totalProductCount);

        query.cancel();

        return products;
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
