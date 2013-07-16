package util.database;

import java.util.List;

import models.CreditCard;
import models.Customer;

import com.avaje.ebean.Ebean;

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

    public static List<CreditCard> getAllCreditCardsOfCustomer(Customer customer)
    {
        return Ebean.find(CreditCard.class).where().eq("customer", customer).findList();
    }
}
