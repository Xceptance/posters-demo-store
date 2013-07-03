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

	public Result addToCart(@Param("productName") String productId,
			Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		CommonInformation.setOverallData(data, context);

		// get product by id
		Product product = ProductInformation.getProductById(Integer
				.parseInt(productId));
		// get basket by session
		Basket basket = BasketInformation.getBasketById(SessionHandling
				.getBasketId(context));
		// put basket id to session
		SessionHandling.setBasketId(context, basket.getId());

		BasketInformation.addProductToBasket(basket, product);

		// System.out.println("---- Count " + basket.getProductCount().keySet()
		// + " ----");
		// System.out.println("---- Count of product " +
		// basket.getProductCount().values() + " ----");
		BasketInformation.addBasketDetailToMap(basket, data);

		return Results.html().render(data)
				.template("views/BasketController/basketOverview.ftl.html");
	}

	public Result basket(Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		CommonInformation.setOverallData(data, context);

		return Results.html().render(data)
				.template("views/BasketController/basketOverview.ftl.html");
	}
}
