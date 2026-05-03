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
 * Automates TC_SRC_005 - Partial / Prefix Search
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/search/TC_SRC_005_PrefixSearch.yaml")
@Tag("search")
@Tag("prefix")
@Tag("partial")
@Tag("type-ahead")
@Tag("wildcard")
@Tag("regression")
@Tag("full")
public class TC_SRC_005_PrefixSearch
{
    @NeodymiumTest
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that entering an incomplete word (prefix) into the search returns products whose indexed fields contain words starting with the typed prefix.")
    void executePosterTest() throws Throwable
    {
        Neodymium.ai().execute();
    }
}
