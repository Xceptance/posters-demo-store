package util.database;

import java.util.List;
import java.util.Map;

import models.Product;

import com.avaje.ebean.Ebean;

public abstract class CarouselInformation
{

    /**
     * Adds all products to the data map, which should be shown in the carousel on the main page.
     * 
     * @param data
     */
    public static void getCarouselProducts(final Map<String, Object> data)
    {
        // find all products
        List<Product> products = Ebean.find(Product.class).where().eq("showInCarousel", true).findList();
        // add products to data map
        data.put("carousel", products);
    }
}
