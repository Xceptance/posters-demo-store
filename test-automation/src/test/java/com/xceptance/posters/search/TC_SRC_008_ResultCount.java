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
 * Automates TC_SRC_008 - Search Result Count & Heading
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/search/TC_SRC_008_ResultCount.yaml")
@Tag("search")
@Tag("result-count")
@Tag("heading")
@Tag("singular-plural")
@Tag("UI")
@Tag("regression")
@Tag("full")
public class TC_SRC_008_ResultCount
{
    @NeodymiumTest
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the search results heading accurately displays the search term and the correct result count with proper singular/plural grammar.")
    void executePosterTest() throws Throwable
    {
        Neodymium.ai().execute();
    }
}
