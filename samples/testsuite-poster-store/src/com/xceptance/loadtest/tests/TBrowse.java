package com.xceptance.loadtest.tests;

import org.junit.Test;
import com.xceptance.loadtest.actions.Homepage;
import com.xceptance.loadtest.flows.BrowsingFlow;
import com.xceptance.xlt.api.tests.AbstractTestCase;
import com.xceptance.xlt.api.util.XltProperties;


/**
 * Open the homepage, browse the catalog. If there's a product overview open random poster's detail view.
 **/
public class TBrowse extends AbstractTestCase
{

    /**
     *  The probability to perform a paging during browsing the categories
     */
    final int pagingProbability = getProperty("paging.probability", 0);
    
    /**
     *  The min number of paging rounds
     */
    final int pagingMin = getProperty("paging.min", 0);
    
    /**
     *  The max number of paging rounds
     */
    final int pagingMax = getProperty("paging.max", 0);
    
    
    /**
     * Main test method
     */
    @Test
    public void browsePosterStore() throws Throwable
    {
        // Read the store URL from properties. Directly referring to the properties allows to access them by the full
        // path.
        final String url = XltProperties.getInstance().getProperty("com.xceptance.xlt.loadtest.tests.store-url",
                                                                   "http://localhost:8080/posters/");

        // Go to poster store homepage
        Homepage homepage = new Homepage(url);
        homepage.run();

        // Browse
        BrowsingFlow browse = new BrowsingFlow(homepage, pagingProbability, pagingMin, pagingMax);
        browse.run();



    }
}
