package com.xceptance.loadtest.actions.account;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.loadtest.validators.HeaderValidator;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.util.HtmlPageUtils;
import com.xceptance.xlt.api.validators.ContentLengthValidator;
import com.xceptance.xlt.api.validators.HtmlEndTagValidator;
import com.xceptance.xlt.api.validators.HttpResponseCodeValidator;

/**
 * This {@link AbstractHtmlPageAction} opens the sign in page.
 * 
 * @author sebastianloob
 */
public class GoToSignIn extends AbstractHtmlPageAction
{

    /**
     * The sign in button.
     */
    private HtmlElement signInButton;

    public GoToSignIn(AbstractHtmlPageAction previousAction, String timerName)
    {
        super(previousAction, timerName);
    }

    @Override
    public void preValidate() throws Exception
    {
        // Get the result of the last action
        final HtmlPage page = getPreviousAction().getHtmlPage();
        Assert.assertNotNull("Failed to get page from previous action.", page);

        // check that no customer is logged
        Assert.assertTrue("A customer is already logged.",
                          HtmlPageUtils.isElementPresent(page, "id('btnShowLoginForm')"));

        // remember the sign in button
        this.signInButton = HtmlPageUtils.findSingleHtmlElementByID(page, "btnShowLoginForm");
    }

    @Override
    protected void execute() throws Exception
    {
        // load the page to sign in
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

        // check that it's the sign in page
        Assert.assertTrue("Sign in form not found.", HtmlPageUtils.isElementPresent(page, "id('formLogin')"));
        Assert.assertTrue("Link to register not found.", HtmlPageUtils.isElementPresent(page, "id('linkRegister')"));
    }

}
