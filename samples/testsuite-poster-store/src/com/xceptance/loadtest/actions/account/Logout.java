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
 * This {@link AbstractHtmlPageAction} logs out the currently logged in user.
 * 
 * @author sebastianloob
 */
public class Logout extends AbstractHtmlPageAction
{

    /**
     * The log out link.
     */
    HtmlElement logout;

    public Logout(AbstractHtmlPageAction previousAction, String timerName)
    {
        super(previousAction, timerName);
    }

    @Override
    public void preValidate() throws Exception
    {
        // get the result of the last action
        final HtmlPage page = getPreviousAction().getHtmlPage();
        Assert.assertNotNull("Failed to get page from previous action.", page);

        // check that the customer is logged
        Assert.assertTrue("No customer is logged.", HtmlPageUtils.isElementPresent(page, "id('headerLoggedCustomer')"));

        // remember log out link
        this.logout = HtmlPageUtils.findSingleHtmlElementByXPath(page, "id('btnLogout')/a");
    }

    @Override
    protected void execute() throws Exception
    {
        // log out
        loadPageByClick(logout);
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

        // check that no customer is logged
        Assert.assertTrue("A customer is still logged.", HtmlPageUtils.isElementPresent(page, "id('btnShowLoginForm')"));

        // check that it's the home page
        final HtmlElement blogNameElement = page.getHtmlElementById("titleIndex");
        Assert.assertNotNull("Title not found", blogNameElement);
        // check the title
        Assert.assertEquals("Title does not match", "Check out our new panorama posters!", blogNameElement.asText());
    }

}
