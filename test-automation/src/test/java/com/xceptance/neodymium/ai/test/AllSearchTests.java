package com.xceptance.neodymium.ai.test;

import com.xceptance.neodymium.common.browser.Browser;
import com.xceptance.neodymium.common.testdata.DataFolder;
import com.xceptance.neodymium.junit5.NeodymiumTest;
import com.xceptance.neodymium.util.Neodymium;

/**
 * Just run everything from the folder. New tests will be put there, and will be executed with the whole set
 */
@Browser("Chrome_1500x1000")
@DataFolder("search")
public class AllSearchTests
{
    @NeodymiumTest
    void executePosterTest() throws Throwable
    {
        Neodymium.ai().execute();
    }
}
