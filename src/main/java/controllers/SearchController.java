package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Product;
import models.SubCategory;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.params.Param;
import util.database.CarouselInformation;
import util.database.CommonInformation;

import com.avaje.ebean.Ebean;
import com.google.common.base.Optional;
import com.google.inject.Inject;

public class SearchController
{

    @Inject
    Messages msg;

    public Result search(@Param("searchText") String searchText, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        CommonInformation.setCommonData(data, context);
        String template;
        // search text is empty
        if (searchText.isEmpty() || searchText.equals(" "))
        {
            template = "views/WebShopController/index.ftl.html";
            Optional language = Optional.of("en");
            data.put("infoMessage", msg.get("infoNoSearchTerm", language).get());
            CarouselInformation.getCarouselProducts(data);
        }
        else
        {
            final List<Product> products = new ArrayList<Product>();
            // search products, which contain search text in the name
            mergeProductLists(products, searchProductName(searchText));
            // search products, which contain search text in the short description
            mergeProductLists(products, searchProductShortDescription(searchText));
            // search products, which contain search text in the long description
            mergeProductLists(products, searchProductLongDescription(searchText));
            // search products, which contain search text in category they belong to
            mergeProductLists(products, searchCategory(searchText));
            // search text contains at least two words
            if (searchText.contains(" "))
            {
                // divide search text by spaces
                String[] searchTerms = searchText.split(" ");
                for (String searchTerm : searchTerms)
                {
                    mergeProductLists(products, searchProductName(searchTerm));
                    mergeProductLists(products, searchProductShortDescription(searchText));
                    mergeProductLists(products, searchProductLongDescription(searchText));
                    mergeProductLists(products, searchCategory(searchTerm));
                }
            }
            data.put("products", products);

            if (products.isEmpty())
            {
                Optional language = Optional.of("en");
                data.put("noResults", msg.get("noSearchResults", language).get());
            }
            else
            {
                Optional language = Optional.of("en");
                data.put("searchText", msg.get("searchProductMatch", language).get() + " '" + searchText + "'");
            }
            template = "views/CatalogController/productOverview.ftl.html";
        }
        return Results.html().render(data).template(template);
    }

    /**
     * Returns a list of products, which contain the search text in the name.
     * 
     * @param searchText
     * @return
     */
    protected List<Product> searchProductName(String searchText)
    {
        return Ebean.find(Product.class).where().icontains("name", searchText).findList();
    }

    /**
     * Returns a list of products, which contain the search text in the long description.
     * 
     * @param searchText
     * @return
     */
    protected List<Product> searchProductLongDescription(String searchText)
    {
        return Ebean.find(Product.class).where().icontains("descriptionDetail", searchText).findList();
    }

    /**
     * Returns a list of products, which contain the search text in the short description.
     * 
     * @param searchText
     * @return
     */
    protected List<Product> searchProductShortDescription(String searchText)
    {
        return Ebean.find(Product.class).where().icontains("descriptionOverview", searchText).findList();
    }

    /**
     * Returns a list of products, which contain the search text in the category they belong to.
     * 
     * @param searchText
     * @return
     */
    protected List<Product> searchCategory(String searchText)
    {
        List<Product> products = new ArrayList<Product>();
        List<SubCategory> categories = Ebean.find(SubCategory.class).where().icontains("name", searchText).findList();
        for (SubCategory category : categories)
        {
            products.addAll(Ebean.find(Product.class).where().eq("subCategory", category).findList());
        }
        return products;
    }

    protected static void mergeProductLists(final List<Product> products, final List<Product> productsToAdd)
    {
        for (Product productToAdd : productsToAdd)
        {
            boolean isInList = false;
            for (int i = 0; i < products.size(); i++)
            {
                if (products.get(i).getId() == productToAdd.getId())
                {
                    isInList = true;
                }
            }
            if (!isInList)
            {
                products.add(productToAdd);
            }
        }
    }
}
