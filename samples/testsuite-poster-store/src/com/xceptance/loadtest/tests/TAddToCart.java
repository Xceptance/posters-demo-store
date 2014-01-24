package com.xceptance.loadtest.tests;

import org.junit.Test;

import com.xceptance.loadtest.actions.AddToCart;
import com.xceptance.loadtest.actions.Homepage;
import com.xceptance.loadtest.actions.ProductDetailView;
import com.xceptance.loadtest.actions.SelectCategory;
import com.xceptance.loadtest.actions.SelectTopCategory;
import com.xceptance.xlt.api.tests.AbstractTestCase;
import com.xceptance.xlt.api.util.XltProperties;

public class TAddToCart extends AbstractTestCase
{
    /**
     * Main test method
     */
    @Test
    public void addToCart() throws Throwable
    {
    // Read the store URL from properties. Directly referring to the properties allows to access them by the full
    // path.
    final String url = XltProperties.getInstance().getProperty("com.xceptance.xlt.loadtest.tests.store-url", "http://localhost:8080/");
    
    // Go to poster store homepage
    Homepage homepage = new Homepage(url, "Homepage");
    homepage.run();
    
    //Select a random top category from side navigation
    SelectTopCategory selectTopCategory = new SelectTopCategory(homepage, "SelectTopCategory");
    selectTopCategory.run();
    
    //Select a random level-1 category from side navigation
    SelectCategory selectCategory = new SelectCategory(selectTopCategory, "SelectCategory");
    selectCategory.run();
    
    // Select a random poster from product overview and show product detail page
    ProductDetailView productDetailView = new ProductDetailView(selectCategory, "ProductDetailView");
    productDetailView.run();
    
    // Configure the product (size and finish) and add it to cart
    AddToCart addToCart = new AddToCart(productDetailView, "AddToCart");
    addToCart.run();
    
    }
}
