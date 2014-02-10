package com.xceptance.loadtest.actions.catalog;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.loadtest.validators.HeaderValidator;
import com.xceptance.loadtest.validators.SideNavValidator;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.util.HtmlPageUtils;
import com.xceptance.xlt.api.validators.ContentLengthValidator;
import com.xceptance.xlt.api.validators.HtmlEndTagValidator;
import com.xceptance.xlt.api.validators.HttpResponseCodeValidator;

public class SelectTopCategory extends AbstractHtmlPageAction
{


    /**
     * Constructor.
     * 
     * @param lastAction
     *            previous action
     */
    public SelectTopCategory(final AbstractHtmlPageAction lastAction, String timerName)
    {
        super(lastAction, timerName);
    }
    
    /**
     * Chosen top-category.
     */
    private HtmlElement topCategoryLink;
    
    
    @Override
    public void preValidate() throws Exception
    {
	//Get all top category links and select a random one
	topCategoryLink = HtmlPageUtils.findHtmlElementsAndPickOne(getPreviousAction().getHtmlPage(), "id('sidebarNav')/ul/li[@class='topCategory']/h4/a");

    }

    @Override
    protected void execute() throws Exception
    {
        // click the link
        loadPageByClick(topCategoryLink);

    }

    @Override
    protected void postValidate() throws Exception
    {
        // get the result of the last action
        final HtmlPage page = getHtmlPage();

        // First, we check all common criteria. This code can be bundled and
        // reused if needed. For the purpose of a
        // programming example, we leave it here as detailed as possible.

        // check the response code, the singleton instance validates for 200
        HttpResponseCodeValidator.getInstance().validate(page);

        // check the content length, compare delivered content length to the
        // content length that was announced in the
        // HTTP response header
        ContentLengthValidator.getInstance().validate(page);
        
        // check for complete HTML
        HtmlEndTagValidator.getInstance().validate(page);
        

        // We can be pretty sure now, that the page fulfils the basic
        // requirements to be a valid page from our demo poster store.
        // Run more page specific tests now.
        // Check that we arrived on a category page.
        
        // check for the header
        HeaderValidator.getInstance().validate(page);
        
        //Check the side navigation
        SideNavValidator.getInstance().validate(page);
        
        // The product over view element is present
        HtmlPageUtils.isElementPresent(page, "id('productOverview')");
        
        // and we also see some poster's thumbnail images
        HtmlPageUtils.findHtmlElements(page, "id('productOverview')/div/ul/li/div[@class='thumbnail']");
        
    }

}
