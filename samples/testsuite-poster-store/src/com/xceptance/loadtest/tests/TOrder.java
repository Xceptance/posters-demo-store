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
 * Open the landing page, register account and browse the catalog to a random product. Configure this product and add it
 * to the cart. Finally process the checkout including the final order placement step.
 * 
 * @author sebastianloob
 */
public class TOrder extends AbstractTestCase
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
     * Create account data. 
     * This data will be used to register a new shop account.
     */
    private final Account account = new Account();
    
    /**
     *  Read the store URL from properties. Directly referring to the properties allows to access them by the full path.
     */
    private final String url = XltProperties.getInstance().getProperty("com.xceptance.xlt.loadtest.tests.store-url",
                                                               "http://localhost:8080/posters/");
    
    /**
     * Main test method.
     * 
     * @throws Throwable
     */
    @Test
    public void order() throws Throwable
    {

        // Go to poster store homepage
        Homepage homepage = new Homepage(url);
        // Disable JavaScript to reduce client side resource consumption
        // If JavaScript executes needed functionality (i.e. AJAX calls) we will simulate this in the related action
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
