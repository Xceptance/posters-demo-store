package com.xceptance.neodymium.ai.test;

import org.junit.jupiter.api.BeforeAll;

import com.xceptance.neodymium.common.browser.Browser;
import com.xceptance.neodymium.common.testdata.DataFile;
import com.xceptance.neodymium.junit5.NeodymiumTest;
import com.xceptance.neodymium.util.Neodymium;

/**
 * Showcase for the interactive mode
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/search/TC_SRC_001_SingleTerm.yaml")
public class InteractiveDebugTest
{

    @BeforeAll
    public static void setup()
    {
        // can be set in properties, just here so it's directly visible
        System.setProperty("neodymium.ai.interactive", "true");

    }

    @NeodymiumTest
    void debugTest() throws Throwable
    {
        Neodymium.ai().execute();
    }

}
