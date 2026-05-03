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
 * Automates TC_SRC_001 - Simple Search — Single Term (Happy Path)
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/search/TC_SRC_001_SingleTerm.yaml")
@Tag("search")
@Tag("happy-path")
@Tag("localization")
@Tag("stemming")
@Tag("smoke")
@Tag("regression")
@Tag("full")
public class TC_SRC_001_SingleTerm
{
    @NeodymiumTest
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that entering a single known product-related search term returns relevant results with correctly localized product names, descriptions, images, prices, and a 'Buy Here' action link.")
    void executePosterTest() throws Throwable
    {
        Neodymium.ai().execute();
    }
}
