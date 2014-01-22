package com.xceptance.loadtest.tests;

import org.junit.Test;

import com.xceptance.loadtest.actions.Homepage;
import com.xceptance.loadtest.actions.SelectCategory;
import com.xceptance.xlt.api.tests.AbstractTestCase;
import com.xceptance.xlt.api.util.XltProperties;

/**
 * Open the homepage, browse the catalog. If there's a product overview
 * open random poster's detail view.
 * 
 **/
public class TBrowse extends AbstractTestCase
{
    /**
     * Main test method
     */
    @Test
    public void browsePosterStore() throws Throwable
    {
    // Read the store URL from properties. Directly referring to the properties allows to access them by the full
    // path.
    final String url = XltProperties.getInstance().getProperty("com.xceptance.xlt.loadtest.tests.store-url", "http://localhost:8080/");
    
    // Go to poster store homepage
    final Homepage homepage = new Homepage(url);
    homepage.run();
    }
}
