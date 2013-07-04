package controllers;

import java.util.HashMap;
import java.util.Map;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.PathParam;
import util.database.CommonInformation;
import util.database.ProductInformation;

public class CatalogController {

	/**
	 * Returns a product detail page for the given product.
	 * 
	 * @param productUrl
	 * @param context
	 * @return
	 */
	public Result productDetail(@PathParam("product") String productUrl,
			Context context) {
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
	public Result productOverview(@PathParam("subCategory") String subCategory,
			Context context) {

		final Map<String, Object> data = new HashMap<String, Object>();
		CommonInformation.setCommonData(data, context);
		// put products for the given sub category to data map
		ProductInformation.addSubCategoryProductsToMap(subCategory, data);

		return Results.html().render(data);
	}

	/**
	 * Returns a product overview page for the given top category.
	 * 
	 * @param topCategory
	 * @param context
	 * @return
	 */
	public Result topCategoryOverview(
			@PathParam("topCategory") String topCategory, Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();
		CommonInformation.setCommonData(data, context);
		// put products for the given top category to data map
		ProductInformation.addTopCategoryProductsToMap(topCategory, data);

		return Results.html()
				.template("views/CatalogController/productOverview.ftl.html")
				.render(data);
	}
}
