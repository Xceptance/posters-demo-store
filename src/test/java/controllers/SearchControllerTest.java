package controllers;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import models.Product;
import ninja.NinjaTest;

import org.junit.Test;

public class SearchControllerTest extends NinjaTest
{


    @Test
    public void testMergeProductLists()
    {
        final List<Product> productList = new ArrayList<Product>();
        final List<Product> productsToAdd = new ArrayList<Product>();
        Product product1 = new Product();
        product1.setName("plane");
        product1.save();
        Product product2 = new Product();
        product2.setName("railway");
        product2.save();
        Product product3 = new Product();
        product3.setName("car");
        product3.save();
        productList.add(product1);
        productList.add(product2);
        productsToAdd.add(product2);
        productsToAdd.add(product3);
        SearchController.mergeProductLists(productList, productsToAdd);
        Assert.assertNotNull(productList);
        Assert.assertNotNull(productsToAdd);
        Assert.assertEquals(3, productList.size());
        Assert.assertTrue(productList.contains(product1));
        Assert.assertTrue(productList.contains(product2));
        Assert.assertTrue(productList.contains(product3));
    }

}
