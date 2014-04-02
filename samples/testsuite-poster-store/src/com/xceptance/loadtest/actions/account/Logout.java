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
     * The logout link.
     */
    HtmlElement logoutLink;

    /**
     * Constructor
     * 
     * @param previousAction
     * @param timerName
     */
    public Logout(AbstractHtmlPageAction previousAction)
    {
        super(previousAction, null);
    }

    
    @Override
    public void preValidate() throws Exception
    {
        // get the result of the last action
        final HtmlPage page = getPreviousAction().getHtmlPage();
        Assert.assertNotNull("Failed to get page from previous action.", page);

        // check that the customer is logged in
        Assert.assertTrue("No customer is logged in.", HtmlPageUtils.isElementPresent(page, "id('headerLoggedCustomer')"));

        // remember logout link
        this.logoutLink = HtmlPageUtils.findSingleHtmlElementByXPath(page, "id('btnLogout')/a");
    }

    @Override
    protected void execute() throws Exception
    {
        // log out by clicking the link
        loadPageByClick(logoutLink);
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

        // check that no customer is logged in
        Assert.assertTrue("A customer is still logged in.", HtmlPageUtils.isElementPresent(page, "id('btnShowLoginForm')"));

        // check that it's the home page
        final HtmlElement blogNameElement = page.getHtmlElementById("titleIndex");
        Assert.assertNotNull("Title not found", blogNameElement);
        
        // check the title
        Assert.assertEquals("Title does not match", "Check out our new panorama posters!", blogNameElement.asText());
    }

}
