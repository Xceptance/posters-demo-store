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

public class SelectCategory extends AbstractHtmlPageAction
{
    
    public SelectCategory(AbstractHtmlPageAction previousAction)
    {
	super(previousAction, null);
    }

    /**
     * Chosen level-1 category.
     */
    private HtmlElement categoryLink;
    
    @Override
    public void preValidate() throws Exception
    {
	//Get all level-1 category links and select a random one
	categoryLink = HtmlPageUtils.findHtmlElementsAndPickOne(getPreviousAction().getHtmlPage(), "id('sidebarNav')/ul/li[@class='level-1']/a");

    }

    @Override
    protected void execute() throws Exception
    {
        // click the link
        loadPageByClick(categoryLink);

    }

    @Override
    protected void postValidate() throws Exception
    {
        // get the result of the last action
        final HtmlPage page = getHtmlPage();

        // Basic checks that are part of the XLT API
        HttpResponseCodeValidator.getInstance().validate(page);
        ContentLengthValidator.getInstance().validate(page);
        HtmlEndTagValidator.getInstance().validate(page);
      
        // Check for the header
        HeaderValidator.getInstance().validate(page);
        
        // Check the side navigation
        SideNavValidator.getInstance().validate(page);
        
        // The product over view element is present
        Assert.assertTrue("Product over view element is bot present", HtmlPageUtils.isElementPresent(page, "id('productOverview')"));
        
        // and we also see some poster's thumbnail images
        HtmlPageUtils.findHtmlElements(page, "id('productOverview')/div/ul/li/div[@class='thumbnail']");

    }

}
