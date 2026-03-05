package com.xceptance.posters.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.mindrot.jbcrypt.BCrypt;

/**
 * A customer of the poster demo store with email, password, addresses, credit cards, and orders.
 */
@Entity
@Table(name = "customer")
public class Customer
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String email;

    private String password;
    private String name;
    private String firstName;

    @OneToOne(cascade = CascadeType.ALL)
    private Cart cart;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ShippingAddress> shippingAddresses = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<BillingAddress> billingAddresses = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<CreditCard> creditCards = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<Order> orders = new ArrayList<>();

    public Customer()
    {
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
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

    public Cart getCart()
    {
        return cart;
    }

    public void setCart(Cart cart)
    {
        this.cart = cart;
    }

    public List<ShippingAddress> getShippingAddresses()
    {
        return shippingAddresses;
    }

    public void setShippingAddresses(List<ShippingAddress> shippingAddresses)
    {
        this.shippingAddresses = shippingAddresses;
    }

    public void addShippingAddress(ShippingAddress address)
    {
        this.shippingAddresses.add(address);
    }

    public List<BillingAddress> getBillingAddresses()
    {
        return billingAddresses;
    }

    public void setBillingAddresses(List<BillingAddress> billingAddresses)
    {
        this.billingAddresses = billingAddresses;
    }

    public void addBillingAddress(BillingAddress address)
    {
        this.billingAddresses.add(address);
    }

    public List<CreditCard> getCreditCards()
    {
        return creditCards;
    }

    public void setCreditCards(List<CreditCard> creditCards)
    {
        this.creditCards = creditCards;
    }

    public void addCreditCard(CreditCard card)
    {
        this.creditCards.add(card);
    }

    public List<Order> getOrders()
    {
        return orders;
    }

    public void setOrders(List<Order> orders)
    {
        this.orders = orders;
    }

    /**
     * Hashes the given plain-text password and sets it.
     */
    public void hashPassword(String plainPassword)
    {
        setPassword(BCrypt.hashpw(plainPassword, BCrypt.gensalt()));
    }

    /**
     * Checks whether the given plain-text password matches the stored hash.
     */
    public boolean checkPassword(String plainPassword)
    {
        return BCrypt.checkpw(plainPassword, this.password);
    }
}
