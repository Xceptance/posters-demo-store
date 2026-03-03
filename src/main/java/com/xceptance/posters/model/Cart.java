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

    private double shippingCosts;
    private double subTotalPrice;
    private double tax;
    private double totalTaxPrice;
    private double totalPrice;

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

    public double getShippingCosts()
    {
        return shippingCosts;
    }

    public void setShippingCosts(double shippingCosts)
    {
        this.shippingCosts = shippingCosts;
    }

    public double getSubTotalPrice()
    {
        return subTotalPrice;
    }

    public void setSubTotalPrice(double subTotalPrice)
    {
        this.subTotalPrice = subTotalPrice;
    }

    public double getTax()
    {
        return tax;
    }

    public void setTax(double tax)
    {
        this.tax = tax;
    }

    public double getTotalTaxPrice()
    {
        return Math.max(totalTaxPrice, 0);
    }

    public void setTotalTaxPrice(double totalTaxPrice)
    {
        this.totalTaxPrice = totalTaxPrice;
    }

    public double getTotalPrice()
    {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice)
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
        setTotalTaxPrice(getTax() * (getSubTotalPrice() + getShippingCosts()));
    }

    public void calculateTotalPrice()
    {
        setTotalPrice(getSubTotalPrice() + getTotalTaxPrice() + getShippingCosts());
    }

    // --- Formatted Strings ---

    private static String formatPrice(double value)
    {
        final DecimalFormat f = new DecimalFormat("#0.00");
        double temp = Math.round(value * 100.0) / 100.0;
        return f.format(temp).replace(',', '.');
    }

    public String getShippingCostsAsString()
    {
        return formatPrice(shippingCosts);
    }

    public String getSubTotalPriceAsString()
    {
        return formatPrice(subTotalPrice);
    }

    public String getTotalTaxPriceAsString()
    {
        return formatPrice(totalTaxPrice);
    }

    public String getTotalPriceAsString()
    {
        return formatPrice(totalPrice);
    }

    public String getTaxAsString()
    {
        return String.valueOf(tax * 100);
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
