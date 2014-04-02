package com.xceptance.loadtest.tests;

import org.junit.Test;

import com.xceptance.xlt.api.tests.AbstractTestCase;
import com.xceptance.xlt.api.util.XltProperties;
import com.xceptance.loadtest.actions.*;

public class TVisit extends AbstractTestCase
{
    /**
     * Main test method
     */
    @Test
    public void visitPosterStore() throws Throwable
    {
    // Read the store URL from properties. Directly referring to the properties allows to access them by the full
    // path.
    final String url = XltProperties.getInstance().getProperty("com.xceptance.xlt.loadtest.tests.store-url", "http://localhost:8080/posters/");
    
    // Go to poster store homepage
    final Homepage homepage = new Homepage(url);
    homepage.run();
    }
}
