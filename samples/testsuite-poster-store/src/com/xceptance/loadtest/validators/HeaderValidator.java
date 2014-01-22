package com.xceptance.loadtest.validators;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.xlt.api.util.HtmlPageUtils;

/**
 * Checks for the correct header elements
 */
public class HeaderValidator
{
    /**
     * Make a stateless singleton available.
     */
    private static final HeaderValidator instance = new HeaderValidator();

    /**
     * Checks the poster store header elements
     * 
     * @param page
     *            the page to check
     */
    public void validate(final HtmlPage page) throws Exception
    {
	//assert presence of some basic elements in the header
	//the brand logo
	HtmlPageUtils.isElementPresent(page, "id('brand')");
	//The search form
	HtmlPageUtils.isElementPresent(page, "id('search')");
	//The search input
	HtmlPageUtils.isElementPresent(page, "id('searchText')");
	//The serach button
	HtmlPageUtils.isElementPresent(page, "id('searchBtn')");
	//The basket overview
	HtmlPageUtils.isElementPresent(page, "id('headerBasketOverview')");
	
    }



    /**
     * The instance for easy reuse. Possible because this validator is stateless.
     * 
     * @return the instance
     */
    public static HeaderValidator getInstance()
    {
        return instance;
    }
}
