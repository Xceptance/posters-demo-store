package com.xceptance.loadtest.flows;

import com.xceptance.loadtest.actions.AddToCart;
import com.xceptance.loadtest.actions.catalog.ProductDetailView;
import com.xceptance.loadtest.actions.catalog.SelectCategory;
import com.xceptance.loadtest.actions.catalog.SelectTopCategory;
import com.xceptance.loadtest.actions.order.ViewCart;
import com.xceptance.xlt.api.actions.AbstractHtmlPageAction;

/**
 * Browse the catalog, add product to cart and view cart overview page.
 * 
 * @author sebastian
 * 
 */
public class BrowseAndAddToCartFlow
{

    /**
     * The previous action
     */
    private AbstractHtmlPageAction previousAction;

    /**
     * Constructor
     * 
     * @param previousAction
     */
    public BrowseAndAddToCartFlow(AbstractHtmlPageAction previousAction)
    {
	this.previousAction = previousAction;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractHtmlPageAction run() throws Throwable
    {

	// Select a random top category from side navigation
	SelectTopCategory selectTopCategory = new SelectTopCategory(previousAction,
		"SelectTopCategory");
	selectTopCategory.run();

	// Select a random level-1 category from side navigation
	SelectCategory selectCategory = new SelectCategory(selectTopCategory,
		"SelectCategory");
	selectCategory.run();

	// Select a random poster from product overview and show product detail
	// page
	ProductDetailView productDetailView = new ProductDetailView(
		selectCategory, "ProductDetailView");
	productDetailView.run();

	// Configure the product (size and finish) and add it to cart
	AddToCart addToCart = new AddToCart(productDetailView, "AddToCart");
	addToCart.run();

	// go to the cart overview page
	ViewCart viewCart = new ViewCart(addToCart, "ViewCart");
	viewCart.run();

	return viewCart;
    }

}
