package util.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.Product;
import models.SubCategory;
import models.TopCategory;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;

public abstract class ProductInformation
{

    /**
     * Adds all products from the given sub category to the given data map.
     * 
     * @param category
     * @param data
     */
    public static void addSubCategoryProductsToMap(String subCategoryUrl, int pageNumber, int pageSize,
                                                   final Map<String, Object> data)
    {
        // get the sub category by the given category
        SubCategory category = Ebean.find(SubCategory.class).where().eq("url", subCategoryUrl).findUnique();
        // get all products of the sub category
        PagingList<Product> pagingList = Ebean.find(Product.class).where().eq("subCategory", category)
                                              .findPagingList(pageSize);
        // add the products to the data map
        createPagingListProductOverview(pagingList, pageNumber, data);
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
    public static void addTopCategoryProductsToMap(String topCategoryUrl, int pageNumber, int pageSize,
                                                   final Map<String, Object> data)
    {
        // get the given top category
        TopCategory category = Ebean.find(TopCategory.class).where().eq("url", topCategoryUrl).findUnique();
        // get the marked products, which should show in the top category
        PagingList<Product> pagingList = Ebean.find(Product.class).where().eq("topCategory", category)
                                              .eq("showInTopCategorie", true).findPagingList(pageSize);
        // add all products to the data map
        createPagingListProductOverview(pagingList, pageNumber, data);
    }

    /**
     * Adds the given product to the data map.
     * 
     * @param productUrl
     * @param data
     */
    public static void addProductDetailToMap(String productUrl, final Map<String, Object> data)
    {
        // get product by url
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

    private static void createPagingListProductOverview(PagingList<Product> pagingList, int pageNumber,
                                                       final Map<String, Object> data)
    {
        // get row count in background
        pagingList.getFutureRowCount();
        // get the current page
        Page<Product> page = pagingList.getPage(pageNumber - 1);
        // get the products of the current page
        List<Product> list = page.getList();
        // get the total page count
        int pageCount = pagingList.getTotalPageCount();
        // add the products to the data map
        data.put("products", list);
        // add the page count to the data map
        data.put("pageCount", pageCount);
    }
}
