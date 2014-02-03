package com.xceptance.loadtest.actions;

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
 * This {@link AbstractHtmlPageAction} fills in and submits the sign in form. <br>
 * The previous action should be {@link GoToSignIn}.
 * 
 * @author sebastianloob
 */
public class Login extends AbstractHtmlPageAction
{

    /**
     * The sign in form.
     */
    private HtmlForm signInForm;

    /**
     * The button to submit the sign in form.
     */
    private HtmlElement signInButton;

    /**
     * The account to log in.
     */
    private Account account;

    /**
     * @param previousAction
     *            previous action
     * @param timerName
     *            timer name
     * @param account
     *            the account to log in
     */
    public Login(AbstractHtmlPageAction previousAction, String timerName, Account account)
    {
        super(previousAction, timerName);
        this.account = account;
    }

    @Override
    public void preValidate() throws Exception
    {
        // Get the result of the last action
        final HtmlPage page = getPreviousAction().getHtmlPage();
        // check that the sign in form is available
        Assert.assertTrue("Sign in form not found", HtmlPageUtils.isElementPresent(page, "id('formLogin')"));
        // remember the sign in form
        this.signInForm = HtmlPageUtils.findSingleHtmlElementByID(page, "formLogin");
        // check that the registration link is available
        Assert.assertTrue("Sign in button not found", HtmlPageUtils.isElementPresent(page, "id('btnSignIn')"));
        // remember the sign in button
        this.signInButton = HtmlPageUtils.findSingleHtmlElementByID(page, "btnSignIn");
    }

    @Override
    protected void execute() throws Exception
    {
        // Fill in the form
        HtmlPageUtils.setInputValue(signInForm, "email", account.getEmail());
        HtmlPageUtils.setInputValue(signInForm, "password", account.getPassword());

        // submit the registration form
        loadPageByClick(this.signInButton);
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

        // check that the customer is logged
        Assert.assertTrue("No customer is logged.", HtmlPageUtils.isElementPresent(page, "id('headerLoggedCustomer')"));

        // check that it's the home page
        final HtmlElement blogNameElement = page.getHtmlElementById("titleIndex");
        Assert.assertNotNull("Title not found", blogNameElement);
        // check the title
        Assert.assertEquals("Title does not match", "Check out our new panorama posters!", blogNameElement.asText());
    }

}
