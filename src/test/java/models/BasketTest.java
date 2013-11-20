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

    @Before
    public void setUp()
    {
        product1.setName("product1");
        product1.setPrice(5.55);
        product1.save();
        product2.setName("product2");
        product2.setPrice(7.77);
        product2.save();
        basket.save();
    }
    
    @After
    public void tearDown()
    {
        basket.clearProducts();
    }

    @Test
    public void testCreateBasket()
    {
        Basket basket = new Basket();
        basket.setTotalPrice(3.33);
        basket.save();

        Assert.assertEquals(3.33, basket.getTotalPrice(), 0.01);

        basket.setTotalPrice(4.44);
        basket.update();

        Assert.assertEquals(4.44, basket.getTotalPrice(), 0.01);
    }

    @Test
    public void testAddProductToBasket()
    {
        basket.addProduct(product1, "matt");
        basket.update();

        // get all products of the basket
        List<Basket_Product> basketProducts = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();

        // product one is in the basket
        Assert.assertEquals(product1.getName(), basketProducts.get(0).getProduct().getName());
        // one of product one is in the basket
        Assert.assertEquals(1, basketProducts.get(0).getCountProduct());
    }
    
    @Test
    public void testDeleteSingleProductFromBasket()
    {
        basket.addProduct(product1, "matt");
        basket.update();

        // get all products of the basket
        List<Basket_Product> basketProducts = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();
        // product one is in the basket
        Assert.assertEquals(product1.getName(), basketProducts.get(0).getProduct().getName());
        // one of product one is in the basket
        Assert.assertEquals(1, basketProducts.get(0).getCountProduct());
        
        // delete product one from basket
        basket.deleteProduct(basketProducts.get(0));
        basket.update();
        
        // get all products of the basket
        List<Basket_Product> basketProducts2 = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();
        Assert.assertTrue(basketProducts2.size() == 0);
    }
    
    @Test
    public void testDeleteProductsFromBasket()
    {
        basket.addProduct(product1, "matt");
        basket.update();
        
        basket.addProduct(product1, "matt");
        basket.update();
        
        basket.addProduct(product2, "matt");
        basket.update();

        // get all products of the basket
        List<Basket_Product> basketProducts = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();
        // product one is in the basket
        Assert.assertEquals(product1.getName(), basketProducts.get(0).getProduct().getName());
        // two of product one are in the basket
        Assert.assertEquals(2, basketProducts.get(0).getCountProduct());
        // product two is in the basket
        Assert.assertEquals(product2.getName(), basketProducts.get(1).getProduct().getName());
        // one of product two is in the basket
        Assert.assertEquals(1, basketProducts.get(1).getCountProduct());
        
        // delete product one from basket
        basket.deleteProduct(basketProducts.get(0));
        basket.update();
        
        // get all products of the basket
        List<Basket_Product> basketProducts2 = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();
        // product one is in the basket
        Assert.assertEquals(product1.getName(), basketProducts2.get(0).getProduct().getName());
        // one of product one is in the basket
        Assert.assertEquals(1, basketProducts2.get(0).getCountProduct());
        // product two is in the basket
        Assert.assertEquals(product2.getName(), basketProducts2.get(1).getProduct().getName());
        // one of product two is in the basket
        Assert.assertEquals(1, basketProducts2.get(1).getCountProduct());
        // total price is sum of all three product prices
        Assert.assertEquals(13.32, basket.getTotalPrice(), 0.01);
        
        // delete product two from basket
        basket.deleteProduct(basketProducts2.get(1));
        basket.update();
        
        // get all products of the basket
        List<Basket_Product> basketProducts3 = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();
        // product one is in the basket
        Assert.assertEquals(product1.getName(), basketProducts3.get(0).getProduct().getName());
        // one of product one is in the basket
        Assert.assertEquals(1, basketProducts3.get(0).getCountProduct());
        // product two is not in the basket
        Assert.assertTrue(basketProducts3.size() == 1);
        // total price is sum of all three product prices
        Assert.assertEquals(5.55, basket.getTotalPrice(), 0.01);
        
        // delete product one from basket
        basket.deleteProduct(basketProducts3.get(0));
        basket.update();
        
        // get all products of the basket
        List<Basket_Product> basketProducts4 = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();
        Assert.assertTrue(basketProducts4.size() == 0);
        // total price is sum of all three product prices
        Assert.assertEquals(0.0, basket.getTotalPrice(), 0.01);
    }

    @Test
    public void testAddSameProductMoreThanOnce()
    {
        basket.addProduct(product1, "matt");
        basket.update();

        basket.addProduct(product2, "matt");
        basket.update();

        basket.addProduct(product1, "matt");
        basket.update();

        // two different products
        Assert.assertEquals(2, basket.getProducts().size());

        // get all products of the basket
        List<Basket_Product> basketProducts = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();

        // product one is in the basket
        Assert.assertEquals(product1.getName(), basketProducts.get(0).getProduct().getName());
        // two of product one are in the basket
        Assert.assertEquals(2, basketProducts.get(0).getCountProduct());
        // product two is in the basket
        Assert.assertEquals(product2.getName(), basketProducts.get(1).getProduct().getName());
        // one of product two is in the basket
        Assert.assertEquals(1, basketProducts.get(1).getCountProduct());
        // total price is sum of all three product prices
        Assert.assertEquals(18.87, basket.getTotalPrice(), 0.01);
    }

    @Test
    public void testAddCustomerToBasket()
    {
        Customer customer = new Customer();
        customer.setName("customer");
        customer.save();

        basket.setCustomer(customer);
        basket.update();

        // get customer by basket
        Assert.assertEquals(customer, basket.getCustomer());
    }
    
    @Test
    public void testClearProducts()
    {
        basket.addProduct(product1, "matt");
        basket.update();

        basket.addProduct(product2, "matt");
        basket.update();

        basket.addProduct(product1, "matt");
        basket.update();

        // two different products
        Assert.assertEquals(2, basket.getProducts().size());

        // get all products of the basket
        List<Basket_Product> basketProducts = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();

        // product one is in the basket
        Assert.assertEquals(product1.getName(), basketProducts.get(0).getProduct().getName());
        // two of product one are in the basket
        Assert.assertEquals(2, basketProducts.get(0).getCountProduct());
        // product two is in the basket
        Assert.assertEquals(product2.getName(), basketProducts.get(1).getProduct().getName());
        // one of product two is in the basket
        Assert.assertEquals(1, basketProducts.get(1).getCountProduct());
        
        basket.clearProducts();
        // get all products of the basket
        List<Basket_Product> basketProducts2 = Ebean.find(Basket_Product.class).where().eq("basket", basket).findList();
        Assert.assertEquals(0, basketProducts2.size());
    }
}
