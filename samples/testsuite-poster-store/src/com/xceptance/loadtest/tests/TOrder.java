package com.xceptance.loadtest.tests;

import org.junit.Test;

import com.xceptance.loadtest.actions.AddToCart;
import com.xceptance.loadtest.actions.EnterBillingAddress;
import com.xceptance.loadtest.actions.EnterShippingAddress;
import com.xceptance.loadtest.actions.GoToRegistrationForm;
import com.xceptance.loadtest.actions.GoToSignIn;
import com.xceptance.loadtest.actions.Homepage;
import com.xceptance.loadtest.actions.Login;
import com.xceptance.loadtest.actions.ProductDetailView;
import com.xceptance.loadtest.actions.Register;
import com.xceptance.loadtest.actions.SelectCategory;
import com.xceptance.loadtest.actions.StartCheckout;
import com.xceptance.loadtest.actions.ViewCart;
import com.xceptance.loadtest.util.Account;
import com.xceptance.loadtest.util.Address;
import com.xceptance.loadtest.util.CreditCard;
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
     * Main test method.
     * 
     * @throws Throwable
     */
    @Test
    public void order() throws Throwable
    {
        // Read the store URL from properties. Directly referring to the properties allows to access them by the full
        // path.
        final String url = XltProperties.getInstance().getProperty("com.xceptance.xlt.loadtest.tests.store-url",
                                                                   "http://localhost:8080/");

        // Create new account data. These account data will be used to create a new account.
        Account account = new Account();

        // Create new address. Use this address as shipping address.
        Address shippingAddress = new Address();

        // Create new address. Use this address as billing address.
        Address billingAddress = new Address();

        // Create new credit card. Use this credit card as payment method.
        CreditCard creditCard = new CreditCard();

        // Go to poster store homepage
        Homepage homepage = new Homepage(url, "Homepage");
        homepage.run();

        // go to sign in
        GoToSignIn goToSignIn = new GoToSignIn(homepage, "GoToSignIn");
        goToSignIn.run();

        // go to registration form
        GoToRegistrationForm goToRegistrationForm = new GoToRegistrationForm(goToSignIn, "GoToRegistrationForm");
        goToRegistrationForm.run();

        // register
        Register register = new Register(goToRegistrationForm, "Register", account);
        register.run();

        // log in
        Login login = new Login(register, "Login", account);
        login.run();

        // Select a random level-1 category from side navigation
        SelectCategory selectCategory = new SelectCategory(login, "SelectCategory");
        selectCategory.run();

        // Select a random poster from product overview and show product detail page
        ProductDetailView productDetailView = new ProductDetailView(selectCategory, "ProductDetailView");
        productDetailView.run();

        // Configure the product (size and finish) and add it to cart
        AddToCart addToCart = new AddToCart(productDetailView, "AddToCart");
        addToCart.run();

        // go to the cart overview page
        ViewCart viewCart = new ViewCart(addToCart, "ViewCart");
        viewCart.run();

        // start the checkout
        StartCheckout startCheckout = new StartCheckout(viewCart, "StartCheckout");
        startCheckout.run();

        // enter the shipping address
        EnterShippingAddress enterShippingAddress = new EnterShippingAddress(startCheckout, "EnterShippingAddress",
                                                                             account, shippingAddress);
        enterShippingAddress.run();

        // enter the billing address
        EnterBillingAddress enterBillingAddress = new EnterBillingAddress(enterShippingAddress, "EnterBillingAddress",
                                                                          account, billingAddress);
        enterBillingAddress.run();

        // enter the payment method

        // place the order
    }
}
