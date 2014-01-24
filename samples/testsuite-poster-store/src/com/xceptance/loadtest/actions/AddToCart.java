package com.xceptance.loadtest.actions;

import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.util.HtmlPageUtils;
import com.xceptance.loadtest.util.AjaxUtils;

public class AddToCart extends AbstractHtmlPageAction
{

    /**
     * The 'Add to cart' button
     */
    HtmlElement addToCartButton;
    
    /**
     * The ID of the product to add to cart
     */
    String productId;
    
    /**
     * The selected poster size
     */
    String size;
    
    /**
     * The selected poster finish (matte or gloss)
     */
    String finish;
    
    /**
     * The 'Add to cart' form
     */
    HtmlForm addToCartForm;
    
    /**
     * 	Constructor
     * @param previousAction
     * @param timerName
     */
    public AddToCart(AbstractHtmlPageAction previousAction, String timerName)
    {
	super(previousAction, timerName);
    }

    @Override
    public void preValidate() throws Exception
    {       
        // Get the result of the last action
        final HtmlPage page = getPreviousAction().getHtmlPage();
	
        // Look up the add to cart form
        addToCartForm = HtmlPageUtils.findSingleHtmlElementByID(page, "addToCartForm");
	
        // Configure the product by selecting a random finish (matte or gloss).
	HtmlPageUtils.checkRadioButtonRandomly(addToCartForm, "finish");
	// FIXME: extract the real selected finish 
	finish = "matte";
	
	// We choose one of the size options randomly and remember it as a string.
	// We will need it as a parameter later on in the subsequent AJAX calls to update price and add product to cart
	HtmlElement option = HtmlPageUtils.findHtmlElementsAndPickOne(page, "id('selectSize')/option");
	size = option.getTextContent();
	
	// Get the product ID. This is also needed for the AJAX calls
	productId = HtmlPageUtils.findSingleHtmlElementByXPath(page, "id('main')/div/div[2]/div[@class='row-fluid']").getAttribute("productid");
	 
        // Look up the 'Add to cart' button
        addToCartButton = HtmlPageUtils.findSingleHtmlElementByID(page, "btnAddToCart");

    }

    @Override
    protected void execute() throws Exception
    {
        // Get the result of the last action
        final HtmlPage page = getPreviousAction().getHtmlPage();
        
	// With JavaScript disabled due to performance reasons on the load generating agents... 
	getWebClient().getOptions().setJavaScriptEnabled(false);
	// ... we need to simulate the AJAX calls that update the price after selecting a size and add the product to cart.
	
        // Update price:
	// First we collect the request (POST) parameters for the call and create a list of name value pairs, one for each parameter
	List<NameValuePair> updatePriceParams = new ArrayList<NameValuePair>();
	updatePriceParams.add(new NameValuePair("productId", productId));
	updatePriceParams.add(new NameValuePair("size", size));
	
	// Perform the AJAX call and return the result.
	WebResponse updatePriceResponse = AjaxUtils.callPost(page, "/updatePrice", updatePriceParams);
	
        // Put the resulting price into the page
        // TODO
        
        // Get cart element slider content before adding poster to cart
        // Perform the AJAX call and return the result.
        WebResponse cartElementSliderResponse = AjaxUtils.callGet(page, "/getCartElementSlider", null);
        
        
        // Add poster to cart:
	// First we collect the request parameters for the call and create a list of name value pairs, one for each parameter
	List<NameValuePair> addToCartParams = new ArrayList<NameValuePair>();
	addToCartParams.add(new NameValuePair("productId", productId));
	addToCartParams.add(new NameValuePair("finish", finish));
	addToCartParams.add(new NameValuePair("size", size));
	
	// Perform the AJAX call and return the result.
	AjaxUtils.callGet(page, "/addToCartSlider", addToCartParams);

	// Publish the results.
	setHtmlPage(page);
    }

    @Override
    protected void postValidate() throws Exception
    {
	// TODO Auto-generated method stub

    }
    
    


}
