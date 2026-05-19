package com.xceptance.posters.search;

import com.xceptance.neodymium.common.browser.Browser;
import com.xceptance.neodymium.common.testdata.DataFile;
import com.xceptance.neodymium.common.testdata.DataSet;
import com.xceptance.neodymium.junit5.NeodymiumTest;
import com.xceptance.neodymium.util.Neodymium;

import org.junit.jupiter.api.BeforeAll;
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

    @BeforeAll
    public static void setup()
    {
        // can be set in properties, just here so it's directly visible
        // System.setProperty("neodymium.ai.interactive", "true");
    }

    /**
     * Common execution logic for the test.
     * @throws Throwable if an error occurs
     */
    private final void executeTest() throws Throwable
    {
        Neodymium.ai().execute();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_001_en_GB")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that entering a single known product-related search term returns relevant results with correctly localized product names, descriptions, images, prices, and a 'Buy Here' action link.")
    final void testEnGb() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_001_en_US")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that entering a single known product-related search term returns relevant results with correctly localized product names, descriptions, images, prices, and a 'Buy Here' action link.")
    final void testEnUs() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_001_de_DE")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that entering a single known product-related search term returns relevant results with correctly localized product names, descriptions, images, prices, and a 'Buy Here' action link.")
    final void testDeDe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_001_sv_SE")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that entering a single known product-related search term returns relevant results with correctly localized product names, descriptions, images, prices, and a 'Buy Here' action link.")
    final void testSvSe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_001_ja_JP")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that entering a single known product-related search term returns relevant results with correctly localized product names, descriptions, images, prices, and a 'Buy Here' action link.")
    final void testJaJp() throws Throwable
    {
        executeTest();
    }

}
