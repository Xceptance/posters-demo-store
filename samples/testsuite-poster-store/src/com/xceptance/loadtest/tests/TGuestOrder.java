package com.xceptance.loadtest.tests;

import org.junit.Test;

import com.xceptance.loadtest.actions.AddToCart;
import com.xceptance.loadtest.actions.Homepage;
import com.xceptance.loadtest.actions.order.PlaceOrder;
import com.xceptance.loadtest.actions.order.ViewCart;
import com.xceptance.loadtest.flows.BrowsingFlow;
import com.xceptance.loadtest.flows.CheckoutFlow;
import com.xceptance.loadtest.util.Account;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.tests.AbstractTestCase;
import com.xceptance.xlt.api.util.XltProperties;

/**
 * Open the landing page and browse the catalogue to a random product. Configure this product and add it to the cart.
 * Finally process the checkout as guest including the final order placement step.
 */
public class TGuestOrder extends AbstractTestCase
{
    /**
     * Main test method.
     * 
     * @throws Throwable
     */
    @Test
    public void guestOrder() throws Throwable
    {
	// The previous action
	AbstractHtmlPageAction previousAction;

	// Read the store URL from properties.
	final String url = XltProperties.getInstance().getProperty("com.xceptance.xlt.loadtest.tests.store-url", "http://localhost:8080/posters/");

	// The probability to perform a paging during browsing the categories
	final int pagingProbability = getProperty("paging.probability", 0);

	// The min. number of paging rounds
	final int pagingMin = getProperty("paging.min", 0);

	// The max. number of paging rounds
	final int pagingMax = getProperty("paging.max", 0);


	// Go to poster store homepage
	final Homepage homepage = new Homepage(url);
	// Disable JavaScript for the complete test case to reduce client side resource consumption.
	// If JavaScript executed functionality is needed to proceed with the scenario (i.e. AJAX calls) 
	// we will simulate this in the related actions.
	homepage.getWebClient().getOptions().setJavaScriptEnabled(false);
	homepage.run();
	previousAction = homepage;

	// Browse the catalogue and view a product detail page
	// The browsing is encapsulated in a flow that combines a sequence of several XLT actions.
	// Different test cases can call this method now to reuse the flow. 
	// This is a concept for code structuring you can implement if needed, yet explicit support 
	// is neither available in the XLT framework nor necessary when you manually create a flow.
	BrowsingFlow browsingFlow = new BrowsingFlow(previousAction, pagingProbability, pagingMin, pagingMax);
	previousAction = browsingFlow.run();

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
        AbstractHtmlPageAction enterPaymentMethod = checkoutFlow.run();
        previousAction = enterPaymentMethod;

        // place the order
        PlaceOrder placeOrder = new PlaceOrder(previousAction);
        placeOrder.run();
    }
}
