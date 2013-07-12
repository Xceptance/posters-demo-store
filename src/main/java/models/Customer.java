package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.mindrot.jbcrypt.BCrypt;

import com.avaje.ebean.Ebean;

@Entity
@Table(name = "customer")
public class Customer
{

    @Id
    private int id;

    private String email;

    private String password;

    private String name;

    private String firstName;

    @OneToMany(cascade = CascadeType.ALL)
    private List<DeliveryAddress> deliveryAddress;

    @OneToMany(cascade = CascadeType.ALL)
    private List<BillingAddress> billingAddress;

    @OneToMany(cascade = CascadeType.ALL)
    private List<CreditCard> creditCard;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Order> order;

    @OneToOne(cascade = CascadeType.ALL)
    private Basket basket;

    public Customer()
    {
        this.deliveryAddress = new ArrayList<DeliveryAddress>();
        this.billingAddress = new ArrayList<BillingAddress>();
        this.creditCard = new ArrayList<CreditCard>();
        this.order = new ArrayList<Order>();
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public List<DeliveryAddress> getDeliveryAddress()
    {
        return deliveryAddress;
    }

    public void setDeliveryAddress(List<DeliveryAddress> deliveryAddress)
    {
        this.deliveryAddress = deliveryAddress;
    }

    public void addDeliveryAddress(DeliveryAddress deliveryAddress)
    {
        this.deliveryAddress.add(deliveryAddress);
    }

    public List<BillingAddress> getBillingAddress()
    {
        return billingAddress;
    }

    public void setBillingAddress(List<BillingAddress> billingAddress)
    {
        this.billingAddress = billingAddress;
    }

    public void addBillingAddress(BillingAddress billingAddress)
    {
        this.billingAddress.add(billingAddress);
    }

    public int getId()
    {
        return id;
    }

    public void setId(int customerId)
    {
        this.id = customerId;
    }

    public List<CreditCard> getCreditCard()
    {
        return creditCard;
    }

    public void setCreditCard(List<CreditCard> creditCard)
    {
        this.creditCard = creditCard;
    }

    public List<Order> getOrder()
    {
        return order;
    }

    public void setOrder(List<Order> order)
    {
        this.order = order;
    }

    public Basket getBasket()
    {
        return basket;
    }

    public void setBasket(Basket basket)
    {
        this.basket = basket;
    }

    public void addCreditCard(CreditCard creditCard)
    {
        this.creditCard.add(creditCard);
    }

    /**
     * Hashes the given password and sets to the current password.
     * 
     * @param password
     */
    public void hashPasswd(String password)
    {
        setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
    }

    /**
     * Returns true, if the given password is equal to the customer's password.
     * 
     * @param password
     * @return
     */
    public boolean checkPasswd(String password)
    {
        return BCrypt.checkpw(password, this.password);
    }

    public void update()
    {
        Ebean.update(this);
    }

    public void save()
    {
        Ebean.save(this);
    }
}
