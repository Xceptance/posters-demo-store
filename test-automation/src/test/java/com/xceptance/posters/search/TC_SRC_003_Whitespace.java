package com.xceptance.posters.search;

import com.xceptance.neodymium.common.browser.Browser;
import com.xceptance.neodymium.common.testdata.DataFile;
import com.xceptance.neodymium.junit5.NeodymiumTest;
import com.xceptance.neodymium.util.Neodymium;

import org.junit.jupiter.api.Tag;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

/**
 * Automates TC_SRC_003 - Search Whitespace & Empty Query Handling
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/search/TC_SRC_003_Whitespace.yaml")
@Tag("search")
@Tag("whitespace")
@Tag("regression")
@Tag("full")
public class TC_SRC_003_Whitespace
{
    @NeodymiumTest
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the search gracefully handles various forms of empty, whitespace-only, and whitespace-padded input without errors or unexpected results.")
    void executePosterTest() throws Throwable
    {
        Neodymium.ai().execute();
    }
}
