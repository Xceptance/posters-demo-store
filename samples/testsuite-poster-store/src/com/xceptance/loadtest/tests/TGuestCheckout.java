package com.xceptance.loadtest.tests;

import org.junit.Test;


import com.xceptance.loadtest.actions.Homepage;
import com.xceptance.loadtest.flows.BrowseAndAddToCartFlow;
import com.xceptance.loadtest.flows.CheckoutFlow;
import com.xceptance.loadtest.util.Account;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.tests.AbstractTestCase;
import com.xceptance.xlt.api.util.XltProperties;

/**
 * Open the landing page and browse the catalog to a random product. Configure this product and add it to the cart.
 * Finally process the checkout. But do NOT execute the final order placement step. This is to simulate an abandoned checkout.
 * 
 */
public class TGuestCheckout extends AbstractTestCase
{
    /**
     * Main test method.
     * 
     * @throws Throwable
     */
    @Test
    public void guestCheckout() throws Throwable
    {
        // Read the store URL from properties. Directly referring to the properties allows to access them by the full
        // path.
        final String url = XltProperties.getInstance().getProperty("com.xceptance.xlt.loadtest.tests.store-url",
                                                                   "http://localhost:8080/posters/");


        // Go to poster store homepage
        Homepage homepage = new Homepage(url, "Homepage");
        homepage.run();

        // Browse and add a product to cart (FLOW!!)
        // TODO Add more comments to explain what a flow is and how it works
        BrowseAndAddToCartFlow browseAndAddToCart = new BrowseAndAddToCartFlow(homepage);
        AbstractHtmlPageAction viewCart = browseAndAddToCart.run();

        // Checkout Flow
        CheckoutFlow checkoutFlow = new CheckoutFlow(viewCart, new Account());
        checkoutFlow.run();

    }
}
