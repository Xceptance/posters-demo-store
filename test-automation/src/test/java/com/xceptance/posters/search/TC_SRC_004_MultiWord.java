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
    /**
     * Common execution logic for the test.
     * @throws Throwable if an error occurs
     */
    private final void executeTest() throws Throwable
    {
        Neodymium.ai().execute();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_004_sv_SE")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the search engine uses AND as the default operator for multi-word queries, returning only products matching all terms.")
    final void testSvSe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_004_en_US")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the search engine uses AND as the default operator for multi-word queries, returning only products matching all terms.")
    final void testEnUs() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_004_de_DE")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the search engine uses AND as the default operator for multi-word queries, returning only products matching all terms.")
    final void testDeDe() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_004_ja_JP")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the search engine uses AND as the default operator for multi-word queries, returning only products matching all terms.")
    final void testJaJp() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_004_en_reversed")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the search engine uses AND as the default operator for multi-word queries, returning only products matching all terms.")
    final void testEnReversed() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_004_en_three_words")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the search engine uses AND as the default operator for multi-word queries, returning only products matching all terms.")
    final void testEnThreeWords() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_004_en_full_desc")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the search engine uses AND as the default operator for multi-word queries, returning only products matching all terms.")
    final void testEnFullDesc() throws Throwable
    {
        executeTest();
    }

}
