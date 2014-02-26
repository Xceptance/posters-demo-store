package com.xceptance.loadtest.tests;

import org.junit.Test;

import com.xceptance.loadtest.actions.Homepage;
import com.xceptance.loadtest.actions.catalog.Paging;
import com.xceptance.loadtest.actions.catalog.ProductDetailView;
import com.xceptance.loadtest.actions.catalog.SelectCategory;
import com.xceptance.loadtest.actions.catalog.SelectTopCategory;
import com.xceptance.xlt.api.tests.AbstractTestCase;
import com.xceptance.xlt.api.util.XltProperties;

/**
 * Open the homepage, browse the catalog. If there's a product overview open random poster's detail view.
 **/
public class TBrowse extends AbstractTestCase
{
    /**
     * Main test method
     */
    @Test
    public void browsePosterStore() throws Throwable
    {
        // Read the store URL from properties. Directly referring to the properties allows to access them by the full
        // path.
        final String url = XltProperties.getInstance().getProperty("com.xceptance.xlt.loadtest.tests.store-url",
                                                                   "http://localhost:8080/");

        // Go to poster store homepage
        Homepage homepage = new Homepage(url, "Homepage");
        homepage.run();

        // Select a random top category from side navigation
        SelectTopCategory selectTopCategory = new SelectTopCategory(homepage, "SelectTopCategory");
        selectTopCategory.run();

        // Select a random level-1 category from side navigation
        SelectCategory selectCategory = new SelectCategory(selectTopCategory, "SelectCategory");
        selectCategory.run();

        // Page through results
        Paging paging = new Paging(selectCategory, "Paging");
        paging.run();

        // Select a random poster from product overview and show product detail page
        ProductDetailView productDetailView = new ProductDetailView(paging, "ProductDetailView");
        productDetailView.run();

    }
}
