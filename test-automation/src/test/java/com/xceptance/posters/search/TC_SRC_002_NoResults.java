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
 * Automates TC_SRC_002 - Search — No Results
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/search/TC_SRC_002_NoResults.yaml")
@Tag("search")
@Tag("no-results")
@Tag("empty-state")
@Tag("localization")
@Tag("regression")
@Tag("full")
public class TC_SRC_002_NoResults
{
    @NeodymiumTest
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that searching for a term that does not match any product in the catalog displays a user-friendly empty state with an appropriate message and a link to continue shopping.")
    void executePosterTest() throws Throwable
    {
        Neodymium.ai().execute();
    }
}
