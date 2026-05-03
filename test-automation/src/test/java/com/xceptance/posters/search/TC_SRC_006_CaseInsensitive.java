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
 * Automates TC_SRC_006 - Case-Insensitive Search
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/search/TC_SRC_006_CaseInsensitive.yaml")
@Tag("search")
@Tag("case-insensitive")
@Tag("analyzer")
@Tag("localization")
@Tag("regression")
@Tag("full")
public class TC_SRC_006_CaseInsensitive
{
    @NeodymiumTest
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the search is case-insensitive: the same query in different letter cases returns identical results.")
    void executePosterTest() throws Throwable
    {
        Neodymium.ai().execute();
    }
}
