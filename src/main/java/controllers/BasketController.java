package controllers;

import java.util.HashMap;
import java.util.Map;

import models.Basket;
import models.Product;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import util.database.BasketInformation;
import util.database.CommonInformation;
import util.database.ProductInformation;
import util.session.SessionHandling;

public class BasketController {

	/**
	 * Adds one product, given by the product id, to the basket.
	 * The basket will be chosen by the session.
	 * 
	 * @param productId The ID of the product.
	 * @param context
	 * @return The basket overview page.
	 */
	public Result addToCart(@Param("productName") String productId,
			Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		CommonInformation.setCommonData(data, context);

		// get product by id
		Product product = ProductInformation.getProductById(Integer
				.parseInt(productId));
		// get basket by session
		Basket basket = BasketInformation.getBasketById(SessionHandling
				.getBasketId(context));
		// put basket id to session
		SessionHandling.setBasketId(context, basket.getId());
		// add product to basket
		BasketInformation.addProductToBasket(basket, product);
		// put basket to data map
		BasketInformation.addBasketDetailToMap(basket, data);
		// return basket overview page
		return Results.html().render(data)
				.template("views/BasketController/basketOverview.ftl.html");
	}

	/**
	 * Returns the basket overview page.
	 * 
	 * @param context
	 * @return The basket overview page.
	 */
	public Result basket(Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		CommonInformation.setCommonData(data, context);
		// return basket overview page
		return Results.html().render(data)
				.template("views/BasketController/basketOverview.ftl.html");
	}
}
