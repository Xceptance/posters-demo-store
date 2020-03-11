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

/**
 * This {@link Entity} provides a credit card. A credit card can be set to at most one {@link Customer}.
 * 
 * @author sebastianloob
 */
@Entity
@Table(name = "creditCard")
public class CreditCard
{

    /**
     * The ID of the entity.
     */
    @Id
    private int id;

    /**
     * The credit card number.
     */
    private String cardNumber;

    /**
     * The name field of the credit card.
     */
    private String name;

    /**
     * The month of the expiration date.
     */
    private int month;

    /**
     * The year of the expiration date.
     */
    private int year;

    /**
     * The {@link Customer} of the credit card. Can be {@code null}, if its a credit card of an unregistered customer.
     */
    @ManyToOne
    private Customer customer;

    /**
     * A list of {@link Order}s, which have been paid with this credit card.
     */
    @OneToMany
    private List<Order> order;

    /**
     * The constructor.
     */
    public CreditCard()
    {
        order = new ArrayList<Order>();
    }

    /**
     * Returns the ID of the entity.
     * 
     * @return the ID
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the ID of the entity.
     * 
     * @param id
     *            the ID
     */
    public void setId(final int id)
    {
        this.id = id;
    }

    /**
     * Returns only the last four digits of the credit card number. All other digits will be replaced with an 'x'. The
     * result will be like 'xxxx xxxx xxxx 1234'.
     * 
     * @return the last four digits of the credit card number, all other digits as an 'x'
     */
    public String getCardNumberCryptic()
    {
        final StringBuilder cardNumber = new StringBuilder();
        if (this.cardNumber != null)
        {
            // replace the digit with an 'x'
            for (int i = 1; i <= (this.cardNumber.length() - 4); i++)
            {
                // to get a well formatted number, add every fourth digits a space character
                if (i % 4 == 0)
                {
                    cardNumber.append("x ");
                }
                else
                {
                    cardNumber.append("x");
                }
            }
            // add the last four digits as 'plain' numbers
            cardNumber.append(this.cardNumber.substring(this.cardNumber.length() - 4));
        }
        return cardNumber.toString();
    }

    /**
     * Returns the credit card number.
     * 
     * @return the credit card number
     */
    public String getCardNumber()
    {
        return cardNumber;
    }

    /**
     * Sets the credit card number.
     * 
     * @param cardNumber
     *            the credit card number
     */
    public void setCardNumber(final String cardNumber)
    {
        this.cardNumber = cardNumber;
    }

    /**
     * Returns the name field of the credit card.
     * 
     * @return the name field
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name field of the credit card.
     * 
     * @param name
     *            the name field
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * Returns the month of the expiration date.
     * 
     * @return the month of the expiration date
     */
    public int getMonth()
    {
        return month;
    }

    /**
     * Returns the month of the expiration date with a leading zero like '01', '02', ... , '11', '12'.
     * 
     * @return the month with leading zero
     */
    public String getMonthLeadingZero()
    {
        final DecimalFormat df = new DecimalFormat("00");
        return df.format(getMonth());
    }

    /**
     * Sets the month of the expiration date.
     * 
     * @param month
     *            the month of the expiration date
     */
    public void setMonth(final int month)
    {
        this.month = month;
    }

    /**
     * Returns the year of the expiration date.
     * 
     * @return the year of the expiration date
     */
    public int getYear()
    {
        return year;
    }

    /**
     * Sets the year of the expiration date
     * 
     * @param year
     *            the year of the expiration date
     */
    public void setYear(final int year)
    {
        this.year = year;
    }

    /**
     * Returns the {@link Customer} with this credit card.
     * 
     * @return the {@link Customer} with this credit card
     */
    public Customer getCustomer()
    {
        return customer;
    }

    /**
     * Sets the {@link Customer} to this credit card
     * 
     * @param customer
     *            the {@link Customer} with this credit card
     */
    public void setCustomer(final Customer customer)
    {
        this.customer = customer;
    }

    /**
     * Returns a list of {@link Order}s, which have been paid with this credit card.
     * 
     * @return a list of {@link Order}s
     */
    public List<Order> getOrder()
    {
        return order;
    }

    /**
     * Sets the list of {@link Order}s, which have been paid with this credit card.
     * 
     * @param orders
     *            the list of {@link Order}s
     */
    public void setOrder(final List<Order> orders)
    {
        order = orders;
    }

    /**
     * Updates the entity in the database.
     */
    public void update()
    {
        Ebean.update(this);
    }

    /**
     * Saves the entity in the database.
     */
    public void save()
    {
        Ebean.save(this);
    }

    /**
     * Deletes the entity from the database.
     */
    public void delete()
    {
        Ebean.delete(this);
    }

    /**
     * Returns the {@link CreditCard} with the given id. Returns {@code null}, if no credit card was found.
     * 
     * @param id
     *            the ID of the credit card
     * @return the {@link CreditCard} that matches the unique id
     */
    public static CreditCard getCreditCardById(final int id)
    {
        return Ebean.find(CreditCard.class, id);
    }

    /**
     * Sets the {@link Customer} of the {@link CreditCard} to {@code null}.
     * 
     * @param id
     *            the ID of the credit card
     */
    public static void removeCustomerFromCreditCard(final int id)
    {
        final CreditCard card = getCreditCardById(id);
        card.setCustomer(null);
        card.update();
    }
}
