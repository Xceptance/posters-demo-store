package com.xceptance.loadtest.actions.order;

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
 * This {@link AbstractHtmlPageAction} opens the cart overview page.
 * 
 * @author sebastianloob
 */
public class ViewCart extends AbstractHtmlPageAction
{

    /**
     * Link to shopping cart page.
     */
    private HtmlElement viewCartLink;

    /**
     * Constructor
     * 
     * @param previousAction
     * @param timerName
     */
    public ViewCart(AbstractHtmlPageAction previousAction)
    {
        super(previousAction, null);
    }

    @Override
    public void preValidate() throws Exception
    {
        // Get the current page.
        final HtmlPage page = getPreviousAction().getHtmlPage();
        Assert.assertNotNull("Failed to get page from previous action.", page);
        
        // check that the cart overview link is available
        Assert.assertTrue("Cart overview link not found",
                          HtmlPageUtils.isElementPresent(page, "id('headerCartOverview')"));
        
        // remember cart overview link
        this.viewCartLink = HtmlPageUtils.findSingleHtmlElementByID(page, "headerCartOverview");
    }

    @Override
    protected void execute() throws Exception
    {
        // load the cart overview page
        loadPageByClick(this.viewCartLink);
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

        // check that it's the cart overview page
        Assert.assertTrue("Title not found", HtmlPageUtils.isElementPresent(page, "id('titleCart')"));
        Assert.assertTrue("Total price not found", HtmlPageUtils.isElementPresent(page, "id('totalPrice')"));
        Assert.assertTrue("Checkout button not found", HtmlPageUtils.isElementPresent(page, "id('btnStartCheckout')"));
    }
}
