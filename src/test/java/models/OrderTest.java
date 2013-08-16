package models;

import java.util.List;

import ninja.NinjaTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.avaje.ebean.Ebean;

public class OrderTest extends NinjaTest
{

    Product product1 = new Product();

    Product product2 = new Product();

    Basket basket = new Basket();

    @Before
    public void setUp() throws Exception
    {
        product1.setName("product1");
        product1.setPrice(5.55);
        product1.save();
        product2.setName("product2");
        product2.setPrice(7.77);
        product2.save();
        basket.save();

        basket.addProduct(product1);
        basket.update();

        basket.addProduct(product2);
        basket.update();

        basket.addProduct(product1);
        basket.update();
    }

    @Test
    public void testAddProductsFromBasket()
    {
        Order order = new Order();
        order.save();

        order.addProductsFromBasket(basket);
        order.update();

        List<Order_Product> orderProducts = Ebean.find(Order_Product.class).where().eq("order", order).findList();

        // product one is in the basket
        Assert.assertEquals(product1.getName(), orderProducts.get(0).getProduct().getName());
        // two of product one are in the basket
        Assert.assertEquals(2, orderProducts.get(0).getCountProduct());
        // product two is in the basket
        Assert.assertEquals(product2.getName(), orderProducts.get(1).getProduct().getName());
        // one of product two is in the basket
        Assert.assertEquals(1, orderProducts.get(1).getCountProduct());
        // total price is sum of all three product prices
        Assert.assertEquals(18.87, order.getTotalCosts(), 0.01);
    }

    @Test
    public void testAddShippingCosts()
    {
        Order order = new Order();
        order.save();

        order.addProductsFromBasket(basket);
        order.update();

        // total price is sum of all three product prices
        Assert.assertEquals(18.87, order.getTotalCosts(), 0.01);

        order.setShippingCosts(4.99);
        order.addShippingCostsToTotalCosts();
        order.update();

        // total price is sum of all three product prices and shipping costs
        Assert.assertEquals(23.86, order.getTotalCosts(), 0.01);
    }

    @Test
    public void testAddTaxToTotalCosts()
    {
        Order order = new Order();
        order.save();

        order.addProductsFromBasket(basket);
        order.update();

        // total price is sum of all three product prices
        Assert.assertEquals(18.87, order.getTotalCosts(), 0.01);

        order.setShippingCosts(4.99);
        order.addShippingCostsToTotalCosts();
        order.setTax(0.10);
        order.addTaxToTotalCosts();
        order.update();

        // total price is sum of all three product prices and shipping costs and tax
        double totalCosts = 23.86 * 0.10 + 23.86;
        Assert.assertEquals(totalCosts, order.getTotalCosts(), 0.01);
    }

}
