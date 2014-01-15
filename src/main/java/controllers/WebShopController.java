package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Product;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import util.database.CommonInformation;

import com.avaje.ebean.Ebean;
import com.google.inject.Inject;

import conf.XCPosterConf;

public class WebShopController
{

    @Inject
    XCPosterConf xcpConf;

    /**
     * Returns the main page of the web shop.
     * 
     * @param context
     * @return
     */
    public Result index(Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        CommonInformation.setCommonData(data, context, xcpConf);
        // get all products, which should be shown in the carousel on the main page.
        List<Product> products = Ebean.find(Product.class).where().eq("showInCarousel", true).findList();
        // add products to data map
        data.put("carousel", products);
        return Results.html().render(data);
    }
}
