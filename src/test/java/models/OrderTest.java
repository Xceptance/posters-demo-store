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

    PosterSize size;

    @Before
    public void setUp() throws Exception
    {
        product1.setName("product1");
        product1.setMinimumPrice(5.55);
        product1.save();
        product2.setName("product2");
        product2.setMinimumPrice(7.77);
        product2.save();
        basket.save();

        size = new PosterSize();
        size.setWidth(16);
        size.setHeight(12);
        size.save();

        Product_PosterSize productPosterSize = new Product_PosterSize();
        productPosterSize.setProduct(product1);
        productPosterSize.setSize(size);
        productPosterSize.setPrice(5.55);
        productPosterSize.save();

        Product_PosterSize productPosterSize2 = new Product_PosterSize();
        productPosterSize2.setProduct(product2);
        productPosterSize2.setSize(size);
        productPosterSize2.setPrice(7.77);
        productPosterSize2.save();

        basket.addProduct(product1, "matte", size);
        basket.update();

        basket.addProduct(product2, "matte", size);
        basket.update();

        basket.addProduct(product1, "matte", size);
        basket.update();
    }

    @Test
    public void testAddProductsFromBasket()
    {
        // create new order
        Order order = new Order();
        order.save();
        // add all products from the basket to the order
        order.addProductsFromBasket(basket);
        order.update();
        // get all products from the order
        List<Order_Product> orderProducts = Ebean.find(Order_Product.class).where().eq("order", order).findList();
        // verify, that product one is in the order...
        Assert.assertEquals(product1.getName(), orderProducts.get(0).getProduct().getName());
        // ...with an amount of two
        Assert.assertEquals(2, orderProducts.get(0).getCountProduct());
        // verify, that product two is in the order...
        Assert.assertEquals(product2.getName(), orderProducts.get(1).getProduct().getName());
        // ...with an amount of one
        Assert.assertEquals(1, orderProducts.get(1).getCountProduct());
        // verify, that total price is sum of all three product prices
        Assert.assertEquals(18.87, order.getTotalCosts(), 0.01);
    }

    @Test
    public void testAddShippingCosts()
    {
        Order order = new Order();
        order.save();

        order.addProductsFromBasket(basket);
        order.update();

        Order updatedOrder = Ebean.find(Order.class, order.getId());
        // total price is sum of all three product prices
        Assert.assertEquals(18.87, updatedOrder.getTotalCosts(), 0.01);

        order.setShippingCosts(4.99);
        order.addShippingCostsToTotalCosts();
        order.update();

        updatedOrder = Ebean.find(Order.class, order.getId());
        // total price is sum of all three product prices and shipping costs
        Assert.assertEquals(23.86, updatedOrder.getTotalCosts(), 0.01);
    }

    @Test
    public void testAddTaxToTotalCosts()
    {
        Order order = new Order();
        order.save();

        order.addProductsFromBasket(basket);
        order.update();

        Order updatedOrder = Ebean.find(Order.class, order.getId());

        // total price is sum of all three product prices
        Assert.assertEquals(18.87, updatedOrder.getTotalCosts(), 0.01);

        order.setShippingCosts(4.99);
        order.addShippingCostsToTotalCosts();
        order.setTax(0.10);
        order.addTaxToTotalCosts();
        order.update();

        updatedOrder = Ebean.find(Order.class, order.getId());
        // total price is sum of all three product prices and shipping costs and tax
        double totalCosts = 23.86 * 1.10;
        Assert.assertEquals(totalCosts, updatedOrder.getTotalCosts(), 0.01);
    }

}
