package models;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;

@Entity
@Table(name = "creditCard")
public class CreditCard
{

    @Id
    private int id;

    private String cardNumber;

    private String name;

    private int month;

    private int year;

    @ManyToOne
    private Customer customer;

    @OneToMany
    private List<Order> order;

    public CreditCard()
    {
        order = new ArrayList<Order>();
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getCardNumberCryptic()
    {
        String number = "";
        if (cardNumber != null)
        {
            for (int i = 1; i <= (cardNumber.length() - 4); i++)
            {
                if (i % 4 == 0)
                {
                    number += "x ";
                }
                else
                {
                    number += "x";
                }
            }
            number += cardNumber.substring(cardNumber.length() - 4);
        }
        return number;
    }

    public String getCardNumber()
    {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber)
    {
        this.cardNumber = cardNumber;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getMonth()
    {
        return month;
    }

    public String getMonthLeadingZero()
    {
        DecimalFormat df = new DecimalFormat("00");
        return df.format(getMonth());
    }

    public void setMonth(int month)
    {
        this.month = month;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public List<Order> getOrder()
    {
        return order;
    }

    public void setOrder(List<Order> order)
    {
        this.order = order;
    }

    public void update()
    {
        Ebean.update(this);
    }

    public void save()
    {
        Ebean.save(this);
    }

    public void delete()
    {
        Ebean.delete(this);
    }

    /**
     * Returns the credit card by the given id.
     * 
     * @param id
     * @return
     */
    public static CreditCard getCreditCardById(int id)
    {
        // get credit card by id
        return Ebean.find(CreditCard.class, id);
    }

    public static void removeCreditCardFromCustomer(int id)
    {
        CreditCard card = getCreditCardById(id);
        card.setCustomer(null);
        card.update();
    }
}
