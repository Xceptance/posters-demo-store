package controllers;

import java.util.HashMap;
import java.util.Map;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import util.database.CarouselInformation;
import util.database.CommonInformation;

public class WebShopController {

	public Result index(Context context) {
		final Map<String, Object> data = new HashMap<String, Object>();

		CommonInformation.setOverallData(data, context);

		// remember products for carousel
		CarouselInformation.getCarouselProducts(data);

		return Results.html().render(data);
	}
}
