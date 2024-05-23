/*
 * Copyright (c) 2013-2024 Xceptance Software Technologies GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package models;

import java.util.List;

import ninja.NinjaTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.ebean.Ebean;

public class OrderTest extends NinjaTest
{

    Product product1 = new Product();

    Product product2 = new Product();

    Cart cart = new Cart();

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
        cart.save();

        size = new PosterSize();
        size.setWidth(16);
        size.setHeight(12);
        size.save();

        final ProductPosterSize productPosterSize = new ProductPosterSize();
        productPosterSize.setProduct(product1);
        productPosterSize.setSize(size);
        productPosterSize.setPrice(5.55);
        productPosterSize.save();

        final ProductPosterSize productPosterSize2 = new ProductPosterSize();
        productPosterSize2.setProduct(product2);
        productPosterSize2.setSize(size);
        productPosterSize2.setPrice(7.77);
        productPosterSize2.save();

        cart.addProduct(product1, "matte", size);
        cart.update();

        cart.addProduct(product2, "matte", size);
        cart.update();

        cart.addProduct(product1, "matte", size);
        cart.update();
    }

    @Test
    public void testAddProductsFromCart()
    {
        // create new order
        final Order order = new Order();
        order.save();
        // add all products from the cart to the order
        order.addProductsFromCart(cart);
        order.update();
        // get all products from the order
        final List<OrderProduct> orderProducts = Ebean.find(OrderProduct.class).where().eq("order", order).findList();
        // verify, that product one is in the order...
        Assert.assertEquals(product1.getName(), orderProducts.get(0).getProduct().getName());
        // ...with an amount of two
        Assert.assertEquals(2, orderProducts.get(0).getProductCount());
        // verify, that product two is in the order...
        Assert.assertEquals(product2.getName(), orderProducts.get(1).getProduct().getName());
        // ...with an amount of one
        Assert.assertEquals(1, orderProducts.get(1).getProductCount());
        // verify, that total price is sum of all three product prices
        Assert.assertEquals(18.87, order.getSubTotalCosts(), 0.01);
    }

    @Test
    public void testAddShippingCosts()
    {
        final Order order = new Order();
        order.save();
        
        order.setShippingCosts(4.99);
 //       order.addShippingCostsToTotalCosts();
        order.update();
        
        order.addProductsFromCart(cart);
        order.update();
        

        
        Order updatedOrder = Ebean.find(Order.class, order.getId());
        // total price is sum of all three product prices
        Assert.assertEquals(18.87, updatedOrder.getSubTotalCosts(), 0.01);



        updatedOrder = Ebean.find(Order.class, order.getId());
        // total price is sum of all three product prices and shipping costs
        Assert.assertEquals(23.86, updatedOrder.getTotalCosts(), 0.01);
    }

    @Test
    public void testAddTaxToTotalCosts()
    {
        final Order order = new Order();
        order.save();

        order.setShippingCosts(4.99);
        order.setTax(0.10);
        order.update();
        
        order.addProductsFromCart(cart);
        order.update();

        Order updatedOrder = Ebean.find(Order.class, order.getId());

        // total price is sum of all three product prices
        Assert.assertEquals(18.87, updatedOrder.getSubTotalCosts(), 0.01);


        updatedOrder = Ebean.find(Order.class, order.getId());
        // total price is sum of all three product prices and shipping costs and tax
        final double totalCosts = 23.86 * 1.10;
        Assert.assertEquals(totalCosts, updatedOrder.getTotalCosts(), 0.01);
    }

}
