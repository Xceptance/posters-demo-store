package com.xceptance.loadtest.validators;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

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
        //TODO more header validations, e.g. search and mini-cart
	// get the header
        final HtmlElement header = page.getHtmlElementById("xxxxx");
        Assert.assertNotNull("Header not found", header);

        // get the content form the element
        final String text = header.asText();

        // compare it
        Assert.assertEquals("Brand does not match", "-Poster", text);
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
