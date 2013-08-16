package models;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.avaje.ebean.Ebean;

public class CreditCardTest
{
    CreditCard card;

    @Before
    public void setUp() throws Exception
    {
        card = new CreditCard();
        card.setCardNumber("1234567890123456");
        card.save();
    }

    @Test
    public void testGetCardNumberCryptic()
    {
        CreditCard creditCard = Ebean.find(CreditCard.class, card.getId());
        Assert.assertEquals("xxxx xxxx xxxx 3456", creditCard.getCardNumberCryptic());
    }

}
