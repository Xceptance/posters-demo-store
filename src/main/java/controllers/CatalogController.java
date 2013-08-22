package controllers;

import java.util.HashMap;
import java.util.Map;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.PathParam;
import util.database.CategoryInformation;
import util.database.CommonInformation;
import util.database.ProductInformation;

import com.google.inject.Inject;

import conf.XCPosterConf;

public class CatalogController
{

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
    public Result productOverview(@PathParam("subCategory") String subCategory,
                                  @PathParam("pageNumber") int pageNumber, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        int pageSize = xcpConf.pageSize;
        CommonInformation.setCommonData(data, context, xcpConf);
        // add products of the given sub category to data map
        ProductInformation.addSubCategoryProductsToMap(subCategory, pageNumber, pageSize, data);
        // add sub category to data map
        CategoryInformation.addSubCategoryToMap(subCategory, data);
        data.put("isSubCategory", true);
        return Results.html().render(data);
    }

    /**
     * Returns a product overview page for the given top category.
     * 
     * @param topCategory
     * @param context
     * @return
     */
    public Result topCategoryOverview(@PathParam("topCategory") String topCategory,
                                      @PathParam("pageNumber") int pageNumber, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        int pageSize = xcpConf.pageSize;
        CommonInformation.setCommonData(data, context, xcpConf);
        // add products of the given top category to data map
        ProductInformation.addTopCategoryProductsToMap(topCategory, pageNumber, pageSize, data);
        // add top category to data map
        CategoryInformation.addTopCategoryToMap(topCategory, data);
        data.put("isTopCategory", true);
        // return product overview page
        return Results.html().template(xcpConf.templateProductOverview).render(data);
    }
}
