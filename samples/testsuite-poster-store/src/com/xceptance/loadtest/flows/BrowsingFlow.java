package com.xceptance.loadtest.flows;

import com.xceptance.loadtest.actions.catalog.Paging;
import com.xceptance.loadtest.actions.catalog.ProductDetailView;
import com.xceptance.loadtest.actions.catalog.SelectCategory;
import com.xceptance.loadtest.actions.catalog.SelectTopCategory;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Browse the catalogue by selecting a random top category followed by a sub category.
 * If paging is configured in the project properties we perform one or more paging actions
 * and finally loading a random product detail page.
 * The browsing is encapsulated in a flow that combines a sequence of several XLT actions.
 * Different test cases can call this method now to reuse the flow. 
 * This is a concept for code structuring you can implement if needed, yet explicit support 
 * is neither available in the XLT framework nor necessary when you manually create a flow.
 */
public class BrowsingFlow
{

    /**
     * The previous action
     */
    private AbstractHtmlPageAction previousAction;

    /**
     * The probability to perform a paging
     */
    private int pagingProbability;

    /**
     * The minimum number of paging rounds
     */
    private int pagingMin;

    /**
     * The maximum number of paging rounds
     */
    private int pagingMax;


    /**
     * Constructor
     * 
     * @param previousAction
     * 		The previously performed action
     * @param pagingProbability
     * 		The paging probability
     * @param pagingMin
     * 		The minimum number of paging rounds
     * @param pagingMax
     * 		The maximum number of paging rounds
     */
    public BrowsingFlow(AbstractHtmlPageAction previousAction, int pagingProbability, int pagingMin, int pagingMax)
    {
	this.previousAction = previousAction;
	this.pagingProbability = pagingProbability;
	this.pagingMin = pagingMin;
	this.pagingMax = pagingMax;
    }

    
    /**
     * {@inheritDoc}
     */
    public AbstractHtmlPageAction run() throws Throwable
    {

	// Select a random top category from side navigation
	SelectTopCategory selectTopCategory = new SelectTopCategory(previousAction);
	selectTopCategory.run();
	previousAction = selectTopCategory;

	// Select a random level-1 category from side navigation
	SelectCategory selectCategory = new SelectCategory(previousAction);
	selectCategory.run();
	previousAction = selectCategory;

	// According to the configured probability perform the paging or not.
	if (XltRandom.nextBoolean(pagingProbability))
	{
	    // Get current number of paging rounds determined from the configured
	    // min and max value for paging.
	    final int pagingRounds = XltRandom.nextInt(pagingMin, pagingMax);
	    for (int j = 0; j < pagingRounds; j++)
	    {
		// perform a paging if possible
		Paging paging = new Paging(previousAction);

		if (paging.preValidateSafe())
		{
		    paging.run();
		}
		else
		{
		    break;
		}

		previousAction = paging;
	    }
	}

	// Select a random poster from product overview and show product detail page
	ProductDetailView productDetailView = new ProductDetailView(previousAction);
	productDetailView.run();
	previousAction = productDetailView;

	// Return the result of the last action in this flow
	return previousAction;
    }

}
