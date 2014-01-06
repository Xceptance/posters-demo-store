package models;

import java.util.List;

import ninja.NinjaTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.avaje.ebean.Ebean;

public class BasketTest extends NinjaTest
{
    Product product1 = new Product();

    Product product2 = new Product();

    Basket basket = new Basket();

    PosterSize size = new PosterSize();;

    @Before
    public void setUp()
    {
        // set some data for product1
        product1.setName("product1");
        product1.setMinimumPrice(5.55);
        product1.save();
        // set some data for product2
        product2.setName("product2");
        product2.setMinimumPrice(7.77);
        product2.save();
        // persist basket
        basket.save();
        // set some data for product size
        size.setWidth(16);
        size.setHeight(12);
        size.save();
        // set product size of product1
        Product_PosterSize productPosterSize = new Product_PosterSize();
        productPosterSize.setProduct(product1);
        productPosterSize.setSize(size);
        productPosterSize.setPrice(product1.getMinimumPrice());
        productPosterSize.save();
        // set product size of product2
        Product_PosterSize productPosterSize2 = new Product_PosterSize();
        productPosterSize2.setProduct(product2);
        productPosterSize2.setSize(size);
        productPosterSize2.setPrice(product2.getMinimumPrice());
        productPosterSize2.save();
    }

    @After
    public void tearDown()
    {
        // remove products from basket
        basket.clearProducts();
    }

    @Test
    public void testCreateBasket()
    {
        // create new basket
        Basket basket = new Basket();
        // persist basket
        basket.save();
        // verify, that basket is persistent
        Assert.assertNotNull(Ebean.find(Basket.class, basket.getId()));
        // set some data
        basket.setTotalPrice(3.33);
        // persist again
        basket.update();
        // verify, that basket is persistent
        Assert.assertEquals(3.33, Ebean.find(Basket.class, basket.getId()).getTotalPrice(), 0.01);
    }

    @Test
    public void testAddProductToBasket()
    {
        // add product to basket
        addProductToBasket(product1, "matte", size);
        // get all products of the basket
        List<Basket_Product> basketProducts = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();
        // verify, that product one is in the basket...
        Assert.assertEquals(product1.getName(), basketProducts.get(0).getProduct().getName());
        // ...with an amount of one
        Assert.assertEquals(1, basketProducts.get(0).getCountProduct());
        // verify the finish
        Assert.assertEquals("matte", basketProducts.get(0).getFinish());
        // verify the size
        Assert.assertEquals(size.getId(), basketProducts.get(0).getSize().getId());
    }

    @Test
    public void testDeleteSingleProductFromBasket()
    {
        // add product to basket
        addProductToBasket(product1, "matte", size);
        // get all products of the basket
        List<Basket_Product> basketProducts = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();
        // verify, that product one is in the basket...
        Assert.assertEquals(product1.getName(), basketProducts.get(0).getProduct().getName());
        // ...with an amount of one
        Assert.assertEquals(1, basketProducts.get(0).getCountProduct());
        // delete product one from basket
        basket.deleteProduct(basketProducts.get(0));
        // persist
        basket.update();
        // get all products of the basket
        List<Basket_Product> basketProducts2 = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();
        // verify, that no product is in the basket
        Assert.assertTrue(basketProducts2.size() == 0);
    }

