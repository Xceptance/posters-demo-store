package com.xceptance.loadtest.tests;

import org.junit.Test;

import com.xceptance.loadtest.actions.AddToCart;
import com.xceptance.loadtest.actions.Homepage;
import com.xceptance.loadtest.actions.account.GoToRegistrationForm;
import com.xceptance.loadtest.actions.account.GoToSignIn;
import com.xceptance.loadtest.actions.account.Login;
import com.xceptance.loadtest.actions.account.Logout;
import com.xceptance.loadtest.actions.account.Register;
import com.xceptance.loadtest.actions.order.PlaceOrder;
import com.xceptance.loadtest.actions.order.ViewCart;
import com.xceptance.loadtest.flows.BrowsingFlow;
import com.xceptance.loadtest.flows.CheckoutFlow;
import com.xceptance.loadtest.util.Account;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.tests.AbstractTestCase;
import com.xceptance.xlt.api.util.XltProperties;

/**
 * Open the landing page, register account and browse the catalogue to a random product. Configure this product and add it
 * to the cart. Finally process the checkout including the final order placement step.
 * 
 */
public class TOrder extends AbstractTestCase
{
    /**
     * Main test method.
     * 
     * @throws Throwable
     */
    @Test
    public void order() throws Throwable
    {
	// The previous action
	AbstractHtmlPageAction previousAction;

	// Create new account data. These account data will be used to create a new account.
	Account account = new Account();

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

        // go to sign in
        GoToSignIn goToSignIn = new GoToSignIn(previousAction);
        goToSignIn.run();
        previousAction = goToSignIn;

        // go to registration form
        GoToRegistrationForm goToRegistrationForm = new GoToRegistrationForm(previousAction);
        goToRegistrationForm.run();
        previousAction = goToRegistrationForm;

        // register
        Register register = new Register(previousAction, account);
        register.run();
        previousAction = register;

        // log in
        Login login = new Login(previousAction, account);
        login.run();
        previousAction = login;

	// Browse the catalogue and view a product detail page
	// The browsing is encapsulated in flow that combines a sequence of several XLT actions.
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
        CheckoutFlow checkoutFlow = new CheckoutFlow(previousAction, account);
        previousAction = checkoutFlow.run();

        // place the order
        PlaceOrder placeOrder = new PlaceOrder(previousAction, "PlaceOrder");
        placeOrder.run();
        previousAction = placeOrder;
        
        // log out
        Logout logout = new Logout(previousAction);
        logout.run();
    }
}
