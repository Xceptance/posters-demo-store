package com.xceptance.neodymium.ai.test;

import com.xceptance.neodymium.common.browser.Browser;
import com.xceptance.neodymium.junit5.NeodymiumTest;
import com.xceptance.neodymium.util.Neodymium;

/**
 * Steps defined directly in Java.
 * No data file is used. The AI agent executes the provided string.
 * Demonstrates the use of the fluent API for before/after/system context and the steps themselves.
 */
@Browser("Chrome_1500x1000")
public class InlinePromptShowcaseTest
{
    @NeodymiumTest
    void testWithPromptDirectlyInJava() throws Throwable
    {
        Neodymium.ai()
                 .systemContext("""
                     - On the page the 'Buy here' buttons are only to OPEN a product detail page, NOT to add it to the cart.
                     - There is a bug on the page. After adding a product to the cart ALWAYS use a REFRESH action.
                     """)
            .before("Navigate to ${neodymium.url}")
                 .steps("Verify the site contains 'Posters'")
            .after("Clear cookies")
            .execute();
    }
    
    @NeodymiumTest
    void testPromptInJavaWithDynamicData() throws Throwable
    {
        // You can push data to Neodymium's context so it can be resolved in the steps.
        Neodymium.getData().put("dynamicSearchTerm", "car");
        
        Neodymium.ai()
                 .before("Navigate to ${neodymium.url}")
            .steps(
                """
                Type '${dynamicSearchTerm}' into the search bar
                Click the search button
                Verify that the search results for ${dynamicSearchTerm} are displayed
                """
            )
            .execute();
    }
}
