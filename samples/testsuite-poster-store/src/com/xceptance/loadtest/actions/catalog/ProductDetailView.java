package com.xceptance.loadtest.actions.catalog;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.loadtest.validators.HeaderValidator;
import com.xceptance.loadtest.validators.SideNavValidator;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.util.HtmlPageUtils;
import com.xceptance.xlt.api.validators.ContentLengthValidator;
import com.xceptance.xlt.api.validators.HtmlEndTagValidator;
import com.xceptance.xlt.api.validators.HttpResponseCodeValidator;

/**
 * Opens the product detail page for a randomly chosen poster.
 * 
 */
public class ProductDetailView extends AbstractHtmlPageAction
{

    public ProductDetailView(AbstractHtmlPageAction previousAction,
	    String timerName)
    {
	super(previousAction, timerName);
    }

    /**
     * The product detail link to follow
     */
    private HtmlElement productDetailLink;
    
    @Override
    public void preValidate() throws Exception
    {
        // Get the current page.
        final HtmlPage page = getPreviousAction().getHtmlPage();
        Assert.assertNotNull("Failed to get page from previous action", page);

        // Check the current page is a product overview category page.
        HtmlPageUtils.isElementPresent(page, "id('productOverview')");
        
        // and we also see some poster's thumbnail images
        HtmlPageUtils.findHtmlElements(page, "id('productOverview')/div/ul/li/div[@class='thumbnail']");

        // Remember a random product's link URL.
        productDetailLink = HtmlPageUtils.findHtmlElementsAndPickOne(page, "id('productOverview')/div/ul[@class='thumbnails']/li/div/a");

    }

    @Override
    protected void execute() throws Exception
    {
	// Click on the chosen product detail link to load the prouct detail page
	loadPageByClick(productDetailLink);

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
        SideNavValidator.getInstance().validate(page);
        
        // Check it's a product detail page
        // The product's name element in the headline is present
        HtmlPageUtils.isElementPresent(page, "id('main')/div/div/h1[@id='titleProductName']");
        
        // The product description is there in the right presentation (h3 - h4 -span)
        HtmlPageUtils.isElementPresent(page, "id('main')/div/div[2]/div/div[2]/h3[@id='prodDescriptionOverview']");
        HtmlPageUtils.isElementPresent(page, "id('main')/div/div[2]/div/div[2]/h4[@id='prodDescriptionDetail']");
        HtmlPageUtils.isElementPresent(page, "id('main')/div/div[2]/div/div[2]/p[@id='prodFinish']");
        
        // There is a price with the correct currency
        HtmlElement productPriceElement = HtmlPageUtils.findSingleHtmlElementByID(page, "prodPrice");
        String productPrice = productPriceElement.getTextContent();
        productPrice.endsWith("$");
        
        // Product configuration elements are present
        HtmlPageUtils.isElementPresent(page, "id('selectFinish')");
        HtmlPageUtils.isElementPresent(page, "id('finish-matte')");
        HtmlPageUtils.isElementPresent(page, "id('finish-gloss')");
        HtmlPageUtils.isElementPresent(page, "id('size')");
        
        // 'Add to cart' button is available
        HtmlPageUtils.isElementPresent(page, "id('btnAddToCart')");
        
    }

}
