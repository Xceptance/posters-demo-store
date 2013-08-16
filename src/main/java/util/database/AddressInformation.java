package util.database;

import java.util.List;
import java.util.Map;

import com.avaje.ebean.Ebean;

import models.BillingAddress;
import models.Customer;
import models.DeliveryAddress;

public abstract class AddressInformation
{

    public static DeliveryAddress getDeliveryAddressById(int id)
    {
        return Ebean.find(DeliveryAddress.class, id);
    }

    public static BillingAddress getBillingAddressById(int id)
    {
        return Ebean.find(BillingAddress.class, id);
    }

    public static void deleteDeliveryAddressFromCustomer(int id)
    {
        DeliveryAddress address = getDeliveryAddressById(id);
        address.setCustomer(null);
        address.update();
    }

    public static void deleteBillingAddressFromCustomer(int id)
    {
        BillingAddress address = getBillingAddressById(id);
        address.setCustomer(null);
        address.update();
    }

    public static void addDeliveryAddressToMap(DeliveryAddress deliveryAddress, Map<String, Object> data)
    {
        data.put("deliveryAddress", deliveryAddress);
    }

    public static void addBillingAddressToMap(BillingAddress billingAddress, Map<String, Object> data)
    {
        data.put("billingAddress", billingAddress);
    }

    public static List<DeliveryAddress> getAllDeliveryAddressesOfCustomer(Customer customer)
    {
        return Ebean.find(DeliveryAddress.class).where().eq("customer", customer).findList();
    }

    public static List<BillingAddress> getAllBillingAddressesOfCustomer(Customer customer)
    {
        return Ebean.find(BillingAddress.class).where().eq("customer", customer).findList();
    }
}
