package com.xceptance.posters.search;

import com.xceptance.neodymium.common.browser.Browser;
import com.xceptance.neodymium.common.testdata.DataFile;
import com.xceptance.neodymium.common.testdata.DataSet;
import com.xceptance.neodymium.junit5.NeodymiumTest;
import com.xceptance.neodymium.util.Neodymium;

import org.junit.jupiter.api.Tag;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

/**
 * Automates TC_SRC_007 - Search Result Correctness & Navigation
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/search/TC_SRC_007_ResultCorrectness.yaml")
@Tag("search")
@Tag("result-correctness")
@Tag("navigation")
@Tag("PDP")
@Tag("data-integrity")
@Tag("smoke")
@Tag("regression")
@Tag("full")
public class TC_SRC_007_ResultCorrectness
{
    /**
     * Common execution logic for the test.
     * @throws Throwable if an error occurs
     */
    private final void executeTest() throws Throwable
    {
        Neodymium.ai().execute();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_007_en_GB")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that search results are valid and correct: each rendered product tile genuinely matches the search query and navigates correctly.")
    final void testEnGb() throws Throwable
    {
        executeTest();
    }
    
    @NeodymiumTest
    @DataSet(id = "tc_src_007_en_US")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that search results are valid and correct: each rendered product tile genuinely matches the search query and navigates correctly.")
    final void testEnUs() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_007_de_DE")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that search results are valid and correct: each rendered product tile genuinely matches the search query and navigates correctly.")
    final void testDeDe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_007_sv_SE")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that search results are valid and correct: each rendered product tile genuinely matches the search query and navigates correctly.")
    final void testSvSe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_007_ja_JP")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that search results are valid and correct: each rendered product tile genuinely matches the search query and navigates correctly.")
    final void testJaJp() throws Throwable
    {
        executeTest();
    }

}
