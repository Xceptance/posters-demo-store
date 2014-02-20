package com.xceptance.loadtest.actions;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xceptance.loadtest.util.SearchOption;
import com.xceptance.loadtest.validators.HeaderValidator;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.util.HtmlPageUtils;
import com.xceptance.xlt.api.validators.ContentLengthValidator;
import com.xceptance.xlt.api.validators.HtmlEndTagValidator;
import com.xceptance.xlt.api.validators.HttpResponseCodeValidator;

/**
 * Enter the given search phrase in the site's search bar and submit.
 * 
 * @author sebastianloob
 */
public class Search extends AbstractHtmlPageAction
{

    /**
     * Search phrase.
     */
    private final String phrase;

    /**
     * Search form.
     */
    private HtmlForm searchForm;

    /**
     * Search option ({@link SearchOption#HITS} or {@link SearchOption#NO_HITS} ).
     */
    private final SearchOption searchOption;

    /**
     * Constructor.
     */
    public Search(AbstractHtmlPageAction previousAction, String timerName, String phrase, SearchOption option)
    {
        super(previousAction, timerName);
        this.phrase = phrase;
        this.searchOption = option;
    }

    @Override
    public void preValidate() throws Exception
    {
        // Get the current page.
        final HtmlPage page = getPreviousAction().getHtmlPage();
        Assert.assertNotNull("Failed to get page from previous action.", page);

        // check that the search form is available
        Assert.assertTrue("Search form not found.", HtmlPageUtils.isElementPresent(page, "id('search')"));
        // remember the search form
        this.searchForm = HtmlPageUtils.findSingleHtmlElementByID(page, "search");
    }

    @Override
    protected void execute() throws Exception
    {
        // fill the search form with the given phrase
        HtmlPageUtils.setInputValue(searchForm, "searchText", phrase);
        // submit the search
        loadPageByFormSubmit(searchForm);
    }

    @Override
    protected void postValidate() throws Exception
    {
        // get the result of the last action
        final HtmlPage page = getHtmlPage();

        // Basic checks - see action 'Homepage' for some more details how and when to use these validators
        HttpResponseCodeValidator.getInstance().validate(page);
        ContentLengthValidator.getInstance().validate(page);
        HtmlEndTagValidator.getInstance().validate(page);

        HeaderValidator.getInstance().validate(page);

        // Check that the desired option result was achieved.
        switch (searchOption)
        {
            case HITS:
                Assert.assertNotNull("Expected at least one hit for '" + phrase + "'.",
                                     HtmlPageUtils.findSingleHtmlElementByID(page, "productOverview"));
                break;

            case NO_HITS:
                Assert.assertFalse("Search phrase '" + phrase + "' should result in no hits.",
                                  HtmlPageUtils.isElementPresent(page, "productOverview"));
                break;

            default:
                Assert.fail("Unknown search option.");
                break;
        }
    }
}
