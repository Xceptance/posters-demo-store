package com.xceptance.loadtest.tests;

import org.junit.Test;
import com.xceptance.loadtest.actions.Homepage;
import com.xceptance.loadtest.flows.BrowsingFlow;
import com.xceptance.xlt.api.tests.AbstractTestCase;
import com.xceptance.xlt.api.util.XltProperties;

/**
 * Open the homepage, browse the catalogue. If there's a product overview open a random posters detail view.
 **/
public class TBrowse extends AbstractTestCase
{
    /**
     * Main test method
     */
    @Test
    public void browsePosterStore() throws Throwable
    {
	// Read the store URL from properties.
	final String url = XltProperties.getInstance().getProperty("com.xceptance.xlt.loadtest.tests.store-url", "http://localhost:8080/posters/");

	// The probability to perform a paging during browsing the categories
	final int pagingProbability = getProperty("paging.probability", 0);

	// The min. number of paging rounds
	final int pagingMin = getProperty("paging.min", 0);

	// The max. number of paging rounds
	final int pagingMax = getProperty("paging.max", 0);


	// Go to poster store homepage
	final Homepage homepage = new Homepage(url);
	// Disable JavaScript for the complete test case to reduce client side resource consumption.
	// If JavaScript executed functionality is needed to proceed with the scenario (i.e. AJAX calls) 
	// we will simulate this in the related actions.
	homepage.getWebClient().getOptions().setJavaScriptEnabled(false);
	homepage.run();

	// Browse the catalogue and view a product detail page
	// The browsing is encapsulated in a flow that combines a sequence of several XLT actions.
	// Different test cases can call this method now to reuse the flow. 
	// This is a concept for code structuring you can implement if needed, yet explicit support 
	// is neither available in the XLT framework nor necessary when you manually create a flow.
	BrowsingFlow browsingFlow = new BrowsingFlow(homepage, pagingProbability, pagingMin, pagingMax);
	browsingFlow.run();



    }
}
