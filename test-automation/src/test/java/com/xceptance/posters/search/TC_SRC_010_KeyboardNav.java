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
 * Automates TC_SRC_010 - Keyboard Navigation & Accessibility
 */
@Browser("Chrome_1500x1000")
@DataFile("posters/search/TC_SRC_010_KeyboardNav.yaml")
@Tag("search")
@Tag("keyboard")
@Tag("a11y")
@Tag("navigation")
@Tag("regression")
@Tag("full")
public class TC_SRC_010_KeyboardNav
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
    @DataSet(id = "tc_src_010_en_US")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a user can fully utilize the search functionality using only a keyboard (en-US).")
    final void testKeyboardNavEnUS() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_010_en_GB")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a user can fully utilize the search functionality using only a keyboard (en-GB).")
    final void testKeyboardNavEnGB() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_010_de_DE")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a user can fully utilize the search functionality using only a keyboard (de-DE).")
    final void testKeyboardNavDeDE() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_010_sv_SE")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a user can fully utilize the search functionality using only a keyboard (sv-SE).")
    final void testKeyboardNavSvSE() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_010_ja_JP")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a user can fully utilize the search functionality using only a keyboard (ja-JP).")
    final void testKeyboardNavJaJP() throws Throwable
    {
        executeTest();
    }
}
