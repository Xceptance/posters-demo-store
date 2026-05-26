package com.xceptance.neodymium.ai.test;

import com.xceptance.neodymium.common.browser.Browser;
import com.xceptance.neodymium.common.testdata.DataFile;
import com.xceptance.neodymium.junit5.NeodymiumTest;
import com.xceptance.neodymium.util.Neodymium;

/**
 * Prompt directly in Java, but data is loaded from a file (or dataset). The AI agent resolves placeholders like
 * ${searchTerm} using the loaded TestData before sending the prompt to the LLM.
 */
@Browser("Chrome_1500x1000")
@DataFile("posters_tests/hybridTestData.json")
public class HybridPromptShowcaseTest
{
    @NeodymiumTest
    void testPromptInJavaWithDataFromFile() throws Throwable
    {
        // Any properties in the data file can be referenced via ${property}
        // Note: the test will fail if 'searchTerm' is not defined in the loaded YAML/JSON
        Neodymium.ai().execute(
            """
            Open ${posters.storefront.url}
            Type '${searchTerm}' into the search bar
            Click the search button
            Verify that the search results are displayed
            """
        );
    }
}
