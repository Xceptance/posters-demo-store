package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Product;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.params.Param;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import com.avaje.ebean.Query;
import com.google.common.base.Optional;
import com.google.inject.Inject;

import conf.PosterConstants;
import filters.SessionCustomerExistFilter;

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

    private Optional<String> language = Optional.of("en");

    /**
     * Returns a product overview page with products, that matches the search text.
     * 
     * @param searchText
     * @param context
     * @return
     */
    @FilterWith(SessionCustomerExistFilter.class)
    public Result searchProduct(@Param("searchText") String searchText, Context context)
    {
        // search text is empty
        if (searchText.isEmpty() || searchText.trim().isEmpty())
        {
            // show info message
            context.getFlashCookie().put("info", msg.get("infoNoSearchTerm", language).get());
            // return index page
            return Results.redirect(context.getContextPath() + "/");
        }
        else
        {
            final Map<String, Object> data = new HashMap<String, Object>();
            // search for products
            List<Product> products = searchForProducts(searchText, 1, data);
            // no product was found
            if (products.isEmpty())
            {
                // show info message
                context.getFlashCookie().put("info", msg.get("infoNoSearchTerm", language).get());
                // return index page
                return Results.redirect(context.getContextPath() + "/");
            }
            // at least one product was found
            else
            {
                data.put("products", products);
                WebShopController.setCommonData(data, context, xcpConf);
                data.put("searchText", msg.get("searchProductMatch", language).get() + " '" + searchText + "'");
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
     */
    public Result getProductOfSearch(@Param("searchText") String searchText, @Param("page") int pageNumber,
                                     Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        // search for products
        List<Product> products = searchForProducts(searchText, pageNumber, data);
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
    private List<Product> searchForProducts(String searchText, int pageNumber, final Map<String, Object> data)
    {
        // build query string
        String queryString = "find product where ";
        // divide search text by spaces
        String[] searchTerms = searchText.split(" ");
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
        Query<Product> query = Ebean.createQuery(Product.class, queryString);
        int pageSize = xcpConf.PRODUCTS_PER_PAGE;
        // get paging list
        PagingList<Product> pagingList = query.findPagingList(pageSize);
        // get all products to
        int totalProductCount = query.findList().size();
        // get row count in background
        pagingList.getFutureRowCount();
        // get the current page
        Page<Product> page = pagingList.getPage(pageNumber - 1);
        // get the products of the current page
        List<Product> products = page.getList();
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

        query.cancel();

        return products;
    }
}
