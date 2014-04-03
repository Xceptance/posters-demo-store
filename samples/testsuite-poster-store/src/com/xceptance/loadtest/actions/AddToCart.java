package com.xceptance.loadtest.actions;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.Assert;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.util.HtmlPageUtils;
import com.xceptance.loadtest.util.AjaxUtils;

/**
 * Adds a previously configured product to cart. 
 * This action does not result in a page load but consists of a sequence of three AJAX calls. 
 * JavaScript is disabled due to performance reasons. So assembling the request parameters, 
 * make the call and evaluating the response content makes this kind of actions a bit more complex.
 */
public class AddToCart extends AbstractHtmlPageAction
{
    /**
     * The ID of the product to add to cart
     */
    private String productId;

    /**
     * The selected poster size
     */
    private String size;

    /**
     * The selected poster finish (matte or gloss)
     */
    private String finish;

    /**
     * The 'Add to cart' form
     */
    private HtmlForm addToCartForm;

    /**
     * Constructor
     * 
     * @param previousAction
     * @param timerName
     */
    public AddToCart(AbstractHtmlPageAction previousAction)
    {
	super(previousAction, null);
    }

    @Override
    public void preValidate() throws Exception
    {
	// Get the result of the previous action
	final HtmlPage page = getPreviousAction().getHtmlPage();
	Assert.assertNotNull("Failed to get page from previous action.", page);

	// Look up the 'add to cart' form
	addToCartForm = HtmlPageUtils.findSingleHtmlElementByID(page, "addToCartForm");

	// Configure the product by selecting a random finish (matte or gloss).
	finish = HtmlPageUtils.findHtmlElementsAndPickOne(addToCartForm, "id('selectFinish')/label").getTextContent().trim();

	// We choose one of the size options randomly and remember it as a string.
	// We will need it as a parameter later on in the subsequent AJAX calls
	// to update the price and add poster to cart.
	HtmlElement option = HtmlPageUtils.findHtmlElementsAndPickOne(page,
		"id('selectSize')/option");
	// Get the text content of the element as trimmed string
	size = option.getTextContent().trim();

	// Get the product ID. This is also needed for the AJAX calls
	productId = HtmlPageUtils.findSingleHtmlElementByXPath(page,
		"id('main')/div/div[2]/div[@class='row-fluid']").getAttribute(
		"productid");

	// Assert the presence of the add to cart button (even though we do not use
	// it here since we're just simulating the AJAX calls that are normally
	// triggered by JavaScript after hitting the button)
	Assert.assertTrue("AddToCart button is not present.", HtmlPageUtils.isElementPresent(page,"id('btnAddToCart')"));

    }

    @Override
    protected void execute() throws Exception
    {
	// Get the result of the this action
	final HtmlPage page = getPreviousAction().getHtmlPage();

	
	// Update price:
	// First we collect the (POST) request parameters for the call and
	// create a list of name value pairs, one for each parameter
	List<NameValuePair> updatePriceParams = new ArrayList<NameValuePair>();
	updatePriceParams.add(new NameValuePair("productId", productId));
	updatePriceParams.add(new NameValuePair("size", size));
	
	// Perform the AJAX call and return the result.
	WebResponse updatePriceResponse = AjaxUtils.callPost(page,
		"/posters/updatePrice", updatePriceParams);	
        
	// get JSON object from response
        JSONObject updatePriceJsonResponse = new JSONObject(updatePriceResponse.getContentAsString());
        
        // get the new price from JSON object
        String newPrice = updatePriceJsonResponse.getString("newPrice");
        
        // Validate the call returned a price in the correct currency
        Assert.assertTrue("The price does not end with $", newPrice.endsWith("$"));
        
        // Put the returned price into the page. 
        // Note: This is not necessary since we just want to simulate realistic traffic for the server and nromally do not care about client side stuff.
        HtmlPageUtils.findSingleHtmlElementByID(page, "prodPrice").setTextContent(newPrice);
           

	// Get cart element slider content before adding poster to cart:
	// Perform the AJAX call and return the result.
	// This call doesn't have any parameters
	WebResponse cartElementSliderResponse = AjaxUtils.callGet(page,
		"/posters/getCartElementSlider", null);
	
        // get JSON object from response
        JSONObject getCartElementSliderJsonResponse = new JSONObject(cartElementSliderResponse.getContentAsString());
        
        // get the keys from JSON object and do some basic validation
        Assert.assertEquals("Length unit is not 'in'.", getCartElementSliderJsonResponse.getString("unitLength"), "in");
        Assert.assertFalse("The 'total price' JSON key is empty.", getCartElementSliderJsonResponse.getString("totalPrice").isEmpty());
        Assert.assertEquals("Currency is not '$'.", getCartElementSliderJsonResponse.getString("currency"), "$");
        Assert.assertEquals("There are cart elements but the cart elements key sould be empty.", getCartElementSliderJsonResponse.getString("cartElements"), "[]");
	
	
	// Add poster to cart:
	// Collect the request parameters
	List<NameValuePair> addToCartParams = new ArrayList<NameValuePair>();
	addToCartParams.add(new NameValuePair("productId", productId));
	addToCartParams.add(new NameValuePair("finish", finish));
	addToCartParams.add(new NameValuePair("size", size));
	
	// Perform the AJAX call and return the result.
	WebResponse addToCartResponse = AjaxUtils.callGet(page, "/posters/addToCartSlider", addToCartParams);
        
	// get JSON object from response
        JSONObject addToCartJsonResponse = new JSONObject(addToCartResponse.getContentAsString());
        
        // get the keys from JSON object and do some basic validation
        JSONObject product = addToCartJsonResponse.getJSONObject("product");
        Assert.assertEquals("The addToCart call returned the wrong finish.", product.get("finish"), finish);
        Assert.assertEquals("The addToCart call returned the wrong count", product.getInt("productCount"), 1);
        Assert.assertEquals("The addToCart call returned the wrong price", product.get("productPrice"), newPrice.substring(0, newPrice.length()-1));
        Assert.assertEquals("The addToCart call returned the wrong product ID", product.getInt("productId"), Integer.parseInt(productId));
	
	// Publish the results of that action.
	setHtmlPage(page);
    }

    @Override
    protected void postValidate() throws Exception
    {
	// Since the AJAX calls in this action do not load a new page 
	// and all the JSON responses have been validated already in the execute() method 
	// there is no need for further post validation here.

    }

}
