package util.database;

import java.util.List;
import java.util.Map;

import com.avaje.ebean.Ebean;

import models.BillingAddress;
import models.Customer;
import models.ShippingAddress;

public abstract class AddressInformation
{

    public static ShippingAddress getDeliveryAddressById(int id)
    {
        return Ebean.find(ShippingAddress.class, id);
    }

    public static BillingAddress getBillingAddressById(int id)
    {
        return Ebean.find(BillingAddress.class, id);
    }

    public static void deleteDeliveryAddressFromCustomer(int id)
    {
        ShippingAddress address = getDeliveryAddressById(id);
        address.setCustomer(null);
        address.update();
    }

    public static void deleteBillingAddressFromCustomer(int id)
    {
        BillingAddress address = getBillingAddressById(id);
        address.setCustomer(null);
        address.update();
    }

    public static void addDeliveryAddressToMap(ShippingAddress deliveryAddress, Map<String, Object> data)
    {
        data.put("deliveryAddress", deliveryAddress);
    }

    public static void addBillingAddressToMap(BillingAddress billingAddress, Map<String, Object> data)
    {
        data.put("billingAddress", billingAddress);
    }

    public static List<ShippingAddress> getAllDeliveryAddressesOfCustomer(Customer customer)
    {
        return Ebean.find(ShippingAddress.class).where().eq("customer", customer).findList();
    }

    public static List<BillingAddress> getAllBillingAddressesOfCustomer(Customer customer)
    {
        return Ebean.find(BillingAddress.class).where().eq("customer", customer).findList();
    }
}
