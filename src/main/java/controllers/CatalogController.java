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

	public Result productDetail(@PathParam("product") String productUrl,
			Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		CommonInformation.setOverallData(data, context);

		// remember product for product detail site
		ProductInformation.addProductDetailToMap(productUrl, data);

		return Results.html().render(data);
	}

	public Result productOverview(@PathParam("subCategory") String subCategory,
			Context context) {

		final Map<String, Object> data = new HashMap<String, Object>();
		CommonInformation.setOverallData(data, context);
		// remember products for the given sub category
		ProductInformation.addSubCategoryProductsToMap(subCategory, data);

		return Results.html().render(data);
	}

	public Result topCategoryOverview(
			@PathParam("topCategory") String topCategory, Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();
		CommonInformation.setOverallData(data, context);
		// remember products for the given top category
		ProductInformation.addTopCategoryProductsToMap(topCategory, data);

		return Results.html()
				.template("views/CatalogController/productOverview.ftl.html")
				.render(data);
	}
}
