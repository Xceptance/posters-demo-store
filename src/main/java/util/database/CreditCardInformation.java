package util.database;

import com.avaje.ebean.Ebean;

import models.CreditCard;
import models.DeliveryAddress;

public abstract class CreditCardInformation
{

    /**
     * Returns the credit card by the given id.
     * 
     * @param id
     * @return
     */
    public static CreditCard getCreditCardById(int id)
    {
        // get credit card by id
        CreditCard creditCard = Ebean.find(CreditCard.class, id);
        return creditCard;
    }

    public static void deleteCreditCardFromCustomer(int id)
    {
        CreditCard card = getCreditCardById(id);
        card.setCustomer(null);
        card.update();
    }
}
