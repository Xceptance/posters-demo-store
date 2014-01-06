package models;

import ninja.NinjaTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.avaje.ebean.Ebean;

public class CustomerTest extends NinjaTest
{

    Customer customer;

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
        Customer customer = new Customer();
        customer.setEmail("email");
        customer.hashPasswd("password");
        customer.save();

        Customer savedCustomer = Ebean.find(Customer.class, customer.getId());
        // verify, that the customer is persistent
        Assert.assertNotNull(savedCustomer);
        // verify the email of the customer
        Assert.assertEquals("email", savedCustomer.getEmail());
    }

    @Test
    public void testPasswordHash()
    {
        Customer customer = Ebean.find(Customer.class, this.customer.getId());
        // verify, that the password is not saved as plain text
        Assert.assertNotSame("password", customer.getPassword());
        // verify the password
        Assert.assertTrue(customer.checkPasswd("password"));
    }

    @Test
    public void testAddDeliveryAddress()
    {
        // add delivery address to the customer
        DeliveryAddress address = new DeliveryAddress();
        address.setName("customer");
        customer.addDeliveryAddress(address);
        customer.update();
        // verify, that address is persistent
        Assert.assertNotNull(Ebean.find(DeliveryAddress.class, address.getId()));
        // get updated customer
        Customer newCustomer = Ebean.find(Customer.class, customer.getId());
        // verify, that customer has one delivery address
        Assert.assertEquals(1, newCustomer.getDeliveryAddress().size());
        // verify, that the address is trhe right one
        Assert.assertEquals(address.getId(), newCustomer.getDeliveryAddress().get(0).getId());
    }

}
