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
 * Automates TC_SRC_004 - Multi-Word Search (AND Behavior)
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/search/TC_SRC_004_MultiWord.yaml")
@Tag("search")
@Tag("multi-word")
@Tag("AND-operator")
@Tag("term-order")
@Tag("description-paste")
@Tag("regression")
@Tag("full")
public class TC_SRC_004_MultiWord
{
    @NeodymiumTest
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the search engine uses AND as the default operator for multi-word queries, returning only products matching all terms.")
    void executePosterTest() throws Throwable
    {
        Neodymium.ai().execute();
    }
}
