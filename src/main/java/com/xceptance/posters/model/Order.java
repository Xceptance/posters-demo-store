package com.xceptance.posters.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * A completed customer order with products, addresses, payment, and pricing.
 */
@Entity
@Table(name = "customer_order")
public class Order
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Customer customer;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private ShippingAddress shippingAddress;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private BillingAddress billingAddress;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private CreditCard creditCard;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> products = new ArrayList<>();

    private double subTotalCosts;
    private double totalTaxCosts;
    private double shippingCosts;
    private double totalCosts;
    private double tax;
    private String orderDate;

    public Order()
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

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public ShippingAddress getShippingAddress()
    {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress)
    {
        this.shippingAddress = shippingAddress;
    }

    public BillingAddress getBillingAddress()
    {
        return billingAddress;
    }

    public void setBillingAddress(BillingAddress billingAddress)
    {
        this.billingAddress = billingAddress;
    }

    public CreditCard getCreditCard()
    {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard)
    {
        this.creditCard = creditCard;
    }

    public List<OrderProduct> getProducts()
    {
        return products;
    }

    public void setProducts(List<OrderProduct> products)
    {
        this.products = products;
    }

    public double getSubTotalCosts()
    {
        return subTotalCosts;
    }

    public void setSubTotalCosts(double subTotalCosts)
    {
        this.subTotalCosts = subTotalCosts;
    }

    public double getTotalTaxCosts()
    {
        return totalTaxCosts;
    }

    public void setTotalTaxCosts(double totalTaxCosts)
    {
        this.totalTaxCosts = totalTaxCosts;
    }

    public double getShippingCosts()
    {
        return shippingCosts;
    }

    public void setShippingCosts(double shippingCosts)
    {
        this.shippingCosts = shippingCosts;
    }

    public double getTotalCosts()
    {
        return totalCosts;
    }

    public void setTotalCosts(double totalCosts)
    {
        this.totalCosts = totalCosts;
    }

    public double getTax()
    {
        return tax;
    }

    public void setTax(double tax)
    {
        this.tax = tax;
    }

    public String getOrderDate()
    {
        return orderDate;
    }

    public void setOrderDate(String orderDate)
    {
        this.orderDate = orderDate;
    }

    // --- Formatted Strings ---

    private static String formatPrice(double value)
    {
        final DecimalFormat f = new DecimalFormat("#0.00");
        return f.format(Math.round(value * 100.0) / 100.0).replace(',', '.');
    }

    public String getSubTotalCostsAsString()
    {
        return formatPrice(subTotalCosts);
    }

    public String getTotalTaxCostsAsString()
    {
        return formatPrice(totalTaxCosts);
    }

    public String getShippingCostsAsString()
    {
        return formatPrice(shippingCosts);
    }

    public String getTotalCostsAsString()
    {
        return formatPrice(totalCosts);
    }
}
