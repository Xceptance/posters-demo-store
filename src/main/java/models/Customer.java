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

import util.session.SessionHandling;

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
    private List<ShippingAddress> shippingAddress;

    @OneToMany(cascade = CascadeType.ALL)
    private List<BillingAddress> billingAddress;

    @OneToMany(cascade = CascadeType.ALL)
    private List<CreditCard> creditCard;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Order> order;

    @OneToOne(cascade = CascadeType.ALL)
    private Cart cart;

    public Customer()
    {
        this.shippingAddress = new ArrayList<ShippingAddress>();
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

    public List<ShippingAddress> getShippingAddress()
    {
        return shippingAddress;
    }

    public void setShippingAddress(List<ShippingAddress> shippingAddress)
    {
        this.shippingAddress = shippingAddress;
    }

    public void addShippingAddress(ShippingAddress shippingAddress)
    {
        this.shippingAddress.add(shippingAddress);
        this.update();
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
        this.update();
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

    public void addCreditCard(CreditCard card)
    {
        this.creditCard.add(card);
        this.update();
    }

    public List<Order> getOrder()
    {
        return order;
    }

    public void setOrder(List<Order> order)
    {
        this.order = order;
    }

    public Cart getCart()
    {
        return cart;
    }

    public void setCart(Cart cart)
    {
        this.cart = cart;
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

    public void delete()
    {
        Ebean.delete(this);
    }

    /**
     * Returns true, if there is a customer with the given email address, otherwise false.
     * 
     * @param email
     * @return
     */
    public static boolean emailExist(String email)
    {
        boolean exist = true;
        // get a list of customers, which have the given email address
        List<Customer> loginExist = Ebean.find(Customer.class).where().eq("email", email).findList();
        // no customer has this email address
        if (loginExist.size() == 0)
        {
            exist = false;
        }
        // more than one customer has this email address
        else if (loginExist.size() > 1)
        {
            // FAILURE
        }
        return exist;
    }

    /**
     * Returns the customer by the customer's email address.
     * 
     * @param email
     * @return
     */
    public static Customer getCustomerByEmail(String email)
    {
        // get customer by email address
        return Ebean.find(Customer.class).where().eq("email", email).findUnique();
    }

    /**
     * Returns the customer by the customer's id.
     * 
     * @param customerId
     * @return
     */
    public static Customer getCustomerById(int customerId)
    {
        // get customer by id
        return Ebean.find(Customer.class, customerId);
    }

    public List<Order> getAllOrders()
    {
        return Ebean.find(Order.class).where().eq("customer", this).orderBy("lastUpdate  desc").findList();
    }
}
