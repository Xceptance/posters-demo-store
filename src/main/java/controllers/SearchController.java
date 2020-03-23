package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import com.avaje.ebean.Query;
import com.google.inject.Inject;

import conf.PosterConstants;
import filters.SessionCustomerExistFilter;
import models.Product;
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
     * @param searchText
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
                return Results.redirect(context.getContextPath() + "/");
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
        final PagingList<Product> pagingList = query.findPagingList(pageSize);
        // get all products to
        final int totalProductCount = query.findList().size();
        // get row count in background
        pagingList.getFutureRowCount();
        // get the current page
        final Page<Product> page = pagingList.getPage(pageNumber - 1);
        // get the products of the current page
        final List<Product> products = page.getList();
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
}
