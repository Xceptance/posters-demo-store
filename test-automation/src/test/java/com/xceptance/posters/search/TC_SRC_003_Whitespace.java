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
 * Automates TC_SRC_003 - Search Whitespace & Empty Query Handling
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/search/TC_SRC_003_Whitespace.yaml")
@Tag("search")
@Tag("whitespace")
@Tag("regression")
@Tag("full")
public class TC_SRC_003_Whitespace
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
    @DataSet(id = "tc_src_003_single_space")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the search gracefully handles various forms of empty, whitespace-only, and whitespace-padded input without errors or unexpected results.")
    final void testSingleSpace() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_003_leading_whitespace")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the search gracefully handles various forms of empty, whitespace-only, and whitespace-padded input without errors or unexpected results.")
    final void testLeadingWhitespace() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_003_empty")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the search gracefully handles various forms of empty, whitespace-only, and whitespace-padded input without errors or unexpected results.")
    final void testEmpty() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_003_trailing_whitespace")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the search gracefully handles various forms of empty, whitespace-only, and whitespace-padded input without errors or unexpected results.")
    final void testTrailingWhitespace() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_003_multiple_spaces")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the search gracefully handles various forms of empty, whitespace-only, and whitespace-padded input without errors or unexpected results.")
    final void testMultipleSpaces() throws Throwable
    {
        executeTest();
    }

}
