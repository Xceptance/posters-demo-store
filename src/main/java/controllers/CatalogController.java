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

        CommonInformation.setCommonData(data, context);

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
        CommonInformation.setCommonData(data, context);
        // add products for the given sub category to data map
        ProductInformation.addSubCategoryProductsToMap(subCategory, data);
        // add sub category to data map
        CategoryInformation.addSubCategoryToMap(subCategory, data);
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
        CommonInformation.setCommonData(data, context);
        // add products for the given top category to data map
        ProductInformation.addTopCategoryProductsToMap(topCategory, data);
        // add top category to data map
        CategoryInformation.addTopCategoryToMap(topCategory, data);
        // return product overview page
        return Results.html().template(xcpConf.templateProductOverview).render(data);
    }
}
