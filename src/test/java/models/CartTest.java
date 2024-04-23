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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.avaje.ebean.Ebean;

public class CartTest extends NinjaTest
{
    Product product1 = new Product();

    Product product2 = new Product();

    Cart cart = new Cart();

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
        // persist cart
        cart.save();
        // set some data for product size
        size.setWidth(16);
        size.setHeight(12);
        size.save();
        // set product size of product1
        final ProductPosterSize productPosterSize = new ProductPosterSize();
        productPosterSize.setProduct(product1);
        productPosterSize.setSize(size);
        productPosterSize.setPrice(product1.getMinimumPrice());
        productPosterSize.save();
        // set product size of product2
        final ProductPosterSize productPosterSize2 = new ProductPosterSize();
        productPosterSize2.setProduct(product2);
        productPosterSize2.setSize(size);
        productPosterSize2.setPrice(product2.getMinimumPrice());
        productPosterSize2.save();
    }

    @After
    public void tearDown()
    {
        // remove products from cart
        cart.clearProducts();
    }

    @Test
    public void testCreateCart()
    {
        // create new cart
        final Cart cart = new Cart();
        // persist cart
        cart.save();
        // verify, that cart is persistent
        Assert.assertNotNull(Ebean.find(Cart.class, cart.getId()));
        // set some data
        cart.setSubTotalPrice(3.33);
        // persist again
        cart.update();
        // verify, that cart is persistent
        Assert.assertEquals(3.33, Ebean.find(Cart.class, cart.getId()).getSubTotalPrice(), 0.01);
    }

    @Test
    public void testAddProductToCart()
    {
        // add product to cart
        addProductToCart(product1, "matte", size);
        // get all products of the cart
        final List<CartProduct> cartProducts = Ebean.find(CartProduct.class).where().eq("cart", cart).findList();
        // verify, that product one is in the cart...
        Assert.assertEquals(product1.getName(), cartProducts.get(0).getProduct().getName());
        // ...with an amount of one
        Assert.assertEquals(1, cartProducts.get(0).getProductCount());
        // verify the finish
        Assert.assertEquals("matte", cartProducts.get(0).getFinish());
        // verify the size
        Assert.assertEquals(size.getId(), cartProducts.get(0).getSize().getId());
    }

    @Test
    public void testDeleteSingleProductFromCart()
    {
        // add product to cart
        addProductToCart(product1, "matte", size);
        // get all products of the cart
        final List<CartProduct> cartProducts = Ebean.find(CartProduct.class).where().eq("cart", cart).findList();
        // verify, that product one is in the cart...
        Assert.assertEquals(product1.getName(), cartProducts.get(0).getProduct().getName());
        // ...with an amount of one
        Assert.assertEquals(1, cartProducts.get(0).getProductCount());
        // delete product one from cart
        cart.removeProduct(cartProducts.get(0));
        // persist
        cart.update();
        // get all products of the cart
        final List<CartProduct> cartProducts2 = Ebean.find(CartProduct.class).where().eq("cart", cart).findList();
        // verify, that no product is in the cart
        Assert.assertTrue(cartProducts2.size() == 0);
    }

    @Test
    public void testDeleteProductsFromCart()
    {
        // add products to cart
        addProductToCart(product1, "matte", size);
        addProductToCart(product1, "matte", size);
        addProductToCart(product2, "matte", size);
        // get all products of the cart
        final List<CartProduct> cartProducts = Ebean.find(CartProduct.class).where().eq("cart", cart).findList();
        // verify, that product one is in the cart...
        Assert.assertEquals(product1.getName(), cartProducts.get(0).getProduct().getName());
        // ...with an amount of two
        Assert.assertEquals(2, cartProducts.get(0).getProductCount());
        // verify, that product two is in the cart...
        Assert.assertEquals(product2.getName(), cartProducts.get(1).getProduct().getName());
        // ...with an amount of one
        Assert.assertEquals(1, cartProducts.get(1).getProductCount());

        // delete product one from cart
        cart.removeProduct(cartProducts.get(0));
        cart.update();

        // get all products of the cart
        final List<CartProduct> cartProducts2 = Ebean.find(CartProduct.class).where().eq("cart", cart).findList();
        // verify, that product one is in the cart...
        Assert.assertEquals(product1.getName(), cartProducts2.get(0).getProduct().getName());
        // ...with an amount of one
        Assert.assertEquals(1, cartProducts2.get(0).getProductCount());
        // verify, that product two is in the cart...
        Assert.assertEquals(product2.getName(), cartProducts2.get(1).getProduct().getName());
        // ...with an amount of one
        Assert.assertEquals(1, cartProducts2.get(1).getProductCount());
        // verify, that total price is sum of both product prices
        Assert.assertEquals(13.32, cart.getSubTotalPrice(), 0.01);

        // delete product two from cart
        cart.removeProduct(cartProducts2.get(1));
        cart.update();

        // get all products of the cart
        final List<CartProduct> cartProducts3 = Ebean.find(CartProduct.class).where().eq("cart", cart).findList();
        // verify, that product one is in the cart...
        Assert.assertEquals(product1.getName(), cartProducts3.get(0).getProduct().getName());
        // ...with an amount of one
        Assert.assertEquals(1, cartProducts3.get(0).getProductCount());
        // verify, that product two is not in the cart
        Assert.assertTrue(cartProducts3.size() == 1);
        // verify, that total price is price of product one
        Assert.assertEquals(5.55, cart.getSubTotalPrice(), 0.01);

        // delete product one from cart
        cart.removeProduct(cartProducts3.get(0));
        cart.update();

        // get all products of the cart
        final List<CartProduct> cartProducts4 = Ebean.find(CartProduct.class).where().eq("cart", cart).findList();
        // verify, that no product is in the cart
        Assert.assertTrue(cartProducts4.size() == 0);
        // verify, that total price is zero
        Assert.assertEquals(0.0, cart.getSubTotalPrice(), 0.01);
    }

    @Test
    public void testAddSameProductMoreThanOnce()
    {
        // add products to cart
        addProductToCart(product1, "matte", size);
        addProductToCart(product1, "matte", size);

        // verify, that just one product is in the cart
        Assert.assertEquals(1, cart.getProducts().size());

        // get all products of the cart
        final List<CartProduct> cartProducts = Ebean.find(CartProduct.class).where().eq("cart", cart).findList();

        // verify, that product one is in the cart...
        Assert.assertEquals(product1.getName(), cartProducts.get(0).getProduct().getName());
        // ...with an amount of two
        Assert.assertEquals(2, cartProducts.get(0).getProductCount());
        // verify, that total price is sum of both product prices
        Assert.assertEquals(11.10, cart.getSubTotalPrice(), 0.01);
    }

    @Test
    public void testAddSameProductDifferentFinish()
    {
        // add products to cart
        addProductToCart(product1, "matte", size);
        addProductToCart(product1, "gloss", size);

        // verify, that two different products are in the cart
        Assert.assertEquals(2, cart.getProducts().size());
    }

    @Test
    public void testAddSameProductDifferentSize()
    {
        final PosterSize otherSize = new PosterSize();
        otherSize.setWidth(16);
        otherSize.setHeight(12);
        otherSize.save();
        // add product size to product1
        final ProductPosterSize productPosterSize = new ProductPosterSize();
        productPosterSize.setProduct(product1);
        productPosterSize.setSize(otherSize);
        productPosterSize.setPrice(product1.getMinimumPrice());
        productPosterSize.save();

        // add products to cart
        addProductToCart(product1, "matte", size);
        addProductToCart(product1, "matte", otherSize);

        // verify, that two different products are in the cart
        Assert.assertEquals(2, cart.getProducts().size());
    }

    @Test
    public void testAddCustomerToCart()
    {
        // create new customer
        final Customer customer = new Customer();
        customer.setName("customer");
        customer.save();
        // set customer to cart
        cart.setCustomer(customer);
        cart.update();

        // verify, that customer was set to cart
        Assert.assertEquals(customer, cart.getCustomer());
    }

    @Test
    public void testClearProducts()
    {
        // add products to cart
        addProductToCart(product1, "matte", size);
        addProductToCart(product1, "matte", size);
        addProductToCart(product2, "matte", size);

        // verify, that two different products are in the cart
        Assert.assertEquals(2, cart.getProducts().size());

        // remove all products from the cart
        cart.clearProducts();
        // get all products of the cart
        final List<CartProduct> cartProducts2 = Ebean.find(CartProduct.class).where().eq("cart", cart).findList();
        // verify, that no product is in the cart
        Assert.assertEquals(0, cartProducts2.size());
        // verify, that total price is zero
        Assert.assertEquals(0.0, cart.getSubTotalPrice(), 0.01);
    }

    /**
     * Adds the given product with the given finish and size to the cart.
     * 
     * @param product
     * @param finish
     * @param size
     */
    private void addProductToCart(final Product product, final String finish, final PosterSize size)
    {
        cart.addProduct(product, finish, size);
        cart.update();
    }
}
