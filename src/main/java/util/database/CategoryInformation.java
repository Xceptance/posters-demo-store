package util.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.SubCategory;
import models.TopCategory;

import com.avaje.ebean.Ebean;

public abstract class CategoryInformation
{

    /**
     * Adds all categories to the data map.
     * 
     * @param data
     *            categories will be added here
     */
    public static void addCategoriesToMap(final Map<String, Object> data)
    {
        // get all top categories from database
        List<TopCategory> categories = Ebean.find(TopCategory.class).findList();
        // add top categories
        data.put("topCategory", categories);
    }

    /**
     * Adds the name of all sub categories to the data map.
     * 
     * @param data
     *            name of sub categories will added here
     */
    public static void addSubCategoriesToMap(final Map<String, Object> data)
    {
        // get all sub categories from database
        List<SubCategory> categories = Ebean.find(SubCategory.class).findList();
        // add name of all sub categories to data map
        for (int i = 0; i < categories.size(); i++)
        {
            Map<String, String> category = new HashMap<String, String>();
            data.put("subCategory" + (i + 1), category);
            category.put("name", categories.get(i).getName());
        }
    }

    /**
     * Adds the sub category to the given data map.
     * 
     * @param category
     * @param data
     */
    public static void addSubCategoryToMap(String subCategoryUrl, final Map<String, Object> data)
    {
        // get the sub category by the given category
        SubCategory category = Ebean.find(SubCategory.class).where().eq("url", subCategoryUrl).findUnique();
        // add the category to the data map
        data.put("category", category);
    }

    /**
     * Adds the top category to the given data map.
     * 
     * @param category
     * @param data
     */
    public static void addTopCategoryToMap(String topCategory, final Map<String, Object> data)
    {
        // get the top category by the given category
        TopCategory category = Ebean.find(TopCategory.class).where().eq("url", topCategory).findUnique();
        // add the category to the data map
        data.put("category", category);
    }
}
