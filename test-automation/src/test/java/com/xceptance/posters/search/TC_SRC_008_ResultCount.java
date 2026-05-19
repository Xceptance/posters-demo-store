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
    /**
     * Common execution logic for the test.
     * @throws Throwable if an error occurs
     */
    private final void executeTest() throws Throwable
    {
        Neodymium.ai().execute();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_008_en_US_singular")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the search results heading accurately displays the search term and the correct result count with proper singular/plural grammar.")
    final void testEnUsSingular() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_008_en_US_few")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the search results heading accurately displays the search term and the correct result count with proper singular/plural grammar.")
    final void testEnUsFew() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_008_en_US_many")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the search results heading accurately displays the search term and the correct result count with proper singular/plural grammar.")
    final void testEnUsMany() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_008_en_GB_singular")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the search results heading accurately displays the search term and the correct result count with proper singular/plural grammar.")
    final void testEnGbSingular() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_008_en_GB_few")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the search results heading accurately displays the search term and the correct result count with proper singular/plural grammar.")
    final void testEnGbFew() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_008_de_DE_singular")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the search results heading accurately displays the search term and the correct result count with proper singular/plural grammar.")
    final void testDeDeSingular() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_008_de_DE_few")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the search results heading accurately displays the search term and the correct result count with proper singular/plural grammar.")
    final void testDeDeFew() throws Throwable
    {
        executeTest();
    }

}
