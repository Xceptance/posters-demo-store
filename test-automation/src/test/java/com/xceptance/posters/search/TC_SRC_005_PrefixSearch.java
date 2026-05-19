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
    /**
     * Common execution logic for the test.
     * @throws Throwable if an error occurs
     */
    private final void executeTest() throws Throwable
    {
        Neodymium.ai().execute();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_005_en_griz")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that entering an incomplete word (prefix) into the search returns products whose indexed fields contain words starting with the typed prefix.")
    final void testEnGriz() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_005_en_but")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that entering an incomplete word (prefix) into the search returns products whose indexed fields contain words starting with the typed prefix.")
    final void testEnBut() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_005_en_dah")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that entering an incomplete word (prefix) into the search returns products whose indexed fields contain words starting with the typed prefix.")
    final void testEnDah() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_005_de_Grizz")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that entering an incomplete word (prefix) into the search returns products whose indexed fields contain words starting with the typed prefix.")
    final void testDeGrizz() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_005_de_Schmet")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that entering an incomplete word (prefix) into the search returns products whose indexed fields contain words starting with the typed prefix.")
    final void testDeSchmet() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_005_sv_bjor")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that entering an incomplete word (prefix) into the search returns products whose indexed fields contain words starting with the typed prefix.")
    final void testSvBjor() throws Throwable
    {
        executeTest();
    }

    @NeodymiumTest
    @DataSet(id = "tc_src_005_ja_dari")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that entering an incomplete word (prefix) into the search returns products whose indexed fields contain words starting with the typed prefix.")
    final void testJaDari() throws Throwable
    {
        executeTest();
    }

}
