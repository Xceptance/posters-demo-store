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

    public ProductDetailView(AbstractHtmlPageAction previousAction)
    {
	super(previousAction, null);
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
        Assert.assertTrue("Product Overview element missing.",HtmlPageUtils.isElementPresent(page, "id('productOverview')"));
        
        // and we also see some poster's thumbnail images
        HtmlPageUtils.findHtmlElements(page, "id('productOverview')/div/ul/li/div[@class='thumbnail']");

        // Remember a random product's link URL.
        productDetailLink = HtmlPageUtils.findHtmlElementsAndPickOne(page, "id('productOverview')/div/ul[@class='thumbnails']/li/div/a");
        
        @SuppressWarnings("unused")
	String linkdAddress = productDetailLink.getAttribute("href");

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
        Assert.assertTrue("Product description is not there or not in the right presentation (h3 - h4 -span).", HtmlPageUtils.isElementPresent(page, "id('main')/div/div[2]/div/div[2]/h3[@id='prodDescriptionOverview']"));
        Assert.assertTrue("Product description is not there or not in the right presentation (h3 - h4 -span).", HtmlPageUtils.isElementPresent(page, "id('main')/div/div[2]/div/div[2]/h4[@id='prodDescriptionDetail']"));
        Assert.assertTrue("Product description is not there or not in the right presentation (h3 - h4 -span).", HtmlPageUtils.isElementPresent(page, "id('main')/div/div[2]/div/div[2]/p[@id='prodFinish']"));
        
        // There is a price with the correct currency
        HtmlElement productPriceElement = HtmlPageUtils.findSingleHtmlElementByID(page, "prodPrice");
        String productPrice = productPriceElement.getTextContent();
        Assert.assertTrue("The price does not end with $", productPrice.endsWith("$"));
        
        // Product configuration elements are present
        Assert.assertTrue("Product configuration element (select Finish) is not present.", HtmlPageUtils.isElementPresent(page, "id('selectFinish')"));
        Assert.assertTrue("Product configuration element (finish matte) is not present.", HtmlPageUtils.isElementPresent(page, "id('finish-matte')"));
        Assert.assertTrue("Product configuration element (finish gloss) is not present.", HtmlPageUtils.isElementPresent(page, "id('finish-gloss')"));
        Assert.assertTrue("Product configuration element (size) is not present.", HtmlPageUtils.isElementPresent(page, "id('selectSize')"));
        
        // 'Add to cart' button is available
        Assert.assertTrue("'Add to cart' button is not available", HtmlPageUtils.isElementPresent(page, "id('btnAddToCart')"));
        
    }

}
