package models;

import ninja.NinjaTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import util.database.AddressInformation;

import com.avaje.ebean.Ebean;

public class CustomerTest extends NinjaTest
{

    Customer customer;

    @Before
    public void setUp() throws Exception
    {
        customer = new Customer();
        customer.setEmail("email");
        customer.hashPasswd("password");
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
        Assert.assertNotNull(savedCustomer);
        Assert.assertNotSame("password", savedCustomer.getPassword());
        Assert.assertTrue(savedCustomer.checkPasswd("password"));
    }

    @Test
    public void testAddAndDeleteDeliveryAddress()
    {
        DeliveryAddress address = new DeliveryAddress();
        address.setName("customer");
        customer.addDeliveryAddress(address);
        customer.update();

        Assert.assertTrue(customer.getDeliveryAddress().contains(address));

        DeliveryAddress addedAddress = AddressInformation.getDeliveryAddressById(address.getId());
        AddressInformation.deleteDeliveryAddressFromCustomer(addedAddress.getId());
        Customer newCustomer = Ebean.find(Customer.class, customer.getId());
        Assert.assertEquals(0, newCustomer.getDeliveryAddress().size());
    }

}
