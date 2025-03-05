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

import ninja.NinjaTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.ebean.DB;

public class CustomerTest extends NinjaTest
{

    Customer customer;
    final ShippingAddress address = new ShippingAddress();

    @Before
    public void setUp() throws Exception
    {
        // create new customer
        customer = new Customer();
        // set some data
        customer.setEmail("email");
        customer.hashPasswd("password");
        // persist
        customer.save();
    }

    @Test
    public void testCreateCustomer()
    {
        final Customer customer = new Customer();
        customer.setEmail("emailUnique");
        customer.hashPasswd("password");
        customer.save();

        final Customer savedCustomer = DB.find(Customer.class, customer.getId());
        // verify, that the customer is persistent
        Assert.assertNotNull(savedCustomer);
        // verify the email of the customer
        Assert.assertEquals("emailUnique", savedCustomer.getEmail());
        // clean up
        customer.delete();
    }

    @Test
    public void testPasswordHash()
    {
        final Customer customer = DB.find(Customer.class, this.customer.getId());
        // verify, that the password is not saved as plain text
        Assert.assertNotSame("password", customer.getPassword());
        // verify the password
        Assert.assertTrue(customer.checkPasswd("password"));
    }

    @Test
    public void testAddShippingAddress()
    {
        // add shipping address to the customer
        address.setName("customer");
        customer.addShippingAddress(address);
        customer.update();
        // verify, that address is persistent
        Assert.assertNotNull(DB.find(ShippingAddress.class, address.getId()));
        // get updated customer
        final Customer newCustomer = DB.find(Customer.class, customer.getId());
        // verify, that customer has one shipping address
        Assert.assertEquals(1, newCustomer.getShippingAddress().size());
        // verify, that the address is trhe right one
        Assert.assertEquals(address.getId(), newCustomer.getShippingAddress().get(0).getId());
    }

    @After
    public void tearDown()
    {
        customer.delete();
        address.delete();
    }

}
