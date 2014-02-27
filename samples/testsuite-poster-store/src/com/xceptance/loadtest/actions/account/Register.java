package com.xceptance.loadtest.actions.account;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.loadtest.util.Account;
import com.xceptance.loadtest.validators.HeaderValidator;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.util.HtmlPageUtils;
import com.xceptance.xlt.api.validators.ContentLengthValidator;
import com.xceptance.xlt.api.validators.HtmlEndTagValidator;
import com.xceptance.xlt.api.validators.HttpResponseCodeValidator;

/**
 * This {@link AbstractHtmlPageAction} fills in and submits the registration form.
 * 
 * @author sebastianloob
 */
public class Register extends AbstractHtmlPageAction
{

    /**
     * the registration form
     */
    private HtmlForm registrationForm;

    /**
     * the button to submit the registration form
     */
    private HtmlElement createAccountButton;

    /**
     * the account to register
     */
    private Account account;

    /**
     * Constructor
     * 
     * @param previousAction
     *            previous action
     * @param timerName
     *            timer name
     * @param accountData
     *            The account data used to register new account
     */
    public Register(AbstractHtmlPageAction previousAction, String timerName, Account accountData)
    {
        super(previousAction, timerName);
        this.account = accountData;
    }

    @Override
    public void preValidate() throws Exception
    {
        // Get the result of the last action
        final HtmlPage page = getPreviousAction().getHtmlPage();
        Assert.assertNotNull("Failed to get page from previous action.", page);
        
        // check that the registration form is available
        Assert.assertTrue("Registration form not found", HtmlPageUtils.isElementPresent(page, "id('formRegister')"));
        
        // remember the registration form
        this.registrationForm = HtmlPageUtils.findSingleHtmlElementByID(page, "formRegister");
        
        // check that the create account button is available
        Assert.assertTrue("Create account button not found", HtmlPageUtils.isElementPresent(page, "id('btnRegister')"));
        
        // remember the create account button
        this.createAccountButton = HtmlPageUtils.findSingleHtmlElementByID(page, "btnRegister");
    }

    @Override
    protected void execute() throws Exception
    {
        // Fill in the form
        HtmlPageUtils.setInputValue(registrationForm, "name", account.getLastName());
        HtmlPageUtils.setInputValue(registrationForm, "firstName", account.getFirstName());
        HtmlPageUtils.setInputValue(registrationForm, "eMail", account.getEmail());
        HtmlPageUtils.setInputValue(registrationForm, "password", account.getPassword());
        HtmlPageUtils.setInputValue(registrationForm, "passwordAgain", account.getPassword());

        // submit the registration form
        loadPageByClick(this.createAccountButton);
    }

    @Override
    protected void postValidate() throws Exception
    {
        // get the result of the last action
        final HtmlPage page = getHtmlPage();

        // Basic checks - see action 'Homepage' for some more details how and when to use these validators
        HttpResponseCodeValidator.getInstance().validate(page);
        ContentLengthValidator.getInstance().validate(page);
        HtmlEndTagValidator.getInstance().validate(page);

        HeaderValidator.getInstance().validate(page);

        // check that the account was successfully created
        boolean accountCreated = page.asXml()
                                     .contains("Your account has been created. Log in with your email address and password.");
        Assert.assertTrue("Registration failed.", accountCreated);
        
        // check that it's the sign in page
        Assert.assertTrue("Sign in form not found.", HtmlPageUtils.isElementPresent(page, "id('formLogin')"));
        Assert.assertTrue("Link to register not found.", HtmlPageUtils.isElementPresent(page, "id('linkRegister')"));
        
        // check that the customer is not logged after registration
        Assert.assertTrue("Customer is logged after registration.",
                          HtmlPageUtils.isElementPresent(page, "id('btnShowLoginForm')"));
    }

}