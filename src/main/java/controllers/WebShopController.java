package controllers;

import java.util.HashMap;
import java.util.Map;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import util.database.CarouselInformation;
import util.database.CommonInformation;

public class WebShopController
{

    /**
     * Returns the main page of the web shop.
     * 
     * @param context
     * @return
     */
    public Result index(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        CommonInformation.setCommonData(data, context);
        // remember products for carousel
        CarouselInformation.getCarouselProducts(data);
        return Results.html().render(data);
    }
}
