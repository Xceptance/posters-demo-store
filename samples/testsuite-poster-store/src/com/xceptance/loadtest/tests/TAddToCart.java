package com.xceptance.loadtest.tests;

import org.junit.Test;

import com.xceptance.loadtest.actions.Homepage;
import com.xceptance.loadtest.flows.BrowseAndAddToCartFlow;
import com.xceptance.xlt.api.tests.AbstractTestCase;
import com.xceptance.xlt.api.util.XltProperties;

public class TAddToCart extends AbstractTestCase
{
    /**
     * Main test method
     */
    @Test
    public void addToCart() throws Throwable
    {
    // Read the store URL from properties. Directly referring to the properties allows to access them by the full
    // path.
    final String url = XltProperties.getInstance().getProperty("com.xceptance.xlt.loadtest.tests.store-url", "http://localhost:8080/");
    
    // Go to poster store homepage
    Homepage homepage = new Homepage(url, "Homepage");
    homepage.run();
    
    // Browse and add a product to cart (FLOW!!)
    // TODO Add more comments to explain what a flow is and how it works
    BrowseAndAddToCartFlow browseAndAddToCart = new BrowseAndAddToCartFlow(homepage);
    browseAndAddToCart.run();
    
    }
}
