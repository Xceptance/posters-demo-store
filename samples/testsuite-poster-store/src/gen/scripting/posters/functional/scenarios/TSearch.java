package gen.scripting.posters.functional.scenarios;

import org.junit.After;
import org.junit.Test;
import com.xceptance.xlt.api.webdriver.XltDriver;
import com.xceptance.xlt.api.engine.scripting.AbstractWebDriverScriptTestCase;
import gen.scripting.posters.functional.modules.OpenHomepage;
import gen.scripting.posters.functional.modules.Search;

/**
 * Simulates storefront search including search result browsing.
 */
public class TSearch extends AbstractWebDriverScriptTestCase
{

    /**
     * Constructor.
     */
    public TSearch()
    {
        super(new XltDriver(true), "http://localhost:8080");
    }


    /**
     * Executes the test.
     *
     * @throws Throwable if anything went wrong
     */
    @Test
    public void test() throws Throwable
    {
        final OpenHomepage _openHomepage = new OpenHomepage();
        _openHomepage.execute();


        //
        // ~~~ Search-NoHits ~~~
        //
        startAction("Search_NoHits");
        // Store a search phrase that does not return any search results
        store(resolve("${searchTerm_noHits}"), "searchTerm");
        // Execute the search (module call)
        final Search _search = new Search();
        _search.execute(resolve("${searchTerm_hits}"));

        // Assert presence of info maessage element
        assertElementPresent("id=infoMessage");
        // Validate the 'no results' message
        assertText("xpath=id('infoMessage')/div/strong", "*Sorry! No results found matching your search. Please try again.*");

        //
        // ~~~ Search ~~~
        //
        startAction("Search");
        // Store a search phrase that gives results
        store(resolve("${searchTerm_hits}"), "searchTerm");
        // Execute the search (module call)
        _search.execute(resolve("${searchTerm_hits}"));

        // Validate the entered search phrase is still visible in the input
        assertText("id=searchText", resolve("${searchTerm_hits}"));
        // Validate presence of the search results page headline
        assertElementPresent("id=titleSearchText");
        // Validate the headline contains the search phrase
        assertText("id=titleSearchText", resolve("glob:*Your results for your search: '${searchTerm_hits}'*"));

        //
        // ~~~ ViewProduct ~~~
        //
        startAction("ViewProduct");
        // Assert presence of one of the product thumbnails
        assertElementPresent("id=product0");
        // Store the name of the first product
        storeText("id=product0Name", "productName");
        // Click the product ilnk to open the product detail page
        clickAndWait("//*[@id='product0']//img");
        // Validate it's the correct product detail page
        assertText("id=titleProductName", resolve("${productName}"));

    }


    /**
     * Clean up.
     */
    @After
    public void after()
    {
        // Shutdown WebDriver.
        getWebDriver().quit();
    }
}