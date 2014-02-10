package com.xceptance.loadtest.tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.xceptance.loadtest.actions.Homepage;
import com.xceptance.loadtest.actions.Search;
import com.xceptance.loadtest.actions.catalog.Paging;
import com.xceptance.loadtest.actions.catalog.ProductDetailView;
import com.xceptance.loadtest.util.SearchOption;
import com.xceptance.xlt.api.data.DataProvider;
import com.xceptance.xlt.api.tests.AbstractTestCase;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Open the landing page and search for predefined key words as well as for random string. If there are search results
 * open a random product's quick or detail view.
 * 
 * @author sebastianloob
 */
public class TSearch extends AbstractTestCase
{

    /**
     * Data provider for search results.
     */
    private static final DataProvider HITS_PROVIDER;
    static
    {
        try
        {
            // Initialize the search provider with the search phrases file
            // 'results.txt'
            HITS_PROVIDER = DataProvider.getInstance(DataProvider.DEFAULT + File.separator + "results.txt");
        }
        catch (IOException ioe)
        {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Main test method.
     * 
     * @throws Throwable
     */
    @Test
    public void search() throws Throwable
    {
        // Read the store URL from properties. Directly referring to the properties allows to access them by the full
        // path.
        final String url = getProperty("com.xceptance.xlt.loadtest.tests.store-url", "http://localhost:8080/");

        // Go to poster store homepage
        Homepage homepage = new Homepage(url, "Homepage");
        homepage.run();

        // Get the number of searches determined from the configured min and max
        // products.
        final int searches = XltRandom.nextInt(getProperty("products.min", 1), getProperty("products.max", 1));
        for (int i = 0; i < searches; i++)
        {
            // The search option is the indicator whether to search for one of
            // the search phrases from the 'HITS_PROVIDER' that results in a hit
            // or a generated phrase that results in a 'no results' page.
            final SearchOption option = getSearchOption(getProperty("search.nohits.probability", 0));

            // Run the search with an appropriate search phrase according to the
            // search option.
            Search search = new Search(homepage, "Search", getSearchPhrase(option), option);
            search.run();

            // paging
            Paging paging = new Paging(search, "Paging");
            paging.run();

            // product detail view
            ProductDetailView productDetailView = new ProductDetailView(paging, "ProductDetailView");
            productDetailView.run();
        }
    }

    /**
     * Returns a search option using the given probability.
     * 
     * @param searchNoHitsProbability
     *            probability to grab the {@link SearchOption#NO_HITS} search option
     * @return search option
     */
    private SearchOption getSearchOption(final int searchNoHitsProbability)
    {
        if (XltRandom.nextBoolean(searchNoHitsProbability))
        {
            return SearchOption.NO_HITS;
        }
        else
        {
            return SearchOption.HITS;
        }
    }

    /**
     * Returns a search phrase.
     * 
     * @return search phrase
     */
    protected String getSearchPhrase(SearchOption option)
    {
        switch (option)
        {
            case HITS:
                // Return one of the predefined search phrases.
                return HITS_PROVIDER.getRandomRow(false);

            case NO_HITS:
                // Return a random alphanumeric string, make it random and long enough.
                return RandomStringUtils.randomAlphabetic(XltRandom.nextInt(5, 10)) + " "
                       + RandomStringUtils.randomAlphabetic(XltRandom.nextInt(8, 10));
            default:
                return null;
        }
    }
}
