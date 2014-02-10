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
 * This {@link AbstractHtmlPageAction} opens the registration form.
 * 
 * @author sebastianloob
 */
public class GoToRegistrationForm extends AbstractHtmlPageAction
{

    /**
     * The link to the registration form.
     */
    private HtmlElement registerButton;

    public GoToRegistrationForm(AbstractHtmlPageAction previousAction, String timerName)
    {
        super(previousAction, timerName);
    }

    @Override
    public void preValidate() throws Exception
    {
        // Get the result of the last action
        final HtmlPage page = getPreviousAction().getHtmlPage();
        Assert.assertNotNull("Failed to get page from previous action.", page);

        // check that the registration link is available
        Assert.assertTrue("Registration link not found.", HtmlPageUtils.isElementPresent(page, "id('linkRegister')"));

        // remember the registration link
        this.registerButton = HtmlPageUtils.findSingleHtmlElementByID(page, "linkRegister");
    }

    @Override
    protected void execute() throws Exception
    {
        // load the registration page
        loadPageByClick(this.registerButton);
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

        // check that it's the registration page
        Assert.assertTrue("Registration form not found.", HtmlPageUtils.isElementPresent(page, "id('formRegister')"));
        Assert.assertTrue("Link to create new account not found.",
                          HtmlPageUtils.isElementPresent(page, "id('btnRegister')"));
    }
}
