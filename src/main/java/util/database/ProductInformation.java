package util.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.Product;
import models.SubCategory;
import models.TopCategory;

import com.avaje.ebean.Ebean;

public abstract class ProductInformation
{

    /**
     * Adds all products from the given sub category to the given data map.
     * 
     * @param category
     * @param data
     */
    public static void addSubCategoryProductsToMap(String subCategoryUrl, final Map<String, Object> data)
    {
        // get the sub category by the given category
        SubCategory category = Ebean.find(SubCategory.class).where().eq("url", subCategoryUrl).findUnique();
        // get all products of the sub category
        List<Product> products = Ebean.find(Product.class).where().eq("subCategory", category).findList();
        // add the products to the data map
        data.put("products", products);
    }

    /**
     * Adds all products, which are saved in the database, to the given data map.
     * 
     * @param data
     */
    public static void addAllProductsToMap(final Map<String, Object> data)
    {

        // find all products in the database
        List<Product> products = Ebean.find(Product.class).findList();
        // add all products to the data map
        data.put("products", products);
    }

    /**
     * Adds all products, which should be shown in the given top category, to the given data map.
     * 
     * @param topCategory
     * @param data
     */
    public static void addTopCategoryProductsToMap(String topCategoryUrl, final Map<String, Object> data)
    {

        // get the given top category
        TopCategory category = Ebean.find(TopCategory.class).where().eq("url", topCategoryUrl).findUnique();
        // get all sub categories of the given top category
        List<SubCategory> subCategories = Ebean.find(SubCategory.class).where().eq("topCategory", category).findList();
        // list of products
        List<Product> products = new ArrayList<Product>();
        // get for each sub category the marked products
        for (SubCategory subCategory : subCategories)
        {
            products.addAll(Ebean.find(Product.class).where().eq("subCategory", subCategory)
                                 .eq("showInTopCategorie", true).findList());
        }
        // add all products to the data map
        data.put("products", products);
    }

    /**
     * Adds the given product to the data map.
     * 
     * @param productUrl
     * @param data
     */
    public static void addProductDetailToMap(String productUrl, final Map<String, Object> data)
    {
        // get product by product's url
        Product product = Ebean.find(Product.class).where().eq("url", productUrl).findUnique();
        data.put("productDetail", product);
    }

    /**
     * Returns a product by the product's id.
     * 
     * @param id
     * @return
     */
    public static Product getProductById(int id)
    {
        // get product by id
        Product product = Ebean.find(Product.class, id);
        return product;
    }
}
