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
    /**
     * Common execution logic for the test.
     * @throws Throwable if an error occurs
     */
    private final void executeTest() throws Throwable
    {
        Neodymium.ai().execute();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_006_en_upper")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the search is case-insensitive: the same query in different letter cases returns identical results.")
    final void testEnUpper() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_006_en_mixed")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the search is case-insensitive: the same query in different letter cases returns identical results.")
    final void testEnMixed() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_006_en_title")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the search is case-insensitive: the same query in different letter cases returns identical results.")
    final void testEnTitle() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_006_de_upper")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the search is case-insensitive: the same query in different letter cases returns identical results.")
    final void testDeUpper() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_006_de_lower")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the search is case-insensitive: the same query in different letter cases returns identical results.")
    final void testDeLower() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_006_sv_upper")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the search is case-insensitive: the same query in different letter cases returns identical results.")
    final void testSvUpper() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_006_sv_lower")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the search is case-insensitive: the same query in different letter cases returns identical results.")
    final void testSvLower() throws Throwable
    {
        executeTest();
    }

}