    @Test
    public void testDeleteProductsFromBasket()
    {
        // add products to basket
        addProductToBasket(product1, "matte", size);
        addProductToBasket(product1, "matte", size);
        addProductToBasket(product2, "matte", size);
        // get all products of the basket
        List<Basket_Product> basketProducts = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();
        // verify, that product one is in the basket...
        Assert.assertEquals(product1.getName(), basketProducts.get(0).getProduct().getName());
        // ...with an amount of two
        Assert.assertEquals(2, basketProducts.get(0).getCountProduct());
        // verify, that product two is in the basket...
        Assert.assertEquals(product2.getName(), basketProducts.get(1).getProduct().getName());
        // ...with an amount of one
        Assert.assertEquals(1, basketProducts.get(1).getCountProduct());

        // delete product one from basket
        basket.deleteProduct(basketProducts.get(0));
        basket.update();

        // get all products of the basket
        List<Basket_Product> basketProducts2 = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();
        // verify, that product one is in the basket...
        Assert.assertEquals(product1.getName(), basketProducts2.get(0).getProduct().getName());
        // ...with an amount of one
        Assert.assertEquals(1, basketProducts2.get(0).getCountProduct());
        // verify, that product two is in the basket...
        Assert.assertEquals(product2.getName(), basketProducts2.get(1).getProduct().getName());
        // ...with an amount of one
        Assert.assertEquals(1, basketProducts2.get(1).getCountProduct());
        // verify, that total price is sum of both product prices
        Assert.assertEquals(13.32, basket.getTotalPrice(), 0.01);

        // delete product two from basket
        basket.deleteProduct(basketProducts2.get(1));
        basket.update();

        // get all products of the basket
        List<Basket_Product> basketProducts3 = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();
        // verify, that product one is in the basket...
        Assert.assertEquals(product1.getName(), basketProducts3.get(0).getProduct().getName());
        // ...with an amount of one
        Assert.assertEquals(1, basketProducts3.get(0).getCountProduct());
        // verify, that product two is not in the basket
        Assert.assertTrue(basketProducts3.size() == 1);
        // verify, that total price is price of product one
        Assert.assertEquals(5.55, basket.getTotalPrice(), 0.01);

        // delete product one from basket
        basket.deleteProduct(basketProducts3.get(0));
        basket.update();

        // get all products of the basket
        List<Basket_Product> basketProducts4 = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();
        // verify, that no product is in the basket
        Assert.assertTrue(basketProducts4.size() == 0);
        // verify, that total price is zero
        Assert.assertEquals(0.0, basket.getTotalPrice(), 0.01);
    }

    @Test
    public void testAddSameProductMoreThanOnce()
    {
        // add products to basket
        addProductToBasket(product1, "matte", size);
        addProductToBasket(product1, "matte", size);

        // verify, that just one product is in the basket
        Assert.assertEquals(1, basket.getProducts().size());

        // get all products of the basket
        List<Basket_Product> basketProducts = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();

        // verify, that product one is in the basket...
        Assert.assertEquals(product1.getName(), basketProducts.get(0).getProduct().getName());
        // ...with an amount of two
        Assert.assertEquals(2, basketProducts.get(0).getCountProduct());
        // verify, that total price is sum of both product prices
        Assert.assertEquals(11.10, basket.getTotalPrice(), 0.01);
    }

    @Test
    public void testAddSameProductDifferentFinish()
    {
        // add products to basket
        addProductToBasket(product1, "matte", size);
        addProductToBasket(product1, "gloss", size);

        // verify, that two different products are in the basket
        Assert.assertEquals(2, basket.getProducts().size());
    }

    @Test
    public void testAddSameProductDifferentSize()
    {
        PosterSize otherSize = new PosterSize();
        otherSize.setWidth(16);
        otherSize.setHeight(12);
        otherSize.save();
        // add product size to product1
        Product_PosterSize productPosterSize = new Product_PosterSize();
        productPosterSize.setProduct(product1);
        productPosterSize.setSize(otherSize);
        productPosterSize.setPrice(product1.getMinimumPrice());
        productPosterSize.save();

        // add products to basket
        addProductToBasket(product1, "matte", size);
        addProductToBasket(product1, "matte", otherSize);

        // verify, that two different products are in the basket
        Assert.assertEquals(2, basket.getProducts().size());
    }

    @Test
    public void testAddCustomerToBasket()
    {
        // create new customer
        Customer customer = new Customer();
        customer.setName("customer");
        customer.save();
        // set customer to basket
        basket.setCustomer(customer);
        basket.update();

        // verify, that customer was set to basket
        Assert.assertEquals(customer, basket.getCustomer());
    }

    @Test
    public void testClearProducts()
    {
        // add products to basket
        addProductToBasket(product1, "matte", size);
        addProductToBasket(product1, "matte", size);
        addProductToBasket(product2, "matte", size);

        // verify, that two different products are in the basket
        Assert.assertEquals(2, basket.getProducts().size());

        // remove all products from the basket
        basket.clearProducts();
        // get all products of the basket
        List<Basket_Product> basketProducts2 = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();
        // verify, that no product is in the basket
        Assert.assertEquals(0, basketProducts2.size());
        // verify, that total price is zero
        Assert.assertEquals(0.0, basket.getTotalPrice(), 0.01);
    }

    /**
     * Adds the given product with the given finish and size to the basket.
     * 
     * @param product
     * @param finish
     * @param size
     */
    private void addProductToBasket(Product product, String finish, PosterSize size)
    {
        basket.addProduct(product, finish, size);
        basket.update();
    }
}
