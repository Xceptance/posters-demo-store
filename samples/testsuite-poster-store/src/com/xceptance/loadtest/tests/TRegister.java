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
 * 
 * @author sebastianloob
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
        // Read the store URL from properties. Directly referring to the properties allows to access them by the full
        // path.
        final String url = XltProperties.getInstance().getProperty("com.xceptance.xlt.loadtest.tests.store-url",
                                                                   "http://localhost:8080/");

        // Create new account data. This account data will be used to create a new account.
        Account account = new Account();

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

        // log out
        Logout logout = new Logout(login, "Logout");
        logout.run();
    }
}
