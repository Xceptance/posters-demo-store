package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Product;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.params.Param;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.google.common.base.Optional;
import com.google.inject.Inject;

import conf.PosterConstants;

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
        // build SQL string
        String sql = "SELECT id, name, url, minimum_Price, description_detail FROM product where ";
        // divide search text by spaces
        String[] searchTerms = searchText.split(" ");
        // search in description detail
        sql += "LOWER(description_detail) LIKE LOWER('%" + searchTerms[0] + "%')";
        // search in name
        sql += " OR LOWER(name) LIKE LOWER('%" + searchTerms[0] + "%')";
        if (searchTerms.length > 1)
        {
            // add next search term to select statement
            for (int i = 1; i < searchTerms.length; i++)
            {
                sql += " OR LOWER(description_detail) LIKE LOWER('%" + searchTerms[i] + "%')";
                sql += " OR LOWER(name) LIKE LOWER('%" + searchTerms[i] + "%')";
            }
        }
        // create SQL statement
        RawSql rawSql = RawSqlBuilder.parse(sql).create();
        Query<Product> query = Ebean.find(Product.class);
        query.setRawSql(rawSql);
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
        return products;
    }
}
