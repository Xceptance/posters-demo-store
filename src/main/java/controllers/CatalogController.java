package controllers;

import java.util.HashMap;
import java.util.Map;

import models.SubCategory;
import models.TopCategory;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.params.Param;
import ninja.params.PathParam;
import util.database.CommonInformation;
import util.database.ProductInformation;

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
        CommonInformation.setCommonData(data, context, xcpConf);
        // put product to data map
        ProductInformation.addProductDetailToMap(productUrl, data);
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
        CommonInformation.setCommonData(data, context, xcpConf);
        // add products of the given sub category to data map
        ProductInformation.addSubCategoryProductsToMap(subCategory, 1, pageSize, data);
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
        CommonInformation.setCommonData(data, context, xcpConf);
        // add products of the given top category to data map
        ProductInformation.addTopCategoryProductsToMap(topCategory, 1, pageSize, data);
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
        ProductInformation.addTopCategoryProductsToMap(topCategory, page, pageSize, data);
        return result.render(data);
    }

    public Result getProductOfSubCategory(@Param("pathname") String pathname, @Param("page") int page)
    {
        Result result = Results.json();
        final Map<String, Object> data = new HashMap<String, Object>();
        int pageSize = xcpConf.pageSize;
        String subCategory = pathname.split("/")[2];
        // add products of the given sub category to data map
        ProductInformation.addSubCategoryProductsToMap(subCategory, page, pageSize, data);
        return result.render(data);
    }
}
