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
import ninja.params.PathParam;
import util.database.CarouselInformation;
import util.database.CommonInformation;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.google.common.base.Optional;
import com.google.inject.Inject;

import conf.XCPosterConf;

public class SearchController
{

    @Inject
    Messages msg;

    @Inject
    XCPosterConf xcpConf;

    private Optional language = Optional.of("en");

    public Result search(@Param("searchText") String searchText, Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        CommonInformation.setCommonData(data, context, xcpConf);
        String template;
        // search text is empty
        if (searchText.isEmpty() || searchText.equals(" "))
        {
            template = xcpConf.templateIndex;
            context.getFlashCookie().put("info", msg.get("infoNoSearchTerm", language).get());
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
                data.put("noResults", msg.get("noSearchResults", language).get());
            }
            else
            {
                data.put("searchText", msg.get("searchProductMatch", language).get() + " '" + searchText + "'");
            }
            template = xcpConf.templateProductOverview;
        }
        return Results.html().render(data).template(template);
    }

    public Result search2(@Param("searchText") String searchText, @PathParam("pageNumber") int pageNumber,
                          Context context)
    {
        final Map<String, Object> data = new HashMap<String, Object>();
        CommonInformation.setCommonData(data, context, xcpConf);
        String template;
        // search text is empty
        if (searchText.isEmpty() || searchText.equals(" "))
        {
            template = xcpConf.templateIndex;
            context.getFlashCookie().put("info", msg.get("infoNoSearchTerm", language).get());
            CarouselInformation.getCarouselProducts(data);
        }
        else
        {
            // build SQL string
            String sql = "SELECT id, name, url, price, description_detail FROM product where ";
            // divide search text by spaces
            String[] searchTerms = searchText.split(" ");
            // search in description detail
            sql += "LOWER(description_detail) LIKE LOWER('%" + searchTerms[0] + "%')";
            // search in name
            sql += " OR LOWER(name) LIKE LOWER('%" + searchTerms[0] + "%')";
            if (searchTerms.length > 1)
            {
                // add next search term to select statement
                for (int i = 1; i < searchTerms.length; i++)
                {
                    sql += " OR LOWER(description_detail) LIKE LOWER('%" + searchTerms[i] + "%')";
                    sql += " OR LOWER(name) LIKE LOWER('%" + searchTerms[i] + "%')";
                }
            }
            // create SQL statement
            RawSql rawSql = RawSqlBuilder.parse(sql).create();
            Query<Product> query = Ebean.find(Product.class);
            query.setRawSql(rawSql);
            // get paging list
            final PagingList<Product> pagingList = query.findPagingList(6);
            // get row count in background
            pagingList.getFutureRowCount();
            // get the current page
            Page<Product> page = pagingList.getPage(pageNumber - 1);
            // get the products of the current page
            List<Product> products = page.getList();
            // add the products to the data map
            data.put("products", products);
            // no product was found
            if (products.isEmpty())
            {
                data.put("pageCount", 0);
                data.put("noResults", msg.get("noSearchResults", language).get());
            }
            // at least one product was found
            else
            {
                // get the total page count
                int pageCount = pagingList.getTotalPageCount();
                // add the page count to the data map
                data.put("pageCount", pageCount);
                data.put("searchText", msg.get("searchProductMatch", language).get() + " '" + searchText + "'");
                data.put("isSearch", true);
                data.put("searchTerm", searchText);
            }
            template = xcpConf.templateProductOverview;
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
