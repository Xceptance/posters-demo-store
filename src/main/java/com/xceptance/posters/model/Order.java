package com.xceptance.posters.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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

    @Column(precision = 10, scale = 2)
    private BigDecimal subTotalCosts = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalTaxCosts = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal shippingCosts = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalCosts = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;

    private LocalDateTime orderDate;

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

    public BigDecimal getSubTotalCosts()
    {
        return subTotalCosts;
    }

    public void setSubTotalCosts(BigDecimal subTotalCosts)
    {
        this.subTotalCosts = subTotalCosts;
    }

    public BigDecimal getTotalTaxCosts()
    {
        return totalTaxCosts;
    }

    public void setTotalTaxCosts(BigDecimal totalTaxCosts)
    {
        this.totalTaxCosts = totalTaxCosts;
    }

    public BigDecimal getShippingCosts()
    {
        return shippingCosts;
    }

    public void setShippingCosts(BigDecimal shippingCosts)
    {
        this.shippingCosts = shippingCosts;
    }

    public BigDecimal getTotalCosts()
    {
        return totalCosts;
    }

    public void setTotalCosts(BigDecimal totalCosts)
    {
        this.totalCosts = totalCosts;
    }

    public BigDecimal getTax()
    {
        return tax;
    }

    public void setTax(BigDecimal tax)
    {
        this.tax = tax;
    }

    public LocalDateTime getOrderDate()
    {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate)
    {
        this.orderDate = orderDate;
    }

    // --- Formatted Strings ---

    public String getSubTotalCostsAsString()
    {
        return subTotalCosts.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public String getTotalTaxCostsAsString()
    {
        return totalTaxCosts.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public String getShippingCostsAsString()
    {
        return shippingCosts.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public String getTotalCostsAsString()
    {
        return totalCosts.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
