package com.xceptance.posters.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * A shopping cart containing products, pricing info, and optional checkout details (addresses, payment).
 */
@Entity
@Table(name = "cart")
public class Cart
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    private Customer customer;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private ShippingAddress shippingAddress;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private BillingAddress billingAddress;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private CreditCard creditCard;

    @Column(precision = 10, scale = 2)
    private BigDecimal shippingCosts = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal subTotalPrice = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalTaxPrice = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartProduct> products = new ArrayList<>();

    public Cart()
    {
    }

    // --- ID ---

    public UUID getId()
    {
        return id;
    }

    public void setId(UUID id)
    {
        this.id = id;
    }

    // --- Customer ---

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    // --- Addresses & Payment ---

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

    // --- Pricing ---

    public BigDecimal getShippingCosts()
    {
        return shippingCosts;
    }

    public void setShippingCosts(BigDecimal shippingCosts)
    {
        this.shippingCosts = shippingCosts;
    }

    public BigDecimal getSubTotalPrice()
    {
        return subTotalPrice;
    }

    public void setSubTotalPrice(BigDecimal subTotalPrice)
    {
        this.subTotalPrice = subTotalPrice;
    }

    public BigDecimal getTax()
    {
        return tax;
    }

    public void setTax(BigDecimal tax)
    {
        this.tax = tax;
    }

    public BigDecimal getTotalTaxPrice()
    {
        return totalTaxPrice.max(BigDecimal.ZERO);
    }

    public void setTotalTaxPrice(BigDecimal totalTaxPrice)
    {
        this.totalTaxPrice = totalTaxPrice;
    }

    public BigDecimal getTotalPrice()
    {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice)
    {
        this.totalPrice = totalPrice;
    }

    // --- Products ---

    public List<CartProduct> getProducts()
    {
        return products;
    }

    public void setProducts(List<CartProduct> products)
    {
        this.products = products;
    }

    // --- Price Calculations ---

    public void calculateTotalTaxPrice()
    {
        setTotalTaxPrice(
            getTax().multiply(getSubTotalPrice().add(getShippingCosts()))
                    .setScale(2, RoundingMode.HALF_UP)
        );
    }

    public void calculateTotalPrice()
    {
        setTotalPrice(
            getSubTotalPrice().add(getTotalTaxPrice()).add(getShippingCosts())
                              .setScale(2, RoundingMode.HALF_UP)
        );
    }

    // --- Formatted Strings ---

    public String getShippingCostsAsString()
    {
        return shippingCosts.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public String getSubTotalPriceAsString()
    {
        return subTotalPrice.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public String getTotalTaxPriceAsString()
    {
        return totalTaxPrice.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public String getTotalPriceAsString()
    {
        return totalPrice.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public String getTaxAsString()
    {
        return tax.multiply(BigDecimal.valueOf(100)).toPlainString();
    }

    /**
     * Returns the total number of individual items in the cart.
     */
    public int getProductCount()
    {
        int count = 0;
        for (CartProduct cp : products)
        {
            count += cp.getProductCount();
        }
        return count;
    }
}
