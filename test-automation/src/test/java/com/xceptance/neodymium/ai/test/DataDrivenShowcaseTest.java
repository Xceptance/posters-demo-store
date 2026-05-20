package com.xceptance.neodymium.ai.test;

import com.xceptance.neodymium.common.browser.Browser;
import com.xceptance.neodymium.common.testdata.DataFile;
import com.xceptance.neodymium.junit5.NeodymiumTest;
import com.xceptance.neodymium.util.Neodymium;

/**
 * SHOWCASE 1: Data and steps completely defined in one file.
 * The file must contain a 'steps' property. It can optionally contain 'before', 'after', 
 * and 'context' (system context) properties which will be automatically executed.
 */
@Browser("Chrome_1500x1000")
@DataFile("posters_tests/searchForProduct.yml")
public class DataDrivenShowcaseTest
{
    @NeodymiumTest
    void testDrivenEntirelyByDataFile() throws Throwable
    {
        Neodymium.ai().execute();
    }
}
