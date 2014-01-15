package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Product;
import models.SubCategory;
import models.TopCategory;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.params.Param;
import ninja.params.PathParam;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import com.google.inject.Inject;

import conf.XCPosterConf;

public class CatalogController
{

    @Inject
    Messages msg;

    @Inject
    XCPosterConf xcpConf;

    /**
     * Returns a product detail page for the given product.
     * 
     * @param productUrl
     * @param context
     * @return
     */
    public Result productDetail(@PathParam("product") String productUrl, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        WebShopController.setCommonData(data, context, xcpConf);
        // put product to data map
        data.put("productDetail", Product.getProductByUrl(productUrl));
        return Results.html().render(data);
    }

    /**
     * Returns a product overview page for the given sub category.
     * 
     * @param subCategory
     * @param context
     * @return
     */
    public Result productOverview(@PathParam("subCategory") String subCategory, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        int pageSize = xcpConf.pageSize;
        WebShopController.setCommonData(data, context, xcpConf);
        // add products of the given sub category to data map
        addSubCategoryProductsToMap(subCategory, 1, pageSize, data);
        // add sub category to data map
        data.put("category", SubCategory.getSubCategoryByUrl(subCategory));
        return Results.html().render(data);
    }

    /**
     * Returns a product overview page for the given top category.
     * 
     * @param topCategory
     * @param context
     * @return
     */
    public Result topCategoryOverview(@PathParam("topCategory") String topCategory, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        int pageSize = xcpConf.pageSize;
        WebShopController.setCommonData(data, context, xcpConf);
        // add products of the given top category to data map
        addTopCategoryProductsToMap(topCategory, 1, pageSize, data);
        // add top category to data map
        data.put("category", TopCategory.getTopCategoryByUrl(topCategory));
        // return product overview page
        return Results.html().template(xcpConf.templateProductOverview).render(data);
    }

    public Result getProductOfTopCategory(@Param("pathname") String pathname, @Param("page") int page)
    {
        Result result = Results.json();
        final Map<String, Object> data = new HashMap<String, Object>();
        int pageSize = xcpConf.pageSize;
        String topCategory = pathname.split("/")[2];
        // add products of the given top category to data map
        addTopCategoryProductsToMap(topCategory, page, pageSize, data);
        return result.render(data);
    }

    public Result getProductOfSubCategory(@Param("pathname") String pathname, @Param("page") int page)
    {
        Result result = Results.json();
        final Map<String, Object> data = new HashMap<String, Object>();
        int pageSize = xcpConf.pageSize;
        String subCategory = pathname.split("/")[2];
        // add products of the given sub category to data map
        addSubCategoryProductsToMap(subCategory, page, pageSize, data);
        return result.render(data);
    }

    /**
     * Adds all products, which should be shown in the given top category, to the given data map.
     * 
     * @param topCategory
     * @param data
     */
    private static void addTopCategoryProductsToMap(String topCategoryUrl, int pageNumber, int pageSize,
                                                    final Map<String, Object> data)
    {
        // get the given top category
        TopCategory category = Ebean.find(TopCategory.class).where().eq("url", topCategoryUrl).findUnique();
        // get the marked products, which should show in the top category
        PagingList<Product> pagingList = Ebean.find(Product.class).where().eq("topCategory", category)
                                              .eq("showInTopCategorie", true).findPagingList(pageSize);
        // add all products to the data map
        createPagingListProductOverview(pagingList, pageNumber, data);
    }

    /**
     * Adds all products from the given sub category to the given data map.
     * 
     * @param category
     * @param data
     */
    private static void addSubCategoryProductsToMap(String subCategoryUrl, int pageNumber, int pageSize,
                                                    final Map<String, Object> data)
    {
        // get the sub category by the given category
        SubCategory category = Ebean.find(SubCategory.class).where().eq("url", subCategoryUrl).findUnique();
        // get all products of the sub category
        PagingList<Product> pagingList = Ebean.find(Product.class).where().eq("subCategory", category)
                                              .findPagingList(pageSize);
        // add the products to the data map
        createPagingListProductOverview(pagingList, pageNumber, data);
    }

    private static void createPagingListProductOverview(PagingList<Product> pagingList, int pageNumber,
                                                        final Map<String, Object> data)
    {
        // get row count in background
        pagingList.getFutureRowCount();
        // get the current page
        Page<Product> page = pagingList.getPage(pageNumber - 1);
        // get the products of the current page
        List<Product> list = page.getList();
        // remove some informations of the product list, to render a small-sized json-object
        for (int i = 0; i < list.size(); i++)
        {
            list.get(i).setAvailableSizes(null);
            list.get(i).setSubCategory(null);
            list.get(i).setTopCategory(null);
            list.get(i).setCart(null);
            list.get(i).setOrder(null);
        }
        // get the total page count
        int pageCount = pagingList.getTotalPageCount();
        // add the products to the data map
        data.put("products", list);
        // add the page count to the data map
        data.put("totalPages", pageCount);
        data.put("currentPage", pageNumber);
    }
}
