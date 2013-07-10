package util.database;

import com.avaje.ebean.Ebean;

import models.BillingAddress;
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
}
