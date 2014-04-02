package com.xceptance.loadtest.tests;

import org.junit.Test;


import com.xceptance.loadtest.actions.AddToCart;
import com.xceptance.loadtest.actions.Homepage;
import com.xceptance.loadtest.actions.order.ViewCart;
import com.xceptance.loadtest.flows.BrowsingFlow;
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
     * The previous action
     */
    private AbstractHtmlPageAction previousAction;
    
    /**
     *  The probability to perform a paging during browsing the categories
     */
    final int pagingProbability = getProperty("paging.probability", 0);
    
    /**
     *  The min number of paging rounds
     */
    final int pagingMin = getProperty("paging.min", 0);
    
    /**
     *  The max number of paging rounds
     */
    final int pagingMax = getProperty("paging.max", 0);
    
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
        Homepage homepage = new Homepage(url);
        homepage.run();
        previousAction = homepage;

        // Browse (FLOW!!)
        // TODO Add more comments to explain what a flow is and how it works
        BrowsingFlow browse = new BrowsingFlow(previousAction, pagingProbability, pagingMin, pagingMax);
        previousAction = browse.run();

        // Configure the product (size and finish) and add it to cart
        AddToCart addToCart = new AddToCart(previousAction);
        addToCart.run();
        previousAction = addToCart;

        // go to the cart overview page
        ViewCart viewCart = new ViewCart(previousAction);
        viewCart.run();
        previousAction = viewCart;
        
        // Checkout Flow
        CheckoutFlow checkoutFlow = new CheckoutFlow(previousAction, new Account());
        checkoutFlow.run();

    }
}
