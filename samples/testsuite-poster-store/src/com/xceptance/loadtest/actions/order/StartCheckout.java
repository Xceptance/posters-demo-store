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
 * This {@link AbstractHtmlPageAction} starts the checkout.
 * 
 * @author sebastianloob
 */
public class StartCheckout extends AbstractHtmlPageAction
{

    /**
     * The checkout link.
     */
    private HtmlElement checkoutLink;

    /**
     * Constructor
     * 
     * @param previousAction
     * @param timerName
     */
    public StartCheckout(AbstractHtmlPageAction previousAction, String timerName)
    {
        super(previousAction, timerName);
    }


    @Override
    public void preValidate() throws Exception
    {
        // Get the result of the last action
        final HtmlPage page = getPreviousAction().getHtmlPage();
        Assert.assertNotNull("Failed to get page from previous action.", page);
        
        // check that the cart is not empty
        boolean cartIsEmpty = HtmlPageUtils.findSingleHtmlElementByXPath(page, "id('headerCartOverview')/span")
                                           .asText().matches(".*: 0 Items.*");
        Assert.assertFalse("Cart must not be empty for checkout.", cartIsEmpty);
        
        // check that the checkout link is available
        Assert.assertTrue("Checkout link not found.", HtmlPageUtils.isElementPresent(page, "id('btnStartCheckout')"));
        
        // remember the checkout link
        this.checkoutLink = HtmlPageUtils.findSingleHtmlElementByID(page, "btnStartCheckout");
    }

    @Override
    protected void execute() throws Exception
    {
        // start the checkout
        loadPageByClick(checkoutLink);
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

        // check that it's the page to enter or select a shipping address
        Assert.assertTrue("Title not found.", HtmlPageUtils.isElementPresent(page, "id('titleDelAddr')"));
        
        // check that the form to enter a new shipping address is available
        Assert.assertTrue("Form to enter shipping address not found.",
                          HtmlPageUtils.isElementPresent(page, "id('formAddDelAddr')"));
    }
}
