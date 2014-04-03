package com.xceptance.loadtest.tests;

import org.junit.Test;

import com.xceptance.loadtest.actions.Homepage;
import com.xceptance.loadtest.actions.account.GoToRegistrationForm;
import com.xceptance.loadtest.actions.account.GoToSignIn;
import com.xceptance.loadtest.actions.account.Login;
import com.xceptance.loadtest.actions.account.Logout;
import com.xceptance.loadtest.actions.account.Register;
import com.xceptance.loadtest.util.Account;
import com.xceptance.xlt.api.tests.AbstractTestCase;
import com.xceptance.xlt.api.util.XltProperties;

/**
 * Open landing page and navigate to the registration form. Register a new customer, log in with new account and
 * log out afterwards.
 */
public class TRegister extends AbstractTestCase
{
    /**
     * Main test method.
     * 
     * @throws Throwable
     */
    @Test
    public void register() throws Throwable
    {
	// Create new account data. This account data will be used to create a new account.
	Account account = new Account();

	// Read the store URL from properties.
	final String url = XltProperties.getInstance().getProperty("com.xceptance.xlt.loadtest.tests.store-url", "http://localhost:8080/posters/");
	
	
	// Go to poster store homepage
	final Homepage homepage = new Homepage(url);
	// Disable JavaScript for the complete test case to reduce client side resource consumption.
	// If JavaScript executed functionality is needed to proceed with the scenario (i.e. AJAX calls) 
	// we will simulate this in the related actions.
	homepage.getWebClient().getOptions().setJavaScriptEnabled(false);
	homepage.run();

        // go to sign in
        GoToSignIn goToSignIn = new GoToSignIn(homepage);
        goToSignIn.run();

        // go to registration form
        GoToRegistrationForm goToRegistrationForm = new GoToRegistrationForm(goToSignIn);
        goToRegistrationForm.run();

        // register
        Register register = new Register(goToRegistrationForm, account);
        register.run();

        // log in
        Login login = new Login(register, account);
        login.run();

        // log out
        Logout logout = new Logout(login);
        logout.run();
    }
}
